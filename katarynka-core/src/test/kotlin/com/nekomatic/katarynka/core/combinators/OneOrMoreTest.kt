package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class OneOrMoreTest {
    private val text0 = ""
    private val textA = "a"
    private val textAAB = "aab"
    private val textB = "b"
    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val parser = factory.item('a').oneOrMore()


    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.of(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                { assertEquals("a", (result as Either.Left).a.head.expected) },
                { assertEquals(input.position, (result as Either.Left).a.head.failedAtInput.position) },
                { assertEquals(1, (result as Either.Left).a.size) }
        )

    }

    @DisplayName("Single-match input")
    @Test
    fun singleMatchInput() {
        val input = LineInput.of(textA.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { assertEquals(listOf('a'), (result as Either.Right).b.value) },
                { assertEquals(input.position + 1, (result as Either.Right).b.remainingInput.position) }
        )
    }

    @DisplayName("Multi-match input")
    @Test
    fun multiMatchInput() {
        val input = LineInput.of(textAAB.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { assertEquals(listOf('a', 'a'), (result as Either.Right).b.value) },
                { assertEquals(input.position + 2, (result as Either.Right).b.remainingInput.position) }
        )
    }

    @DisplayName("No-match input")
    @Test
    fun noMatchInput() {
        val input = LineInput.of(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                { assertEquals("a", (result as Either.Left).a.head.expected) },
                { assertEquals(input.position, (result as Either.Left).a.head.failedAtInput.position) },
                { assertEquals(1, (result as Either.Left).a.size) }
        )
    }

}