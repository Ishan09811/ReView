package io.github.review.core

class State<T>(initial: T) {

    private var backing: T = initial

    private val dependents = LinkedHashSet<Effect>()

    var value: T
        get() {
            Tracker.current?.let { effect ->
                dependents.add(effect)
                effect.noteSubscription(this)
            }
            return backing
        }
        set(newValue) {
            if (backing == newValue) return
            backing = newValue
            notifyDependents()
        }

    fun set(newValue: T) {
        value = newValue
    }

    fun peek(): T = backing

    internal fun subscribe(effect: Effect) {
        dependents.add(effect)
    }

    internal fun unsubscribe(effect: Effect) {
        dependents.remove(effect)
    }

    private fun notifyDependents() {
        val toRun = dependents.toList()
        for (effect in toRun) {
            effect.rerun()
        }
    }

    override fun toString(): String = "State($backing)"
}

fun <T> stateOf(initial: T): State<T> = State(initial)
