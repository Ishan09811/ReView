package io.github.review.android.dsl

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Applies system bar + display cutout insets as padding on this View, edge-to-edge
 * style. Thin wrapper around [ViewCompat.setOnApplyWindowInsetsListener] so it can
 * be chained inside a DSL `build` block like any other View setup call:
 *
 * ```
 * reviewRoot(this) {
 *     applyInsetsAsPadding()
 *     textView { bindText { "Hello, ${name.value}" } }
 * }
 * ```
 *
 * Returns the View itself for chaining. Note this *sets* the padding based on
 * insets so if you also set your own padding elsewhere on the same View, apply
 * it before calling this (or account for it inside a custom listener instead),
 * since this call's padding assignment wins.
 */
fun <V : View> V.applyInsetsAsPadding(): V {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )

        view.updatePadding(
            left = insets.left,
            top = insets.top,
            right = insets.right,
            bottom = insets.bottom
        )

        windowInsets
    }
    return this
}

/**
 * Convenience for calling [applyInsetsAsPadding] on the group being built,
 * directly inside a `reviewRoot { }` / `linearLayout { }` block, matching the
 * style of the other DSL calls (no need to reach for `.group` explicitly):
 *
 * ```
 * reviewRoot(this) {
 *     applyInsetsAsPadding()
 *     textView { ... }
 * }
 * ```
 */
fun ViewGroupScope<*>.applyInsetsAsPadding() {
    group.applyInsetsAsPadding()
}