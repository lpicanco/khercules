package com.khercules

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class SegmentManager(private val config: Config) {
    private var directoryFiles: Sequence<File>
    private val dirtySegmentsIds: MutableSet<Long> = mutableSetOf()
    private var lastSegment: Segment

    init {
        Files.createDirectories(Path.of(config.databaseLocation))
        directoryFiles = File(config.databaseLocation).walk()
            .filter { it.isFile }
            .sortedByDescending { it.name }
        lastSegment = loadLastSegment()
    }

    fun allSegments(): Sequence<Segment> {
        return directoryFiles.sortedBy { it.name }
            .map { Segment(it) }
    }

    fun allInactiveSegments(): Sequence<Segment> {
        return allSegments().filter { it.id != lastSegment.id }
    }

    fun allDirtySegments(): Sequence<Segment> {
        return dirtySegmentsIds.sorted().asSequence()
                .filter { it != lastSegment.id }
                .map { getSegmentById(it) }
    }

    fun getActiveSegment(): Segment {
        if (lastSegment.size >= config.segmentSizeInBytes) {
            lastSegment = Segment(getSegmentFileById(lastSegment.lastOffset + 1), writeMode = true)
        }

        return lastSegment
    }

    fun markSegmentIdAsDirty(id: Long) {
        dirtySegmentsIds.add(id)
    }

    fun getSegmentById(id: Long): Segment {
        return Segment(getSegmentFileById(id))
    }

    fun getCompactedSegment(originalSegment: Segment): Segment {
        return Segment(getSegmentFileById(originalSegment.id, COMPACTED_SEGMENT_FILE_EXTENSION), writeMode = true)
    }

    fun promoteCompactedSegment(compactedSegment: Segment) {
        val dataSegmentFile = getSegmentFileById(compactedSegment.id)
        val backupSegmentFile = getSegmentFileById(compactedSegment.id, BACKUP_DATA_SEGMENT_FILE_EXTENSION)
        val compactedSegmentFile = getSegmentFileById(compactedSegment.id, COMPACTED_SEGMENT_FILE_EXTENSION)

        dataSegmentFile.renameTo(backupSegmentFile)
        compactedSegmentFile.renameTo(dataSegmentFile)
        backupSegmentFile.delete()
    }

    fun removeSegment(segment: Segment) {
        getSegmentFileById(segment.id).delete()
    }

    private fun loadLastSegment(): Segment {
        val file = directoryFiles.firstOrNull() ?: getSegmentFileById(0)
        return Segment(file, writeMode = true)
    }
    private fun getSegmentFileById(id: Long, segmentType: String = DATA_SEGMENT_FILE_EXTENSION) =
            Path.of(config.databaseLocation).resolve(formatFileName(id, segmentType)).toFile()
    private fun formatFileName(number: Long, segmentType: String) = number.toString().padStart(10, '0') + segmentType

    companion object {
        const val DATA_SEGMENT_FILE_EXTENSION = ".khs"
        const val COMPACTED_SEGMENT_FILE_EXTENSION = ".khsc"
        const val BACKUP_DATA_SEGMENT_FILE_EXTENSION = ".khsb"
    }
}
