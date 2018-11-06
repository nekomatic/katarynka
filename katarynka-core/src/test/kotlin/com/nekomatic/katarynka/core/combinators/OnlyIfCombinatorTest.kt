package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.result.Success
import com.nekomatic.katarynka.core.parsers.MatchParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

internal class OnlyIfCombinatorTest {
    private val textEmpty = "".toList()
    private val text0 = "0".toList()
    private val text1 = "1".toList()
    private val parser = MatchParser<Char, Input<Char>>({ "non-zero digit" }) { it.isDigit() } onlyIf { it != '0' }

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = Input.create(textEmpty.iterator())
        val result = parser.parse(input)
        org.junit.jupiter.api.assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "OnlyIf parser result of an empty input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected(), { "non-zero digit" }())
                    { "Expected of a failed OnlyIf parser should be the value of parser's rename" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position, result.a.startingInput.position)
                    { "Remaining input of a failed OnlyIf parser should be at the same position as the starting input" }
                }
        )

    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Mathing input")
    @Test
    fun matchingInput() {
        val input = Input.create(text1.iterator())
        val result = parser.parse(input)
        org.junit.jupiter.api.assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "OnlyIf parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals('1', (result as Either.Right<Success<Char, Input<Char>, Char>>).b.value)
                    { "Value of a successful OnlyIf parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 1, (result as Either.Right<Success<Char, Input<Char>, Char>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful ItemParser should be at larger by 1 than the starting input's position" }
                }
        )
    }

    @DisplayName("Non-matching input")
    @Test
    fun nonMatchingInput() {
        val input = Input.create(text0.iterator())
        val result = parser.parse(input)
        org.junit.jupiter.api.assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "OnlyIf parser result of a non-matching input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected(), { "non-zero digit" }())
                    { "Expected of a failed OnlyIf parser should be the value of parser's rename" }
                },
                {
                    assertEquals(input.position, (result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position)
                    { "Remaining input of a failed OnlyIf parser should be at the same position as the starting input" }
                }
        )

    }

}