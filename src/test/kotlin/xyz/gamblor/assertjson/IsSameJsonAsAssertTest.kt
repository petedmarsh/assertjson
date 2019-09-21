package xyz.gamblor.assertjson

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IsSameJsonAsAssertTest {

    @Nested
    inner class assertThat {

        @Test
        fun `when actual is valid JSON`() {
            val assert = IsSameJsonAsAssert.assertThat("{}")

            assertThat(assert).isNotNull()
        }

        @Test
        fun `when actual is not valid JSON`() {
            val assert = IsSameJsonAsAssert.assertThat("this is not valid JSON")

            assertThat(assert).isNotNull()
        }

    }

    @Nested
    inner class constructor {

        @Test
        fun `when actual is valid JSON`() {
            val assert = IsSameJsonAsAssert("{}")

            assertThat(assert).isNotNull()
        }

        @Test
        fun `when actual is not valid JSON`() {
            val assert =  IsSameJsonAsAssert("this is not valid JSON")

            assertThat(assert).isNotNull()
        }

    }

    @Nested
    inner class isSameJsonAs {

        @Test
        fun `when expected is not valid JSON`() {
            val expected = "this is not valid JSON"

            val thrown: Throwable = catchThrowable { IsSameJsonAsAssert.assertThat("{}").isSameJsonAs(expected) }

            assertThat(thrown).hasMessage(
                """Expecting expected:
                | <$expected>
                |to be valid JSON, but it isn't:
                | Unrecognized token 'this': was expecting 'null', 'true', 'false' or NaN
                | at [Source: (String)"this is not valid JSON"; line: 1, column: 5]""".trimMargin())
        }

        @Test
        fun `when actual and expected are semantically identical`() {
            val actual =   "{\"key1\": \"valueA\", \"key2\": \"valueB\"}"
            val expected = "{\"key2\": \"valueB\", \"key1\": \"valueA\"}"

            val assert = IsSameJsonAsAssert(actual)

            assertThat(assert.isSameJsonAs(expected)).isSameAs(assert)
        }

        @Test
        fun `when actual and expected are syntactically identical`() {
            val actual =   "{\"key1\": \"valueA\", \"key2\": \"valueB\"}"
            val expected = "{\"key1\": \"valueA\", \"key2\": \"valueB\"}"

            val assert = IsSameJsonAsAssert(actual)

            assertThat(assert.isSameJsonAs(expected)).isSameAs(assert)
        }

        @Test
        fun `when actual and expected different`() {
            val actual =   "{\"key1\": \"valueA\", \"key2\": \"valueB\"}"
            val expected = "{\"key1\": \"valueA\"}"

            val thrown: Throwable = catchThrowable { IsSameJsonAsAssert.assertThat(actual).isSameJsonAs(expected) }

            assertThat(thrown).hasMessage(
                """Expecting:
                  | <$actual>
                  |to be same JSON as:
                  | <$expected>
                  |but it isn't""".trimMargin())
        }

    }
}