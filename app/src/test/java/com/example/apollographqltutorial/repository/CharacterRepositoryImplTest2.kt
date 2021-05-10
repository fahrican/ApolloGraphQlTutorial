package com.example.apollographqltutorial.repository

import com.apollographql.apollo.ApolloClient
import com.example.apollographqltutorial.repository.BaseRepository.Companion.GENERAL_ERROR_CODE
import com.example.apollographqltutorial.repository.BaseRepository.Companion.SOMETHING_WRONG
import com.example.apollographqltutorial.view.state.ViewState
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CharacterRepositoryImplTest2 {

    private val mockWebServer = MockWebServer()

    private lateinit var mockApi: ApolloClient

    private lateinit var objectUnderTest: CharacterRepository


    @Before
    fun setUp() {
        mockWebServer.start(8080)

        mockApi = ApolloClient.builder()
            .serverUrl(mockWebServer.url("/"))
            .build()

        objectUnderTest = CharacterRepositoryImpl(mockApi)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `given response failure when fetching results then return exception`() {
        mockWebServer.apply {
            enqueue(MockResponse().setResponseCode(GENERAL_ERROR_CODE))
        }

        runBlocking {
            val apiResponse = objectUnderTest.queryCharactersList()

            assertNotNull(apiResponse)
            val expectedValue = ViewState.Error(Exception(SOMETHING_WRONG))
            assertEquals(
                expectedValue.exception.message,
                (apiResponse as ViewState.Error).exception.message
            )
        }
    }

}