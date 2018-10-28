package com.nekomatic.katarynka.parsers

import com.nekomatic.katarynka.core.*

open class Parser<TItem : Any, TIn, TVal : Any>(val name: () -> String, val parserFunction: genericParser<TItem, TIn, TVal>)
        where TIn : IInput<TItem, TIn>




