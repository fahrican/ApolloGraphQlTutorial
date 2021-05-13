package com.example.apollographqltutorial.repository

import com.apollographql.apollo.ApolloClient
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.repository.BaseRepository.Companion.SOMETHING_WRONG
import com.example.apollographqltutorial.util.ResponseFileReader
import com.example.apollographqltutorial.view.state.ViewState
import com.google.gson.Gson
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

    private val mockWebServer = MockWebServer()


    @Before
    fun setUp() {
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `given response failure when fetching results then return exception`() {
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

        val reader = ResponseFileReader("characters_list_success.json")
        val gson = Gson()
        val data: CharactersListQuery.Data =
            gson.fromJson(reader.content, CharactersListQuery.Data::class.java)

        val expectedSuccess = ViewState.Success(data)

        runBlocking {
            val actualResult = objectUnderTest.queryCharactersList()

            assertEquals(expectedSuccess::class.java, actualResult!!::class.java)
        }
    }
}