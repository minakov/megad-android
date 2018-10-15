package com.megad.cpanel.extensions

import java.net.InetAddress
import java.net.NetworkInterface

fun NetworkInterface.getBroadcastAddresses(): List<InetAddress> {
    return NetworkInterface.getNetworkInterfaces().toList()
            .filter { !it.isLoopback && it.isUp }
            .flatMap { it.interfaceAddresses }
            .filter { it.broadcast != null }
            .map { it.broadcast }
}