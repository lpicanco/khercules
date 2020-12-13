[![Kotlin](https://img.shields.io/badge/Kotlin-1.4.10-orange.svg) ](https://kotlinlang.org/)
[![Java 11](https://img.shields.io/badge/Java-11-blue.svg) ](https://openjdk.java.net/)

# khercules
A fast append-only log structured KV store library in Kotlin

## Segment file format
All data are organized in segments

| name       | size in bytes |
| ---------- | ------------- |
| key_size   | 4             |
| value_size | 4             |
| key        | key_size      |
| value      | value_size    |

Periodically a process of compacting and merging segments is performed, removing old data. 

## Index
The index is stored in a Hash table in RAM

## CLI
Khercules comes with a simple CLI:
[![asciicast](https://asciinema.org/a/378996.svg)](https://asciinema.org/a/378996)

### How to run the CLI

```bash
./gradlew -q cli:run --args=/database --console=plain
```
where `/database` is the directory where the segment files will be written.


## Requirements

- Java 11
