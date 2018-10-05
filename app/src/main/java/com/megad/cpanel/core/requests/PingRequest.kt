package com.megad.cpanel.core.requests

import com.megad.cpanel.core.Request
import com.megad.cpanel.extensions.init
import org.json.JSONObject

data class PingRequest(val unused: Unit = Unit) : Request {
    override val payload = PAYLOAD

    companion object {
        private val PAYLOAD = JSONObject().init {
            put("t", "PING")
        }
    }
}
