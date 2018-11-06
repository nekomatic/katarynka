package com.nekomatic.katarynka.chars.parsers

import com.nekomatic.katarynka.chars.CFailure
import com.nekomatic.katarynka.chars.CInput
import com.nekomatic.katarynka.core.combinators.map
import com.nekomatic.katarynka.core.combinators.sequence
import com.nekomatic.katarynka.core.parsers.Parser

//TODO: Create tests
class PString(value: String) : Parser<Char, CInput, String>(
        name = { value },
        parserFunction = { i, n ->
            value.toList()
                    .map { PChar(it) }
                    .sequence().map { it.joinToString("") }
                    .parse(i).mapLeft { CFailure(n, i, i) }
        }
)