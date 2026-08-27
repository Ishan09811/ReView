package io.github.review.material3.dsl

import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.github.review.android.bind.registerReviewBinding
import io.github.review.android.dsl.ViewGroupScope
import io.github.review.android.dsl.wrapContent
import io.github.review.core.Effect
import io.github.review.material3.bind.bindChecked as bindCheckedCore
import io.github.review.material3.bind.bindError as bindErrorCore
import io.github.review.material3.bind.bindLoadingState as bindLoadingStateCore
import io.github.review.material3.bind.bindProgress as bindProgressCore
import io.github.review.material3.bind.bindValue as bindValueCore


fun ViewGroupScope<*>.materialButton(
    text: CharSequence = "",
    params: ViewGroup.LayoutParams = wrapContent(),
    build: MaterialButton.() -> Unit = {}
): MaterialButton {
    val b = MaterialButton(context).apply {
        this.text = text
        build()
    }
    return add(b, params)
}

fun ViewGroupScope<*>.chip(
    text: CharSequence = "",
    params: ViewGroup.LayoutParams = wrapContent(),
    build: Chip.() -> Unit = {}
): Chip {
    val c = Chip(context).apply {
        this.text = text
        build()
    }
    return add(c, params)
}

fun ViewGroupScope<*>.materialSwitch(
    text: CharSequence = "",
    params: ViewGroup.LayoutParams = wrapContent(),
    build: MaterialSwitch.() -> Unit = {}
): MaterialSwitch {
    val s = MaterialSwitch(context).apply {
        this.text = text
        build()
    }
    return add(s, params)
}

fun ViewGroupScope<*>.slider(
    valueFrom: Float = 0f,
    valueTo: Float = 100f,
    params: ViewGroup.LayoutParams = wrapContent(),
    build: Slider.() -> Unit = {}
): Slider {
    val s = Slider(context).apply {
        this.valueFrom = valueFrom
        this.valueTo = valueTo
        build()
    }
    return add(s, params)
}

fun ViewGroupScope<*>.linearProgressIndicator(
    params: ViewGroup.LayoutParams = wrapContent(),
    build: LinearProgressIndicator.() -> Unit = {}
): LinearProgressIndicator {
    val p = LinearProgressIndicator(context).apply(build)
    return add(p, params)
}

/**
 * Builds a [TextInputLayout] wrapping a [TextInputEditText], the standard
 * Material3 text field pairing. [build] configures the outer TextInputLayout
 * (hint, error, box style, etc.); [editText] configures the inner
 * TextInputEditText (input type, listeners, etc.).
 */
fun ViewGroupScope<*>.textInputLayout(
    hint: CharSequence? = null,
    params: ViewGroup.LayoutParams = wrapContent(),
    editText: EditText.() -> Unit = {},
    build: TextInputLayout.() -> Unit = {}
): TextInputLayout {
    val layout = TextInputLayout(context).apply {
        this.hint = hint
        val input = TextInputEditText(context).apply(editText)
        addView(input)
        build()
    }
    return add(layout, params)
}

/**
 * Registers [effect] for disposal-on-detach the same way review-android's
 * own `bindXxx` functions do. Use this when writing a `bindXxx`-style call
 * *inside* a widget's `build: X.() -> Unit` DSL block so it participates in
 * `reviewRoot { }`'s automatic cleanup instead of leaking:
 * ```
 * slider {
 *     registerForDisposal(bindValue { volume.value })
 * }
 * ```
 * `io.github.review.material3.bind`'s functions return their [Effect] precisely so
 * you can pass it here when using them inside the DSL.
 */
fun <T> T.registerForDisposal(effect: Effect): Effect where T : android.view.View {
    registerReviewBinding(effect)
    return effect
}

/**
 * Auto-disposing convenience wrappers for the common case: calling one of
 * `io.github.review.material3.bind`'s `bindXxx` functions *inside* a DSL `build`
 * block where you want it to torn down automatically on detach rather than
 * wrapping every call in `registerForDisposal(...)` by hand:
 * ```
 * slider { bindValueAutoDispose { volume.value } }
 * // instead of:
 * slider { registerForDisposal(bindValue { volume.value }) }
 * ```
 * Prefer the plain `bindXxx` functions from `io.github.review.material3.bind`
 * instead when using a widget outside the DSL (XML inflate, findViewById)
 * there, you're responsible for disposal, so you need the returned [Effect].
 */
fun Slider.bindValueAutoDispose(compute: () -> Float) {
    registerForDisposal(bindValueCore(compute))
}

fun Chip.bindCheckedAutoDispose(compute: () -> Boolean) {
    registerForDisposal(bindCheckedCore(compute))
}

fun MaterialSwitch.bindCheckedAutoDispose(compute: () -> Boolean) {
    registerForDisposal(bindCheckedCore(compute))
}

fun TextInputLayout.bindErrorAutoDispose(compute: () -> CharSequence?) {
    registerForDisposal(bindErrorCore(compute))
}

fun LinearProgressIndicator.bindProgressAutoDispose(compute: () -> Int) {
    registerForDisposal(bindProgressCore(compute))
}

fun MaterialButton.bindLoadingStateAutoDispose(
    isLoading: () -> Boolean,
    idleText: CharSequence,
    loadingText: CharSequence
) {
    registerForDisposal(bindLoadingStateCore(isLoading, idleText, loadingText))
}
