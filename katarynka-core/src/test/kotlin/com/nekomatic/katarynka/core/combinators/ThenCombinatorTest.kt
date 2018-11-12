package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.core.Tuple2
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.result.Success
import com.nekomatic.katarynka.core.parsers.ItemParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ThenCombinatorTest {

    private val textA = "a".toList()
    private val textAB = "ab".toList()
    private val textAA = "aa".toList()
    private val textBB = "bb".toList()
    private val textCC = "cc".toList()

    private val parser = ItemParser<Char, Input<Char>>('a') then ItemParser('b')

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Matching input")
    @Test
    fun matchingInput() {
        val input = Input.create(textAB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assertTrue(
                            result is Either.Right<*>,
                            "Then result of a matching input should be Either.Right"
                    )
                },
                {
                    assertEquals(Tuple2('a', 'b'), (result as Either.Right<Success<Char, Input<Char>, Tuple2<Char, Char>>>).b.value)
                    { "Value of a successful Then should be equal to a tuple of member parsers" }
                },
                {
                    assertEquals(listOf('a', 'b'), (result as Either.Right<Success<Char, Input<Char>, Tuple2<Char, Char>>>).b.payload())
                    { "Payload of a successful Then should be equal to a list of member parsers' payload" }
                }
        )
    }

    @DisplayName("Insufficient input")
    @Test
    fun insufficientInput() {
        val input = Input.create(textA.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assertTrue(
                            result is Either.Left<Failure<Char, Input<Char>>>,
                            "Insufficient input should result in failure of parsers combined with 'then'"
                    )
                },
                {
                    assertEquals(
                            "b",
                            (result as Either.Left<Failure<Char, Input<Char>>>).a.expected,
                            "Expected of failed Then parser should be a sum of meber parser's expected"
                    )
                }
        )
    }

    @DisplayName("Non-matching input of the second parser")
    @Test
    fun nonMatchingSecondInput() {
        val input = Input.create(textAA.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assertTrue(
                            result is Either.Left<Failure<Char, Input<Char>>>,
                            "Input with failed second match should result in failure of parsers combined with 'then'"
                    )
                },
                {
                    assertEquals(
                            input.position, (result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position,
                            "Position of remaining input of failed Then parser should be the same as position of the parser's input"
                    )
                }
        )
    }

    @DisplayName("Non-matching input of the first parser")
    @Test
    fun nonMatchingFirstInput() {
        val input = Input.create(textBB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assertTrue(
                            result is Either.Left<Failure<Char, Input<Char>>>,
                            "Input with failed first match should result in failure of parsers combined with 'then'"
                    )
                },
                {
                    assertEquals(
                            input.position, (result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position,
                            "Position of remaining input of failed Then parser should be the same as position of the parser's input"
                    )
                }
        )
    }

    @DisplayName("Non-matching input of both parsers")
    @Test
    fun nonMatchingBothInputs() {
        val input = Input.create(textCC.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assertTrue(
                            result is Either.Left<Failure<Char, Input<Char>>>,
                            "Input with failed both matches should result in failure of parsers combined with 'then'"
                    )
                },
                {
                    assertEquals(
                            input.position, (result as Either.Left<Failure<Char, Input<Char>>>).a.remainingInput.position,
                            "Position of remaining input of failed Then parser should be the same as position of the parser's input"
                    )
                }
        )
    }
}