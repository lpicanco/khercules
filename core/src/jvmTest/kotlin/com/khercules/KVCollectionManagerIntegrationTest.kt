package com.khercules

import kotlin.test.assertEquals
import org.junit.Test

internal class KVCollectionManagerIntegrationTest {
    private val firstSegmentFirstValue = "firstSegmentFirstValue".padEnd(200, '0')
    private val firstSegmentLastValue = "firstSegmentLastValue".padEnd(200, '1')
    private val secondSegmentFirstValue = "secondSegmentFirstValue".padEnd(402, '2')
    private val thirdSegmentFirstValue = "thirdSegmentFirstValue".padEnd(200, '3')

    @Test
    fun testSegments() {
        val config = Config(
            databaseLocation = createTempDir().toPath().toString(),
            segmentSizeInBytes = 400
        )

        KVCollection(config).use { collection ->
            collection.put("0101", firstSegmentFirstValue.toByteArray())
            collection.put("0102", firstSegmentLastValue.toByteArray())
            collection.put("0201", secondSegmentFirstValue.toByteArray())
            collection.put("0301", thirdSegmentFirstValue.toByteArray())

            verifyCollection(collection)
        }

        // Tests reopening
        KVCollection(config).use { collection ->
            verifyCollection(collection)
        }
    }

    private fun verifyCollection(collection: KVCollection) {
        assertEquals(firstSegmentFirstValue, collection.get("0101")?.decodeToString())
        assertEquals(firstSegmentLastValue, collection.get("0102")?.decodeToString())
        assertEquals(secondSegmentFirstValue, collection.get("0201")?.decodeToString())
        assertEquals(thirdSegmentFirstValue, collection.get("0301")?.decodeToString())
    }
}
