package com.nekomatic.katarynka.core.parsers

import arrow.core.Either
import com.nekomatic.katarynka.core.*
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ItemParserTest {
    private val text0 = "".toList()
    private val textA = "a".toList()
    private val textB = "b".toList()
    private val parser = ItemParser<Char, Input<Char>>('a')

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = Input.create(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "ItemParser result of an empty input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected(), { "a" }())
                    { "Expected of a failed ItemParser should be the value of parser's name" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position, result.a.startingInput.position)
                    { "Remaining input of a failed ItemParser should be at the same position as the starting input" }
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
                    { "ItemParser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals('a', (result as Either.Right<Success<Char, Input<Char>, Char>>).b.value)
                    { "Value of a successful ItemParser should be equal to the current element" }
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
        val input = Input.create(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "ItemParser result of a non-matching input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected(), { "a" }())
                    { "Expected of a failed ItemParser should be the value of parser's name" }
                },
                {
                    assertEquals(input.position, (result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position)
                    { "Remaining input of a failed ItemParser should be at the same position as the starting input" }
                }
        )

    }

}