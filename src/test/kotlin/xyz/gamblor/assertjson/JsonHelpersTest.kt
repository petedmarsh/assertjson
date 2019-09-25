package xyz.gamblor.assertjson

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonHelpersTest {

    class JsonHelpersStub : JsonHelpers {
        override fun failWithMessage(errorMessage: String?, vararg arguments: Any?) {
            throw RuntimeException(errorMessage!!.format(*arguments))
        }
    }

    @Nested
    inner class parseJson {

        @Test
        fun `when JSON is valid`() {
            val validJson = "{}"
            val parsedJson = JsonHelpersStub().parseJson(validJson, "json: %s | exception message: %s")

            assertThat(parsedJson).isEqualTo(mapper.readTree(validJson))
        }

        @Test
        fun `when JSON is not valid`() {
            val invalidJson = "this is not valid json"
            val thrown: Throwable = catchThrowable {
                JsonHelpersStub().parseJson("this is not valid json", "json: %s | exception message: %s")
            }

            assertThat(thrown).hasMessage("json: $invalidJson | exception message: " +
                    "Unrecognized token 'this': was expecting 'null', 'true', 'false' or NaN\n" +
                    " at [Source: (String)\"this is not valid json\"; line: 1, column: 5]")
        }

    }

}
