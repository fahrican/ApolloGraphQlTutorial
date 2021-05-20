package com.example.apollographqltutorial

import com.google.common.io.CharStreams
import okhttp3.mockwebserver.MockResponse
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

object Utils {

    @Throws(IOException::class)
    fun readFileToString(
        contextClass: Class<*>,
        streamIdentifier: String
    ): String {

        var inputStreamReader: InputStreamReader? = null
        try {
            inputStreamReader = InputStreamReader(
                contextClass.getResourceAsStream(streamIdentifier),
                Charset.defaultCharset()
            )
            return CharStreams.toString(inputStreamReader)
        } catch (e: IOException) {
            throw IOException()
        } finally {
            inputStreamReader?.close()
        }
    }

    fun immediateExecutor(): Executor {
        return Executor { command -> command.run() }
    }

    @Throws(IOException::class)
    fun mockResponse(fileName: String): MockResponse {
        return MockResponse().setChunkedBody(readFileToString(Utils::class.java, "/$fileName"), 32)
    }

    fun immediateExecutorService(): ExecutorService {
        return object : AbstractExecutorService() {
            override fun shutdown() = Unit

            override fun shutdownNow(): List<Runnable>? = null

            override fun isShutdown(): Boolean = false

            override fun isTerminated(): Boolean = false

            @Throws(InterruptedException::class)
            override fun awaitTermination(l: Long, timeUnit: TimeUnit): Boolean = false

            override fun execute(runnable: Runnable) = runnable.run()
        }
    }

    class TestExecutor : Executor {

        private val commands = ConcurrentLinkedQueue<Runnable>()

        override fun execute(command: Runnable) {
            commands.add(command)
        }

        fun triggerActions() {
            for (command in commands) {
                command.run()
            }
        }
    }

    fun checkTestFixture(actualText: String, expectedPath: String) {
        val expected = File("src/test/fixtures/$expectedPath")
        val expectedText = try {
            expected.readText().removeSuffix("\n")
        } catch (e: FileNotFoundException) {
            ""
        }

        expected.parentFile.mkdirs()
        if (actualText != expectedText) {
            when (System.getProperty("updateTestFixtures")?.trim()) {
                "on", "true", "1" -> {
                    expected.writeText(actualText)
                }
                else -> {
                    throw java.lang.Exception(
                        """generatedText doesn't match the expectedText.
      |If you changed the compiler recently, you need to update the testFixtures.
      |Run the tests with `-DupdateTestFixtures=true` to do so.
      |generatedText: $actualText
      |expectedText: $expectedText""".trimMargin()
                    )
                }
            }
        }
    }
}
