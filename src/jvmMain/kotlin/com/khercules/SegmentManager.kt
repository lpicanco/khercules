package com.khercules

import com.khercules.io.Segment
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class SegmentManager(private val config: Config) {
    private var directoryFiles: Sequence<File>
    private var activeSegment: Segment

    init {
        Files.createDirectories(Path.of(config.databaseLocation))
        directoryFiles = File(config.databaseLocation).walk()
            .filter { it.isFile }
            .sortedByDescending { it.name }
        activeSegment = loadLastSegment()
    }

    fun allSegments(): Sequence<Segment> {
        return directoryFiles.sortedBy { it.name }
            .map { Segment(it) }
    }

    fun getActiveSegment(): Segment {
        if (activeSegment.size >= DEFAULT_MAX_SEGMENT_SIZE_IN_BYTES) {
            activeSegment = Segment(createSegmentFile(activeSegment.lastOffset + 1), writeMode = true)
        }

        return activeSegment
    }

    private fun loadLastSegment(): Segment {
        val file = directoryFiles.firstOrNull() ?: createSegmentFile(0)
        return Segment(file, writeMode = true)
    }

    private fun createSegmentFile(offset: Long) =
        Path.of(config.databaseLocation).resolve(formatFileName(offset)).toFile()

    private fun formatFileName(number: Long) = number.toString().padStart(10, '0') + FILE_EXTENSION

    fun getSegmentForOffset(offset: Long): Segment {
        val fileNameForOffset = formatFileName(offset)
        return Segment(directoryFiles.first { it.name <= fileNameForOffset })
    }

    companion object {
        const val DEFAULT_MAX_SEGMENT_SIZE_IN_BYTES = 400
        const val FILE_EXTENSION = ".khs"
    }
}
