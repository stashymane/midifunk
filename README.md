# midifunk

![Version tag](https://img.shields.io/github/v/release/stashymane/midifunk?label=version&sort=semver&style=flat-square)

An object- and coroutine-based abstraction over the Java MIDI API using Kotlin Coroutines & Flows.

Note: this is still in a pre-release stage, expect breaking changes until the first stable release.
This README reflects the current development branch, not the released version.

## Features

* Type-safe MIDI events
* MIDI as consumable flows
* Minimal code required for listening
* Easy input & output

## Usage

Gradle Kotlin

```kotlin
implementation("dev.stashy.midifunk", "midifunk", "x.x.x")
```

Gradle

```groovy
implementation group: 'dev.stashy.midifunk', name: 'midifunk', version: 'x.x.x'
```

## Examples

### Opening device for reading inputs

```kotlin
val device = Midifunk.descriptors[index].device as InputDevice
device.input.open().onEach { println(it) }.launchIn(coroutineScope)
```

### Opening output channel

```kotlin
val device = Midifunk.descriptors[index].device as OutputDevice
val channel: SendChannel<MidiEvent> = device.output.open()
channel.trySend(event)
```

### Creating type-safe MIDI events with DSL

```kotlin
NoteEvent.create {
    noteStatus = true
    note = Note.C(4)
    velocity = 127u
}
```

## Contributing

Although not strictly, please try to adhere to the [standard Kotlin code style guidelines][1], more thoroughly defined
in JetBrains IDEs. Other than that, feel free to submit pull requests - I will gladly review them.

[1]: https://kotlinlang.org/docs/reference/coding-conventions.html
