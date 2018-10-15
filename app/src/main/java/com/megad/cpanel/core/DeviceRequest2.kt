package com.megad.cpanel.core

import com.megad.cpanel.extensions.toDatagramPacket
import java.net.InetAddress

data class DeviceRequest2(private val address: DeviceAddress, private val request: ByteArray) {
    fun toDatagramPacket() = request.toDatagramPacket(address.address, address.port)
}
