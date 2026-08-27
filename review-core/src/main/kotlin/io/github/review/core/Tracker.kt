package io.github.review.core

import java.util.ArrayDeque

internal object Tracker {

    private val stack = ArrayDeque<Effect>()

    val current: Effect?
        get() = stack.peekLast()

    fun <T> track(effect: Effect, block: () -> T): T {
        stack.addLast(effect)
        try {
            return block()
        } finally {
            stack.removeLast()
        }
    }
}
