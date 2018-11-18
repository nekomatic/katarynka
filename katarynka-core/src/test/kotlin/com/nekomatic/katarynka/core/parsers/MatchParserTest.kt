package com.nekomatic.katarynka.core.parsers

import arrow.core.Either
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.parserResult
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class MatchParserTest {
    private val text0 = "".toList()
    private val textA = "a".toList()
    private val textB = "b".toList()
    private val parser = MatchParser<Char, Input<Char>>("a") { it == 'a' }

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = Input.create(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "MatchParser result of an empty input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected, { "a" }())
                    { "Expected of a failed MatchParser should be the value of parser's toNamedParser" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position, result.a.failedAtInput.position)
                    { "Remaining input of a failed MatchParser should be at the same position as the starting input" }
                }
        )

    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Mathing input")
    @Test
    fun matchingInput() {
        val input = Input.create(textA.iterator())
        val result: parserResult<Char, Input<Char>, out Char> = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "MatchParser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals('a', (result as Either.Right<Success<Char, Input<Char>, Char>>).b.value)
                    { "Value of a successful MatchParser should be equal to the current element" }
                },
                {
                    assertEquals((result as Either.Right<Success<Char, Input<Char>, Char>>).b.startingInput.position + 1, result.b.remainingInput.position)
                    { "Position of the remaining input of a successful MatchParser should be at larger by 1 than the starting input's position" }
                }
        )
    }

    @DisplayName("Non-matching input")
    @Test
    fun nonMatchingInput() {
        val input = Input.create(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "MatchParser result of a non-matching input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected, "a")
                    { "Expected of a failed MatchParser should be the value of parser's toNamedParser" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position, result.a.failedAtInput.position)
                    { "Remaining input of a failed MatchParser should be at the same position as the starting input" }
                }
        )

    }

}