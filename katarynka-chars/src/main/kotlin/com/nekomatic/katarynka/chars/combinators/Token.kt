package com.nekomatic.katarynka.chars.combinators

import arrow.core.Either
import arrow.core.fix
import arrow.instances.either.monad.monad
import arrow.typeclasses.binding
import com.nekomatic.katarynka.chars.*
import com.nekomatic.katarynka.core.combinators.optional
import com.nekomatic.katarynka.core.result.Success

//TODO: Create tests
fun <A : Any> CParser<A>.token(): CParser<A> {
    val thisParser = this
    fun f(input: CInput, name: () -> String): Either<CFailure, CSuccess<A>> = Either
            .monad<CFailure>()
            .binding {
                val ws1 = Parsers.WHITESPACES.parse(input).bind()
                val r1 = thisParser.parse(ws1.remainingInput).bind()
                val ws2 = Parsers.WHITESPACES.optional().parse(r1.remainingInput).bind()
                Success(
                        value = r1.value,
                        startingInput = input,
                        remainingInput = ws2.remainingInput,
                        payload = { ws1.payload() + r1.payload() + ws2.payload() }
                )
            }
            .fix()
            .mapLeft { CFailure(name, input, input) }
    return CParser(
            name = this.name,
            parserFunction = { input, name -> f(input, name) })
}