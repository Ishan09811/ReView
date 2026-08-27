package io.github.review.android.bind

import android.view.View
import android.widget.TextView
import io.github.review.core.Effect
import java.util.WeakHashMap
import io.github.review.core.bind as coreBind

private val bindingsByView = WeakHashMap<View, MutableList<Effect>>()

fun View.registerReviewBinding(effect: Effect) {
    bindingsByView.getOrPut(this) { mutableListOf() }.add(effect)
}

private fun View.registerBinding(effect: Effect) {
    bindingsByView.getOrPut(this) { mutableListOf() }.add(effect)
}

/** Disposes every ReView binding (Effect) registered on this View. */
fun View.disposeReviewBindings() {
    bindingsByView.remove(this)?.forEach { it.dispose() }
}

/**
 * Binds this TextView's text to a reactive computation. Runs [compute]
 * immediately to set the initial text and then reruns it whenever any [io.github.review.core.State] read inside [compute] changes.
 *
 * Example:
 * ```
 * val name = stateOf("world")
 * textView.bindText { "Hello, ${name.value}!" }
 * ```
 */
fun TextView.bindText(compute: () -> CharSequence) {
    val effect = coreBind { text = compute() }
    registerBinding(effect)
}

/**
 * Binds this View's visibility to a reactive boolean. `true` maps to
 * [View.VISIBLE], `false` to [goneWhenFalse] ? [View.GONE] : [View.INVISIBLE].
 */
fun View.bindVisible(goneWhenFalse: Boolean = true, compute: () -> Boolean) {
    val effect = coreBind {
        visibility = if (compute()) View.VISIBLE else if (goneWhenFalse) View.GONE else View.INVISIBLE
    }
    registerBinding(effect)
}

/** Binds this View's enabled state to a reactive boolean. */
fun View.bindEnabled(compute: () -> Boolean) {
    val effect = coreBind { isEnabled = compute() }
    registerBinding(effect)
}

fun <V : View> V.bind(action: V.() -> Unit) {
    val effect = coreBind { action() }
    registerBinding(effect)
}
