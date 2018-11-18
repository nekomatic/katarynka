package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.parsers.ItemParser
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class NotCombinatorTest {

    private val textA = "a"
    private val textB = "b"
    private val parser = ItemParser<Char, LineInput<Char>>('a').not()

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Mathing input")
    @Test
    fun matchingInput() {
        val input = LineInput.create(textB.iterator())
        val result = parser.parse(input)
        org.junit.jupiter.api.assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Not parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals('b', (result as Either.Right<Success<Char, LineInput<Char>, Char>>).b.value)
                    { "Value of a successful Not parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 1, (result as Either.Right<Success<Char, LineInput<Char>, Char>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful Not parser should be at larger by 1 than the starting input's position" }
                }
        )
    }

    @DisplayName("Non-matching input")
    @Test
    fun nonMatchingInput() {
        val input = LineInput.create(textA.iterator())
        val result = parser.parse(input)
        org.junit.jupiter.api.assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "Not parse result of a non-matching input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, LineInput<Char>>>).a.expected, "a")
                    { "Expected of a failed Not parse should be the value of parser's toNamedParser" }
                },
                {
                    assertEquals(input.position, (result as Either.Left<Failure<Char, LineInput<Char>>>).a.remainingInput.position)
                    { "Remaining input of a failed Not parse should be at the same position as the starting input" }
                }
        )
    }
}