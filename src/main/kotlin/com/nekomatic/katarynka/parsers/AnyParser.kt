package com.nekomatic.katarynka.parsers

import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.core.standardParserFunction

open class AnyParser<TItem : Any, TIn>(name: () -> String)
    : Parser<TItem, TIn, TItem>(name, { input, n -> standardParserFunction(input, n, { true }) })
        where TIn : IInput<TItem, TIn>