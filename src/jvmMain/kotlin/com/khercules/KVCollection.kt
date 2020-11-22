package com.khercules

import java.io.Closeable

class KVCollection(config: Config) : Closeable {
    private val index = mutableMapOf<String, Long>()
    private val segmentManager: SegmentManager = SegmentManager(config)

    init {
        rebuildIndex()
    }

    fun put(key: String, document: ByteArray) {
        val offset = segmentManager.getActiveSegment().write(key, document)
        index[key] = offset
    }

    fun get(key: String): ByteArray? {
        return index[key]?.let { offset ->
            segmentManager.getSegmentForOffset(offset).use {
                it.readValue(offset)
            }
        }
    }

    private fun rebuildIndex() {
        segmentManager.allSegments().forEach { segment ->
            segment.allKeys().forEach { key: Pair<String, Long> ->
                index[key.first] = key.second
            }
        }
    }

    override fun close() {
        segmentManager.getActiveSegment().close()
    }
}
