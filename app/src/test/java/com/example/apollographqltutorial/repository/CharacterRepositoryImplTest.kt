package com.example.apollographqltutorial.repository

import com.apollographql.apollo.ApolloClient
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.repository.BaseRepository.Companion.SOMETHING_WRONG
import com.example.apollographqltutorial.view.state.ViewState
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CharacterRepositoryImplTest {

    @RelaxedMockK
    private lateinit var mockData: CharactersListQuery.Data

    private val mockWebServer = MockWebServer()


    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `given response failure of list when fetching results then return exception`() {
        val apolloClientNoUrl = ApolloClient.builder()
            .serverUrl(mockWebServer.url("/"))
            .build()

        val objectUnderTest = CharacterRepositoryImpl(apolloClientNoUrl)

        val expectedError = ViewState.Error(Exception(SOMETHING_WRONG))

        runBlocking {
            val actualResult = objectUnderTest.queryCharactersList()

            assertEquals(expectedError::class.java, actualResult!!::class.java)
        }
    }

    @Test
    fun `given response characters list when fetching results then return success`() {
        val apolloClientNoUrl = ApolloClient.builder()
            .serverUrl(mockWebServer.url("https://rickandmortyapi.com/graphql"))
            .build()

        val objectUnderTest = CharacterRepositoryImpl(apolloClientNoUrl)

        val expectedSuccess = ViewState.Success(mockData)

        runBlocking {
            val actualResult = objectUnderTest.queryCharactersList()

            assertEquals(expectedSuccess::class.java, actualResult!!::class.java)
        }
    }

    @Test
    fun `given response failure for specific character when fetching results then return exception`() {
        val apolloClientNoUrl = ApolloClient.builder()
            .serverUrl(mockWebServer.url("/"))
            .build()

        val objectUnderTest = CharacterRepositoryImpl(apolloClientNoUrl)

        val expectedError = ViewState.Error(Exception(SOMETHING_WRONG))

        runBlocking {
            val actualResult = objectUnderTest.queryCharacter("14")

            assertEquals(expectedError::class.java, actualResult!!::class.java)
        }
    }

    @Test
    fun `given response specific character when fetching results then return success`() {
        val apolloClientNoUrl = ApolloClient.builder()
            .serverUrl(mockWebServer.url("https://rickandmortyapi.com/graphql"))
            .build()

        val objectUnderTest = CharacterRepositoryImpl(apolloClientNoUrl)

        val expectedSuccess = ViewState.Success(mockData)

        runBlocking {
            val actualResult = objectUnderTest.queryCharacter("14")

            assertEquals(expectedSuccess::class.java, actualResult!!::class.java)
        }
    }
}