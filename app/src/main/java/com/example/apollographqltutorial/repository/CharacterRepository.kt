package com.example.apollographqltutorial.repository

import com.example.apollographqltutorial.CharacterQuery
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.view.state.ViewState

interface CharacterRepository {

    suspend fun queryCharactersList(): ViewState<CharactersListQuery.Data>?

    suspend fun queryCharacter(id: String): ViewState<CharacterQuery.Data>?

}