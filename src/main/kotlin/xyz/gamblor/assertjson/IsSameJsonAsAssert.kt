package xyz.gamblor.assertjson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import org.assertj.core.api.AbstractAssert

val mapper = ObjectMapper()

class IsSameJsonAsAssert(actual: String) : AbstractAssert<IsSameJsonAsAssert, String>(actual, IsSameJsonAsAssert::class.java), JsonHelpers {
    companion object {
        @JvmStatic
        fun assertThat(actual: String): IsSameJsonAsAssert {
            return IsSameJsonAsAssert(actual)
        }
    }

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

}
