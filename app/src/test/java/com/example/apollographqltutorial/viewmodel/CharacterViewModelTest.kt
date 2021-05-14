package com.example.apollographqltutorial.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.MainCoroutineRule
import com.example.apollographqltutorial.repository.CharacterRepository
import com.example.apollographqltutorial.view.state.ViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
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

/*
    @Test
    fun `given loading state confirm it happens`() {
        coEvery { mockRepository.queryCharactersList() } returns ViewState.Loading

        val objectUnderTest = CharacterViewModel(mockRepository)

        val observer = mockk<Observer<ViewState.Loading>>(relaxUnitFun = true)

        //objectUnderTest.charactersList.observeForever { ViewState.Loading }

        objectUnderTest.queryCharactersList()

        coVerify { observer.onChanged(ViewState.Loading) }

        confirmVerified(observer)
    }
*/


    @Test
    fun `when calling for results then return loading`() {
        testCoroutineRule.runBlockingTest {
            objectUnderTest.charactersList.observeForever(responseObserver)

            objectUnderTest.queryCharactersList()

            Mockito.verify(responseObserver).onChanged(ViewState.Loading)
        }
    }


}