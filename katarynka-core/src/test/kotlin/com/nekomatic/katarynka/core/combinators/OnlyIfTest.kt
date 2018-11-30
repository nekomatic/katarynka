package com.nekomatic.katarynka.core.combinators

import arrow.core.Either
import arrow.data.NonEmptyList
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class OnlyIfTest {
    private val textEmpty = ""
    private val text0 = "0"
    private val text1 = "1"
    private val parser = TestBuilder {
        match("non-zero digit") {
            it.isDigit()
        } onlyIf {
            it != '0'
        }
    }.build()

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.of(textEmpty.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'non-zero digit' at position 0")
                    assertEquals(expected, actual)
                }
        )
    }

    @DisplayName("Matching input")
    @Test
    fun matchingInput() {
        val input = LineInput.of(text1.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { assertEquals('1', (result as Either.Right).b.value) },
                { assertEquals(input.position + 1, (result as Either.Right).b.remainingInput.position) }
        )
    }

    @DisplayName("Non-matching input")
    @Test
    fun nonMatchingInput() {
        val input = LineInput.of(text0.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                {
                    val actual = (result as Either.Left).a.map { "expected '${it.expected}' at position ${it.failedAtInput.position}" }
                    val expected = NonEmptyList.of("expected 'non-zero digit' at position 0")
                    assertEquals(expected, actual)
                }
        )
    }
}