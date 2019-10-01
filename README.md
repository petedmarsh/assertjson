# Assert JSON

Custom AssertJ assertions for comparing JSON.

## Installation

Assert JSON is not currently published to any repository. It can be added as a source dependency to a gradle (4.10+)
project (see: https://blog.gradle.org/introducing-source-dependencies).

Add the following to `settings.gradle`:

```
//settings.gradle
//...
sourceControl {
    gitRepository("https://github.com/petedmarsh/assertjson.git") {
        producesModule("xyz.gamblor:assertjson")
    }
}
/...
```

Then add a dependency to `build.gradle.kt` as usual:

```kotlin
//build.gradle.kt
//...
dependencies {
    testImplementation("xyz.gamblor:assertjson:1.1.1")
}
//...
```

The version (e.g. `1.1.0`) should match a published tag, see: https://github.com/petedmarsh/assertjson/releases for a
complete list.

### IntelliJ

If you are using IntelliJ you will also likely want to use the `idea` plugin (if you are not already):

```kotlin
//build.gradle.kt
plugins {
    idea
    //...
}
//...
```

This will make sure the JAR for Assert JSON is downloaded and available in IntelliJ.

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
    fun `deserialize a string and compare to an object`() {
        assertThat("{\"something\": \"deserialize\"}")
            .deserializedBy { json -> ObjectMapper().readValue(json, Map::class.java) }
            .isEqualTo(mapOf("something" to "deserialize"))
    }

    @Test
    fun `deserialize a string to a specified type and compare to an object`() {
        assertThat("{\"something\": \"deserialize\"}")
            // clazz is inferred from the instance given in isEqualTo
            .deserializedBy { json, clazz -> ObjectMapper().readValue(json, clazz) }
            .isEqualTo(mapOf("something" to "deserialize"))
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
