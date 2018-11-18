@file:JvmName("CInput")

package com.nekomatic.katarynka.chars

import com.nekomatic.katarynka.core.combinators.orElse
import com.nekomatic.katarynka.core.combinators.toNamedParser
import com.nekomatic.katarynka.core.combinators.then
import com.nekomatic.katarynka.core.combinators.toConst
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.parsers.ItemParser
import com.nekomatic.katarynka.core.parsers.Parser
import com.nekomatic.katarynka.core.result.Success

typealias CInput = LineInput<Char>


val CInputEolParser = Parser(
        name = "eol",
        parserFunction = { i: Input<Char>, _ ->
            val eol = ((ItemParser<Char, Input<Char>>('\r') then ItemParser<Char, Input<Char>>('\n')).toConst('\n')
                    orElse ItemParser<Char, Input<Char>>('\n')
                    orElse ItemParser<Char, Input<Char>>('\r')) toNamedParser "end of line"
            eol.parse(i).map { Success(it.payload().size.toLong() - 1, it.startingInput, it.remainingInput, it.payload) }
        }
)
