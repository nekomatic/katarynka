@file:JvmName("Name")

package com.nekomatic.katarynka.combinators

import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.parsers.Parser

infix fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.name(name: String): Parser<TItem, TIn, A> where TIn : IInput<TItem, TIn> =
        Parser({ name }, this.parserFunction)

infix fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.name(name: () -> String): Parser<TItem, TIn, A> where TIn : IInput<TItem, TIn> =
        Parser(name, this.parserFunction)