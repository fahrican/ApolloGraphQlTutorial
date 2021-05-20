package com.example.apollographqltutorial.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.Logger
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.Utils
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import com.example.apollographqltutorial.repository.BaseRepository.Companion.SOMETHING_WRONG
import com.example.apollographqltutorial.view.state.ViewState
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import okhttp3.Dispatcher


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CharacterRepositoryImplTest {

    @RelaxedMockK
    private lateinit var mockData: CharactersListQuery.Data

    private val mockWebServer = MockWebServer()

    private lateinit var apolloClient: ApolloClient

    private lateinit var objectUnderTest: CharacterRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockWebServer.start(8080)

        val okHttpClient = OkHttpClient.Builder()
            .dispatcher(Dispatcher(Utils.immediateExecutorService()))
            .build()

        apolloClient = ApolloClient.builder()
            .serverUrl(mockWebServer.url("/"))
            .dispatcher(Utils.immediateExecutor())
            .okHttpClient(okHttpClient)
            .logger(object : Logger {
                override fun log(priority: Int, message: String, t: Throwable?, vararg args: Any) {
                    println(String.format(message, *args))
                    t?.printStackTrace()
                }
            }).build()

        objectUnderTest = CharacterRepositoryImpl(apolloClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `given response failure of list when fetching results then return exception`() {
        val expectedError = ViewState.Error(Exception(SOMETHING_WRONG))

        runBlocking {
            val actualResult = objectUnderTest.queryCharactersList()

            assertEquals(expectedError::class.java, actualResult!!::class.java)
        }
    }

    @Test
    fun `given response characters list when fetching results then return success`() {
        mockWebServer.enqueue(Utils.mockResponse("characters_list_response.json"))

        val expectedSuccess = ViewState.Success(mockData)

        runBlocking {
            val actualResult = objectUnderTest.queryCharactersList()

            assertEquals(expectedSuccess::class.java, actualResult!!::class.java)
        }
    }

    @Test
    fun `given response failure for specific character when fetching results then return exception`() {
        val expectedError = ViewState.Error(Exception(SOMETHING_WRONG))

        runBlocking {
            val actualResult = objectUnderTest.queryCharacter("14")

            assertEquals(expectedError::class.java, actualResult!!::class.java)
        }
    }

    @Test
    fun `given response specific character when fetching results then return success`() {
        mockWebServer.enqueue(Utils.mockResponse("specific_character_response.json"))

        val expectedSuccess = ViewState.Success(mockData)

        runBlocking {
            val actualResult = objectUnderTest.queryCharacter("14")

            assertEquals(expectedSuccess::class.java, actualResult!!::class.java)
        }
    }
}