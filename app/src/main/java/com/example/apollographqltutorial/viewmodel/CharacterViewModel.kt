package com.example.apollographqltutorial.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _character by lazy { MutableLiveData<ViewState<CharacterQuery.Data>>() }
    val character: LiveData<ViewState<CharacterQuery.Data>>
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

    fun queryCharacter(id: String) {
        _character.postValue(ViewState.Loading)
        viewModelScope.launch {
            val response = repository.queryCharacter(id)
            response.let { data ->
                when (data) {
                    is ViewState.Success -> {
                        _character.postValue(data)
                        Log.d("queryCharacter(id)", "response: $data")
                    }
                    else -> {
                        _character.postValue(data)
                        Log.e("queryCharacter(id)", "catch block")
                    }
                }
            }
        }
    }

}