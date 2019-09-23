package xyz.gamblor.assertjson

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SerializesToSameJsonAsAssertTest {

    @Nested
    inner class assertThat {

        @Test
        fun `when actual is null`() {
            val assert = SerializesToSameJsonAsAssert.assertThat(null)

            assertThat(assert).isNotNull()
        }

        @Test
        fun `when actual is not null`() {
            val assert = SerializesToSameJsonAsAssert.assertThat(Any())

            assertThat(assert).isNotNull()
        }

    }

    @Nested
    inner class constructor {

        @Test
        fun `when actual is null`() {
            val assert = SerializesToSameJsonAsAssert(null)

            assertThat(assert).isNotNull()
        }

        @Test
        fun `when actual is not null`() {
            val assert = SerializesToSameJsonAsAssert(Any())

            assertThat(assert).isNotNull()
        }

    }

    @Nested
    inner class serializedBy {

        @Test
        fun `returns self for method chaining`() {
            val assert = SerializesToSameJsonAsAssert.assertThat(Any())
            val assertWithSerializer = assert.serializedBy { _ -> "{}" }

            assertThat(assertWithSerializer).isSameAs(assert)
        }

        @Test
        fun `does not check validity of serializer`() {
            val assert = SerializesToSameJsonAsAssert.assertThat(Any())
            val assertWithSerializer = assert.serializedBy { _ -> throw RuntimeException() }

            assertThat(assertWithSerializer).isSameAs(assert)
        }

    }

    @Nested
    inner class isSameJsonAs {

        val mapper = ObjectMapper()
        val serializer: JsonSerializer = { a -> mapper.writeValueAsString(a) }

        @Test
        fun `when actual serializes to JSON that is semantically identical to expected`() {
            val actual = mapOf("key1" to "valueA", "key2" to "valueB")
            val expected = "{\"key2\": \"valueB\", \"key1\": \"valueA\"}"

            SerializesToSameJsonAsAssert.assertThat(actual)
                .serializedBy(serializer).isSameJsonAs(expected)
        }

        @Test
        fun `when actual serializes to JSON that is syntactically identical to expected`() {
            val actual= mapOf("key1" to "valueA", "key2" to "valueB")
            val expected = "{\"key1\":\"valueA\",\"key2\":\"valueB\"}"

            assertThat(serializer.invoke(actual)).isEqualTo(expected)  // sanity check

            SerializesToSameJsonAsAssert.assertThat(actual)
                .serializedBy(serializer).isSameJsonAs(expected)
        }

        @Test
        fun `when actual serializes to JSON that is semantically different to expected`() {
            val actual = mapOf("key1" to "valueA", "key2" to "valueB")
            val expected = "{\"key1\": \"valueA\"}"

            val thrown: Throwable = catchThrowable {
                SerializesToSameJsonAsAssert.assertThat(actual)
                    .serializedBy(serializer).isSameJsonAs(expected)
            }

            assertThat(thrown).hasMessage(
                """Expecting serialized actual:
                | <${serializer.invoke(actual)}>
                |to be same JSON as:
                | <$expected>
                |but it isn't""".trimMargin())
        }

        @Test
        fun `when no serializer has been provided`() {
            val actual = "{\"key1\": \"valueA\"}"
            val expected = "{\"key1\": \"valueA\"}"
            val methodName = SerializesToSameJsonAsAssert::serializedBy.name

            val thrown: Throwable = catchThrowable {
                SerializesToSameJsonAsAssert.assertThat(actual).isSameJsonAs(expected)
            }

            assertThat(thrown)
                .hasMessage("To use this assertion first you must provide a serializer using $methodName(...)")
        }

        @Test
        fun `when actual serializes to invalid JSON`() {
            val actual = "irrelevant"
            val expected = "{\"key1\": \"valueA\"}"
            val faultySerializer: JsonSerializer = { _ -> "this is not valid JSON" }

            val thrown: Throwable = catchThrowable {
                SerializesToSameJsonAsAssert.assertThat(actual)
                    .serializedBy(faultySerializer).isSameJsonAs(expected)
            }

            assertThat(thrown).hasMessage(
                """Expecting serialized actual:
                | <${faultySerializer.invoke(actual)}>
                |to be valid JSON, but it isn't:
                | Unrecognized token 'this': was expecting 'null', 'true', 'false' or NaN
                | at [Source: (String)"this is not valid JSON"; line: 1, column: 5]""".trimMargin())
        }

        @Test
        fun `when serializer raises when serializing actual`() {
            val actual = "irrelevant"
            val expected = "{\"key1\": \"valueA\"}"
            val expectedException = RuntimeException("testing serialization failure")
            val faultySerializer: JsonSerializer = { _ -> throw expectedException }

            val thrown: Throwable = catchThrowable {
                SerializesToSameJsonAsAssert.assertThat(actual)
                    .serializedBy(faultySerializer).isSameJsonAs(expected)
            }

            assertThat(thrown).hasMessage(
                """Expecting actual:
                | <$actual>
                |to be serializable to JSON by:
                | <$faultySerializer>
                |but serialization failed:
                | ${expectedException.message}""".trimMargin())
        }

        @Test
        fun `when expected is not valid JSON`() {
            val actual = "{\"key1\": \"valueA\"}"
            val expected = "this is not valid JSON"

            val thrown: Throwable = catchThrowable {
                SerializesToSameJsonAsAssert.assertThat(actual)
                    .serializedBy(serializer).isSameJsonAs(expected)
            }

            assertThat(thrown).hasMessage(
                """Expecting expected:
                | <$expected>
                |to be valid JSON, but it isn't:
                | Unrecognized token 'this': was expecting 'null', 'true', 'false' or NaN
                | at [Source: (String)"this is not valid JSON"; line: 1, column: 5]""".trimMargin())
        }
    }

}