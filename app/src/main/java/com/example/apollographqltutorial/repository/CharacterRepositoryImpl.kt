package com.example.apollographqltutorial.repository

import com.apollographql.apollo.api.Response
import com.example.apollographqltutorial.CharactersListQuery
import javax.inject.Inject

class CharacterRepositoryImpl : CharacterRepository {

    override suspend fun queryCharactersList(): Response<CharactersListQuery.Data> {
    }

}