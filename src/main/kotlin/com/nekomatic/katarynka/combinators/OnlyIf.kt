@file:JvmName("OnlyIf")

package com.nekomatic.katarynka.combinators

import arrow.core.Either
import arrow.core.flatMap
import com.nekomatic.katarynka.core.Failure
import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.core.parse
import com.nekomatic.katarynka.parsers.Parser


infix fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.onlyIf(f: (A) -> Boolean): Parser<TItem, TIn, A> where TIn : IInput<TItem, TIn> =
        Parser(name, { input, n ->
            this.parse(input)
                    .flatMap { success ->
                        when {
                            f(success.value) -> Either.Right(success)
                            else -> Either.Left(Failure(n, success.startingInput, success.startingInput))
                        }
                    }
        })