package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.result.Success
import com.nekomatic.katarynka.core.parsers.ItemParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll

internal class OneOrMoreCombinatorTest {
    private val text0 = ""
    private val textA = "a"
    private val textAAB = "aab"
    private val textB = "b"
    private val parser = ItemParser<Char, LineInput<Char>>('a').oneOrMore()


    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.create(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "OneOrMore parser result of a matching input should be Either.Left" }
                },
                {
                    assertEquals("a", (result as Either.Left<Failure<Char, LineInput<Char>>>).a.expected)
                    { "Expected of a failed OneOrMore parser should be the value of parser's rename" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, LineInput<Char>>>).a.remainingInput.position, result.a.failedAtInput.position)
                    { "Remaining input of a failed OneOrMore parser should be at the same position as the starting input" }
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
                    { "OneOrMore parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(listOf('a'), (result as Either.Right<Success<Char, LineInput<Char>, List<Char>>>).b.value)
                    { "Value of a successful OneOrMore parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 1, (result as Either.Right<Success<Char, LineInput<Char>, List<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful OneOrMore parser should be at larger by 1 than the starting input's position" }
                }
        )
    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Multi-match input")
    @Test
    fun multiMatchInput() {
        val input = LineInput.create(textAAB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "OneOrMore parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(listOf('a', 'a'), (result as Either.Right<Success<Char, LineInput<Char>, List<Char>>>).b.value)
                    { "Value of a successful OneOrMore parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 2, (result as Either.Right<Success<Char, LineInput<Char>, List<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful OneOrMore parser should be at larger by 1 than the starting input's position" }
                }
        )
    }


    @DisplayName("No-match input")
    @Test
    fun noMatchInput() {
        val input = LineInput.create(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "OneOrMore parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals("a", (result as Either.Left<Failure<Char, LineInput<Char>>>).a.expected)
                    { "Expected of a failed OneOrMore parser should be the value of parser's rename" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, LineInput<Char>>>).a.remainingInput.position, result.a.failedAtInput.position)
                    { "Remaining input of a failed OneOrMore parser should be at the same position as the starting input" }
                }
        )
    }

}