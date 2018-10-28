package com.nekomatic.katarynka.parsers

import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.core.standardParserFunction

open class ItemParser<TItem : Any, TIn>(name: () -> String, item: TItem)
    : Parser<TItem, TIn, TItem>(name, { input, n -> standardParserFunction(input, n, { it == item }) })
        where TIn : IInput<TItem, TIn> {
    constructor(item: TItem) : this({ item.toString() }, item)
}

