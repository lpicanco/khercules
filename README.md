# khercules
A fast append-only log structured KV store in Kotlin

## Segment file format
All data are organized in segments

| name       | size in bytes |
| ---------- | ------------- |
| key_size   | 4             |
| value_size | 4             |
| key        | key_size      |
| value      | value_size    |

