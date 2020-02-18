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

@file:JvmName("NamedParser")

package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.core.NonEmptyList
import com.nekomatic.katarynka.core.IParser
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success

/**
 *
 * @receiver IParser<TItem, TIn, A>
 * @param name String
 * @return IParser<TItem, TIn, A>
 */
infix fun <TItem, TIn, A> IParser<TItem, TIn, A>.toNamedParser(name: String): IParser<TItem, TIn, A> where TIn : IInput<TItem, TIn> {


    @Suppress("UNUSED_PARAMETER")
    fun f(input: TIn, _name: String, func: ParserFactory<TItem, TIn>): Either<NonEmptyList<Failure<TItem, TIn>>, Success<TItem, TIn, out A>> =
            this.parse(input, func).mapLeft { failure ->
                NonEmptyList.of(Failure(
                        expected = name,
                        failedAtInput = input
                )) + failure
            }
    return factory.parser(name, ::f)
}



