package com.nekomatic.katarynka.core.parsers

import arrow.core.Either
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ItemParserTest {
    private val text0 = ""
    private val textA = "a"
    private val textB = "b"
    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val parser = factory.item('a')

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.of(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                { assertEquals("a", (result as Either.Left).a.head.expected) },
                { assertEquals(input.position, (result as Either.Left).a.head.failedAtInput.position) }
        )

    }

    @DisplayName("Matching input")
    @Test
    fun matchingInput() {
        val input = LineInput.of(textA.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { assertEquals('a', (result as Either.Right).b.value) },
                { assertEquals(input.position + 1, (result as Either.Right).b.remainingInput.position) }
        )
    }

    @DisplayName("Non-matching input")
    @Test
    fun nonMatchingInput() {
        val input = LineInput.of(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                { assertEquals("a", (result as Either.Left).a.head.expected) },
                { assertEquals(input.position, (result as Either.Left).a.head.failedAtInput.position) }
        )

    }

}