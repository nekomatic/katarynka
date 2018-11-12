/*******************************************************************************
 * The MIT License
 *
 * Copyright (c)  2018.  nekomatic.
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
 *
 ******************************************************************************/

@file:JvmName("SurroundedBy")

package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.core.fix
import arrow.instances.either.monad.monad
import arrow.typeclasses.binding
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parsers.Parser
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success

//TODO: create tests
//TODO: create documentation
/**
 *
 * @receiver Parser<TItem, TIn, A>
 * @param thatParser Parser<TItem, TIn, B>
 * @return Parser<TItem, TIn, A>
 */
infix fun <TItem : Any, TIn, A : Any, B : Any> Parser<TItem, TIn, A>.surroundedBy(thatParser: Parser<TItem, TIn, B>): Parser<TItem, TIn, A>
        where TIn : IInput<TItem, TIn> {
    val thisParser = this
    fun f(input: TIn, name: String): Either<Failure<TItem, TIn>, Success<TItem, TIn, A>> {
        return Either
                .monad<Failure<TItem, TIn>>()
                .binding {
                    val thatResultLeft = thatParser.parse(input).bind()
                    val thisResult = thisParser.parse(thatResultLeft.remainingInput).bind()
                    val thatResultRight = thatParser.parse(thisResult.remainingInput).bind()
                    Success(
                            value = thisResult.value,
                            startingInput = input,
                            remainingInput = thatResultRight.remainingInput,
                            payload = { thatResultLeft.payload() + thisResult.payload() + thatResultRight.payload() }
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
            name = this.name,
            parserFunction = { input, name -> f(input, name) })
}

