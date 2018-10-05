package com.megad.cpanel.extensions

import android.util.Log
import com.megad.cpanel.core.DeviceAddress
import com.megad.cpanel.core.DeviceType
import com.megad.cpanel.core.DeviceState
import com.megad.cpanel.core.states.MulticolorLightingState
import java.net.DatagramPacket
import java.net.DatagramSocket

private val TAG = DatagramPacket::class.java.simpleName

fun DatagramPacket.toByteArray() = data.sliceArray(0..length)

fun DatagramPacket.sendTo(socket: DatagramSocket) {
    Log.v(TAG, "Sending $length bytes to $socketAddress")
    socket.send(this)
}

fun DatagramPacket.toDeviceState() : DeviceState {
    val payload = this.toByteArray().toString(Charsets.UTF_8).toJSONObject()
    val deviceType = DeviceType.valueOf(payload.getString("t"))
    return DeviceState(
        address = DeviceAddress.from(this),
        messageId = payload.optInt("mid", 0),
        uuid = payload.getString("id"),
        deviceType = deviceType,
        name = payload.getString("name"),
        customState = when (deviceType) {
            DeviceType.MULTICOLOR_LIGHTING -> MulticolorLightingState(payload)
        }
    )
}
