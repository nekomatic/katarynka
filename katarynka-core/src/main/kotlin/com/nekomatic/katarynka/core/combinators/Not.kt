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

@file:JvmName("Not")

package com.nekomatic.katarynka.core.combinators

import arrow.core.left
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.core.IParser
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parserResult
import com.nekomatic.katarynka.core.result.Failure

/**
 *
 * @receiver IParser<TItem, TIn, A>
 * @return IParser<TItem, TIn, TItem>
 */
fun <TItem, TIn, A> IParser<TItem, TIn, A>.not(): IParser<TItem, TIn, TItem> where TIn : IInput<TItem, TIn> =
        this.factory.parser(
                name = name,
                parserFunction = fun(input: TIn, n: String, fact: ParserFactory<TItem, TIn>): parserResult<TItem, TIn, out TItem> =
                        when {
                            this.parse(input, fact).isRight() -> NonEmptyList.of(Failure(n, input)).left()
                            else -> this.factory.any(n).parse(input, fact)
                        }
        )
