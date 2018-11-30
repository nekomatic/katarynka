package com.nekomatic.katarynka.chars.parsers

import arrow.core.Either
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.chars.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class PStringTest {

    private val textABCD = "abcde".toList()
    private val textAB_D = "ab_de".toList()

    private val parser =
            CBuilder {
                string("abcd")
            }.build()

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Matching sequence")
    @Test
    fun matchingSequence() {
        val input = CInput.of(textABCD.iterator())
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
                            "the expected value should be equal to the requested string"
                    )
                }
        )
    }

}