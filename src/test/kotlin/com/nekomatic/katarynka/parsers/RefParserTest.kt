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

package com.nekomatic.katarynka.parsers

import arrow.core.Either
import com.nekomatic.katarynka.combinators.map
import com.nekomatic.katarynka.combinators.orElse
import com.nekomatic.katarynka.combinators.then
import com.nekomatic.katarynka.core.Input
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


sealed class Element(val name: String)
class Leaf(val i: Int) : Element("leaf") // 1
class Branch(val e: Element) : Element("branch") //[]

class EParser() {

    private val elementRef = RefParser<Char, Input<Char>, Element>()
    private val pLeaf: Parser<Char, Input<Char>, Element> by lazy { MatchParser<Char, Input<Char>>({ "leaf" }) { it.isDigit() } map { (it - '0').toInt() } map { Leaf(it) as Element } }
    private val pBranch: Parser<Char, Input<Char>, Element> by lazy {
        ItemParser<Char, Input<Char>>('[') then
                elementRef then
                ItemParser<Char, Input<Char>>(']') map
                { it.a.b } map
                { Branch(it) as Element }
    }
    val elementParser by lazy { pBranch orElse pLeaf }

    init {
        runBlocking { elementRef.set(elementParser) }
    }
}


internal class RefParserTest {

    private val text0 = "1".toList()
    private val text1 = "[1]".toList()
    private val text2 = "[[[[[[[[[1]]]]]]]]]".toList()
    private val parser = EParser().elementParser

    @Test
    fun basicTest() {
        val input = Input.create(text0.iterator())
        val result = runBlocking { parser.parseAsync(input) }
//        val expected = if (result is Either.Left) result.a.expected() else ""
        assertTrue(result is Either.Right)
    }

    @Test
    fun singleTest() {
        val input = Input.create(text1.iterator())
        val result = runBlocking { parser.parseAsync(input) }
//        val expected = if (result is Either.Left) result.a.expected() else ""
        assertTrue(result is Either.Right)
    }

    @Test
    fun doubleTest() {
        val input = Input.create(text2.iterator())
        val result = runBlocking { parser.parseAsync(input) }
//        val expected = if (result is Either.Left) result.a.expected() else ""
        assertTrue(result is Either.Right)
    }
}