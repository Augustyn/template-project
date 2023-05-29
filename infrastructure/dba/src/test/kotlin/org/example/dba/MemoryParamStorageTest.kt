package org.example.dba

import com.example.domain.model.DomainModel
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

private const val PARAMETER = "test param"

class MemoryParamStorageTest : StringSpec({
    val mockStorage = mutableListOf<StorageModel>()
    val tested = MemoryParamStorage(mockStorage)
    "Should store param" {
        // given:
        mockStorage.clear()
        // and:
        val storedObject = DomainModel(PARAMETER)
        // when:
        tested.store(storedObject).domainParam shouldBe PARAMETER
        // then:
        assertSoftly {
            mockStorage.size shouldBe 1
            mockStorage.first().param shouldBe PARAMETER
        }
    }

    "Should be able to retrieve last item" {
        // given:
        mockStorage.clear()
        // when:
        tested.store(DomainModel("first"))
        // and:
        tested.store(DomainModel("second"))
        // when:
        tested.retrieve().domainParam shouldBe "second"
    }
})
