package com.example.apollographqltutorial

import com.google.common.io.CharStreams
import okhttp3.mockwebserver.MockResponse
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

object Utils {

    @Throws(IOException::class)
    fun readFileToString(
        contextClass: Class<*>,
        streamIdentifier: String
    ): String = InputStreamReader(
        contextClass.getResourceAsStream(streamIdentifier),
        Charset.defaultCharset()
    ).use {
        CharStreams.toString(it)
    }

    fun immediateExecutor() = Executor { command -> command.run() }

    @Throws(IOException::class)
    fun mockResponse(fileName: String) =
        MockResponse().setChunkedBody(
            readFileToString(Utils::class.java, "/$fileName"), 32
        )

    fun immediateExecutorService() = object : AbstractExecutorService() {
        override fun shutdown() = Unit

        override fun shutdownNow(): List<Runnable>? = null

        override fun isShutdown() = false

        override fun isTerminated() = false

        @Throws(InterruptedException::class)
        override fun awaitTermination(l: Long, timeUnit: TimeUnit) = false

        override fun execute(runnable: Runnable) = runnable.run()
    }
}