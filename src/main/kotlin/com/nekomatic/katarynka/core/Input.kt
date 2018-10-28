package com.nekomatic.katarynka.core

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