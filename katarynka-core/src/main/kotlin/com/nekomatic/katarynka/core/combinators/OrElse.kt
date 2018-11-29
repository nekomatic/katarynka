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

@file:JvmName("OrElse")

package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.IParser
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parserResult


infix fun <TItem, TIn, A> IParser<TItem, TIn, A>.orElse(that: IParser<TItem, TIn, A>): IParser<TItem, TIn, A>
        where TIn : IInput<TItem, TIn> {
    val thisParser = this

    fun f(input: TIn, fact: ParserFactory<TItem, TIn>): parserResult<TItem, TIn, out A> {
        val r1 = thisParser.parse(input, fact)
        return when (r1) {
            is Either.Right -> r1
            is Either.Left -> that.parse(input, fact)
        }.mapLeft { it + (r1 as Either.Left).a }
    }
    return thisParser.factory.parser(
            name = "${thisParser.name} or ${that.name}",
            parserFunction = { input, _, fact -> f(input, fact) })
}





