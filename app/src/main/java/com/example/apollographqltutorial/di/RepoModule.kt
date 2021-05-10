package com.example.apollographqltutorial.di

import com.apollographql.apollo.ApolloClient
import com.example.apollographqltutorial.networking.RickAndMortyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Singleton
    @Provides
    fun provideWebService(): ApolloClient = RickAndMortyApi.getApolloClient()

}