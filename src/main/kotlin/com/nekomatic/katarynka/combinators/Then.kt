@file:JvmName("Then")

package com.nekomatic.katarynka.combinators

import arrow.core.*
import arrow.typeclasses.binding
import com.nekomatic.katarynka.core.*
import com.nekomatic.katarynka.core.Failure
import com.nekomatic.katarynka.core.Success
import com.nekomatic.katarynka.parsers.Parser


infix fun <TItem : Any, TIn, A : Any, B : Any> Parser<TItem, TIn, A>.then(thatParser: Parser<TItem, TIn, B>): Parser<TItem, TIn, Tuple2<A, B>>
        where TIn : IInput<TItem, TIn> {
    val thisParser = this
    fun f(input: TIn, name: () -> String): Either<Failure<TItem, TIn>, Success<TItem, TIn, Tuple2<A, B>>> {
        return Either
                .monad<Failure<TItem, TIn>>()
                .binding {
                    val r1 = thisParser.parse(input).bind()
                    val r2 = thatParser.parse(r1.remainingInput).bind()
                    Success(
                            value = Tuple2(r1.value, r2.value),
                            startingInput = input,
                            remainingInput = r2.remainingInput,
                            payload = { r1.payload() + r2.payload() }
                    )
                }
                .fix()
                .mapLeft { Failure(name, input, input) }
    }

    return Parser(
            name = { this.name() + thatParser.name() },
            parserFunction = { input, name -> f(input, name) })
}
