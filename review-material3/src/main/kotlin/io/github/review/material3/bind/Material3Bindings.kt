package io.github.review.material3.bind

import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout
import io.github.review.core.Effect
import io.github.review.core.bind as coreBind

/**
 * Binds a [Slider]'s value to a reactive computation. Note this is one-way
 * (State -> Slider); if the user drags the slider that doesn't write back
 * to your State automatically instead you need to add your own `addOnChangeListener { _, v, fromUser -> ... }`
 * for that same as you would with plain Compose/View interop.
 */
fun Slider.bindValue(compute: () -> Float): Effect = coreBind { value = compute() }

/** Binds a [Chip]'s checked state to a reactive boolean. One-way, same caveat as [Slider.bindValue]. */
fun Chip.bindChecked(compute: () -> Boolean): Effect = coreBind { isChecked = compute() }

/** Binds a [MaterialSwitch]'s checked state to a reactive boolean. One-way, same caveat as [Slider.bindValue]. */
fun MaterialSwitch.bindChecked(compute: () -> Boolean): Effect = coreBind { isChecked = compute() }

/**
 * Binds a [TextInputLayout]'s error text to a reactive computation. A `null`
 * result clears the error and hides the error UI via `isErrorEnabled = false`;
 * a non-null result shows it.
 */
fun TextInputLayout.bindError(compute: () -> CharSequence?): Effect = coreBind {
    val message = compute()
    isErrorEnabled = message != null
    error = message
}

/** Binds a [LinearProgressIndicator]'s progress to a reactive Int (0..max). */
fun LinearProgressIndicator.bindProgress(compute: () -> Int): Effect = coreBind { progress = compute() }

/**
 * Binds a [MaterialButton]'s enabled state and text together in one Effect,
 * for the common "disable + relabel while loading" pattern:
 * ```
 * submitButton.bindLoadingState(
 *     isLoading = { isSubmitting.value },
 *     idleText = "Submit",
 *     loadingText = "Submitting..."
 * )
 * ```
 * For anything more custom than this, prefer composing `View.bindEnabled { }`
 * (from review-android) with `TextView.bindText { }` directly instead.
 */
fun MaterialButton.bindLoadingState(
    isLoading: () -> Boolean,
    idleText: CharSequence,
    loadingText: CharSequence
): Effect = coreBind {
    val loading = isLoading()
    isEnabled = !loading
    text = if (loading) loadingText else idleText
}
