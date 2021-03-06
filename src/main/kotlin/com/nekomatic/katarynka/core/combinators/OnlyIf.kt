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

@file:JvmName("OnlyIf")

package com.nekomatic.katarynka.core.combinators

import arrow.core.*
import arrow.core.NonEmptyList
import com.nekomatic.katarynka.core.IParser
import com.nekomatic.katarynka.core.input.IInput

//TODO: Move try result outside the parser result
//TODO: Try is obsolete - replace with another construct
/**
 *
 * @receiver IParser<TItem, TIn, A>
 * @param f (A) -> Boolean
 * @return IParser<TItem, TIn, A>
 */
infix fun <TItem, TIn, A> IParser<TItem, TIn, A>.onlyIf(f: (A) -> Boolean): IParser<TItem, TIn, A> where TIn : IInput<TItem, TIn> =
        this.factory.parser(this.name) { input, n, fact ->
            this.parse(input, fact)
                    .flatMap { success ->
                        val t = Try { f(success.value) }
                        when {
                            t is Success && t.value -> success.right()
                            t is Try.Failure -> NonEmptyList.of(com.nekomatic.katarynka.core.result.Failure(t.exception.toString(), input)).left()
                            else -> NonEmptyList.of(com.nekomatic.katarynka.core.result.Failure(n, input)).left()
                        }
                    }
        }
