package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.result.Failure
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.result.Success
import com.nekomatic.katarynka.core.parsers.ItemParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class CombinatorTest {

    private val textA: List<Char> = "a".toList()
    private val textB: List<Char> = "b".toList()
    private val parser = ItemParser<Char, Input<Char>>('a')

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Map combinator")
    @Test
    fun map() {
        val mapped = parser map { listOf(it) }
        val input = Input.create(textA.iterator())
        val result = mapped.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Mapped parser result of a matching input should be Either.Right" }
                },
                {
                    Assertions.assertEquals(listOf('a'), (result as Either.Right<Success<Char, Input<Char>, List<Char>>>).b.value)
                    { "Value of a successful Mapped parser should be equal to the result of current element's transformation " }
                }
        )
    }

    @DisplayName("Name combinator")
    @Test
    fun name() {
        val named = parser rename "Char 'a'"
        val input = Input.create(textB.iterator())
        val result = named.parse(input)
        assertAll(
                {
                    assert(result is Either.Left<*>)
                    { "Named parser result of a matching input should be Either.Left" }
                },
                {
                    Assertions.assertEquals((result as Either.Left<Failure<Char, Input<Char>>>).a.expected, "Char 'a'")
                    { "Expected of a failed ItemParser should be the value provided by the right side of the combinator" }
                }
        )
    }


    @Suppress("UNCHECKED_CAST")
    @DisplayName("ToConst combinator")
    @Test
    fun toConst() {
        val mapped = parser toConst 5
        val input = Input.create(textA.iterator())
        val result = mapped.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "Mapped parser result of a matching input should be Either.Right" }
                },
                {
                    Assertions.assertEquals(5, (result as Either.Right<Success<Char, Input<Char>, Int>>).b.value)
                    { "Value of a successful ToConst parser should be equal to the constant provided by the right side of the combinator" }
                }
        )
    }
}