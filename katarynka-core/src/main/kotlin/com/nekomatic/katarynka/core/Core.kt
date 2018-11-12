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

@file:JvmName("Core")

package com.nekomatic.katarynka.core

import arrow.core.*
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success

//TODO: Failure needs to preserve the fail position

typealias ParserFunction<TItem, TIn, A> = (TIn, String) -> parserResult<TItem, TIn, out A>

//TODO: create documentation
/**
 *
 * @param input TIn
 * @param name () -> String
 * @param match (TItem) -> Boolean
 * @return parserResult<TItem, TIn, out TItem>
 */
fun <TItem : Any, TIn> standardParserFunction(input: TIn, name: String, match: (TItem) -> Boolean): parserResult<TItem, TIn, out TItem>
        where TIn : IInput<TItem, TIn> {
    val currentItem = input.item
    return when (currentItem) {
        None -> Failure(
                expected = name,
                failedAtInput = input,
                remainingInput = input
        ).left()
        is Some -> {
            val current = currentItem.t
            if (match(current)) {
                val remaining = input.next
                Success(
                        value = current,
                        startingInput = input,
                        remainingInput = remaining,
                        payload = { listOf(current) }
                ).right()
            } else
                Failure(
                        expected = name,
                        failedAtInput = input,
                        remainingInput = input
                ).left()
        }
    }
}

//TODO: create documentation
/**
 *
 * @param input TIn
 * @param name () -> String
 * @return parserResult<TItem, TIn, out EOF>
 */
fun <TItem : Any, TIn> eofParserFunction(input: TIn, name: String): parserResult<TItem, TIn, out EOF>
        where TIn : IInput<TItem, TIn> {
    return when (input.item) {
        None -> Success(
                value = EOF,
                startingInput = input,
                remainingInput = input,
                payload = { listOf() }
        ).right()
        is Some -> Failure(
                expected = name,
                failedAtInput = input,
                remainingInput = input
        ).left()
    }
}

object EOF

typealias parserResult<TItem, TIn, TVal> = Either<Failure<TItem, TIn>, Success<TItem, TIn, TVal>>




