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

@file:JvmName("Sequence")

package com.nekomatic.katarynka.core.combinators


import arrow.core.*
import arrow.data.NonEmptyList
import arrow.instances.either.monadError.monadError
import arrow.instances.nonemptylist.foldable.foldM
import arrow.instances.option.monad.monad
import arrow.typeclasses.binding
import com.nekomatic.katarynka.core.IParser
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parserResult
import com.nekomatic.katarynka.core.result.Success

/**
 *
 * @receiver NonEmptyList<IParser<TItem, TIn, A>>
 * @return IParser<TItem, TIn, List<A>>
 */
fun <TItem, TIn, A> NonEmptyList<IParser<TItem, TIn, A>>.sequence(): IParser<TItem, TIn, List<A>> where TIn : IInput<TItem, TIn> {
    fun f(input: TIn, fact: ParserFactory<TItem, TIn>): parserResult<TItem, TIn, List<A>> =
            this.foldM(Either.monadError(), Success<TItem, TIn, List<A>>(listOf(), input, input, if (this.head.factory.keepPayload) Some(listOf()) else None))
            { acc, p ->
                p.parse(acc.remainingInput, fact)
                        .map { s ->
                            Success(
                                    value = acc.value + s.value,
                                    startingInput = acc.startingInput,
                                    remainingInput = s.remainingInput,
                                    payload = Option.monad().binding { acc.payload.bind() + s.payload.bind() }.fix())
                        }
            }.fix()
    return this.head.factory.parser(
            name = this.all.joinToString("") { p -> p.name },
            parserFunction = { input, _, fact -> f(input, fact) })
}

