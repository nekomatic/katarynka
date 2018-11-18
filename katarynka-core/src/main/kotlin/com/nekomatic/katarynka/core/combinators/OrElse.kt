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
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parserResult
import com.nekomatic.katarynka.core.parsers.Parser
import com.nekomatic.katarynka.core.result.Failure


/**
 *
 * @receiver Parser<TItem, TIn, A>
 * @param thatParser Parser<TItem, TIn, A>
 * @return Parser<TItem, TIn, A>
 */
infix fun <TItem, TIn, A> Parser<TItem, TIn, A>.orElse(thatParser: Parser<TItem, TIn, A>): Parser<TItem, TIn, A>
        where TIn : IInput<TItem, TIn> {

    fun f(input: TIn, name: String): parserResult<TItem, TIn, out A> {
        val r1 = this.parse(input)
        return when (r1) {
            is Either.Right -> r1
            is Either.Left -> thatParser.parse(input)
        }.mapLeft {
            Failure(
                    expected = name,
                    failedAtInput = input,
                    remainingInput = input,
                    innerFailures = listOf<Failure<TItem, TIn>>() + (r1 as Either.Left).a + it
            )
        }
    }

    return Parser(
            name = "${this.name} or ${thatParser.name}",
            parserFunction = { input, name -> f(input, name) })
}


