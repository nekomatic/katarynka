package com.nekomatic.katarynka.core

import arrow.core.None
import arrow.core.left
import arrow.core.right
import arrow.core.some
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success

open class ParserFactory<TItem, TIn>(val keepPayload: Boolean = true) where TIn : IInput<TItem, TIn> {

    private val eof = this.parser("eof") { input, n, _ -> eofParserFunction(input, n, this) }

    private val successful =
            this.parser("success") { input, _, _ -> Success(Unit, input, input, if (this.keepPayload) listOf<TItem>().some() else None).right() }

    private val any = this.any("any")

    @Suppress("UNUSED_PARAMETER")
    private fun anyFunction(input: TIn, n: String, fact: ParserFactory<TItem, TIn>) = standardParserFunction(input, n, { true }, this)

    fun <TVal> parser(name: String, parserFunction: ParserFunction<TItem, TIn, TVal>) =
            Parser(name, this, parserFunction)

    fun <TVal> failing(name: String) =
            parser<TVal>("eof") { input, _, _ -> NonEmptyList.of(Failure(name, input)).left() }

    fun successful() = successful

    fun match(name: String, match: (TItem) -> Boolean): Parser<TItem, TIn, TItem> {
        @Suppress("UNUSED_PARAMETER")
        fun f(input: TIn, n: String, fact: ParserFactory<TItem, TIn>) = standardParserFunction(input, n, match, this)
        return this.parser(name, ::f)
    }

    fun item(name: String, item: TItem): Parser<TItem, TIn, TItem> {
        @Suppress("UNUSED_PARAMETER")
        fun f(input: TIn, n: String, fact: ParserFactory<TItem, TIn>) = standardParserFunction(input, n, { it == item }, this)
        return this.parser(name, ::f)
    }

    fun item(item: TItem) =
            this.item(item.toString(), item)

    fun any(name: String) = this.parser(name, ::anyFunction)

    fun any() = any

    fun eof() = eof

    fun <TVal> ref() = ParserRef<TItem, TIn, TVal>(this)
}


fun <TItem, TIn, TVal, TF> TF.of(f: (TF) -> IParser<TItem, TIn, TVal>)
        where TIn : IInput<TItem, TIn>, TF : ParserFactory<TItem, TIn> = f(this)


