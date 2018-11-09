package com.nekomatic.katarynka.samples.json

import arrow.core.Either
import com.nekomatic.katarynka.chars.CInput
import com.nekomatic.katarynka.core.input.Input
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

@ExperimentalUnsignedTypes
internal class JsonParserTest {

    val validJsonSampleWithNoNumbers: String = """
    {"widget": {
        "debug": "on\u0020",
        "window": {
            "title": "Sample \nKonfabulator Widget",
            "parserName": "\u0021main_window",
            "width": "500",
            "height": "500"
        },
        "image": {
            "src": "Images/Sun.png",
            "parserName": "sun1",
            "hOffset": "250",
            "vOffset": "250",
            "alignment": "cent\u0020er"
        },
        "text": {
            "data": "Click \tHere",
            "size": "36",
            "style": "bold",
            "parserName": "\u0020text1\u0020",
            "hOffset": "250",
            "vOffset": "100",
            "alignment": "center",
            "onMouseUp": "sun1.opacity = (sun1.opacity / 100) * 90;"
        }
    }}
    """.trimIndent()

    val validJsonSample01: String = """
    {"widget": {
        "debug": "on\u0020",
        "window": {
            "title": "Sample \nKonfabulator Widget",
            "parserName": "\u0021main_window",
            "width": 500,
            "height": 500
        },
        "image": {
            "src": "Images/Sun.png",
            "parserName": "sun1",
            "hOffset": 250,
            "vOffset": 250,
            "alignment": "cent\u0020er"
        },
        "text": {
            "data": "Click \tHere",
            "size": 36,
            "style": "bold",
            "parserName": "\u0020text1\u0020",
            "hOffset": 250,
            "vOffset": 100,
            "alignment": "center",
            "onMouseUp": "sun1.opacity = (sun1.opacity / 100) * 90;"
        }
    }}
    """.trimIndent()

    val invalidJsonSample01: String = """
    {"widget": {
        "debug": "on",
        "window": {
            "title": "Sample Konfabulator Widget",
            "parserName": "main_window",
            "width": 500,
            "height": 500
        },
        "image": {
            "src": "Images/Sun.png",
            "parserName": "sun1",
            "hOffset": 250,
            "vOffset": 250,
            "alignment": "center"
        },
        "text": {
            "data": "Click \Here",
            "size": 36,
            "style": "bold",
            "parserName": "text1",
            "hOffset": 250,
            "vOffset": 100,
            "alignment": "center",
            "onMouseUp": "sun1.opacity = (sun1.opacity / 100) * 90;"
        }
    }}
    """.trimIndent()
    val parser = JsonParser

    @Test
    fun parse() {
        val input = CInput.create(validJsonSampleWithNoNumbers.iterator())
        val result = parser.parse(input)
        assertTrue(result is Either.Right<*>)
    }
}