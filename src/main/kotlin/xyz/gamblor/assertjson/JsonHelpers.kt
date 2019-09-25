package xyz.gamblor.assertjson

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode

internal val EMPTY_DIFF = mapper.readTree("[]")!!

internal interface JsonHelpers {
    fun failWithMessage(errorMessage: String?, vararg arguments: Any?)

    fun parseJson(json: String, failureMessageTemplate: String): JsonNode {
        return try {
            mapper.readTree(json)
        } catch (e: JsonParseException) {
            failWithMessage(failureMessageTemplate, json, e.message)
            throw RuntimeException() // failWithMessage will raise, this is to satisfy compiler
        }
    }
}
