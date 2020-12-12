package com.khercules

import java.io.Closeable
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

class Segment(private val file: File, writeMode: Boolean = false) : Closeable {
    private val fileChannel: FileChannel = if (writeMode) {
        FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
    } else {
        FileChannel.open(file.toPath(), StandardOpenOption.READ)
    }

    val id = file.nameWithoutExtension.toLong()
    var size: Long = fileChannel.size()
        private set

    val lastOffset get() = id + size

    fun makeWritable() = Segment(file, writeMode = true)

    fun write(key: String, value: ByteArray): Long {
        val keySize: Int = key.length
        val valueSize: Int = value.size

        val startPosition = fileChannel.position()
        val totalSize = keySize + valueSize + HEADER_SIZE
        val buffer = ByteBuffer.allocate(totalSize)
        buffer.putInt(keySize)
        buffer.putInt(valueSize)
        buffer.put(key.toByteArray())
        buffer.put(value)
        val bytesWritten = fileChannel.write(buffer.flip())
        size += bytesWritten
        return id + startPosition
    }

    fun readValue(absoluteOffset: Long): ByteArray {
        val position = absoluteOffset - id
        val header = readHeader(position)
        return readValue(position, header)
    }

    fun allKeys(): Sequence<Pair<String, Long>> {
        return sequence {
            var position = 0L
            while (position < size) {
                var header = readHeader(position)
                var key = readKey(position, header)
                yield(key.decodeToString() to id + position)

                position += HEADER_SIZE + header.keySize + header.valueSize
            }
        }
    }

    private fun readKey(position: Long, header: KVHeader): ByteArray {
        val keyBuffer = ByteBuffer.allocate(header.keySize)
        fileChannel.read(keyBuffer, position + HEADER_SIZE)
        return keyBuffer.array()
    }

    private fun readValue(position: Long, header: KVHeader): ByteArray {
        val valueBuffer = ByteBuffer.allocate(header.valueSize)
        fileChannel.read(valueBuffer, position + HEADER_SIZE + header.keySize)
        return valueBuffer.array()
    }

    private fun readHeader(position: Long): KVHeader {
        val headerBuffer = ByteBuffer.allocateDirect(HEADER_SIZE)
        fileChannel.read(headerBuffer, position)

        val keySize = headerBuffer.flip().int
        val valueSize = headerBuffer.int
        return KVHeader(keySize, valueSize)
    }

    override fun close() {
        fileChannel.close()
    }

    companion object {
        const val HEADER_SIZE = 8
    }
}
