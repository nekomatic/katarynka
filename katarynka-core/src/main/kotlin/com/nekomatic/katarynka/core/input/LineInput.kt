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

package com.nekomatic.katarynka.core.input

import arrow.core.*
import com.nekomatic.katarynka.core.IParser


/**
 *
 * @param TItem
 * @property baseInput Input<TItem>
 * @property line Long
 * @property column Long
 * @property lastKnownEolPosition Long
 * @property eolParser Option<IParser<TItem, Input<TItem>, Long>>
 * @property ignoreEolParser Boolean
 * @property item Option<TItem>
 * @property position Long
 * @property next LineInput<TItem>
 * @constructor
 */
class LineInput<TItem> private constructor(
        private val baseInput: Input<TItem>,
        override val line: Long = 1,
        override val column: Long = 1,
        private val lastKnownEolPosition: Long = -1,
        val eolParser: Option<IParser<TItem, Input<TItem>, Long>>,
        private val ignoreEolParser: Boolean = false
) : ILineInput<TItem, LineInput<TItem>> {

    companion object {
        /**
         * Creates instance of the parser input with line counter enabled
         * @param iterator Iterator<TItem>
         * @param eolParser Parser<TItem, Input<TItem>, Long> parser used to detect the end of line.
         * The returned value shoul dintcate the ddistance from the current item to the last item of the line break sequence
         * @return LineInput<TItem>
         */
        fun <TItem> of(iterator: Iterator<TItem>, eolParser: IParser<TItem, Input<TItem>, Long>): LineInput<TItem> =
                LineInput(
                        baseInput = Input.of(iterator),
                        eolParser = eolParser.some(),
                        lastKnownEolPosition = -1
                )

        /**
         * Creates instance of the parser input with line counter disabled
         * @param iterator Iterator<TItem>
         * @return LineInput<TItem>
         */
        fun <TItem> of(iterator: Iterator<TItem>): LineInput<TItem> =
                LineInput(
                        baseInput = Input.of(iterator),
                        eolParser = None,
                        ignoreEolParser = true,
                        lastKnownEolPosition = 1
                )
    }

    override val item = baseInput.item
    override val position = baseInput.position

    override val next: LineInput<TItem> by lazy {
        when (item) {
            is None -> this
            is Some -> {
                val newLastKnownEolPosition: Long = when {
                    lastKnownEolPosition < position && eolParser is Some -> {
                        val r = eolParser.t
                                .parse(baseInput, eolParser.t.factory)
                                .map { s -> s.value + position }
                                .mapLeft { lastKnownEolPosition }
                        when (r) {
                            is Either.Left -> r.a
                            is Either.Right -> r.b
                        }
                    }
                    else -> lastKnownEolPosition
                }

                LineInput(
                        baseInput = baseInput.next,
                        line = if (newLastKnownEolPosition == position) line + 1 else line,
                        column = if (newLastKnownEolPosition == position) 1 else column + 1,
                        lastKnownEolPosition = if (ignoreEolParser) lastKnownEolPosition + 1 else newLastKnownEolPosition,
                        eolParser = eolParser,
                        ignoreEolParser = ignoreEolParser

                )
            }
        }
    }
}