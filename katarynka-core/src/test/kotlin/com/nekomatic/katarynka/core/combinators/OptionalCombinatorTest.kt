package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.parsers.ItemParser
import com.nekomatic.katarynka.core.result.Success
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class OptionalCombinatorTest {
    private val text0 = ""
    private val textA = "a"
    private val textB = "b"
    private val parser = ItemParser<Char, LineInput<Char>>('a').optional()

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.create(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Option parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(None, (result as Either.Right<Success<Char, LineInput<Char>, Option<Char>>>).b.value)
                    { "Value of a successful Option parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position, (result as Either.Right<Success<Char, LineInput<Char>, Option<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful Option parser should be equal the starting input's position" }
                }
        )

    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Single-match input")
    @Test
    fun singleMatchInput() {
        val input = LineInput.create(textA.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Option parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(Some('a'), (result as Either.Right<Success<Char, LineInput<Char>, Option<Char>>>).b.value)
                    { "Value of a successful Option parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 1, (result as Either.Right<Success<Char, LineInput<Char>, Option<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful ZeroOrMore parser should be at larger by 1 than the starting input's position" }
                }
        )
    }


    @Suppress("UNCHECKED_CAST")
    @DisplayName("No-match input")
    @Test
    fun noMatchInput() {
        val input = LineInput.create(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Option parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(None, (result as Either.Right<Success<Char, LineInput<Char>, Option<Char>>>).b.value)
                    { "Value of a successful Option parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position, (result as Either.Right<Success<Char, LineInput<Char>, Option<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful ZeroOrMore parser should be equal the starting input's position" }
                }
        )
    }

}