package xyz.gamblor.assertjson

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import com.flipkart.zjsonpatch.JsonDiff
import org.assertj.core.api.AbstractAssert

typealias JsonSerializer = (something: Any?) -> String

class SerializesToSameJsonAsAssert(actual: Any?) : AbstractAssert<SerializesToSameJsonAsAssert, Any?>(actual, SerializesToSameJsonAsAssert::class.java) {

    lateinit var serializer: JsonSerializer

    companion object {
        @JvmStatic
        fun assertThat(actual: Any?): SerializesToSameJsonAsAssert {
            return SerializesToSameJsonAsAssert(actual)
        }
    }

    fun serializedBy(serializer: JsonSerializer): SerializesToSameJsonAsAssert {
        this.serializer = serializer
        return this
    }

    fun isSameJsonAs(expected: String) {
        val actualJson = try {
            serializer.invoke(actual)
        } catch (e: UninitializedPropertyAccessException) {
            failWithMessage("To use this assertion first you must provide a serializer using serializedBy(...)")
            throw RuntimeException() // failWithMessage will raise, this is to satisfy compiler
        } catch (e: Exception) {
            failWithMessage("Expecting actual:\n <%s>\nto be serializable to JSON by:\n <%s>\nbut serialization failed:\n %s", actual, serializer.toString(), e.message)
            throw RuntimeException() // failWithMessage will raise, this is to satisfy compiler
        }

        val actualJsonNode = try {
            mapper.readTree(actualJson)
        } catch(e: JsonParseException) {
            failWithMessage("Expecting serialized actual:\n <%s>\nto be valid JSON, but it isn't:\n %s", actualJson, e.message)
            throw RuntimeException() // failWithMessage will raise, this is to satisfy compiler
        }

        val expectedJsonNode: JsonNode = try {
            mapper.readTree(expected)
        } catch (e: JsonParseException) {
            failWithMessage("Expecting expected:\n <%s>\nto be valid JSON, but it isn't:\n %s", expected, e.message)
            throw RuntimeException() // failWithMessage will raise, this is to satisfy compiler
        }

        val diff = JsonDiff.asJson(expectedJsonNode, actualJsonNode)

        if (diff != EMPTY_DIFF) {
            failWithMessage("Expecting serialized actual:\n <%s>\nto be same JSON as:\n <%s>\nbut it isn't", actualJson, expected)
        }

    }
}
