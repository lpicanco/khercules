package com.khercules

class SegmentCompactor(
    private val segmentManager: SegmentManager,
    private val index: MutableMap<String, CollectionEntry>
) {

    fun execute() {
        segmentManager.allDirtySegments().forEach {
            compact(it)
        }
    }

    private fun compact(segment: Segment) {
        // create a new segment
        val compactedSegment = segmentManager.getCompactedSegment(segment)
        // for each segment key
        segment.allKeys().forEach { key ->
            // if the offset is correct, should write to compacted segment
            if (index.getValue(key.first).offset == key.second) {
                val newOffset = compactedSegment.write(key.first, segment.readValue(key.second))
                // update the map
                index[key.first] = CollectionEntry(newOffset, compactedSegment.id)
            }
        }
        compactedSegment.close()
        segmentManager.promoteCompactedSegment(compactedSegment)
    }
}
