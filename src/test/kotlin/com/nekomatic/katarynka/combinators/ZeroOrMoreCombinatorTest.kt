package com.nekomatic.katarynka.combinators

import arrow.core.Either
import com.nekomatic.katarynka.core.Input
import com.nekomatic.katarynka.core.Success
import com.nekomatic.katarynka.parsers.ItemParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ZeroOrMoreCombinatorTest {
    private val text0 = "".toList()
    private val textA = "a".toList()
    private val textAAB = "aab".toList()
    private val textB = "b".toList()
    private val parser = ItemParser<Char, Input<Char>>('a').zeroOrMore()

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = Input.create(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "ZeroOrMore parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(listOf<Char>(), (result as Either.Right<Success<Char, Input<Char>, List<Char>>>).b.value)
                    { "Value of a successful ZeroOrMore parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position, (result as Either.Right<Success<Char, Input<Char>, List<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful ZeroOrMore parser should be at larger by 1 than the starting input's position" }
                }
        )

    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Single-match input")
    @Test
    fun singleMatchInput() {
        val input = Input.create(textA.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "ZeroOrMore parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(listOf('a'), (result as Either.Right<Success<Char, Input<Char>, List<Char>>>).b.value)
                    { "Value of a successful ZeroOrMore parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 1, (result as Either.Right<Success<Char, Input<Char>, List<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful ZeroOrMore parser should be at larger by 1 than the starting input's position" }
                }
        )
    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("Multi-match input")
    @Test
    fun multiMatchInput() {
        val input = Input.create(textAAB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "ZeroOrMore parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(listOf('a', 'a'), (result as Either.Right<Success<Char, Input<Char>, List<Char>>>).b.value)
                    { "Value of a successful ZeroOrMore parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position + 2, (result as Either.Right<Success<Char, Input<Char>, List<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful ZeroOrMore parser should be at larger by 1 than the starting input's position" }
                }
        )
    }

    @Suppress("UNCHECKED_CAST")
    @DisplayName("No-match input")
    @Test
    fun noMatchInput() {
        val input = Input.create(textB.iterator())
        val result = parser.parse(input)
        assertAll(
                {
                    assert(result is Either.Right<*>)
                    { "ZeroOrMore parser result of a matching input should be Either.Right" }
                },
                {
                    assertEquals(listOf<Char>(), (result as Either.Right<Success<Char, Input<Char>, List<Char>>>).b.value)
                    { "Value of a successful ZeroOrMore parser should be equal to the current element" }
                },
                {
                    assertEquals(input.position, (result as Either.Right<Success<Char, Input<Char>, List<Char>>>).b.remainingInput.position)
                    { "Position of the remaining input of a successful ZeroOrMore parser should be at larger by 1 than the starting input's position" }
                }
        )
    }

}