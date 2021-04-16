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
implementation("dev.stashy.midifunk", "midifunk", "0.3.0")
```

Gradle

```groovy
implementation group: 'dev.stashy.midifunk', name: 'midifunk', version: '0.3.0'
```

## Examples

### Opening device for reading inputs

```kotlin
Midifunk.descriptors[0].device.let {
    it.from.subscribe { /* you got a midi event! */ }
}
```

### Listening for note on/off events only

```kotlin
device.from.filter { it is NoteData }.subscribe { /* you got a note on or off event! */ }
```

### Passing events to another device

```kotlin
//open is required for OUT devices
//only IN devices automatically open on first subscription
outDevice.open()
inDevice.from.subscribe { outDevice.to(it) }
```

## Contributing

Please follow [standard Kotlin code style guidelines][1], more thoroughly defined in JetBrains IDEs. Other than that,
feel free to submit pull requests - I will gladly review them.

[1]: https://kotlinlang.org/docs/reference/coding-conventions.html
