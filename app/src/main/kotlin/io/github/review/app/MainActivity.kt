package io.github.review.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.review.android.bind.bindText
import io.github.review.android.bind.bindVisible
import io.github.review.android.dsl.applyInsetsAsPadding
import io.github.review.android.dsl.button
import io.github.review.android.dsl.matchWidthWrapHeight
import io.github.review.android.dsl.reviewRoot
import io.github.review.android.dsl.textView
import io.github.review.core.stateOf
import io.github.review.material3.dsl.bindProgressAutoDispose
import io.github.review.material3.dsl.bindValueAutoDispose
import io.github.review.material3.dsl.linearProgressIndicator
import io.github.review.material3.dsl.slider

class MainActivity : AppCompatActivity() {

    private val count = stateOf(0)
    private val maxCount = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            reviewRoot(this) {
                applyInsetsAsPadding()

                textView(params = matchWidthWrapHeight()) {
                    textSize = 18f
                    setPadding(32, 32, 32, 16)
                    bindText { "Count: ${count.value}" }
                }

                textView(params = matchWidthWrapHeight()) {
                    textSize = 14f
                    setPadding(32, 0, 32, 16)
                    bindText {
                        when {
                            count.value == 0 -> "Nothing counted yet."
                            count.value % 2 == 0 -> "${count.value} is even."
                            else -> "${count.value} is odd."
                        }
                    }
                }

                textView(text = "This label never reads `count`, so it is never touched by count changes.") {
                    setPadding(32, 0, 32, 24)
                    alpha = 0.6f
                }

                slider(valueFrom = 0f, valueTo = maxCount.toFloat(), params = matchWidthWrapHeight()) {
                    setPadding(16, 0, 16, 0)
                    bindValueAutoDispose { count.value.toFloat().coerceIn(valueFrom, valueTo) }
                }

                linearProgressIndicator(params = matchWidthWrapHeight()) {
                    setPadding(32, 0, 32, 16)
                    max = maxCount
                    bindProgressAutoDispose { count.value.coerceIn(0, maxCount) }
                }

                button("Increment") {
                    setPadding(32, 16, 32, 16)
                    setOnClickListener { count.value = (count.value + 1).coerceAtMost(maxCount) }
                }

                button("Reset") {
                    setPadding(32, 16, 32, 16)
                    bindVisible { count.value > 0 }
                    setOnClickListener { count.value = 0 }
                }
            }
        )
    }
}