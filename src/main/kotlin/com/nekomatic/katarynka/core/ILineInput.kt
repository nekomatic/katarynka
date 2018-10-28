package com.nekomatic.katarynka.core

interface ILineInput<TItem : Any, out TIn> : IInput<TItem, TIn> where TIn : ILineInput<TItem, TIn> {
    val line: Long
    val column: Long
}