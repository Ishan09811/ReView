package io.github.review.core

class Effect(private val block: () -> Unit) {

    private val subscriptions = mutableSetOf<State<*>>()
    private var disposed = false

    init {
        run()
    }

    /** Rerun the block, retracking dependencies. Called by [State] on change. */
    internal fun rerun() {
        if (disposed) return
        run()
    }

    private fun run() {
        detachFromCurrentSubscriptions()
        Tracker.track(this) {
            block()
        }
    }

    fun dispose() {
        disposed = true
        detachFromCurrentSubscriptions()
    }

    private fun detachFromCurrentSubscriptions() {
        if (subscriptions.isEmpty()) return
        val old = subscriptions.toList()
        subscriptions.clear()
        for (state in old) state.unsubscribe(this)
    }

    internal fun noteSubscription(state: State<*>) {
        subscriptions.add(state)
    }
}
