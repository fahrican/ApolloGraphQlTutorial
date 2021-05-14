package com.example.apollographqltutorial.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.apollographql.apollo.exception.ApolloException
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.MainCoroutineRule
import com.example.apollographqltutorial.repository.CharacterRepository
import com.example.apollographqltutorial.view.state.ViewState
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CharacterViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mockRepository: CharacterRepository

    @Mock
    private lateinit var responseObserver: Observer<ViewState<CharactersListQuery.Data>>

    private lateinit var objectUnderTest: CharacterViewModel


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        objectUnderTest = CharacterViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        objectUnderTest.charactersList.removeObserver(responseObserver)
    }

    @Test
    fun `when calling for results then return loading`() {
        testCoroutineRule.runBlockingTest {
            objectUnderTest.charactersList.observeForever(responseObserver)

            objectUnderTest.queryCharactersList()

            Mockito.verify(responseObserver).onChanged(ViewState.Loading)
        }
    }

    @Test
    fun `when fetching results fails then return an error`() {
        val exception = mock(ApolloException::class.java)
        testCoroutineRule.runBlockingTest {
            objectUnderTest.charactersList.observeForever(responseObserver)

            Mockito.`when`(mockRepository.queryCharactersList()).thenAnswer {
                ViewState.Error(exception)
            }

            objectUnderTest.queryCharactersList()

            assertNotNull(objectUnderTest.charactersList.value)
            assertEquals(ViewState.Error(exception), objectUnderTest.charactersList.value)
        }
    }

    @Test
    fun `when fetching results ok then return a list successfully`() {
        val mockCharacters = mockk<CharactersListQuery.Data>()

        testCoroutineRule.runBlockingTest {
            objectUnderTest.charactersList.observeForever(responseObserver)

            Mockito.`when`(mockRepository.queryCharactersList()).thenAnswer {
                ViewState.Success(mockCharacters)
            }

            objectUnderTest.queryCharactersList()

            assertNotNull(objectUnderTest.charactersList.value)
            assertEquals(ViewState.Success(mockCharacters), objectUnderTest.charactersList.value)
        }
    }

}