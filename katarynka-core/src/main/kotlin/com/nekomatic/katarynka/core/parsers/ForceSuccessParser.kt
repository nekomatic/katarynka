package com.nekomatic.katarynka.core.parsers

import arrow.core.right
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.result.Success

//TODO: Create tests
open class ForceSuccessParser<TItem : Any, TIn>()
    : Parser<TItem, TIn, Unit>(
        name = { "success" },
        parserFunction = { input, _ -> Success<TItem, TIn, Unit>(Unit, input, input) { listOf<TItem>() }.right() }
) where TIn : IInput<TItem, TIn>