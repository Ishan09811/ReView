package io.github.review.android.dsl

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import io.github.review.android.bind.disposeReviewBindings

@DslMarker
annotation class ReViewDsl

@ReViewDsl
class ViewGroupScope<G : ViewGroup>(val context: Context, val group: G) {

    /** Adds [view] as a child of [group] with [params], returning it for chaining. */
    fun <V : View> add(view: V, params: ViewGroup.LayoutParams = wrapContent()): V {
        group.addView(view, params)
        return view
    }
}

/** Shorthand for the extremely common `WRAP_CONTENT x WRAP_CONTENT` case. */
fun wrapContent(): ViewGroup.LayoutParams = ViewGroup.LayoutParams(
    ViewGroup.LayoutParams.WRAP_CONTENT,
    ViewGroup.LayoutParams.WRAP_CONTENT
)

/** Shorthand for `MATCH_PARENT x WRAP_CONTENT`, common for rows in a vertical LinearLayout. */
fun matchWidthWrapHeight(): ViewGroup.LayoutParams = ViewGroup.LayoutParams(
    ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.WRAP_CONTENT
)

/**
 * Root entry point: builds a [LinearLayout]-rooted View tree with ReView's
 * DSL, and wires automatic disposal of every `bindXxx` Effect created inside
 * it (transitively, for every descendant View) to the root's detach event
 * so you don't have to manually call [disposeReviewBindings] on each child.
 *
 * Typical use in an Activity:
 * ```
 * setContentView(reviewRoot(this) {
 *     textView { bindText { "Hello, ${name.value}" } }
 *     button("Increment") { setOnClickListener { count.value++ } }
 * })
 * ```
 */
fun reviewRoot(
    context: Context,
    orientation: Int = LinearLayout.VERTICAL,
    build: ViewGroupScope<LinearLayout>.() -> Unit
): LinearLayout {
    val root = LinearLayout(context).apply { this.orientation = orientation }
    ViewGroupScope(context, root).build()

    root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = Unit
        override fun onViewDetachedFromWindow(v: View) {
            disposeAllReviewBindings(root)
        }
    })

    return root
}

/** Recursively disposes ReView bindings on [view] and if it's a ViewGroup, all descendants. */
private fun disposeAllReviewBindings(view: View) {
    view.disposeReviewBindings()
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            disposeAllReviewBindings(view.getChildAt(i))
        }
    }
}

fun ViewGroupScope<*>.linearLayout(
    orientation: Int = LinearLayout.VERTICAL,
    params: ViewGroup.LayoutParams = wrapContent(),
    build: ViewGroupScope<LinearLayout>.() -> Unit = {}
): LinearLayout {
    val layout = LinearLayout(context).apply { this.orientation = orientation }
    ViewGroupScope(context, layout).build()
    return add(layout, params)
}

fun ViewGroupScope<*>.textView(
    text: CharSequence = "",
    params: ViewGroup.LayoutParams = wrapContent(),
    build: TextView.() -> Unit = {}
): TextView {
    val tv = TextView(context).apply {
        this.text = text
        build()
    }
    return add(tv, params)
}

fun ViewGroupScope<*>.button(
    text: CharSequence = "",
    params: ViewGroup.LayoutParams = wrapContent(),
    build: Button.() -> Unit = {}
): Button {
    val b = Button(context).apply {
        this.text = text
        build()
    }
    return add(b, params)
}
