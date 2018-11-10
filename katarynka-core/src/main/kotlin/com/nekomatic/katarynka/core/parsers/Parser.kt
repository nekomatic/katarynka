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

package com.nekomatic.katarynka.core.parsers

import com.nekomatic.katarynka.core.*
import com.nekomatic.katarynka.core.input.IInput
//TODO: create documentation
/**
 *
 * @param TItem : Any
 * @param TIn
 * @param TVal : Any
 * @property name Function0<String>
 * @property parserFunction Function2<TIn, Function0<String>, Either<Failure<TItem, TIn>, Success<TItem, TIn, out TVal>>>
 * @constructor
 */
open class Parser<TItem : Any, TIn, TVal : Any>(val name: () -> String, val parserFunction: genericParser<TItem, TIn, TVal>)
        where TIn : IInput<TItem, TIn> {

    open suspend fun parseAsync(input: TIn): parserResult<TItem, TIn, out TVal> {
        return parserFunction.invoke(input, name)
    }

    open fun parse(input: TIn): parserResult<TItem, TIn, out TVal> {
        return parserFunction.invoke(input, name)
    }
}

