package com.nekomatic.katarynka.core

import arrow.core.*
import com.nekomatic.katarynka.core.combinators.sMap
import com.nekomatic.katarynka.core.combinators.then
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success

open class Builder<TItem, TIn, TVal, TF>(private val factory: TF, private val block: TF.() -> IParser<TItem, TIn, TVal>)
        where TIn : IInput<TItem, TIn>, TF : ParserFactory<TItem, TIn> {
    fun build() = block.invoke(factory)
}


val p = Builder(ParserFactory<Char, LineInput<Char>>()) {
    val a = item('a')
    a then a sMap { 2 }
}.build()

@Suppress("UNUSED_PARAMETER")
open class ParserFactory<TItem, TIn>(val keepPayload: Boolean = true) where TIn : IInput<TItem, TIn> {

    private val eof =
        this.parser("eof") { input, n, _ ->
            eofParserFunction(
                input = input,
                name = n,
                factory = this
            )
        }

    fun successful() = this.parser("success") { input, _, _ ->
        Success(
            value = Unit,
            startingInput = input,
            remainingInput = input,
            payload = when {
                this.keepPayload -> listOf<TItem>().some()
                else -> None
            }
        ).right()
    }

    fun <TVal : Any> successful(const: TVal) = this.parser("success") { input, _, _ ->
        Success(
            value = const,
            startingInput = input,
            remainingInput = input,
            payload = when {
                this.keepPayload -> listOf<TItem>().some()
                else -> None
            }
        ).right()
    }

    private val any =
        this.any("any")

    private fun anyFunction(input: TIn, n: String, fact: ParserFactory<TItem, TIn>) =
        standardParserFunction(
            input = input,
            name = n,
            match = { true },
            factory = this
        )

    fun <TVal> parser(name: String, parserFunction: ParserFunction<TItem, TIn, TVal>) =
        Parser(
            name = name,
            factory = this,
            parserFunction = parserFunction
        )

    fun <TVal> failing(name: String) =
        parser<TVal>("eof") { input, _, _ ->
            NonEmptyList.of(Failure(name, input)).left()
        }

    fun match(name: String, match: (TItem) -> Boolean): Parser<TItem, TIn, TItem> {
        fun f(input: TIn, n: String, fact: ParserFactory<TItem, TIn>) =
            standardParserFunction(
                input = input,
                name = n,
                match = match,
                factory = this
            )
        return this.parser(name, ::f)
    }

    fun item(name: String, item: TItem): Parser<TItem, TIn, TItem> {
        fun f(input: TIn, n: String, fact: ParserFactory<TItem, TIn>) =
            standardParserFunction(
                input = input,
                name = n,
                match = { it == item },
                factory = this
            )
        return this.parser(name, ::f)
    }

    fun item(item: TItem) = this.item(item.toString(), item)

    fun any(name: String) = this.parser(name, ::anyFunction)

    fun any() = any

    fun eof() = eof

    fun <TVal> ref() = ParserRef<TItem, TIn, TVal>(this)
}


fun <TItem, TIn, TVal, TF> TF.of(f: (TF) -> IParser<TItem, TIn, TVal>)
        where TIn : IInput<TItem, TIn>, TF : ParserFactory<TItem, TIn> = f(this)


