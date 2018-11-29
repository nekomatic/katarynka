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

package com.nekomatic.katarynka.core


import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.some
import com.nekomatic.katarynka.core.input.IInput

//TODO: add negative tests
class ParserRef<TItem, TIn, A>(override val factory: ParserFactory<TItem, TIn>) : IParser<TItem, TIn, A>
        where TIn : IInput<TItem, TIn> {

    private val parser by lazy {
        synchronized(innerParser) {
            when (innerParser) {
                is Some -> (innerParser as Some).t
                is None -> factory.failing("Reference parser not initialised")
            }
        }
    }

    fun set(p: IParser<TItem, TIn, A>): IParser<TItem, TIn, A> {
        synchronized(innerParser) {
            innerParser = p.some()
            return parser
        }
    }

    override val name: String by lazy {
        synchronized(innerParser)
        { if (innerParser is None) "Reference parser not initialised" else parser.name }
    }

    override fun parse(input: TIn): parserResult<TItem, TIn, out A> = parse(input, factory)

    override fun parse(input: TIn, factory: ParserFactory<TItem, TIn>): parserResult<TItem, TIn, out A> = parser.parse(input, factory)

    private var innerParser: Option<IParser<TItem, TIn, A>> = None
}