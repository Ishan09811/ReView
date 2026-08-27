package io.github.review.core

import kotlin.test.Test
import kotlin.test.assertEquals

class StateEffectTest {

    @Test
    fun `effect runs immediately on creation`() {
        var runs = 0
        bind { runs++ }
        assertEquals(1, runs)
    }

    @Test
    fun `effect reruns when a read state changes`() {
        val name = stateOf("Alice")
        var seen = ""
        bind { seen = name.value }

        assertEquals("Alice", seen)
        name.value = "Bob"
        assertEquals("Bob", seen)
    }

    @Test
    fun `setting equal value does not trigger a rerun`() {
        val count = stateOf(5)
        var runs = 0
        bind { count.value; runs++ }

        assertEquals(1, runs)
        count.value = 5 // same value
        assertEquals(1, runs, "should not rerun on no-op set")
    }

    @Test
    fun `effect only depends on state it actually reads`() {
        val a = stateOf(1)
        val b = stateOf(100)
        var runs = 0
        bind { a.value; runs++ } // never reads b

        assertEquals(1, runs)
        b.value = 200
        assertEquals(1, runs, "changing an unread state must not trigger a rerun")
        a.value = 2
        assertEquals(2, runs)
    }

    @Test
    fun `conditional reads update dependency set on each run`() {
        val useA = stateOf(true)
        val a = stateOf("A1")
        val b = stateOf("B1")
        var seen = ""
        bind { seen = if (useA.value) a.value else b.value }

        assertEquals("A1", seen)

        b.value = "B2"
        assertEquals("A1", seen, "not reading b's branch, so b changes shouldn't matter")

        useA.value = false
        assertEquals("B2", seen)

        a.value = "A3"
        assertEquals("B2", seen, "no longer reading a's branch, so a changes shouldn't matter")

        b.value = "B3"
        assertEquals("B3", seen)
    }

    @Test
    fun `disposed effect does not rerun`() {
        val count = stateOf(0)
        var runs = 0
        val effect = bind { count.value; runs++ }

        assertEquals(1, runs)
        effect.dispose()
        count.value = 1
        assertEquals(1, runs, "disposed effect must not rerun")
    }

    @Test
    fun `multiple independent effects on same state all rerun`() {
        val shared = stateOf(1)
        val seenByA = mutableListOf<Int>()
        val seenByB = mutableListOf<Int>()
        bind { seenByA.add(shared.value) }
        bind { seenByB.add(shared.value) }

        shared.value = 2
        shared.value = 3

        assertEquals(listOf(1, 2, 3), seenByA)
        assertEquals(listOf(1, 2, 3), seenByB)
    }

    @Test
    fun `diamond dependency does not double-fire`() {
        val base = stateOf(1)
        var sinkRuns = 0
        bind {
            // reading base twice inside the same effect must still be one subscription
            val x = base.value
            val y = base.value
            sinkRuns++
            check(x == y)
        }

        assertEquals(1, sinkRuns)
        base.value = 2
        assertEquals(2, sinkRuns, "one effect reading the same state twice should rerun once per change not twice")
    }

    @Test
    fun `peek does not subscribe`() {
        val count = stateOf(0)
        var runs = 0
        bind { count.peek(); runs++ }

        assertEquals(1, runs)
        count.value = 1
        assertEquals(1, runs, "peek() reads must not create a subscription")
    }

    @Test
    fun `nested state changes during a run do not corrupt iteration`() {
        val trigger = stateOf(0)
        val log = mutableListOf<String>()

        val effect1 = bind {
            log.add("effect1:${trigger.value}")
        }
        bind {
            log.add("effect2:${trigger.value}")
        }

        trigger.value = 1
        assertEquals(
            listOf("effect1:0", "effect2:0", "effect1:1", "effect2:1"),
            log
        )
        effect1.dispose()
        trigger.value = 2
        assertEquals(
            listOf("effect1:0", "effect2:0", "effect1:1", "effect2:1", "effect2:2"),
            log
        )
    }
}
