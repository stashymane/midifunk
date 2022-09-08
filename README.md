# midifunk

![Version tag](https://img.shields.io/github/v/release/stashymane/midifunk?label=version&sort=semver&style=flat-square)

An object-based abstraction over the Java MIDI API using Kotlin Coroutines.

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

**NOTE**: `whileActive()` stops collecting MIDI events when the device is closed.  
If you don't use it, make sure you have a way to stop collecting.

### Opening device for reading inputs

```kotlin
Midifunk.descriptors[index].device.receive.whileActive()
    .collect { /* `from` automatically opens the device on its first subscription */ }
```

### Event filtering

```kotlin
device.receive.whileActive().filter { it is NoteData || it is ControlData }
    .collect { /* `it` is either a note event, or a CC event */ }
```

### Listening to a single event

```kotlin
device.receive.whileActive().mapNotNull { it as? NoteData }.collect { /* `it` is NoteData */ }
```

### Passing events to another device

```kotlin
//open is required for OUT devices
inDevice.receive.whileActive().onStart { outDevice.open() }.onCompletion { outDevice.close() }
    .collect { outDevice.send(it) }
```

## Contributing

Although not strictly, please try to adhere to the [standard Kotlin code style guidelines][1], more thoroughly defined
in JetBrains IDEs. Other than that, feel free to submit pull requests - I will gladly review them.

[1]: https://kotlinlang.org/docs/reference/coding-conventions.html
