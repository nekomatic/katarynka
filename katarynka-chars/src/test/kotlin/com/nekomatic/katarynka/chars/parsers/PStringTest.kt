package com.nekomatic.katarynka.chars.parsers

import arrow.core.Either
import com.nekomatic.katarynka.chars.*
import com.nekomatic.katarynka.core.combinators.map
import com.nekomatic.katarynka.core.combinators.sequence
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.parserResult
import com.nekomatic.katarynka.core.parsers.ItemParser
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class PStringTest {

    private val textABCD = "abcde".toList()
    private val textAB_D = "ab_de".toList()

    private val parser = PString("abcd")

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Matching sequence")
    @Test
    fun matchingSequence() {
        val input = CInput.create(textABCD.iterator())
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
                            "c",
                            (result as Either.Left<CFailure>).a.expected,
                            "the expected value should be equal to the requested string"
                    )
                }
        )
    }

}