package com.example.apollographqltutorial.util

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class ResponseFileReaderTest {

    @Test
    fun `given simple json file then check for matching mock property`() {
        val reader = ResponseFileReader("test_file_reader.json")

        val gson = Gson()
        val expected: TestWord = gson.fromJson(reader.content, TestWord::class.java)

        assertEquals("success", expected.word)
    }

}
