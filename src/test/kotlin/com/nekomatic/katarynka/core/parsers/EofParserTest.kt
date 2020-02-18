package com.nekomatic.katarynka.core.parsers

import arrow.core.Either
import com.nekomatic.katarynka.core.EOF
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class EofParserTest {

    private val text0 = ""
    private val text1 = "a"
    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val parser = factory.eof()

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.of(text0.iterator())
        val result = parser.parse(input)

        assertAll(
                { assert(result is Either.Right) },
                { assertEquals(EOF, (result as Either.Right).b.value) },
                { assertEquals((result as Either.Right).b.startingInput.position, result.b.remainingInput.position) }
        )
    }


    @DisplayName("Non-empty input")
    @Test
    fun nonEmptyInput() {
        val input = LineInput.of(text1.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                { assertEquals((result as Either.Left).a.head.expected, "eof") },
                { assertEquals(input.position, (result as Either.Left).a.head.failedAtInput.position) }
        )

    }
}