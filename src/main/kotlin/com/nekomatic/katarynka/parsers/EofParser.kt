package com.nekomatic.katarynka.parsers

import com.nekomatic.katarynka.core.EOF
import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.core.eofParserFunction


open class EofParser<TItem : Any, TIn>
    : Parser<TItem, TIn, EOF>({ "eof" }, { input, n -> eofParserFunction(input, n) })
        where TIn : IInput<TItem, TIn>