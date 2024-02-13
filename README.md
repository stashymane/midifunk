# midifunk

![Version tag](https://img.shields.io/github/v/release/stashymane/midifunk?label=version&sort=semver&style=flat-square)

An object- and coroutine-based abstraction over the Java MIDI API using Kotlin coroutines & channels.

Note: this is still in an experimental stage, expect breaking changes until the first stable release.
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
val device = MidiDevice.list()[0]
device.input.open(coroutineScope).onEach { println(it) }
```

### Opening output channel

```kotlin
val device = MidiDevice.list()[0]
val channel: SendChannel<MidiData> = device.output.open()
channel.send(event)
```

### Creating type-safe MIDI events with DSL

```kotlin
NoteEvent.create {
    noteStatus = true
    note = Note.C(4)
    velocity = 127u
}
```

## Roadmap

Top priority at the moment is to finalize the device input/output API.
The eventual goal is to be able to completely rip out the Java MIDI backend & use something else, which will also
provide multiplatform support.

After the device API, virtual device support is going to be prioritized.
This will most likely require separate backends for each platform to be implemented already.

## Contributing

Although not strictly, please try to adhere to the [standard Kotlin code style guidelines][1], more thoroughly defined
in JetBrains IDEs. Other than that, feel free to submit pull requests - I will gladly review them.

[1]: https://kotlinlang.org/docs/reference/coding-conventions.html
