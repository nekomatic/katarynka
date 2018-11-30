package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class OrElseTest {

    fun <T> List<T>.toNelUnsafe() = NonEmptyList.fromListUnsafe(this)

    private val textD = "d"
    private val textABCDE = "abcde"

    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val parserA = factory.item('a')
    private val parserB = factory.item('b')
    private val parserC = factory.item('c')
    private val fail1 = "abcdx".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }
    private val fail2 = "axcd".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }
    private val fail3 = "abcx".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }
    private val pass1 = "abc".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }
    private val pass2 = "abcd".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }
    private val pass3 = "abcde".map { factory.item(it) }.toNelUnsafe().sequence() sMap { c -> c.joinToString("") }

    private val parserABC = parserA orElse parserB orElse parserC
    private val parserAllFail = fail1 orElse fail2 orElse fail3
    private val parser1onlyPass = pass1 orElse fail1
    private val parser2onlyPass = fail1 orElse pass2
    private val parser12Pass = pass3 orElse pass2


    @DisplayName("Non-matching input, same failure position")
    @Test
    fun nonMatchingShort() {
        val input = LineInput.of(textD.iterator())
        val result = parserABC.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'c' at position 0", "expected 'b' at position 0", "expected 'a' at position 0")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Non-matching input, various failure positions")
    @Test
    fun nonMatchingLong() {
        val input = LineInput.of(textABCDE.iterator())
        val result = parserAllFail.parse(input)
        assertAll(
                { assertTrue(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'x' at position 3", "expected 'x' at position 1", "expected 'x' at position 4")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Matching input, first Builder only")
    @Test
    fun matchingInputToFirst() {
        val input = LineInput.of(textABCDE.iterator())
        val result = parser1onlyPass.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals("abc", (result as Either.Right).b.value) },
                { assertEquals(3, (result as Either.Right).b.remainingInput.position) }
        )
    }

    @DisplayName("Matching input, second Builder only")
    @Test
    fun matchingInputToSecond() {
        val input = LineInput.of(textABCDE.iterator())
        val result = parser2onlyPass.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals("abcd", (result as Either.Right).b.value) },
                { assertEquals(4, (result as Either.Right).b.remainingInput.position) }
        )
    }

    @DisplayName("Matching input, both parsers, first retuns")
    @Test
    fun matchingInputToBoth() {
        val input = LineInput.of(textABCDE.iterator())
        val result = parser12Pass.parse(input)
        assertAll(
                { assertTrue(result is Either.Right) },
                { assertEquals("abcde", (result as Either.Right).b.value) },
                { assertEquals(5, (result as Either.Right).b.remainingInput.position) }
        )
    }
}