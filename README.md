# khercules
A fast append-only KV store


## Segment file format
All data are organized in segments

| name       | size in bytes |
| ---------- | ------------- |
| key_size   | 2             |
| key        | key_size      |
| value_size | 4             |
| value      | value_size    |

## Index file format
| name        | size in bytes |
| ----------- | ------------- |
| key_size    | 2             |
| key         | key_size      |
| file_offset | 4             |

