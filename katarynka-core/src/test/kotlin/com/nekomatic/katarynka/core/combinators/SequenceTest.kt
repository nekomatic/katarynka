/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018. nekomatic.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.parserResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class SequenceTest {

    fun <T> List<T>.toNelUnsafe() = NonEmptyList.fromListUnsafe(this)

    private val textABCD = "abcde"
    private val textAB_D = "abc_de"

    private val parser =
            TestBuilder {
                "abcd".map { item(it) }
                        .toNelUnsafe()
                        .sequence() sMap {
                    it.joinToString("")
                }
            }.build()


    @DisplayName("Matching sequence")
    @Test
    fun matchingSequence() {
        val input = LineInput.of(textABCD.iterator())
        val result: parserResult<Char, LineInput<Char>, out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals("abcd", (result as Either.Right).b.value) }
        )
    }

    @DisplayName("Non-matching sequence")
    @Test
    fun nonMatchingSequence() {
        val input = LineInput.of(textAB_D.iterator())
        val result: parserResult<Char, LineInput<Char>, out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'd' at position 3")
                    assertEquals(expected, actual)
                }
        )
    }
}