package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ZeroOrMoreTest {

    private val factory = ParserFactory<Char, LineInput<Char>>()

    private val text0 = "".toList()
    private val textA = "a".toList()
    private val textAAB = "aab".toList()
    private val textB = "b".toList()
    private val parser = factory.item('a').zeroOrMore()

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.of(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { assertEquals(listOf<Char>(), (result as Either.Right).b.value) },
                { assertEquals(input.position, (result as Either.Right).b.remainingInput.position) }
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
                { assert(result is Either.Right) },
                { assertEquals(listOf<Char>(), (result as Either.Right).b.value) },
                { assertEquals(input.position, (result as Either.Right).b.remainingInput.position) }
        )
    }

}