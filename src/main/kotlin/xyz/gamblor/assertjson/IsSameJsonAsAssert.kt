package xyz.gamblor.assertjson

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import org.assertj.core.api.AbstractAssert

val mapper = ObjectMapper()

class IsSameJsonAsAssert(actual: String) : AbstractAssert<IsSameJsonAsAssert, String>(actual, IsSameJsonAsAssert::class.java) {

    companion object {
        @JvmStatic
        fun assertThat(actual: String): IsSameJsonAsAssert {
            return IsSameJsonAsAssert(actual)
        }
    }

    fun isSameJsonAs(expected: String): IsSameJsonAsAssert {
        val actualJsonNode: JsonNode = try {
            mapper.readTree(actual)
        } catch (e: JsonParseException) {
            failWithMessage("Expecting actual:\n <%s>\nto be valid JSON, but it isn't:\n %s", actual, e.message)
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
            failWithMessage("Expecting:\n <%s>\nto be same JSON as:\n <%s>\nbut it isn't", actual, expected)
        }

        return this
    }

}
