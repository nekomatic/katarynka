package com.nekomatic.katarynka.core

import arrow.core.*
import com.nekomatic.katarynka.parsers.Parser


class LineInput<TItem : Any> private constructor(
        private val baseInput: Input<TItem>,
        override val line: Long = 1,
        override val column: Long = 1,
        private val lastKnownEolPosition: Long = -1,
        val eolParser: Parser<TItem, Input<TItem>, Long>
) : ILineInput<TItem, LineInput<TItem>> {

    companion object {
        fun <TItem : Any> create(iterator: Iterator<TItem>, eolParser: Parser<TItem, Input<TItem>, Long>): LineInput<TItem> =
                LineInput(
                        baseInput = Input.create(iterator),
                        eolParser = eolParser
                )
    }

    override val item = baseInput.item
    override val position = baseInput.position

    override val next: LineInput<TItem> by lazy {
        when (item) {
            is None -> this
            is Some -> {
                val newLastKnownEolPosition: Long = if (lastKnownEolPosition < position) {
                    val r = eolParser
                            .parse(baseInput)
                            .map { s -> s.value + position }
                            .mapLeft { lastKnownEolPosition }
                    when (r) {
                        is Either.Left -> r.a
                        is Either.Right -> r.b
                    }
                } else
                    lastKnownEolPosition

                LineInput(
                        baseInput = baseInput.next,
                        line = if (newLastKnownEolPosition == position) line + 1 else line,
                        column = if (newLastKnownEolPosition == position) 1 else column + 1,
                        lastKnownEolPosition = newLastKnownEolPosition,
                        eolParser = eolParser
                )
            }
        }
    }
}