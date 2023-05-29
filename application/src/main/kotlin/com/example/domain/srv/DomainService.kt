package com.example.domain.srv

import com.example.domain.model.DomainModel

fun interface DomainService {

    fun handle(param: String): DomainModel

}