package com.nekomatic.katarynka.core

import arrow.core.Option

interface IInput<TItem : Any, out TIn> where TIn : IInput<TItem, TIn> {
    val item: Option<TItem>
    val position: Long
    val next: TIn
}


