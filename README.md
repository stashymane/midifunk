# midifunk

Wrapper library for receiving and manipulating MIDI events.  
**Currently in a pre-release stage, expect breaking changes until the first major release.**

## Features

* Type-safe MIDI events
* MIDI as reactive streams (via RxJava)
* Minimal code required for listening
* Easy input & ~~output~~ _(coming soon)_

## Examples

### Opening device for reading inputs

```kotlin
Midifunk.deviceInfos[0].device.let {
    it.open()
    it.from.subscribe { /* you got a midi event! */ }
}
```

### Listening for note on/off events only

```kotlin
device.from.filter { it is NoteData }.subscribe { /* you got a note on or off event! */ }
```

## Performance

Currently, Midifunk on average takes about 10 times longer to process MIDI inputs compared to pure Java. Here is a rough
sheet of how fast each take to process a certain amount of events on my machine (results via JMH):

| Benchmark             | Mode  | Cnt | Score   | Error    | Units  |
| --------------------- | ----- | --- | ------- | -------- | ------ |
| JavaBench.send        | thrpt | 10  | 214.944 | ± 4.341 |  ops/us |
| MidifunkBench.send    | thrpt | 10  | 24.501  | ± 0.390 |  ops/us |

My Launchkey Mini MK3 sends about 50 clock events per second on idle and about 300 events when actively twisting two CC
knobs, so the performance penalty is negligible.  
Regardless, I will still try to optimize the library more by the first major release.

## Contributing

Please follow [standard Kotlin code style guidelines][1], more thoroughly defined in JetBrains IDEs. Other than that,
feel free to submit pull requests - I will gladly review them.

[1]: https://kotlinlang.org/docs/reference/coding-conventions.html
