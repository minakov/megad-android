package com.megad.cpanel.core.requests

import com.megad.cpanel.core.*
import okio.Buffer
import java.net.DatagramPacket
import java.net.InetAddress

data class ScanRequest(val unused: Unit = Unit) : Request2 {
    override val payload = PAYLOAD

    fun toDeviceRequests(addresses: List<DeviceAddress>) = addresses.map { DeviceRequest2(it, PAYLOAD) }

    companion object {
        private val PAYLOAD = Buffer().writeByte(0xAA).writeByte(0x00).writeByte(0x0C).snapshot().toByteArray()
    }
}
