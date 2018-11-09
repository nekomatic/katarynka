@file:JvmName("Parsers")

package com.nekomatic.katarynka.chars

import arrow.core.Either
import com.nekomatic.katarynka.chars.parsers.PChar
import com.nekomatic.katarynka.core.combinators.*
import com.nekomatic.katarynka.core.parsers.MatchParser
import com.nekomatic.katarynka.core.parsers.Parser
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success


typealias CParser<A> = Parser<Char, CInput, A>
typealias CSuccess<A> = Success<Char, CInput, A>
typealias CFailure = Failure<Char, CInput>
typealias CResult<A> = Either<CFailure, CSuccess<A>>
typealias CMatchParser = MatchParser<Char, CInput>


//TODO: Create tests
val WHITESPACE = CMatchParser({ "whitespace" }) { it.isWhitespace() }
val WHITESPACES = WHITESPACE.oneOrMore() rename { "whitespaces" }

val DIGIT = CMatchParser({ "digit" }) { it.isDigit() }
val LETTER_OR_DIGIT = CMatchParser({ "letter or digit" }) { it.isLetterOrDigit() }

val LOWERCASE_CHAR = CMatchParser({ "lowercase letter" }) { it.isLowerCase() }
val UPPERCASE_CHAR = CMatchParser({ "uppercase letter" }) { it.isUpperCase() }
val LETTER = CMatchParser({ "letter" }) { it.isLetter() }
val EOL = ((PChar('\r') then PChar('\n')).toConst('\n')
        orElse PChar('\n')
        orElse PChar('\r')) rename { "end of line" }

