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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class OnlyIfSomeTest {
    private val text0 = ""
    private val textA = "a"
    private val textB = "b"
    private val parser = ItemParser<Char, LineInput<Char>>('a').optional().onlyIfSome()

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.create(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left<*>) },
                { assertEquals("a", (result as Either.Left).a.expected) },
                { assertEquals(input.position, (result as Either.Left).a.remainingInput.position) }
        )

    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Single-match input")
    @Test
    fun singleMatchInput() {
        val input = LineInput.create(textA.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right<*>) },
                { assertEquals('a', (result as Either.Right).b.value) },
                { assertEquals(input.position + 1, (result as Either.Right).b.remainingInput.position) }
        )
    }


    @Suppress("UNCHECKED_CAST")
    @DisplayName("No-match input")
    @Test
    fun noMatchInput() {
        val input = LineInput.create(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left<*>) },
                { assertEquals("a", (result as Either.Left).a.expected) },
                { assertEquals(input.position, (result as Either.Left).a.remainingInput.position) }
        )
    }
}