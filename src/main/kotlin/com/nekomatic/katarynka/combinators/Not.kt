@file:JvmName("Not")

package com.nekomatic.katarynka.combinators

import arrow.core.Either
import arrow.core.left
import com.nekomatic.katarynka.core.Failure
import com.nekomatic.katarynka.core.IInput
import com.nekomatic.katarynka.core.Success
import com.nekomatic.katarynka.core.parse
import com.nekomatic.katarynka.parsers.AnyParser
import com.nekomatic.katarynka.parsers.Parser


fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.not(): Parser<TItem, TIn, TItem> where TIn : IInput<TItem, TIn> =
        Parser(
                name = name,
                parserFunction = fun(input: TIn, n: () -> String): Either<Failure<TItem, TIn>, Success<TItem, TIn, out TItem>> =
                        if (this.parse(input).isRight()) Failure(n, input, input).left()
                        else AnyParser<TItem, TIn>(n).parse(input)
        )