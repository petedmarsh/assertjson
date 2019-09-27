package xyz.gamblor.assertjson

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AssertJsonAssertionsTest {

    @Nested
    inner class assertThat {

        @Test
        fun `when passed a string returns a IsSameJsonAsAssert instance`() {
            assertThat(AssertJsonAssertions.assertThat("{}")).isInstanceOf(IsSameJsonAsAssert::class.java)
        }

        @Test
        fun `when passed a non-string instance returns a SerializesToSameJsonAsAssert instance`() {
            assertThat(AssertJsonAssertions.assertThat(Any())).isInstanceOf(SerializesToSameJsonAsAssert::class.java)
        }

    }

}
