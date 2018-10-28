@file:JvmName("ToConst")

package com.nekomatic.katarynka.combinators

import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.core.map
import com.nekomatic.katarynka.parsers.Parser

infix fun <TItem : Any, TIn, A : Any, B : Any> Parser<TItem, TIn, A>.toConst(c: B): Parser<TItem, TIn, B> where TIn : IInput<TItem, TIn> =
        Parser(name, { input, n -> this.parserFunction(input, n).map { s -> s.map { _ -> c } } })