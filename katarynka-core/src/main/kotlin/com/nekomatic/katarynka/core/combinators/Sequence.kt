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


import arrow.core.Either
import arrow.core.fix
import arrow.instances.either.monadError.monadError
import arrow.instances.list.foldable.foldM
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parserResult
import com.nekomatic.katarynka.core.parsers.Parser
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success


fun <TItem : Any, TIn, A : Any> List<Parser<TItem, TIn, A>>.sequence(): Parser<TItem, TIn, List<A>> where TIn : IInput<TItem, TIn> {
    fun f(input: TIn, name: () -> String): parserResult<TItem, TIn, List<A>> =
            this.foldM(Either.monadError(), Success<TItem, TIn, List<A>>(listOf(), input, input) { listOf() })
            { s, p ->
                p.parse(s.remainingInput)
                        .map { Success(s.value + it.value, s.startingInput, it.remainingInput) { s.payload() + it.payload() } }
            }.fix().mapLeft { Failure(name, input, input) }

    return Parser(
            name = { this.joinToString("") { it.name() } },
            parserFunction = { input, name -> f(input, name) })
}

