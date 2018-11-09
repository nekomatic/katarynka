@file:JvmName("Prefixed")

package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.core.fix
import arrow.instances.either.monad.monad
import arrow.typeclasses.binding
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parsers.Parser
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success

//TODO: create tests
infix fun <TItem : Any, TIn, A : Any, B : Any> Parser<TItem, TIn, A>.prefixedBy(thatParser: Parser<TItem, TIn, B>): Parser<TItem, TIn, A>
        where TIn : IInput<TItem, TIn> {
    val thisParser = this
    fun f(input: TIn, name: () -> String): Either<Failure<TItem, TIn>, Success<TItem, TIn, A>> {
        return Either
                .monad<Failure<TItem, TIn>>()
                .binding {
                    val thatResultLeft = thatParser.parse(input).bind()
                    val thisResult = thisParser.parse(thatResultLeft.remainingInput).bind()
                    Success(
                            value = thisResult.value,
                            startingInput = input,
                            remainingInput = thisResult.remainingInput,
                            payload = { thatResultLeft.payload() + thisResult.payload() }
                    )
                }
                .fix()
                .mapLeft { Failure(name, input, input) }
    }

    return Parser(
            name = { this.name() },
            parserFunction = { input, name -> f(input, name) })
}