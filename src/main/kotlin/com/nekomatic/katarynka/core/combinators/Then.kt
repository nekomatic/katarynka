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
import arrow.core.extensions.fx
import arrow.core.extensions.option.monad.monad
import com.nekomatic.katarynka.core.IParser
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.result.Success

/**
 *
 * @receiver IParser<TItem, TIn, A>
 * @param that IParser<TItem, TIn, B>
 * @param joinPayload (List<TItem>, List<TItem>) -> List<TItem>
 * @param joinNames (String, String) -> String
 * @return IParser<TItem, TIn, Tuple2<A, B>>
 */
fun <TItem, TIn, A, B> IParser<TItem, TIn, A>.then(that: IParser<TItem, TIn, B>, joinPayload: (List<TItem>, List<TItem>) -> List<TItem>, joinNames: (String, String) -> String): IParser<TItem, TIn, Tuple2<A, B>> where TIn : IInput<TItem, TIn> =
        this.factory.parser(
                name = joinNames(this.name, that.name),
                parserFunction = { input, _, fact ->
                    this
                            .parse(input, fact)
                            .map { r1 -> Tuple2(r1, that.parse(r1.remainingInput, fact)) }
                            .map { r ->
                                r.b.map { (r.a to it).toTuple2() }
                            }.flatMap { it }
                            .map {
                                Success(
                                        value = Tuple2(it.a.value, it.b.value),
                                        startingInput = input,
                                        remainingInput = it.b.remainingInput,
                                        payload = Option.fx { joinPayload(it.a.payload.bind(), it.b.payload.bind()) }.fix()
                                )
                            }
                }
        )

/**
 *
 * @receiver IParser<TItem, TIn, A>
 * @param that IParser<TItem, TIn, B>
 * @return IParser<TItem, TIn, Tuple2<A, B>>
 */
infix fun <TItem, TIn, A, B> IParser<TItem, TIn, A>.then(that: IParser<TItem, TIn, B>): IParser<TItem, TIn, Tuple2<A, B>> where TIn : IInput<TItem, TIn> =
        this.then(that, { a, b -> a + b }, { a, b -> a + b })


