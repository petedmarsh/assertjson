package xyz.gamblor.assertjson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import org.assertj.core.api.AbstractAssert
import org.assertj.core.util.Objects

typealias JsonDeserializer = (json: String) -> Any?
typealias TypedJsonDeserializer = (json: String, clazz: Class<*>) -> Any?

val mapper = ObjectMapper()

class IsSameJsonAsAssert(actual: String) : AbstractAssert<IsSameJsonAsAssert, String>(actual, IsSameJsonAsAssert::class.java), JsonHelpers {
    companion object {
        @JvmStatic
        fun assertThat(actual: String): IsSameJsonAsAssert {
            return IsSameJsonAsAssert(actual)
        }
    }


    lateinit var deserializer: JsonDeserializer
    lateinit var typedDeserializer: TypedJsonDeserializer

    override fun failWithMessage(errorMessage: String?, vararg arguments: Any?) {
        super.failWithMessage(errorMessage, *arguments)
    }

    fun isSameJsonAs(expected: String): IsSameJsonAsAssert {
        val actualJsonNode: JsonNode = parseJson(actual, "Expecting actual:\n <%s>\nto be valid JSON, but it isn't:\n %s")

        val expectedJsonNode: JsonNode = parseJson(expected, "Expecting expected:\n <%s>\nto be valid JSON, but it isn't:\n %s")

        val diff = JsonDiff.asJson(expectedJsonNode, actualJsonNode)

        if (diff != EMPTY_DIFF) {
            failWithMessage("Expecting:\n <%s>\nto be same JSON as:\n <%s>\nbut it isn't", actual, expected)
        }

        return this
    }

    fun deserializedBy(deserializer: JsonDeserializer): IsSameJsonAsAssert {
        this.deserializer = deserializer
        return this
    }

    fun deserializedBy(typedDeserializer: TypedJsonDeserializer): IsSameJsonAsAssert {
        this.typedDeserializer = typedDeserializer
        return this
    }

    override fun isEqualTo(expected: Any?): IsSameJsonAsAssert {
        if (!this::deserializer.isInitialized && !this::typedDeserializer.isInitialized) {
            failWithMessage("To use this assertion first you must provide a deserializer using deserializedBy(...)")
        }

        if (!this::deserializer.isInitialized.xor(this::typedDeserializer.isInitialized)) {
            failWithMessage("To use this assertion first you must provide either a JsonDeserializer or a TypedJsonDeserializer (but not both!) using deserializedBy(...)")
        }

        val actualDeserialized = if (this::deserializer.isInitialized) {
            try {
                deserializer.invoke(actual)
            }  catch (e: Exception) {
                failWithMessage("Expecting actual:\n <%s>\nto be deserializable from JSON by:\n <%s>\nbut deserialization failed:\n %s", actual, deserializer.toString(), e.message)
                throw RuntimeException() // failWithMessage will raise, this is to satisfy compiler
            }
        } else {
            try {
                typedDeserializer.invoke(actual, if (expected == null) Any::class.java else expected::class.java)
            }  catch (e: Exception) {
                failWithMessage("Expecting actual:\n <%s>\nto be deserializable from JSON by:\n <%s>\nbut deserialization failed:\n %s", actual, typedDeserializer.toString(), e.message)
                throw RuntimeException() // failWithMessage will raise, this is to satisfy compiler
            }
        }

        if (!Objects.areEqual(actualDeserialized, expected)) {
            failWithMessage("Expecting deserialized actual:\n <%s>\nto be equal to:\n <%s>\nbut they are not equal", actualDeserialized, expected)
        }

        return this
    }
}
