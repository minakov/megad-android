package com.megad.cpanel.core

import org.json.JSONObject

interface Request {
    val payload: JSONObject
}
