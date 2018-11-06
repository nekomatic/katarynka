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

package com.nekomatic.katarynka.core.input

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.some

class Input<TItem : Any> private constructor(
        override val item: Option<TItem>,
        override val position: Long,
        private val iterator: Iterator<TItem>
) : IInput<TItem, Input<TItem>> {

    companion object {
        fun <TItem : Any> create(iterator: Iterator<TItem>): Input<TItem> =
                Input(
                        item = if (iterator.hasNext()) iterator.next().some() else None,
                        position = 0,
                        iterator = iterator
                )
    }

    override val next: Input<TItem> by lazy {
        when (item) {
            is None -> this
            is Some -> Input(
                    item = if (iterator.hasNext()) iterator.next().some() else None,
                    position = position + 1,
                    iterator = iterator
            )
        }
    }
}