package xyz.gamblor.assertjson

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeserializesFromJsonToAssertTest {

    @Nested
    inner class assertThat {

        @Test
        fun `when actual is valid JSON`() {
            val assert = DeserializesFromJsonToAssert.assertThat("{}")

            assertThat(assert).isNotNull()
        }

        @Test
        fun `when actual is not valid JSON`() {
            val assert = DeserializesFromJsonToAssert.assertThat("this is not valid JSON")

            assertThat(assert).isNotNull()
        }

    }

    @Nested
    inner class isEqualTo {

        @Nested
        inner class `when a deserializer has been specified` {

           inline fun <reified T> deserializer(): JsonDeserializer = { mapper.readValue(it, T::class.java) }

            @Test
            fun `when actual is not valid JSON`() {
                val actual = "this is not valid JSON"

                val deserializer = deserializer<Any>()

                val thrown: Throwable = catchThrowable {
                        DeserializesFromJsonToAssert.assertThat(actual)
                            .deserializedBy(deserializer).isEqualTo(Any())
                    }

                assertThat(thrown).hasMessage(
                    """Expecting actual:
                      | <$actual>
                      |to be deserializable from JSON by:
                      | <$deserializer>
                      |but deserialization failed:
                      | Unrecognized token 'this': was expecting 'null', 'true', 'false' or NaN
                      | at [Source: (String)"this is not valid JSON"; line: 1, column: 5]""".trimMargin())
            }

            @Test
            fun `when actual is valid JSON but does not deserialize to an instance equal to expected`() {
                val actual = "{\"some\": \"json\"}"
                val expected = mapOf("some" to "map")

                val deserializer = deserializer<Map<String, String>>()

                val thrown: Throwable = catchThrowable {
                    DeserializesFromJsonToAssert.assertThat(actual)
                        .deserializedBy(deserializer).isEqualTo(expected)
                }

                assertThat(thrown).hasMessage(
                    """Expecting deserialized actual:
                      | <${mapOf("some" to "json")}>
                      |to be equal to:
                      | <$expected>
                      |but they are not equal""".trimMargin())
            }

            @Test
            fun `when actual is valid JSON and does not deserialize to an instance equal to expected`() {
                val actual = "{\"some\": \"json\"}"
                val expected = mapOf("some" to "json")

                val assert = DeserializesFromJsonToAssert.assertThat(actual)

                assertThat(assert.deserializedBy(deserializer<Map<String, String>>()).isEqualTo(expected)).isSameAs(assert)
            }

        }

        @Nested
        inner class `when a typed deserializer has been specified` {

            val typedDeserializer: TypedJsonDeserializer = { json, clazz -> mapper.readValue(json, clazz) }

            @Test
            fun `when actual is not valid JSON`() {
                val actual = "this is not valid JSON"

                val thrown: Throwable = catchThrowable {
                    DeserializesFromJsonToAssert.assertThat(actual)
                        .deserializedBy(typedDeserializer).isEqualTo(Any())
                }

                assertThat(thrown).hasMessage(
                    """Expecting actual:
                      | <$actual>
                      |to be deserializable from JSON by:
                      | <$typedDeserializer>
                      |but deserialization failed:
                      | Unrecognized token 'this': was expecting 'null', 'true', 'false' or NaN
                      | at [Source: (String)"this is not valid JSON"; line: 1, column: 5]""".trimMargin())
            }

            @Test
            fun `when actual is valid JSON but does not deserialize to an instance equal to expected`() {
                val actual = "{\"some\": \"json\"}"
                val expected = mapOf("some" to "map")

                val thrown: Throwable = catchThrowable {
                    DeserializesFromJsonToAssert.assertThat(actual)
                        .deserializedBy(typedDeserializer).isEqualTo(expected)
                }

                assertThat(thrown).hasMessage(
                    """Expecting deserialized actual:
                      | <${mapOf("some" to "json")}>
                      |to be equal to:
                      | <$expected>
                      |but they are not equal""".trimMargin())
            }

            @Test
            fun `when actual is valid JSON and does not deserialize to an instance equal to expected`() {
                val actual = "{\"some\": \"json\"}"
                val expected = mapOf("some" to "json")

                val assert = DeserializesFromJsonToAssert.assertThat(actual)

                assertThat(assert.deserializedBy(typedDeserializer).isEqualTo(expected)).isSameAs(assert)
            }
        }

        @Test
        fun `when a deserializer has not been specified`() {
            val thrown: Throwable = catchThrowable {
                DeserializesFromJsonToAssert.assertThat("{\"some\": \"json\"}")
                    .isEqualTo(Any())
            }

            assertThat(thrown).hasMessage("To use this assertion first you must provide a deserializer using deserializedBy(...)")
        }
    }

}
