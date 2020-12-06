package com.khercules

import com.khercules.io.Segment

class SegmentMerger(
    private val segmentManager: SegmentManager,
    private val index: MutableMap<String, CollectionEntry>,
    private val config: Config
) {

    fun execute() {
        val iterator = segmentManager.allInactiveSegments().iterator()
        // If the iterator is empty, return
        if (!iterator.hasNext()) return

        var previousSegment = iterator.next()

        iterator.forEachRemaining { currentSegment ->
            previousSegment = if (shouldMerge(previousSegment, currentSegment)) {
                merge(previousSegment, currentSegment)
            } else currentSegment
        }
    }

    private fun shouldMerge(previousSegment: Segment, currentSegment: Segment) =
        previousSegment.size + currentSegment.size <= config.segmentSizeInBytes * SEGMENT_SIZE_THRESHOLD

    private fun merge(previousSegment: Segment, currentSegment: Segment): Segment {
        val writableSegment = previousSegment.makeWritable()

        currentSegment.allKeys().forEach { key ->
            val newOffset = writableSegment.write(key.first, currentSegment.readValue(key.second))
            // update the map
            index[key.first] = CollectionEntry(newOffset, writableSegment.id)
        }
        writableSegment.close()
        segmentManager.removeSegment(currentSegment)
        return writableSegment
    }

    companion object {
        private const val SEGMENT_SIZE_THRESHOLD = 1.10 // 10%
    }
}
