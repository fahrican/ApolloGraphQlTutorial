package com.example.apollographqltutorial.repository

import com.apollographql.apollo.ApolloClient
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.repository.BaseRepository.Companion.GENERAL_ERROR_CODE
import com.example.apollographqltutorial.repository.BaseRepository.Companion.SOMETHING_WRONG
import com.example.apollographqltutorial.util.ResponseFileReader
import com.example.apollographqltutorial.view.state.ViewState
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.mockwebserver.MockResponse
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

    private lateinit var mockApi: ApolloClient

    private lateinit var objectUnderTest: CharacterRepository

    private val SUCCESS_RESPONSE = "{\n" +
            "    \"data\": {\n" +
            "        \"characters\": {\n" +
            "            \"results\": [\n" +
            "                {\n" +
            "                    \"id\": \"1\",\n" +
            "                    \"name\": \"Rick Sanchez\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"2\",\n" +
            "                    \"name\": \"Morty Smith\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"3\",\n" +
            "                    \"name\": \"Summer Smith\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"4\",\n" +
            "                    \"name\": \"Beth Smith\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"5\",\n" +
            "                    \"name\": \"Jerry Smith\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"6\",\n" +
            "                    \"name\": \"Abadango Cluster Princess\",\n" +
            "                    \"species\": \"Alien\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"7\",\n" +
            "                    \"name\": \"Abradolf Lincler\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"8\",\n" +
            "                    \"name\": \"Adjudicator Rick\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"9\",\n" +
            "                    \"name\": \"Agency Director\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"10\",\n" +
            "                    \"name\": \"Alan Rails\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"11\",\n" +
            "                    \"name\": \"Albert Einstein\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"12\",\n" +
            "                    \"name\": \"Alexander\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"13\",\n" +
            "                    \"name\": \"Alien Googah\",\n" +
            "                    \"species\": \"Alien\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"14\",\n" +
            "                    \"name\": \"Alien Morty\",\n" +
            "                    \"species\": \"Alien\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"15\",\n" +
            "                    \"name\": \"Alien Rick\",\n" +
            "                    \"species\": \"Alien\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"16\",\n" +
            "                    \"name\": \"Amish Cyborg\",\n" +
            "                    \"species\": \"Alien\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"17\",\n" +
            "                    \"name\": \"Annie\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"18\",\n" +
            "                    \"name\": \"Antenna Morty\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"19\",\n" +
            "                    \"name\": \"Antenna Rick\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"20\",\n" +
            "                    \"name\": \"Ants in my Eyes Johnson\",\n" +
            "                    \"species\": \"Human\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    }\n" +
            "}"

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
    @Throws(IOException::class)
    fun testTeachersAreDisplayed() {
        */
    /**
     * Setting up mockWebServer at localhost:9900.
     *//*
        val server = MockWebServer()
        // start the server at port 9900
        server.start(9900)
        */
    /**
     * Get the target application context during testing and cast onto the TestDemoApplication
     *//*
        val testApp: TestDemoApplication = InstrumentationRegistry.getTargetContext()
            .getApplicationContext() as TestDemoApplication
        // Set the base url of the test app using the url of the mocked local server
        testApp.setBaseUrl(server.url("/").toString())

        // Enqueue the response you want the server to respond with when querried in the course to this test.
        server.enqueue(MockResponse().setBody(SUCCESS_RESPONSE))


        // LAUNCH ACTIVITY
        mainActivityTestRule.launchActivity(null)
        // UI testing
        onView(withText("John Doe")).check(matches(isDisplayed()))
        onView(withText("William Smith")).check(matches(isDisplayed()))

        // Shut down the server when done with testing
        server.shutdown()
    }  */

    @Test
    fun testTeachersAreDisplayed() {
        val reader = ResponseFileReader("characters_list_success.json")
        val gson = Gson()
        val expected: CharactersListQuery.Data =
            gson.fromJson(reader.content, CharactersListQuery.Data::class.java)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(SUCCESS_RESPONSE))
        }

        runBlocking {

            coEvery { objectUnderTest.queryCharactersList() } returns ViewState.Success(expected)

            val posts: ViewState<CharactersListQuery.Data>? = objectUnderTest.queryCharactersList()
            val actualResult = posts?.extractData

            coEvery { objectUnderTest.queryCharactersList() }

            assertEquals(expected, actualResult)
        }
    }

}