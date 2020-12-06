package com.khercules

import java.io.File
import kotlin.test.assertEquals
import org.junit.Test

class SegmentMergeIntegrationTest {
    private val neoKey = "Neo"
    private val trinityKey = "Trinity"
    private val morpheusKey = "Morpheus"
    private val firstSegmentNeoValue = "firstSegmentNeoValue".padEnd(50, '1')
    private val firstSegmentTrinityValue = "firstSegmentTrinityValue".padEnd(50, '3')
    private val secondSegmentNeoValue = "secondSegmentNeoValue".padEnd(40, '1')
    private val thirdSegmentTrinityValue = "thirdSegmentTrinityValue".padEnd(40, '3')
    private val activeSegmentMorpheusValue = "activeSegmentMorpheusValue".padEnd(30, '2')
    private val activeSegmentMorpheusFinalValue = "activeSegmentMorpheusFinalValue".padEnd(30, '2')

    @Test
    fun testMerge() {
        val config = Config(
            databaseLocation = createTempDir().toPath().toString(),
            segmentSizeInBytes = 100
        )

        KVCollection(config).use { collection ->
            collection.put(neoKey, firstSegmentNeoValue.toByteArray())
            collection.put(trinityKey, firstSegmentTrinityValue.toByteArray())
            collection.put(neoKey, secondSegmentNeoValue.toByteArray())
            collection.put(trinityKey, thirdSegmentTrinityValue.toByteArray())
            collection.put(morpheusKey, activeSegmentMorpheusValue.toByteArray())
            collection.put(morpheusKey, activeSegmentMorpheusFinalValue.toByteArray())

            collection.getSegmentCompactor().execute()
            collection.getSegmentMerger().execute()
            verifyCollection(collection)
        }

        verifyMerge(config)

        // Tests reopening
        KVCollection(config).use { collection ->
            verifyCollection(collection)
        }
    }

    // Neo and key should be compacted. Morpheus, shouldn't(active segment)
    private fun verifyMerge(config: Config) {
        File(config.databaseLocation).walk().filter { it.isFile }.forEach { println(it) }
        assertEquals(2, SegmentManager(config).allSegments().count())
    }

    private fun verifyCollection(collection: KVCollection) {
        assertEquals(secondSegmentNeoValue, collection.get(neoKey)?.decodeToString())
        assertEquals(thirdSegmentTrinityValue, collection.get(trinityKey)?.decodeToString())
        assertEquals(activeSegmentMorpheusFinalValue, collection.get(morpheusKey)?.decodeToString())
    }
}
