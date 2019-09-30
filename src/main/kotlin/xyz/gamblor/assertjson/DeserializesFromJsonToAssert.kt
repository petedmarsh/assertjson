package xyz.gamblor.assertjson

import org.assertj.core.api.AbstractAssert
import org.assertj.core.util.Objects.areEqual


typealias JsonDeserializer = (json: String) -> Any?
typealias TypedJsonDeserializer = (json: String, clazz: Class<*>) -> Any?

class DeserializesFromJsonToAssert(actual: String) : AbstractAssert<DeserializesFromJsonToAssert, String>(actual, DeserializesFromJsonToAssert::class.java) {
    companion object {
        @JvmStatic
        fun assertThat(actual: String): DeserializesFromJsonToAssert {
            return DeserializesFromJsonToAssert(actual)
        }
    }

    lateinit var deserializer: JsonDeserializer
    lateinit var typedDeserializer: TypedJsonDeserializer

    fun deserializedBy(deserializer: JsonDeserializer): DeserializesFromJsonToAssert {
        this.deserializer = deserializer
        return this
    }

    fun deserializedBy(typedDeserializer: TypedJsonDeserializer): DeserializesFromJsonToAssert {
        this.typedDeserializer = typedDeserializer
        return this
    }

    override fun isEqualTo(expected: Any?): DeserializesFromJsonToAssert {
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

        if (!areEqual(actualDeserialized, expected)) {
            failWithMessage("Expecting deserialized actual:\n <%s>\nto be equal to:\n <%s>\nbut they are not equal", actualDeserialized, expected)
        }

        return this
    }

}
