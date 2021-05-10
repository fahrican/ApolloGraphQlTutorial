package com.example.apollographqltutorial.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.apollographqltutorial.CharacterQuery
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.repository.CharacterRepository
import com.example.apollographqltutorial.view.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val repository: CharacterRepository,
) : ViewModel() {

    private val _charactersList by lazy { MutableLiveData<ViewState<CharactersListQuery.Data>>() }
    val charactersList: LiveData<ViewState<CharactersListQuery.Data>>
        get() = _charactersList

    private val _character by lazy { MutableLiveData<ViewState<Response<CharacterQuery.Data>>>() }
    val character: LiveData<ViewState<Response<CharacterQuery.Data>>>
        get() = _character

    fun queryCharactersList() {
        _charactersList.postValue(ViewState.Loading)
        viewModelScope.launch {
            val response = repository.queryCharactersList()
            response.let { data ->
                when (data) {
                    is ViewState.Success -> {
                        _charactersList.postValue(data)
                        Log.d("queryCharactersList()", "response: $data")
                    }
                    else -> {
                        _charactersList.postValue(data)
                        Log.e("queryCharactersList()", "catch block")
                    }
                }
            }

        }
    }

    fun queryCharacter(id: String) = viewModelScope.launch {
        _character.postValue(ViewState.Loading)
        try {
            val response = repository.queryCharacter(id)
            _character.postValue(ViewState.Success(response))
        } catch (ae: ApolloException) {
            Log.d("ApolloException", "Failure", ae)
            _character.postValue(ViewState.Error(ApolloException("queryCharacter()")))
        }
    }

}