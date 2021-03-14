package com.example.apollographqltutorial.repository

import com.apollographql.apollo.api.Response
import com.example.apollographqltutorial.CharactersListQuery

interface CharacterRepository {

    suspend fun queryCharactersList(): Response<CharactersListQuery.Data>

}