package com.nekomatic.katarynka.core.core

import arrow.core.None
import arrow.core.some
import com.nekomatic.katarynka.core.input.Input
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll



internal class InputTest {

    private val text0 = ""
    private val text1 = "a"
    private val text2 = "ab"

    @DisplayName("Input.next")
    @Test
    fun getNext() {
        val input0 = Input.of(text0.iterator()).next
        val input1 = Input.of(text1.iterator()).next
        val input2 = Input.of(text2.iterator()).next

        assertAll(
                { assertEquals(None, input0.item) },
                { assertEquals(None, input1.item) },
                { assertEquals('b'.some(), input2.item) }
        )
    }

    @DisplayName("Input.item")
    @Test
    fun getItem() {
        val input0 = Input.of(text0.iterator())
        val input1 = Input.of(text1.iterator())
        val input2 = Input.of(text2.iterator())

        assertAll(
                { assertEquals(None, input0.item) },
                { assertEquals('a'.some(), input1.item) },
                { assertEquals('a'.some(), input2.item) }
        )
    }

    @DisplayName("Input.position")
    @Test
    fun getPosition() {
        val input0 = Input.of(text0.iterator())
        val input0next = input0.next
        val input1 = Input.of(text1.iterator())
        val input1next = input1.next
        val input1nextnext = input1next.next
        val input2 = Input.of(text2.iterator())
        val input2next = input2.next

        assertAll(
                { assertEquals(0, input0.position) },
                { assertEquals(0, input0next.position) },
                { assertEquals(0, input1.position) },
                { assertEquals(1, input1next.position) },
                { assertEquals(input1next.position, input1nextnext.position) },
                { assertEquals(0, input2.position) },
                { assertEquals(1, input2next.position) }
        )
    }
}