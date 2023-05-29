package com.example.infra.rest.controller

import com.example.domain.srv.DomainService
import com.example.infra.rest.adapter.toRestResponseModel
import com.example.infra.rest.model.ResponseModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BlogController(private val domainService: DomainService) {

    @GetMapping("/{param}")
    fun blog(@PathVariable("param") param: String): ResponseModel {
        return ResponseModel(responseParam = domainService.handle(param).toRestResponseModel())
    }

}


