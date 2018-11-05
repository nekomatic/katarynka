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

import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.core.parserResult
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class RefParser<TItem : Any, TIn, A : Any>() : ForceFailParser<TItem, TIn, A>({ "Reference parser not initialised" }) where TIn : IInput<TItem, TIn> {

    private var innerParser: Parser<TItem, TIn, A> = this

    fun set(p: Parser<TItem, TIn, A>): Parser<TItem, TIn, A> {
        innerParser = p
        return runBlocking { parser.await() }
    }

    private val parser by lazy {
        GlobalScope.async(start = CoroutineStart.LAZY) { innerParser }
    }

    override suspend fun parseAsync(input: TIn): parserResult<TItem, TIn, out A> {
        val p = this.parser.await()
        return p.parserFunction(input, p.name)
    }

    override fun parse(input: TIn): parserResult<TItem, TIn, out A> {
        val t = this
        val p = runBlocking { t.parser.await() }
        return p.parserFunction(input, p.name)
    }
}