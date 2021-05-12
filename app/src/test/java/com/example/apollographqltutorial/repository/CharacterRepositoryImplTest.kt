package com.example.apollographqltutorial.repository

import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.repository.BaseRepository.Companion.SOMETHING_WRONG
import com.example.apollographqltutorial.view.state.ViewState
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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

    @RelaxedMockK
    private lateinit var objectUnderTest: CharacterRepository


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `given expected success response check if it matches with actual result`() {
        val expectedSuccess = ViewState.Success(mockData)

        runBlocking {
            coEvery { objectUnderTest.queryCharactersList() } returns ViewState.Success(mockData)

            val actualResult = objectUnderTest.queryCharactersList()

            coVerify { objectUnderTest.queryCharactersList() }

            assertEquals(expectedSuccess, actualResult)
        }
    }

    @Test
    fun `given expected error response check if it matches with actual result`() {
        val expectedError = ViewState.Error(Exception(SOMETHING_WRONG))

        runBlocking {
            coEvery { objectUnderTest.queryCharactersList() } returns expectedError

            val actualResult = objectUnderTest.queryCharactersList()

            coVerify { objectUnderTest.queryCharactersList() }

            assertEquals(expectedError, actualResult)
        }
    }

}