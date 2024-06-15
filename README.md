# midifunk

![Version tag](https://img.shields.io/maven-central/v/dev.stashy.midifunk/midifunk?logo=apachemaven&label=Maven%20Central&color=%23339AF0)

Kotlin Multiplatform MIDI library that just makes sense.  
Currently focused on live MIDI I/O.

Note: this is still in an experimental stage, expect breaking changes until the first stable release.

## Features

* Type-safe MIDI events with the `events` module
* MIDI as consumable channels with Coroutines
* Minimal code required for listening
* Easy input & output

## Platforms

| Platform | Events | Devices                               |
|:---------|:-------|:--------------------------------------|
| JVM      | ✅️     | ✅️                                    |
| Windows  | ✅️     | ✖️ (waiting on Windows Midi Services) |
| Linux    | ✅️     | ✖️ (after Windows)                    |
| MacOS    | ✅️     | ✖️ (no device for testing available)  |

## Usage

Version catalog

```toml
[versions]
midifunk = "[version]"

[libraries]
midifunk-events = { module = "dev.stashy.midifunk:events", version.ref = "midifunk" }
midifunk-devices = { module = "dev.stashy.midifunk:devices", version.ref = "midifunk" }
```

Gradle Kotlin

```kotlin
implementation("dev.stashy.midifunk", "events", "version")
implementation("dev.stashy.midifunk", "devices", "version")
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
val channel: SendChannel<MidiData> = device.output.open(coroutineScope)
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
