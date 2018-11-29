package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.some
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ThenTest {

    fun <T> List<T>.toNelUnsafe() = NonEmptyList.fromListUnsafe(this)
    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val textA = "a"
    private val textABCDE = "abcde"
    private val textABCDX = "abcdx"
    private val textAXCDE = "axcde"
    private val textAXCDX = "axcdx"

    private val parserAB = "ab".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }
    private val parserCDE = "cde".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }
    private val parser = parserAB then parserCDE


    @DisplayName("Matching input")
    @Test
    fun matchingInput() {
        val input = LineInput.of(textABCDE.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals(Tuple2("ab", "cde"), (result as Either.Right).b.value) },
                { assertEquals(listOf('a', 'b', 'c', 'd', 'e').some(), (result as Either.Right).b.payload) }
        )
    }

    @DisplayName("Insufficient input")
    @Test
    fun insufficientInput() {
        val input = LineInput.of(textA.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'b' at position 1")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Non-matching input of the second parser")
    @Test
    fun nonMatchingSecondInput() {
        val input = LineInput.of(textABCDX.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'e' at position 4")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Non-matching input of the first parser")
    @Test
    fun nonMatchingFirstInput() {
        val input = LineInput.of(textAXCDE.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'b' at position 1")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Non-matching input of both parsers")
    @Test
    fun nonMatchingBothInputs() {
        val input = LineInput.of(textAXCDX.iterator())
        val result = parser.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'b' at position 1")
                    assertEquals(expected, actual)
                }
        )
    }
}