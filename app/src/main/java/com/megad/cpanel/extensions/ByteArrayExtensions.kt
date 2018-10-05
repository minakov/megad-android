package com.megad.cpanel.extensions

import java.net.DatagramPacket
import java.net.InetAddress

fun ByteArray.toDatagramPacket(host: InetAddress, port: Int) =
    DatagramPacket(this, this.size, host, port)
