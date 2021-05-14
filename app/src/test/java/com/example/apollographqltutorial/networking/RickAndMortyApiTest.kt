package com.example.apollographqltutorial.networking

import com.apollographql.apollo.ApolloClient
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class RickAndMortyApiTest {

    @Test
    fun `given two webservices then check for not equal`() {
        val mockApollo = mockk<ApolloClient>()

        val objectUnderTest = RickAndMortyApi.getApolloClient()

        assertNotEquals(objectUnderTest, mockApollo)
    }

}