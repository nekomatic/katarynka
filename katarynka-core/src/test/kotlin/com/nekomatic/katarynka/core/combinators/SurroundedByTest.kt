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
import arrow.core.some
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class SurroundedByTest {

    fun <T> List<T>.toNelUnsafe() = NonEmptyList.fromListUnsafe(this)
    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val textA = "a"
    private val textABCDEAB = "abcdeab"
    private val textABCDXAB = "abcdxab"
    private val textAXCDEAB = "axcdeab"
    private val textABCDEAX = "abcdeax"
    private val textAXCDXAC = "axcdxax"

    private val parserAB = "ab".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }
    private val parserCDE = "cde".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }
    private val parser = parserCDE surroundedBy parserAB


    @DisplayName("Matching input")
    @Test
    fun matchingInput() {
        val input = LineInput.of(textABCDEAB.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals("cde", (result as Either.Right).b.value) },
                { assertEquals(listOf('a', 'b', 'c', 'd', 'e', 'a', 'b').some(), (result as Either.Right).b.payload) }
        )
    }

    @DisplayName("Insufficient input")
    @Test
    fun insufficientInput() {
        val input = LineInput.of(textA.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'b' at position 1")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Non-matching input of the item parser")
    @Test
    fun nonMatchingItemInput() {
        val input = LineInput.of(textABCDXAB.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'e' at position 4")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Non-matching input of the prefix parser")
    @Test
    fun nonMatchingPrefixInput() {
        val input = LineInput.of(textAXCDEAB.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'b' at position 1")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Non-matching input of the suffix parser")
    @Test
    fun nonMatchingSuffixInput() {
        val input = LineInput.of(textABCDEAX.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'b' at position 6")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Non-matching input of both parsers")
    @Test
    fun nonMatchingAllInputs() {
        val input = LineInput.of(textAXCDXAC.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'b' at position 1")
                    assertEquals(expected, actual)
                }
        )
    }
}