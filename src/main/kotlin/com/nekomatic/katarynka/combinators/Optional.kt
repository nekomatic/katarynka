@file:JvmName("Option")

package com.nekomatic.katarynka.combinators

import arrow.core.*
import com.nekomatic.katarynka.core.*
import com.nekomatic.katarynka.core.Success
import com.nekomatic.katarynka.parsers.Parser


fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.optional(): Parser<TItem, TIn, Option<A>> where TIn : IInput<TItem, TIn> =
        Parser(name, { input: TIn, _ ->
            val r = this.parse(input)
            when (r) {
                is Either.Left -> Success(None, input, input) { listOf() }.right()
                is Either.Right -> r.map { s -> s.map { v -> Some(v) } }
            }
        })