package com.nekomatic.katarynka.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.Failure
import com.nekomatic.katarynka.core.Input
import com.nekomatic.katarynka.core.Success
import com.nekomatic.katarynka.core.parse
import com.nekomatic.katarynka.parsers.ItemParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

internal class NotCombinatorTest {

    private val textA = "a".toList()
    private val textB = "b".toList()
    private val parser = ItemParser<Char, Input<Char>>('a').not()

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Mathing input")
    @Test
    fun matchingInput() {
        val input = Input.create(textB.iterator())
        val result = parser.parse(input)
        org.junit.jupiter.api.assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Not parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals('b', (result as Either.Right<Success<Char, Input<Char>, Char>>).b.value)
                    { "Value of a successful Not parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 1, (result as Either.Right<Success<Char, Input<Char>, Char>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful Not parser should be at larger by 1 than the starting input's position" }
                }
        )
    }

    @DisplayName("Non-matching input")
    @Test
    fun nonMatchingInput() {
        val input = Input.create(textA.iterator())
        val result = parser.parse(input)
        org.junit.jupiter.api.assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "Not parse result of a non-matching input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected(), { "a" }())
                    { "Expected of a failed Not parse should be the value of parser's name" }
                },
                {
                    assertEquals(input.position, (result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position)
                    { "Remaining input of a failed Not parse should be at the same position as the starting input" }
                }
        )
    }
}