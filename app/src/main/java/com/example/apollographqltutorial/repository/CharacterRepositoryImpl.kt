package com.example.apollographqltutorial.repository

import android.util.Log
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.internal.ResponseReader
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.apollographqltutorial.CharacterQuery
import com.example.apollographqltutorial.CharactersListQuery
import com.example.apollographqltutorial.networking.RickAndMortyApi
import com.example.apollographqltutorial.view.state.ViewState
import java.io.FileReader
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val webService: RickAndMortyApi
) : BaseRepository() {


    override suspend fun queryCharactersList(): ViewState<CharactersListQuery.Data>? {
        var result: ViewState<CharactersListQuery.Data>? = null
        try {
            val response = webService.getApolloClient().query(CharactersListQuery()).await()
            response.let {
                it.data?.let { data -> result = handleSuccess(data) }
            }
        } catch (ae: ApolloException) {
            Log.e("CharacterRepositoryImpl", "Error: ${ae.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return result
    }


    /*  override suspend fun queryCharactersList(): Response<CharactersListQuery.Data> {
          return webService.getApolloClient().query(CharactersListQuery()).await()
      }*/

    override suspend fun queryCharacter(id: String): Response<CharacterQuery.Data> {
        return webService.getApolloClient().query(CharacterQuery(id)).await()
    }

    /*
    override suspend fun getPosts(): ResultState<ArrayList<PostsItem>> {
        var result: ResultState<ArrayList<PostsItem>> = handleSuccess(ArrayList())
        try {
            val response = webService.getPosts()
            response.let {
                it.body()?.let { posts ->
                    result = handleSuccess(posts)
                }
                it.errorBody()?.let { responseErrorBody ->
                    if (responseErrorBody is HttpException) {
                        responseErrorBody.response()?.code()?.let { errorCode ->
                            result = handleException(errorCode)
                        }
                    } else result = handleException(GENERAL_ERROR_CODE)
                }
            }
        } catch (error: HttpException) {
            Log.e("PostRepositoryImpl", "Error: ${error.message}")
            return handleException(error.code())
        }
        return result
    }
     */

}