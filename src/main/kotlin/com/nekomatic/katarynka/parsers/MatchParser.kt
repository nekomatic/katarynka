package com.nekomatic.katarynka.parsers

import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.core.standardParserFunction

open class MatchParser<TItem : Any, TIn>(name: () -> String, match: (TItem) -> Boolean)
    : Parser<TItem, TIn, TItem>(name, { input, n -> standardParserFunction(input, n, match) })
        where TIn : IInput<TItem, TIn>