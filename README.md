# midifunk

Wrapper library for receiving and manipulating MIDI events.  
**Currently in a pre-release stage, expect breaking changes until the first major release.**

## Features

* Type-safe MIDI events
* MIDI as reactive streams (via RxJava)
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
Midifunk.descriptors[index].device.from.subscribe { /* `from` automatically opens the device on its first subscription */ }
```

### Event filtering

```kotlin
device.from.filter { it is NoteData || it is ControlData }.subscribe { /* `it` is either a note event, or a CC event */ }
```

### Listening to a single event

```kotlin
device.from.mapOptional { Optional.ofNullable(it as? NoteData) }?.subscribe { /* `it` is NoteData */ }
```

### Passing events to another device

```kotlin
//open is required for OUT devices
outDevice.open()
inDevice.from.subscribe { outDevice.to(it) }
```

## Contributing

Please follow [standard Kotlin code style guidelines][1], more thoroughly defined in JetBrains IDEs. Other than that,
feel free to submit pull requests - I will gladly review them.

[1]: https://kotlinlang.org/docs/reference/coding-conventions.html
