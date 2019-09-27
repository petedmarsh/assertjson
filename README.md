# Assert JSON

Custom AssertJ assertions for comparing JSON.

## Usage

```kotlin
package xyz.gamblor.example

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import xyz.gamblor.assertjson.AssertJsonAssertions.Companion.assertThat

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExampleTest {

    @Test
    fun `compare strings containing JSON`() {
        assertThat("{\"some\": \"json\"}").isSameJsonAs("{\"some\": \"json\"}")
    }

    @Test
    fun `serialize an object and compare to a string`() {
        assertThat(mapOf("something" to "serialize"))
            .serializedBy { ObjectMapper().writeValueAsString(it) }
            .isSameJsonAs("{\"something\": \"serialize\"}")
    }

}
```

## See

* [AssertJ](https://joel-costigliola.github.io/assertj/)
* [zjsonpatch](https://github.com/flipkart-incubator/zjsonpatch)
