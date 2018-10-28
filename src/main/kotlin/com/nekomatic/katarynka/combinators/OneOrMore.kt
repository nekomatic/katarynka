@file:JvmName("OneOrMore")

package com.nekomatic.katarynka.combinators

import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.parsers.Parser

fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.oneOrMore(): Parser<TItem, TIn, List<A>> where TIn : IInput<TItem, TIn> =
        this then this.zeroOrMore() map { s -> listOf(s.a) + s.b } name this.name