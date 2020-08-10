# midifunk

Wrapper library for receiving and manipulating MIDI events.  
**Currently in a pre-release stage, expect breaking changes until the first major release.**

## Features
* Type-safe MIDI events
* Event filtering and modification (in the order they are defined)
* Stream-like API

## Examples

### Opening device for reading inputs
```kotlin
val device = Device(MidiSystem.getMidiDeviceInfo()[1])
device.receivers += EventReceiver().addAction { println("Midi event received") }
```

### Listening for note on/off events only
```kotlin
device.receivers += EventReceiver()
    .filter { it is NoteData }
    .addAction {
        val e = it as NoteData
        println("Note: ${e.note} ${if (it.noteStatus) "on" else "off"} | Velocity: ${it.velocity}")
    }
```

### Modifying a CC event to be an on/off switch
```kotlin
var lastValue = -1
device.receivers += EventReceiver()
    .filter { it is ControlData }
    .modify {
        val e = it as ControlData
        if (e.value < 64)
            e.value = 0
        else
            e.value = 127
        it
    }
    .filter { (it as ControlData).value != lastValue }
    .addAction {
        val e = it as ControlData
        lastValue = e.value
        println("CC ${e.control} ${e.value == 127}")
    }
```

## Performance
Currently, Midifunk on average takes about 5-7 times longer to process MIDI inputs compared to pure Java.
Here is a rough sheet of how fast each take to process a certain amount of events on my machine (results are according to `TimeTest.kt`):

Events      | Java      | Midifunk
------      | ----      | --------
100         | 1ms       | 6ms
100000      | 16ms      | 51ms
100000000   | 821ms     | 5460ms

My Launchkey Mini MK3 sends about 50 clock events per second on idle and about 300 events when actively twisting two CC knobs, so the performance penalty is somewhat negligible.  
Regardless, I will still try to optimize the library more by the first major release.


## Contributing
Please follow [standard Kotlin code style guidelines][1], more thoroughly defined in JetBrains IDEs.
Other than that, feel free to submit pull requests - I will gladly review them.

[1]: https://kotlinlang.org/docs/reference/coding-conventions.html
