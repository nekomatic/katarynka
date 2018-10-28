@file:JvmName("Core")

package com.nekomatic.katarynka.core

import arrow.core.*
import com.nekomatic.katarynka.parsers.Parser

typealias genericParser<TItem, TIn, A> = (TIn, () -> String) -> parserResult<TItem, TIn, out A>


fun <TItem : Any, TIn> standardParserFunction(input: TIn, name: () -> String, match: (TItem) -> Boolean): parserResult<TItem, TIn, out TItem>
        where TIn : IInput<TItem, TIn> {
    val currentItem = input.item
    return when (currentItem) {
        None -> Failure(
                expected = name,
                startingInput = input,
                remainingInput = input
        ).left()
        is Some -> {
            val current = currentItem.t
            if (match(current)) {
                val remaining = input.next
                Success(
                        value = current,
                        startingInput = input,
                        remainingInput = remaining,
                        payload = { listOf(current) }
                ).right()
            } else
                Failure(
                        expected = name,
                        startingInput = input,
                        remainingInput = input
                ).left()
        }
    }
}

fun <TItem : Any, TIn> eofParserFunction(input: TIn, name: () -> String): parserResult<TItem, TIn, out EOF>
        where TIn : IInput<TItem, TIn> {
    return when (input.item) {
        None -> Success(
                value = EOF,
                startingInput = input,
                remainingInput = input,
                payload = { listOf() }
        ).right()
        is Some -> Failure(
                expected = name,
                startingInput = input,
                remainingInput = input
        ).left()
    }
}

fun <TItem : Any, TIn, A : Any> Parser<TItem, TIn, A>.parse(input: TIn): parserResult<TItem, TIn, out A> where TIn : IInput<TItem, TIn> =
        this.parserFunction(input, this.name)

object EOF

typealias parserResult<TItem, TIn, TVal> = Either<Failure<TItem, TIn>, Success<TItem, TIn, TVal>>

fun <TItem : Any, TIn, A : Any, B : Any> Success<TItem, TIn, A>.map(f: (A) -> B) where TIn : IInput<TItem, TIn> =
        Success(
                value = f(this.value),
                startingInput = this.startingInput,
                remainingInput = this.remainingInput,
                payload = this.payload
        )




