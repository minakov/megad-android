package com.megad.cpanel.core

import com.megad.cpanel.extensions.toDatagramPacket
import java.net.InetAddress

data class DeviceRequest(val address: DeviceAddress, private val request: Request) {
    fun toDatagramPacket(host: InetAddress, port: Int) = request.payload
        .toString()
        .toByteArray(Charsets.UTF_8)
        .toDatagramPacket(host, port)
}
