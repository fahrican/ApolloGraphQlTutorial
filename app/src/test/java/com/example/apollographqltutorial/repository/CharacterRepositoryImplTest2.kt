package com.example.apollographqltutorial.repository

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.MainCoroutineRule
import com.example.apollographqltutorial.repository.BaseRepository.Companion.GENERAL_ERROR_CODE
import com.example.apollographqltutorial.repository.BaseRepository.Companion.SOMETHING_WRONG
import com.example.apollographqltutorial.util.ResponseFileReader
import com.example.apollographqltutorial.view.state.ViewState
import com.google.gson.Gson
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CharacterRepositoryImplTest2 {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

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
        val expectedError = ViewState.Error(Exception(SOMETHING_WRONG))

        mockWebServer.apply {
            enqueue(
                MockResponse().setResponseCode(GENERAL_ERROR_CODE)
                    .setBody(ResponseFileReader("character_list_error.json").content)
            )
        }

        runBlocking {
            val actualResult = objectUnderTest.queryCharactersList()

            assertEquals(expectedError::class.java, actualResult!!::class.java)
        }
    }

    @Test
    fun `given response cahracters list when fetching results then return success`() {
        val reader = ResponseFileReader("characters_list_success.json")
        val gson = Gson()
        val expected: CharactersListQuery.Data =
            gson.fromJson(reader.content, CharactersListQuery.Data::class.java)


        mockWebServer.apply {
            enqueue(
                MockResponse().setResponseCode(200)
                    .setBody(ResponseFileReader("characters_list_success.json").content)
            )
        }

        runBlocking {

            var actual: CharactersListQuery.Data? = null

            mockApi.query(CharactersListQuery())
                .enqueue(object : ApolloCall.Callback<CharactersListQuery.Data>() {
                    override fun onResponse(response: Response<CharactersListQuery.Data>) {
                        actual = response.data
                        assertEquals(expected, actual)
                    }

                    override fun onFailure(e: ApolloException) {}
                })
        }
    }
}