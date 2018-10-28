package com.nekomatic.katarynka.combinators

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.nekomatic.katarynka.core.Input
import com.nekomatic.katarynka.core.Success
import com.nekomatic.katarynka.core.parse
import com.nekomatic.katarynka.parsers.ItemParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll

internal class OptionalCombinatorTest {
    private val text0 = "".toList()
    private val textA = "a".toList()
    private val textB = "b".toList()
    private val parser = ItemParser<Char, Input<Char>>('a').optional()

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = Input.create(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Option parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(None, (result as Either.Right<Success<Char, Input<Char>, Option<Char>>>).b.value)
                    { "Value of a successful Option parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position, (result as Either.Right<Success<Char, Input<Char>, Option<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful Option parser should be equal the starting input's position" }
                }
        )

    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Single-match input")
    @Test
    fun singleMatchInput() {
        val input = Input.create(textA.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Option parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(Some('a'), (result as Either.Right<Success<Char, Input<Char>, Option<Char>>>).b.value)
                    { "Value of a successful Option parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 1, (result as Either.Right<Success<Char, Input<Char>, Option<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful ZeroOrMore parser should be at larger by 1 than the starting input's position" }
                }
        )
    }


    @Suppress("UNCHECKED_CAST")
    @DisplayName("No-match input")
    @Test
    fun noMatchInput() {
        val input = Input.create(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Option parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(None, (result as Either.Right<Success<Char, Input<Char>, Option<Char>>>).b.value)
                    { "Value of a successful Option parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position, (result as Either.Right<Success<Char, Input<Char>, Option<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful ZeroOrMore parser should be equal the starting input's position" }
                }
        )
    }

}