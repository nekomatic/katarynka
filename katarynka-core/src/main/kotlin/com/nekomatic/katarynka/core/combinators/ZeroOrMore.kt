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

@file:JvmName("ZeroOrMore")

package com.nekomatic.katarynka.core.combinators

import arrow.core.*
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.result.Success
import com.nekomatic.katarynka.core.parsers.Parser


fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.zeroOrMore(): Parser<TItem, TIn, List<A>> where TIn : IInput<TItem, TIn> =
        Parser(name, { input, _ ->
            fun nextSuccess(i: TIn): Success<TItem, TIn, out A>? = this.parse(i).fold({ null }, { it })
            generateSequence(nextSuccess(input)) { s -> nextSuccess(s.remainingInput) }
                    .fold(Success(listOf<A>(), input, input) { listOf() }) { acc, success ->
                        Success(
                                value = acc.value + success.value,
                                startingInput = input,
                                remainingInput = success.remainingInput,
                                payload = { acc.payload() + success.payload() }
                        )
                    }.right()
        })

