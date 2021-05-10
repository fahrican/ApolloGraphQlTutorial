package com.example.apollographqltutorial.util

import java.io.InputStreamReader

class ResponseFileReader(path: String) {

    val content: String

    init {
        val reader = InputStreamReader(this.javaClass.classLoader!!.getResourceAsStream(path))
        content = reader.readText()
        reader.close()
    }

}