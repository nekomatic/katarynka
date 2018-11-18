package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.parsers.ItemParser
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.result.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class InPlaceCombinatorTest {

    private val textA = "a"
    private val textB = "b"
    private val parser = ItemParser<Char, LineInput<Char>>('a')

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Map combinator")
    @Test
    fun map() {
        val mapped = parser map { listOf(it) }
        val input = LineInput.create(textA.iterator())
        val result = mapped.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Mapped parser result of a matching input should be Either.Right" }
                },
                {
                    Assertions.assertEquals(listOf('a'), (result as Either.Right<Success<Char, LineInput<Char>, List<Char>>>).b.value)
                    { "Value of a successful Mapped parser should be equal to the result of current element's transformation " }
                }
        )
    }

    @DisplayName("Name combinator")
    @Test
    fun name() {
        val named = parser toNamedParser "Char 'a'"
        val input = LineInput.create(textB.iterator())
        val result = named.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "Named parser result of a matching input should be Either.Left" }
                },
                {
                    Assertions.assertEquals((result as Either.Left<Failure<Char, LineInput<Char>>>).a.expected, "Char 'a'")
                    { "Expected of a failed ItemParser should be the value provided by the right side of the combinator" }
                }
        )
    }


    @Suppress("UNCHECKED_CAST")
    @DisplayName("ToConst combinator")
    @Test
    fun toConst() {
        val mapped = parser toConst 5
        val input = LineInput.create(textA.iterator())
        val result = mapped.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Mapped parser result of a matching input should be Either.Right" }
                },
                {
                    Assertions.assertEquals(5, (result as Either.Right<Success<Char, LineInput<Char>, Int>>).b.value)
                    { "Value of a successful ToConst parser should be equal to the constant provided by the right side of the combinator" }
                }
        )
    }
}