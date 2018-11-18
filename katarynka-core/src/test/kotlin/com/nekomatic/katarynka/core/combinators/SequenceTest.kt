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
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.parserResult
import com.nekomatic.katarynka.core.parsers.ItemParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class SequenceTest {


    private val textABCD = "abcde".toList()
    private val textAB_D = "abc_de".toList()

    private val parser = "abcd".map { ItemParser<Char, LineInput<Char>>(it) }.sequence().map { it.joinToString("") }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Matching sequence")
    @Test
    fun matchingSequence() {
        val input = LineInput.create(textABCD.iterator())
        val result: parserResult<Char, LineInput<Char>, out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right<*>, "result should be Either.Right") },
                {
                    assertEquals(
                            "abcd".toList(),
                            (result as Either.Right).b.value.toList(),
                            "the result value should be equal to the requested items sequence"
                    )

                }
        )
    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Non-matching seqience")
    @Test
    fun nonMatchingSequence() {
        val input = LineInput.create(textAB_D.iterator())
        val result: parserResult<Char, LineInput<Char>, out String> = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left<*>, "result should be Either.Left") },
                { assertEquals("d", (result as Either.Left).a.expected, "the expected value should be equal to the requested items in the sequence") },
                { assertEquals(3, (result as Either.Left).a.failedAtInput.position) }
        )
    }
}