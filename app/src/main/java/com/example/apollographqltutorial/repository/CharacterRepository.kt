package com.example.apollographqltutorial.repository

import com.apollographql.apollo.api.Response
import com.example.apollographqltutorial.CharacterQuery
import com.example.apollographqltutorial.CharactersListQuery

interface CharacterRepository {

    suspend fun queryCharactersList(): Response<CharactersListQuery.Data>

    suspend fun queryCharacter(id: String): Response<CharacterQuery.Data>

}