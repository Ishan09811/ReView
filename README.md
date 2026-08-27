# ReView

A reactive UI framework for Android built on `android.view.View`.

## The idea

Compose's reactive model is coarse recomposition: when a State used
inside a `@Composable` changes, that function reruns and produces a new UI
description and the runtime diffs it against the last one to figure out
what actually changed on screen.

ReView's model is direct dependency tracking:

```kotlin
val count = stateOf(0)

textView.bindText { "Count: ${count.value}" }
```

Reading `count.value` inside `bindText { }` is the subscription -- no
diffing, no virtual tree, no recomposition. When `count.value` changes then only
this exact binding reruns, and it reruns by directly reassigning
`textView.text`. If a sibling `TextView` doesn't read `count` then it is
*never touched*, not even diffed and found equal so it's simply never rerun.

You get to write declaratively (`bindText { computeFromState() }`).

## Modules

- **`review-core`** -- the reactive engine (`State<T>`, `Effect`, dependency
  tracking). written in Kotlin, has zero Android dependencies.
- **`review-android`** -- the Android integration layer: `View.bindText { }`,
  `View.bindVisible { }`, etc., plus a builder style Kotlin DSL
  (`reviewRoot { }`, `linearLayout { }`, `textView { }`...) for constructing
  View trees without XML.
- **`review-material3`** -- optional Material3 widget support (Material
  Components for Android). Two independent halves, so you pull in only what
  you need:
  - `io.github.review.material3.bind` -- reactive bindings for Material 3 specific
    components like (`Slider.bindValue { }`, `Chip.bindChecked { }`,
    `TextInputLayout.bindError { }`, `LinearProgressIndicator.bindProgress { }`,
    `MaterialSwitch.bindChecked { }`) that plain `review-android` bindings
    don't cover. Depends on **`review-core` only** -- usable with Material3
    widgets you construct any other way (XML inflate, `findViewById`), with
    no dependency on `review-android` at all.
  - `io.github.review.material3.dsl` -- DSL builders (`materialButton { }`,
    `slider { }`, `chip { }`, `materialSwitch { }`,
    `linearProgressIndicator { }`, `textInputLayout { }`) that slot into a
    `reviewRoot { }` tree exactly like `review-android`'s own `textView { }`/
    `button { }`, plus `bindXxxAutoDispose { }` wrappers so bindings used
    inside the DSL get torn down automatically on View detach, same as
    `review-android`'s bindings do. This half depends on **both**
    `review-core` and `review-android`.
- **`app`** -- a small demo app (a counter) that exercises the whole stack
  end to end.

## Quick example

```kotlin
class MainActivity : AppCompatActivity() {
    private val count = stateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            reviewRoot(this) {
                applyInsetsAsPadding() // pads the root by system bars + cutout

                textView { bindText { "Count: ${count.value}" } }
                button("+1") { setOnClickListener { count.value++ } }
            }
        )
    }
}
```

`applyInsetsAsPadding()` is only meaningful once the window is actually
drawing edge to edge screen (even behind the system bars) so call
`WindowCompat.setDecorFitsSystemWindows(window, false)` in `onCreate` (or
target an SDK level where edge to edge is the default) for it to have
visible effect. It's a plain `View`/`ViewGroupScope` extension, so it also
works standalone outside the DSL: `someView.applyInsetsAsPadding()`.

## Using Material3

Add `review-material3` alongside `review-core` (and, optionally,
`review-android`) in your module's dependencies:

```kotlin
dependencies {
    implementation(project(":review-core"))       // always
    implementation(project(":review-android"))    // if you want the DSL/plain-View bindings
    implementation(project(":review-material3"))  // adds M3 widgets and bindings
}
```

**With just `review-core`** (no `review-android` DSL — e.g. you inflate
Material3 widgets from XML or build them by hand):

```kotlin
val volume = stateOf(50f)
val slider = findViewById<Slider>(R.id.volumeSlider)
val effect = slider.bindValue { volume.value } // returns Effect -- you dispose it
// ...
effect.dispose()
```

**Alongside both `review-core` and `review-android`** inside a `reviewRoot { }`
tree, disposal handled were handled:

```kotlin
reviewRoot(this) {
    slider(valueTo = 100f) {
        bindValueAutoDispose { volume.value } // disposed automatically on detach
    }
    linearProgressIndicator {
        max = 100
        bindProgressAutoDispose { volume.value.toInt() }
    }
}
```

## Status
its hard to provide the current status of this project right now as i am working on it regularly, adding stuffs, fixing thing etc.
but its fully usable in any app depending on the usage and must check the apis properly before using.

## Building

```bash
./gradlew :review-core:test   # run the reactive engine's unit tests
./gradlew :app:assembleDebug  # build the demo app
```
