package com.khercules

data class Config(
    val databaseLocation: String,
    /**
     * Segment size in bytes. Default: 1G
     */
    val segmentSizeInBytes: Long = 1024 * 1024 * 1024
)
