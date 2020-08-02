package com.tommybart.chicagotraintracker.internal.extensions

import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun JsonObject.getNullable(key: String): JsonElement? {
    val value = this.get(key) ?: return null
    return if (value.isJsonNull) null else value
}