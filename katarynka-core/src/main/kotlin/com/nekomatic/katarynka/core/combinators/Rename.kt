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

@file:JvmName("Rename")

package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parsers.Parser
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success

//TODO: create documentation
/**
 *
 * @receiver Parser<TItem, TIn, A>
 * @param name String
 * @return Parser<TItem, TIn, A>
 */
infix fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.rename(name: String): Parser<TItem, TIn, A> where TIn : IInput<TItem, TIn> {
    fun f(input: TIn, name: String): Either<Failure<TItem, TIn>, Success<TItem, TIn, out A>> =
            this.parse(input).mapLeft {
                Failure(
                        expected = name,
                        failedAtInput = it.failedAtInput,
                        remainingInput = it.remainingInput
                )
            }
    return Parser(name) { i, _ -> f(i, name) }
}
