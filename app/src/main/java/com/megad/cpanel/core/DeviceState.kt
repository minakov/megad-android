package com.megad.cpanel.core

data class DeviceState(
    val address: DeviceAddress,
    val messageId: Int,
    val uuid: String,
    val deviceType: DeviceType,
    val name: String,
    val customState: DeviceCustomState
)
