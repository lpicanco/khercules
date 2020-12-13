package com.khercules

import java.io.Closeable
import java.util.Timer

class CollectionEntry(val offset: Long, val segmentId: Long)

class KVCollection(private val config: Config) : Closeable {
    private val index = mutableMapOf<String, CollectionEntry>()
    private val segmentManager: SegmentManager = SegmentManager(config)
    private val scheduledTasksTimer: Timer

    init {
        rebuildIndex()
        scheduledTasksTimer = scheduleTasks()
    }

    fun put(key: String, document: ByteArray) {
        index[key]?.run {
            segmentManager.markSegmentIdAsDirty(segmentId)
        }
        val segment = segmentManager.getActiveSegment()
        val offset = segment.write(key, document)
        index[key] = CollectionEntry(offset, segment.id)
    }

    fun get(key: String): ByteArray? {
        return index[key]?.let { entry ->
            segmentManager.getSegmentById(entry.segmentId).use {
                it.readValue(entry.offset)
            }
        }
    }

    fun getSegmentCompactor() = SegmentCompactor(segmentManager, index)

    fun getSegmentMerger() = SegmentMerger(segmentManager, index, config)

    private fun rebuildIndex() {
        segmentManager.allSegments().forEach { segment ->
            segment.allKeys().forEach { key: Pair<String, Long> ->
                index[key.first]?.run {
                    segmentManager.markSegmentIdAsDirty(segment.id)
                }
                index[key.first] = CollectionEntry(key.second, segment.id)
            }
        }
    }

    private fun scheduleTasks(): Timer {
        return kotlin.concurrent.fixedRateTimer(period = config.compactionRunIntervalInSeconds * 1000) {
            getSegmentCompactor().execute()
            getSegmentMerger().execute()
        }
    }

    override fun close() {
        scheduledTasksTimer.cancel()
        segmentManager.getActiveSegment().close()
    }
}
