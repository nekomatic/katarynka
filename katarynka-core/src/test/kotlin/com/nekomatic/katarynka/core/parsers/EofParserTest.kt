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

internal class EofParserTest {

    private val text0 = "".toList()
    private val text1 = "a".toList()
    private val parser = EofParser<Char, Input<Char>>()

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = Input.create(text0.iterator())
        val result = parser.parse(input)

        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "EofParser result of an empty input should be Either.Right" }
                },
                {
                    assertEquals(EOF, (result as Either.Right<Success<Char, Input<Char>, EOF>>).b.value)
                    { "Value of a successful EofParser should be EOF" }
                },
                {
                    assertEquals((result as Either.Right<Success<Char, Input<Char>, EOF>>).b.startingInput.position, result.b.remainingInput.position)
                    { "Position of the remaining input of a successful EofParser should be the same as the position of the starting input" }
                }
        )
    }


    @DisplayName("Non-empty input")
    @Test
    fun nonEmptyInput() {
        val input = Input.create(text1.iterator())
        val result: parserResult<Char, Input<Char>, out EOF> = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "EofParser result of a non-empty input should be Either.Left" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected, "eof")
                    { "Expected of a failed EofParser should be {eof}" }
                },
                {
                    assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position, result.a.failedAtInput.position)
                    { "Remaining input of a failed EofParser should be at the same position as the starting input" }
                }
        )

    }
}