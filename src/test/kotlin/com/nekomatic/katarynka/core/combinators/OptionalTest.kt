package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class OptionalTest {
    private val text0 = ""
    private val textA = "a"
    private val textB = "b"

    private val parser = TestBuilder {
        item('a').optional()
    }.build()


    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.of(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { assertEquals(None, (result as Either.Right).b.value) },
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
                { assertEquals(Some('a'), (result as Either.Right).b.value) },
                { assertEquals(input.position + 1, (result as Either.Right).b.remainingInput.position) }
        )
    }


    @DisplayName("No-match input")
    @Test
    fun noMatchInput() {
        val input = LineInput.of(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { assertEquals(None, (result as Either.Right).b.value) },
                { assertEquals(input.position, (result as Either.Right).b.remainingInput.position) }
        )
    }

}