package com.megad.cpanel.core

import android.content.Context
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.util.Log
import com.megad.cpanel.core.requests.ScanRequest
import com.megad.cpanel.extensions.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import java.io.IOException
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException

class DeviceManager {

    companion object {
        private const val DATAGRAM_SIZE = 256
        private const val SERVICE_TYPE = "_smart-home._udp."

        private val TAG = DeviceManager::class.java.simpleName

        private lateinit var socket: DatagramSocket
        private lateinit var broadcastSocket: DatagramSocket

        private val broadcastAddresses: List<DeviceAddress> by lazy {
            NetworkInterface
                    .getNetworkInterfaces().toList()
                    .filter { !it.isLoopback && it.isUp }
                    .flatMap { it.interfaceAddresses }
                    .filter { it.broadcast != null }
                    .map { it.broadcast }
                    .map { DeviceAddress(it, 42000) }
        }

        @Throws(IOException::class)
        private fun getBroadcastAddress(wifi: WifiManager): InetAddress {
//            val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val dhcp = wifi.dhcpInfo
            // handle null somehow
            val broadcast = dhcp.ipAddress and dhcp.netmask or dhcp.netmask.inv()
            val quads = ByteArray(4)
            for (k in 0..3)
                quads[k] = (broadcast shr k * 8).toByte()
            return InetAddress.getByAddress(quads)
        }

        fun discover(wifi: WifiManager): Flowable<DeviceAddress> = Flowable.create({
            Log.d(TAG, "discover()")
            Log.d(TAG, getBroadcastAddress(wifi).toString())
            Log.d(TAG, broadcastAddresses.toString())
            broadcastSocket = DatagramSocket().apply { broadcast = true }
            it.setCancellable { broadcastSocket.close() }
            ScanRequest().toDeviceRequests(broadcastAddresses).forEach { request: DeviceRequest2 ->
                request.toDatagramPacket().sendTo(broadcastSocket)
            }
        }, BackpressureStrategy.BUFFER)

        fun discover(nsdManager: NsdManager): Flowable<DeviceAddress> = nsdManager
                .discoverServicesFlowable(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD)
                .flatMap {
                    nsdManager
                            .resolveServiceSingle(it)
                            .toFlowable()
                            .onErrorResumeNext { throwable: Throwable ->
                                Log.e(TAG, "Failed to resolve service: $it", throwable)
                                Flowable.empty()
                            }
                }
                .map { DeviceAddress(it.host, it.port) }

        fun listen(): Flowable<DeviceState> = Flowable.create({
            socket = DatagramSocket()
            it.setCancellable { socket.close() }
            while (true) {
                try {
                    socket.receive(DATAGRAM_SIZE).toDeviceState().emitTo(it)
                } catch (throwable: SocketException) {
                    Log.w(TAG, throwable.message)
                    it.onComplete()
                    break
                } catch (throwable: Exception) {
                    Log.e(TAG, "Failed to receive a response.", throwable)
                }
            }
        }, BackpressureStrategy.BUFFER)

        fun send(request: DeviceRequest): Completable = Completable.create {
            Log.v(TAG, "Sending $request")
            request.toDatagramPacket(request.address.address, request.address.port).sendTo(socket)
            it.onComplete()
        }

        fun send(address: DeviceAddress, request: Request) = send(DeviceRequest(address, request))
    }
}
