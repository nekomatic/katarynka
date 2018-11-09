package com.nekomatic.katarynka.chars.combinators

import arrow.core.Either
import com.nekomatic.katarynka.chars.CFailure
import com.nekomatic.katarynka.chars.CInput
import com.nekomatic.katarynka.chars.CResult
import com.nekomatic.katarynka.chars.CSuccess
import com.nekomatic.katarynka.chars.parsers.PString
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll

internal class TokenTest {

    private val textABCD1 = " abcde".toList()
    private val textABCD2 = " abcd e".toList()
    private val textABCD3 = "abcd e".toList()
    private val textABCD4 = "abcde".toList()
    private val textAB_D = " ab_d e".toList()

    private val parser = PString("abcd").token()

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Matching sequence with WS on the left side")
    @Test
    fun matchingSequenceLeft() {
        val input = CInput.create(textABCD1.iterator())
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
        val input = CInput.create(textABCD3.iterator())
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
        val input = CInput.create(textABCD2.iterator())
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
        val input = CInput.create(textABCD4.iterator())
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
        val input = CInput.create(textAB_D.iterator())
        val result: CResult<out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left<*>, "result should be Either.Left") },
                {
                    assertEquals(
                            "abcd",
                            (result as Either.Left<CFailure>).a.expected(),
                            "the expected value should be equal to the requested string"
                    )
                }
        )
    }

}