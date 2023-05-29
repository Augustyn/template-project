package com.example.infra.rest.controller

import com.example.domain.model.DomainModel
import com.example.domain.srv.DomainService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

private const val returnedString = "test result"
private const val testParam = "param passed to test"

class BlogControllerTest : StringSpec({

    val domainService: DomainService = mockk()

    val tested = BlogController(domainService)

    "Should call a backend service" {
        // when:
        every { domainService.handle(testParam) } returns DomainModel(returnedString)
        // when:
        val response = tested.blog(testParam)
        // then:
        with(response) {
            title shouldBe "blog title"
            responseParam shouldBe returnedString
        }
    }
})
