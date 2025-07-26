package com.example.aegisai.onnx

import android.content.Context
import ai.onnxruntime.*
import java.io.InputStream
import java.nio.FloatBuffer

class CrashPredictor(context: Context) {
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val ortSession: OrtSession
    private val NUM_FEATURES = 6

    init {
        val modelStream: InputStream = context.assets.open("crash_model.ort")
        val modelBytes = modelStream.readBytes()
        modelStream.close()
        val sessionOptions = OrtSession.SessionOptions().apply {
            setExecutionMode(OrtSession.SessionOptions.ExecutionMode.SEQUENTIAL)
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT)
        }
        ortSession = ortEnv.createSession(modelBytes, sessionOptions)
    }

    fun predict(inputData: FloatArray, sequenceLength: Int): Float {
        val expectedSize = sequenceLength * NUM_FEATURES
        require(inputData.size == expectedSize) {
            "Model expects $expectedSize features, but received ${inputData.size}."
        }

        val inputName = ortSession.inputNames.iterator().next()
        val inputShape = longArrayOf(1, sequenceLength.toLong(), NUM_FEATURES.toLong())
        val inputTensor = OnnxTensor.createTensor(ortEnv, FloatBuffer.wrap(inputData), inputShape)

        inputTensor.use {
            val results = ortSession.run(mapOf(inputName to it))
            val output = results[0].value as Array<FloatArray>
            return output[0][0]
        }
    }

    /**
     * Closes the ONNX session and environment to release native resources.
     * This should be called when the predictor is no longer needed.
     */
    fun close() {
        // --- THIS IS THE FIX ---
        // Just call close() directly.
        ortSession.close()
        ortEnv.close()
    }
}