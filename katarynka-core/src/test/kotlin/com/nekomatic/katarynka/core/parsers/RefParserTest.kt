/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 nekomatic.
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

package com.nekomatic.katarynka.core.parsers

import arrow.core.Either
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.ParserRef
import com.nekomatic.katarynka.core.combinators.orElse
import com.nekomatic.katarynka.core.combinators.sMap
import com.nekomatic.katarynka.core.combinators.then
import com.nekomatic.katarynka.core.combinators.toNamedParser
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


sealed class Element(val name: String)
class Leaf(val i: Int) : Element("leaf") // 1
class Branch(val e: Element) : Element("branch") //[]

class EParser {

    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val elementRef: ParserRef<Char, LineInput<Char>, Element> = factory.ref()
    val pLeaf by lazy {
        factory.match("leaf") { it.isDigit() } sMap { (it - '0') } sMap { Leaf(it) as Element }
    }
    private val pBranch by lazy {
        factory.item('[') then
                elementRef then factory.item(']') sMap { it.a.b } sMap { Branch(it) as Element } toNamedParser "branch"
    }
    val elementParser by lazy { pBranch orElse pLeaf }

    init {

        elementRef.set(elementParser)
    }
}


internal class RefParserTest {

    private val text0 = "1".toList()
    private val text1 = "[1]".toList()
    private val text2 = "[[[[[[[[[1]]]]]]]]]".toList()
    private val text3 = "[[_]]".toList()
    private val parser = EParser().elementParser

    @Test
    fun basicTest() {
        val input = LineInput.of(text0.iterator())
        val result = parser.parse(input)
        assertTrue(result is Either.Right)
    }

    @Test
    fun basicFailTest() {
        val factory = ParserFactory<Char, LineInput<Char>>()
        val parser = ParserRef<Char, LineInput<Char>, Element>(factory)
        parser.set(EParser().pLeaf)
        val input = LineInput.of(text1.iterator())
        val result = parser.parse(input)
        assertTrue(result is Either.Left)
    }

    @Test
    fun singleTest() {
        val input = LineInput.of(text1.iterator())
        val result = parser.parse(input)
        assertTrue(result is Either.Right)
    }

    @Test
    fun complexTest() {
        val input = LineInput.of(text2.iterator())
        val result = parser.parse(input)
        assertTrue(result is Either.Right)
    }

    @Test
    fun complexFailTest() {
        val input = LineInput.of(text3.iterator())
        val result = parser.parse(input)
        assertTrue(result is Either.Left)
    }
}