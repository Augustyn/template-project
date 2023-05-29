package com.example.domain.srv.impl

import com.example.domain.model.DomainModel
import com.example.domain.storage.ParamStorage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.match
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DefaultDomainServiceTest : StringSpec({
    val storage: ParamStorage = mockk()

    val tested = DefaultDomainService(storage)

    "Should enhance the input parameter" {
        // given:
        val inputArgument = "aaa"
        val resultRegex = "^$inputArgument.{10}$"
        // and given response result
        val response = DomainModel("storage returned result")
        every {
            storage.store(match { arg -> arg.domainParam.matches(resultRegex.toRegex()) })
        } returns response
        // when:
        tested.handle("aaa") shouldBe response
        verify(exactly = 1) {
            storage.store(withArg {
                match(resultRegex)
            })
        }
    }

})
