package com.nekomatic.katarynka.core

import com.nekomatic.katarynka.core.input.IInput

/**
 *
 * @param TItem
 * @param TIn
 * @param TVal
 * @property name String
 * @property factory ParserFactory<TItem, TIn>
 */
interface IParser<TItem, TIn, TVal> where TIn : IInput<TItem, TIn> {
    val name: String
    val factory: ParserFactory<TItem, TIn>
    fun parse(input: TIn): parserResult<TItem, TIn, out TVal>
    fun parse(input: TIn, factory: ParserFactory<TItem, TIn>): parserResult<TItem, TIn, out TVal>
}

