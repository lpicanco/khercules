package com.khercules

import kotlin.test.assertEquals
import org.junit.Test

internal class KVCollectionManagerIntegrationTest {
    @Test
    fun testSegments() {
        val config = Config(
            databaseLocation = createTempDir().toPath().toString(),
            segmentSizeInBytes = 400
        )
        val firstSegmentFirstValue = "firstSegmentFirstValue".padEnd(200, '0')
        val firstSegmentLastValue = "firstSegmentLastValue".padEnd(200, '1')
        val secondSegmentFirstValue = "secondSegmentFirstValue".padEnd(402, '2')
        val thirdSegmentFirstValue = "thirdSegmentFirstValue".padEnd(200, '3')

        KVCollection(config).use { collection ->
            collection.put("0101", firstSegmentFirstValue.toByteArray())
            collection.put("0102", firstSegmentLastValue.toByteArray())
            collection.put("0201", secondSegmentFirstValue.toByteArray())
            collection.put("0301", thirdSegmentFirstValue.toByteArray())

            assertEquals(firstSegmentFirstValue, collection.get("0101")?.decodeToString())
            assertEquals(firstSegmentLastValue, collection.get("0102")?.decodeToString())
            assertEquals(secondSegmentFirstValue, collection.get("0201")?.decodeToString())
            assertEquals(thirdSegmentFirstValue, collection.get("0301")?.decodeToString())
        }

        // Tests open/close
        KVCollection(config).use { collection ->
            assertEquals(firstSegmentFirstValue, collection.get("0101")?.decodeToString())
            assertEquals(firstSegmentLastValue, collection.get("0102")?.decodeToString())
            assertEquals(secondSegmentFirstValue, collection.get("0201")?.decodeToString())
            assertEquals(thirdSegmentFirstValue, collection.get("0301")?.decodeToString())
        }
    }
}
