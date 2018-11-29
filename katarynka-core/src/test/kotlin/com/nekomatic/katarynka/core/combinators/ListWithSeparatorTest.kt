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
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ListWithSeparatorTest {

    private val singleItemMatch = "a,b,a,a,a"
    private val fullMatch = "a,a,a,a,a"
    private val matchThenItem = "a,a,a,aa"
    private val matchThenSeparator = "a,a,a,b"
    private val separatorFirst = ",a,a,a,"
    private val noMatchFirst = "b,a,a,a,"
    private val itemSameAsSeparator = "aaaaaaaaa"

    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val parser = factory.item('a') listWithSeparator factory.item(',')
    private val parserItemSameAsSeparator = factory.item('a') listWithSeparator factory.item('a')

    @DisplayName("Single item match")
    @Test
    fun singleItemMatch() {
        val input = LineInput.of(singleItemMatch.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals(listOf('a'), (result as Either.Right).b.value) }
        )
    }

    @DisplayName("All items match")
    @Test
    fun fullMatch() {
        val input = LineInput.of(fullMatch.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals(listOf('a', 'a', 'a', 'a', 'a'), (result as Either.Right).b.value) }
        )
    }

    @DisplayName("All items match, item same as separator")
    @Test
    fun itemSameAsSeparator() {
        val input = LineInput.of(itemSameAsSeparator.iterator())
        val result = parserItemSameAsSeparator.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals(listOf('a', 'a', 'a', 'a', 'a'), (result as Either.Right).b.value) }
        )
    }

    @DisplayName("Some item match followed by an item")
    @Test
    fun matchThenItem() {
        val input = LineInput.of(matchThenItem.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals(listOf('a', 'a', 'a', 'a'), (result as Either.Right).b.value) },
                { assertEquals(7, (result as Either.Right).b.remainingInput.position) }
        )
    }

    @DisplayName("Some item match followed by a separator")
    @Test
    fun matchThenSeparator() {
        val input = LineInput.of(matchThenSeparator.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals(listOf('a', 'a', 'a'), (result as Either.Right).b.value) },
                { assertEquals(5, (result as Either.Right).b.remainingInput.position) }
        )
    }

    @DisplayName("First item is a separator")
    @Test
    fun separatorFirst() {
        val input = LineInput.of(separatorFirst.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                { assertEquals("a", (result as Either.Left).a.head.expected) },
                { assertEquals(0, (result as Either.Left).a.head.failedAtInput.position) },
                { assertEquals(1, (result as Either.Left).a.size) }
        )
    }

    @DisplayName("First item is a non a match")
    @Test
    fun noMatchFirst() {
        val input = LineInput.of(noMatchFirst.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                { assertEquals("a", (result as Either.Left).a.head.expected) },
                { assertEquals(0, (result as Either.Left).a.head.failedAtInput.position) },
                { assertEquals(1, (result as Either.Left).a.size) }
        )
    }
}