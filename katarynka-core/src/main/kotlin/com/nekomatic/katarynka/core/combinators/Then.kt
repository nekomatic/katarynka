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

@file:JvmName("Then")

package com.nekomatic.katarynka.core.combinators

import arrow.core.*
import arrow.instances.either.monad.monad
import arrow.typeclasses.binding
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parsers.Parser

//TODO: create tests
//TODO: create documentation
/**
 *
 * @receiver Parser<TItem, TIn, A>
 * @param thatParser Parser<TItem, TIn, B>
 * @return Parser<TItem, TIn, Tuple2<A, B>>
 */
infix fun <TItem : Any, TIn, A : Any, B : Any> Parser<TItem, TIn, A>.then(thatParser: Parser<TItem, TIn, B>): Parser<TItem, TIn, Tuple2<A, B>>
        where TIn : IInput<TItem, TIn> {
    val thisParser = this
    fun f(input: TIn, name: String): Either<Failure<TItem, TIn>, Success<TItem, TIn, Tuple2<A, B>>> {
        return Either
                .monad<Failure<TItem, TIn>>()
                .binding {
                    val r1 = thisParser.parse(input).bind()
                    val r2 = thatParser.parse(r1.remainingInput).bind()
                    Success(
                            value = Tuple2(r1.value, r2.value),
                            startingInput = input,
                            remainingInput = r2.remainingInput,
                            payload = { r1.payload() + r2.payload() }
                    )
                }
                .fix()
                .mapLeft {
                    Failure(
                            expected = it.expected,
                            failedAtInput = it.failedAtInput,
                            remainingInput = input
                    )
                }
    }

    return Parser(
            name = this.name + thatParser.name,
            parserFunction = { input, name -> f(input, name) })
}


