package com.nekomatic.katarynka.core

import arrow.core.None
import arrow.core.some
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals


internal class InputTest {

    private val text0 = "".toList()
    private val text1 = "a".toList()
    private val text2 = "ab".toList()

    @DisplayName("Input.next")
    @Test
    fun getNext() {
        val input0 = Input.create(text0.iterator()).next
        val input1 = Input.create(text1.iterator()).next
        val input2 = Input.create(text2.iterator()).next

        assertAll(
                { assertEquals(None, input0.item, "Next of an empty Input should None item (should be empty)") },
                { assertEquals(None, input1.item, "Next of last a non-empty Input should have None item (should be empty)") },
                { assertEquals('b'.some(), input2.item, "Next of an Input with successors should have Some item (should not be empty)") }
        )
    }

    @DisplayName("Input.item")
    @Test
    fun getItem() {
        val input0 = Input.create(text0.iterator())
        val input1 = Input.create(text1.iterator())
        val input2 = Input.create(text2.iterator())

        assertAll(
                { assertEquals(None, input0.item, "Item of an empty Input should be None") },
                { assertEquals('a'.some(), input1.item, "Item of non-empty single-element Input should be Some") },
                { assertEquals('a'.some(), input2.item, "Item of non-empty Input with successors should be Some") }
        )
    }

    @DisplayName("Input.position")
    @Test
    fun getPosition() {
        val input0 = Input.create(text0.iterator())
        val input0next = input0.next
        val input1 = Input.create(text1.iterator())
        val input1next = input1.next
        val input1nextnext = input1next.next
        val input2 = Input.create(text2.iterator())
        val input2next = input2.next

        assertAll(
                { assertEquals(0, input0.position, "Start position of an empty Input should be 0") },
                { assertEquals(0, input0next.position, "Position of the next to an empty Input should be 0") },
                { assertEquals(0, input1.position, "Start position of a single-element Input should be 0") },
                { assertEquals(1, input1next.position, "Position of the next to a single-element Input should be 1") },
                { assertEquals(input1next.position, input1nextnext.position, "Position of an Input beyond the last one should be the same as the last one") },
                { assertEquals(0, input2.position, "Start position of a single-element Input should be 0") },
                { assertEquals(1, input2next.position, "Position of the next to an Input with successors should be 1") }
        )
    }
}