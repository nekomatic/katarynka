package com.nekomatic.katarynka.core.combinators

import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parsers.ForceSuccessParser
import com.nekomatic.katarynka.core.parsers.Parser
//TODO: create tests
@ExperimentalUnsignedTypes
infix fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.times(count: UInt): Parser<TItem, TIn, List<A>> where TIn : IInput<TItem, TIn> =
        if (count == 0u)
            ForceSuccessParser<TItem, TIn>() map { listOf<A>() }
        else
            List(count.toInt()) { this }.sequence()