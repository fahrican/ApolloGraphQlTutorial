package com.example.apollographqltutorial.repository

import com.apollographql.apollo.ApolloClient
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.MainCoroutineRule
import com.example.apollographqltutorial.repository.BaseRepository.Companion.GENERAL_ERROR_CODE
import com.example.apollographqltutorial.repository.BaseRepository.Companion.SOMETHING_WRONG
import com.example.apollographqltutorial.util.ResponseFileReader
import com.example.apollographqltutorial.view.state.ViewState
import com.google.gson.Gson
import io.mockk.*
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
class CharacterRepositoryImplTest {

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

/*    @Test
    fun testTeachersAreDisplayed() {
        val reader = ResponseFileReader("characters_list_success.json")
        val gson = Gson()
        val expected: CharactersListQuery.Data =
            gson.fromJson(reader.content, CharactersListQuery.Data::class.java)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(SUCCESS_RESPONSE))
        }

        runBlocking {

            val mockData = mockk<ApolloQueryCall<CharactersListQuery.Data>>()

            mockkObject(RickAndMortyApi)

            val mockApi = mockk<ApolloClient>()

            every { RickAndMortyApi.getApolloClient() } returns mockApi

            coEvery { mockApi.query(CharactersListQuery())} returns mockData

            val actual: ViewState<CharactersListQuery.Data>? = objectUnderTest.queryCharactersList()
            //val actualResult = posts?.extractData

            coEvery { objectUnderTest.queryCharactersList() }

            assertEquals(expected, actual)
        }
    }*/

    @Test
    fun `given list of posts check for equality`() {
        val reader = ResponseFileReader("characters_list_success.json")
        val gson = Gson()
        val expected: CharactersListQuery.Data =
            gson.fromJson(reader.content, CharactersListQuery.Data::class.java)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(ResponseFileReader("characters_list_success.json").content))
        }

        runBlocking {

            coEvery { objectUnderTest.queryCharactersList() } returns ViewState.Success(expected)

            val posts: ViewState<CharactersListQuery.Data>? = objectUnderTest.queryCharactersList()
            val actualResult = posts?.extractData

            assertEquals(expected, posts)
        }
    }

}