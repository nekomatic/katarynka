@file:JvmName("PString")
package com.nekomatic.katarynka.chars.parsers

import com.nekomatic.katarynka.chars.CFailure
import com.nekomatic.katarynka.chars.CInput
import com.nekomatic.katarynka.core.combinators.map
import com.nekomatic.katarynka.core.combinators.sequence
import com.nekomatic.katarynka.core.input.ILineInput
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.parsers.Parser

//TODO: create documentation
/**
 *
 * @constructor
 */
class PString(value: String) : Parser<Char, LineInput<Char>, String>
(
        name = { value },
        parserFunction = { i, n ->
            value.toList()
                    .map { PChar(it) }
                    .sequence().map { it.joinToString("") }
                    .parse(i).mapLeft { CFailure(n, i, i) }
        }
)