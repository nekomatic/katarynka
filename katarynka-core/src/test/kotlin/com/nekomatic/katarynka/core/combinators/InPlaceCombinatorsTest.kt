package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class InPlaceCombinatorsTest {

    private val textA = "a"
    private val textB = "b"
    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val parser = factory.item('a')


    @DisplayName("Map combinator")
    @Test
    fun map() {
        val mapped = parser sMap { listOf(it) }
        val input = LineInput.of(textA.iterator())
        val result = mapped.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { Assertions.assertEquals(listOf('a'), (result as Either.Right).b.value) }
        )
    }

    @DisplayName("Name combinator")
    @Test
    fun name() {
        val named = parser toNamedParser "Char 'a'"
        val input = LineInput.of(textB.iterator())
        val result = named.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'Char 'a'' at position 0", "expected 'a' at position 0")
                    Assertions.assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("ToConst combinator")
    @Test
    fun toConst() {
        val consted = parser toConst 5
        val input = LineInput.of(textA.iterator())
        val result = consted.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { Assertions.assertEquals(5, (result as Either.Right).b.value) }
        )
    }
}