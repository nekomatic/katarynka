package com.nekomatic.katarynka.core.parsers

import arrow.core.Either
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll


internal class AnyParserTest {

    private val text0 = ""
    private val text1 = "a"
    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val parser = factory.any("any")

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.of(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                { assertEquals("any", (result as Either.Left).a.head.expected) },
                { assertEquals(input.position, (result as Either.Left).a.head.failedAtInput.position) }
        )
    }

    @DisplayName("Non-empty input")
    @Test
    fun nonEmptyInput() {
        val input = LineInput.of(text1.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { assertEquals('a', (result as Either.Right).b.value) },
                { assertEquals(input.position + 1, (result as Either.Right).b.remainingInput.position) }
        )
    }
}
