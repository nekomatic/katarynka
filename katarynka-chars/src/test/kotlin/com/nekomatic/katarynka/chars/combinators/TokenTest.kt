package com.nekomatic.katarynka.chars.combinators

import arrow.core.Either
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.chars.*
import com.nekomatic.katarynka.core.of
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class TokenTest {

    private val textABCD1 = " abcde".toList()
    private val textABCD2 = " abcd e".toList()
    private val textABCD3 = "abcd e".toList()
    private val textABCD4 = "abcde".toList()
    private val textAB_D = " ab_d e".toList()

    private val parser = CFactory().of { it.string("abcd").token(it.WS) }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Matching sequence with WS on the left side")
    @Test
    fun matchingSequenceLeft() {
        val input = CInput.of(textABCD1.iterator())
        val result: CResult<out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right<*>, "result should be Either.Right") },
                {
                    assertEquals(
                            "abcd".toList(),
                            (result as Either.Right<CSuccess<String>>).b.value.toList(),
                            "the result value should be equal to the requested string"
                    )

                }
        )
    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Matching sequence with WS on the right side")
    @Test
    fun matchingSequenceRight() {
        val input = CInput.of(textABCD3.iterator())
        val result: CResult<out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right<*>, "result should be Either.Right") },
                {
                    assertEquals(
                            "abcd".toList(),
                            (result as Either.Right<CSuccess<String>>).b.value.toList(),
                            "the result value should be equal to the requested string"
                    )

                }
        )
    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Matching sequence with WS on both sides")
    @Test
    fun matchingSequenceBoth() {
        val input = CInput.of(textABCD2.iterator())
        val result: CResult<out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right<*>, "result should be Either.Right") },
                {
                    assertEquals(
                            "abcd".toList(),
                            (result as Either.Right<CSuccess<String>>).b.value.toList(),
                            "the result value should be equal to the requested string"
                    )

                }
        )
    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Matching sequence with no WS on neither side")
    @Test
    fun matchingSequence() {
        val input = CInput.of(textABCD4.iterator())
        val result: CResult<out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right<*>, "result should be Either.Right") },
                {
                    assertEquals(
                            "abcd".toList(),
                            (result as Either.Right<CSuccess<String>>).b.value.toList(),
                            "the result value should be equal to the requested string"
                    )

                }
        )
    }

    @DisplayName("Non-matching seqience")
    @Test
    fun nonMatchingSequence() {
        val input = CInput.of(textAB_D.iterator())
        val result: CResult<out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left<*>, "result should be Either.Left") },
                {
                    assertEquals(
                            "c",
                            (result as Either.Left<NonEmptyList<CFailure>>).a.head.expected,
                            "the expected value should be equal to the first mismatching element of the requested string"
                    )
                }
        )
    }

}