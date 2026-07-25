package com.example.crypto

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

data class LocalComputeMetrics(
    val cpuUsagePercentage: Float = 14.5f,
    val ramAllocatedMb: Float = 184.2f,
    val mediaEncodingFps: Int = 60,
    val cryptoOpsPerSecond: Int = 12400,
    val pqEntropyPoolBitStrength: Int = 1024,
    val hardwareAccelActive: Boolean = true,
    val activeKeyRotationsCount: Int = 42
)

object ComputeMonitorEngine {

    fun observeComputeMetrics(): Flow<LocalComputeMetrics> = flow {
        var baseCpu = 12.0f
        var baseOps = 11800
        while (true) {
            val cpuVariation = (Random.nextFloat() * 8.0f) - 3.0f
            val currentCpu = (baseCpu + cpuVariation).coerceIn(4.0f, 88.0f)
            val currentOps = baseOps + Random.nextInt(-400, 600)
            val ram = 180.0f + (Random.nextFloat() * 15.0f)

            emit(
                LocalComputeMetrics(
                    cpuUsagePercentage = (currentCpu * 10).toInt() / 10.0f,
                    ramAllocatedMb = (ram * 10).toInt() / 10.0f,
                    mediaEncodingFps = Random.nextInt(58, 61),
                    cryptoOpsPerSecond = currentOps,
                    pqEntropyPoolBitStrength = 1024,
                    hardwareAccelActive = true,
                    activeKeyRotationsCount = Random.nextInt(38, 52)
                )
            )
            delay(1500)
        }
    }
}
