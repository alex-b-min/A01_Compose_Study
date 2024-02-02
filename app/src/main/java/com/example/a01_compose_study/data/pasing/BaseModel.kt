package com.example.a01_compose_study.data.pasing

abstract class BaseModel(var intention: String) {
    open var items = mutableListOf<Any>()
    override fun toString(): String {
        return "HashCode[${this.hashCode()}], Intention[$intention]"
    }
}