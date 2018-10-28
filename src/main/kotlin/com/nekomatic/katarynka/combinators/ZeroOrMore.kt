@file:JvmName("ZeroOrMore")

package com.nekomatic.katarynka.combinators

import arrow.core.Either
import arrow.core.monadError
import arrow.core.right
import com.nekomatic.katarynka.core.Failure
import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.core.Success
import com.nekomatic.katarynka.core.parse
import com.nekomatic.katarynka.parsers.Parser


fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.zeroOrMore(): Parser<TItem, TIn, List<A>> where TIn : IInput<TItem, TIn> =
        Parser(name, { input, _ ->
            fun nextSuccess(i: TIn): Success<TItem, TIn, out A>? = this.parse(i).fold({ null }, { it })
            generateSequence(nextSuccess(input)) { s -> nextSuccess(s.remainingInput) }
                    .fold(Success(listOf<A>(), input, input) { listOf() }) { acc, success ->
                        Success(
                                value = acc.value + success.value,
                                startingInput = input,
                                remainingInput = success.remainingInput,
                                payload = { acc.payload() + success.payload() }
                        )
                    }.right()
        })