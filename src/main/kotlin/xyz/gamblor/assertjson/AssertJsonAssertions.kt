package xyz.gamblor.assertjson

class AssertJsonAssertions {

    companion object {
        @JvmStatic
        fun assertThat(json: String): IsSameJsonAsAssert {
            return IsSameJsonAsAssert(json)
        }

        @JvmStatic
        fun assertThat(o: Any): SerializesToSameJsonAsAssert {
            return SerializesToSameJsonAsAssert(o)
        }
    }

}