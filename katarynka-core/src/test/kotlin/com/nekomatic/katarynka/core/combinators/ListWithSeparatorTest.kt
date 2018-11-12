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
import com.nekomatic.katarynka.core.parsers.ItemParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ListWithSeparatorTest {

    private val singleItemMatch = "a,b,a,a,a"
    private val fulllMatch = "a,a,a,a,a"
    private val matchThenItem = "a,a,a,aa"
    private val matchThenSeparator = "a,a,a,b"
    private val noMatchThenSeparator = ",a,a,a,"

    private val parser = ItemParser<Char, LineInput<Char>>('a') listWithSeparator ItemParser<Char, LineInput<Char>>(',')

    @Test
    fun singleItemMatch() {
        val input = LineInput.create(singleItemMatch.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right<*>) },
                { assertEquals(listOf('a'), (result as Either.Right).b.value) }
        )
    }

    @Test
    fun fulllMatch() {
        val input = LineInput.create(fulllMatch.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right<*>) },
                { assertEquals(listOf('a','a','a','a','a'), (result as Either.Right).b.value) }
        )
    }

    @Test
    fun matchThenItem() {
        val input = LineInput.create(matchThenItem.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right<*>) },
                { assertEquals(listOf('a','a','a','a'), (result as Either.Right).b.value) },
                { assertEquals(7, (result as Either.Right).b.remainingInput.position) }
        )
    }
    @Test
    fun matchThenSeparator() {
        val input = LineInput.create(matchThenSeparator.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right<*>) },
                { assertEquals(listOf('a','a','a'), (result as Either.Right).b.value) },
                { assertEquals(5, (result as Either.Right).b.remainingInput.position) }

        )
    }

    @Test
    fun noMatchThenSeparator() {
        val input = LineInput.create(noMatchThenSeparator.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left<*>) },
                { assertEquals("a", (result as Either.Left).a.expected) },
                { assertEquals(0, (result as Either.Left).a.remainingInput.position) }

        )
    }
}