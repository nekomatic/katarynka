package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.core.fix
import arrow.instances.either.monadError.monadError
import arrow.instances.list.foldable.foldM
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parserResult
import com.nekomatic.katarynka.core.parsers.Parser
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success

//TODO: Create tests
fun <TItem : Any, TIn, A : Any> List<Parser<TItem, TIn, A>>.sequence(): Parser<TItem, TIn, List<A>> where TIn : IInput<TItem, TIn> {
    fun f(input: TIn, name: () -> String): parserResult<TItem, TIn, List<A>> =
            this.foldM(Either.monadError(), Success<TItem, TIn, List<A>>(listOf(), input, input) { listOf() })
            { s, p ->
                p.parse(s.remainingInput)
                        .map { Success(s.value + it.value, s.startingInput, it.remainingInput) { s.payload() + it.payload() } }
            }.fix().mapLeft { Failure(name, input, input) }

    return Parser(
            name = { this.joinToString("") { it.name() } },
            parserFunction = { input, name -> f(input, name) })
}