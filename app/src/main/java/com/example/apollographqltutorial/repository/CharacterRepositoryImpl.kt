package com.example.apollographqltutorial.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.apollographqltutorial.CharacterQuery
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.view.state.ViewState
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val webService: ApolloClient
) : BaseRepository() {

    override suspend fun queryCharactersList(): ViewState<CharactersListQuery.Data>? {
        var result: ViewState<CharactersListQuery.Data>? = null
        try {
            val response = webService.query(CharactersListQuery()).await()
            response.let {
                it.data?.let { data -> result = handleSuccess(data) }
            }
        } catch (ae: ApolloException) {
            Log.e("CharacterRepositoryImpl", "Error: ${ae.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return result
    }

    override suspend fun queryCharacter(id: String): Response<CharacterQuery.Data> {
        return webService.query(CharacterQuery(id)).await()
    }
}