package com.example.infra.rest.adapter

import com.example.domain.model.DomainModel

fun DomainModel.toRestResponseModel(): String {
    return domainParam
}