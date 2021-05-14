package com.example.apollographqltutorial.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.apollographql.apollo.exception.ApolloException
import com.example.apollographqltutorial.CharacterQuery
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
    private lateinit var charactersListObserver: Observer<ViewState<CharactersListQuery.Data>>

    @Mock
    private lateinit var characterObserver: Observer<ViewState<CharacterQuery.Data>>

    private lateinit var objectUnderTest: CharacterViewModel


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        objectUnderTest = CharacterViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        objectUnderTest.charactersList.removeObserver(charactersListObserver)
        objectUnderTest.character.removeObserver(characterObserver)
    }

    @Test
    fun `when calling for characters list then return loading`() {
        testCoroutineRule.runBlockingTest {
            objectUnderTest.charactersList.observeForever(charactersListObserver)

            objectUnderTest.queryCharactersList()

            Mockito.verify(charactersListObserver).onChanged(ViewState.Loading)
        }
    }

    @Test
    fun `when fetching for characters list fails then return an error`() {
        val exception = mock(ApolloException::class.java)
        testCoroutineRule.runBlockingTest {
            objectUnderTest.charactersList.observeForever(charactersListObserver)

            Mockito.`when`(mockRepository.queryCharactersList()).thenAnswer {
                ViewState.Error(exception)
            }

            objectUnderTest.queryCharactersList()

            assertNotNull(objectUnderTest.charactersList.value)
            assertEquals(ViewState.Error(exception), objectUnderTest.charactersList.value)
        }
    }

    @Test
    fun `when fetching for characters list then return a list success state`() {
        val mockCharacters = mockk<CharactersListQuery.Data>()

        testCoroutineRule.runBlockingTest {
            objectUnderTest.charactersList.observeForever(charactersListObserver)

            Mockito.`when`(mockRepository.queryCharactersList()).thenAnswer {
                ViewState.Success(mockCharacters)
            }

            objectUnderTest.queryCharactersList()

            assertNotNull(objectUnderTest.charactersList.value)
            assertEquals(ViewState.Success(mockCharacters), objectUnderTest.charactersList.value)
        }
    }

    @Test
    fun `when calling for a specific character then return loading`() {
        testCoroutineRule.runBlockingTest {
            objectUnderTest.character.observeForever(characterObserver)

            objectUnderTest.queryCharacter("14")

            Mockito.verify(characterObserver).onChanged(ViewState.Loading)
        }
    }

    @Test
    fun `when calling for a specific character fails then return an error`() {
        val exception = mock(ApolloException::class.java)
        testCoroutineRule.runBlockingTest {
            objectUnderTest.character.observeForever(characterObserver)

            Mockito.`when`(mockRepository.queryCharacter("099")).thenAnswer {
                ViewState.Error(exception)
            }

            objectUnderTest.queryCharacter("099")

            assertNotNull(objectUnderTest.character.value)
            assertEquals(ViewState.Error(exception), objectUnderTest.character.value)
        }
    }


    @Test
    fun `when fetching for a specific character then return a list success state`() {
        val mockCharacters = mockk<CharacterQuery.Data>()

        testCoroutineRule.runBlockingTest {
            objectUnderTest.character.observeForever(characterObserver)

            Mockito.`when`(mockRepository.queryCharacter("14")).thenAnswer {
                ViewState.Success(mockCharacters)
            }

            objectUnderTest.queryCharacter("14")

            assertNotNull(objectUnderTest.character.value)
            assertEquals(ViewState.Success(mockCharacters), objectUnderTest.character.value)
        }
    }
}