@file:JvmName("ListWithSeparator")

package com.nekomatic.katarynka.core.combinators

import com.nekomatic.katarynka.core.input.IInput
import com.nekomatic.katarynka.core.parsers.Parser

//TODO: create tests
//TODO: create documentation
/**
 *
 * @receiver Parser<TItem, TIn, A>
 * @param separator Parser<TItem, TIn, B>
 * @return Parser<TItem, TIn, List<A>>
 */
infix fun <TItem : Any, TIn, A : Any, B : Any> Parser<TItem, TIn, A>.listWithSeparator(separator: Parser<TItem, TIn, B>): Parser<TItem, TIn, List<A>>
        where TIn : IInput<TItem, TIn> {
    val p1 = this.map { listOf(it) }
    val p2 = (separator.then(this)).zeroOrMore().map { l -> l.map { it.b } }
    return p1 then p2 map { it.a + it.b }
}