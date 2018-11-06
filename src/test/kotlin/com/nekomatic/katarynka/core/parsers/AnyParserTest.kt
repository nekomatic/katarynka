package com.nekomatic.katarynka.core.parsers

import arrow.core.Either
import com.nekomatic.katarynka.core.*
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.input.Input.Companion.create
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll


internal class AnyParserTest {

    private val text0 = "".toList()
    private val text1 = "a".toList()
    private val parser = AnyParser<Char, Input<Char>> { "any" }

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = create(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "AnyParser result of an empty input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected(), { "any" }())
                    { "Expected of a failed AnyParser should be the value of parser's name" }
                },
                {
                    assertEquals(input.position, (result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position)
                    { "Remaining input of a failed AnyParser should be at the same position as the starting input" }
                }
        )

    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Non-empty input")
    @Test
    fun nonEmptyInput() {
        val input = create(text1.iterator())
        val result: parserResult<Char, Input<Char>, out Char> = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "AnyParser result of a non-empty input should be Either.Right" }
                },
                {
                    assertEquals('a', (result as Either.Right<Success<Char, Input<Char>, Char>>).b.value)
                    { "Value of a successful AnyParser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 1, (result as Either.Right<Success<Char, Input<Char>, Char>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful AnyParser should be at larger by 1 than the starting input's position" }
                }
        )

    }
}