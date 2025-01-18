package com.ghostcat.deadzone.services

import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class FullKalmanFilter(private val dt: Double) {
    // State vector: [x, y, vx, vy]
    var state: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

    // State transition matrix A (4x4):
    private val A: Array<DoubleArray> = arrayOf(
        doubleArrayOf(1.0, 0.0, dt, 0.0),
        doubleArrayOf(0.0, 1.0, 0.0, dt),
        doubleArrayOf(0.0, 0.0, 1.0, 0.0),
        doubleArrayOf(0.0, 0.0, 0.0, 1.0)
    )

    // Control input matrix B (4x2) - for acceleration input [ax, ay]
    private val B: Array<DoubleArray> = arrayOf(
        doubleArrayOf(0.5 * dt * dt, 0.0),
        doubleArrayOf(0.0, 0.5 * dt * dt),
        doubleArrayOf(dt, 0.0),
        doubleArrayOf(0.0, dt)
    )

    // Initial error covariance matrix P (4x4)
    var P: Array<DoubleArray> = Array(4) { i ->
        DoubleArray(4) { j -> if (i == j) 1.0 else 0.0 }
    }

    // Process noise covariance Q (4x4) - tune these values as needed.
    private val Q: Array<DoubleArray> = arrayOf(
        doubleArrayOf(0.1, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.1, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, 0.1, 0.0),
        doubleArrayOf(0.0, 0.0, 0.0, 0.1)
    )

    // Measurement matrix H (2x4) - we directly measure position [x, y]
    private val H: Array<DoubleArray> = arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 1.0, 0.0, 0.0)
    )

    // Measurement noise covariance R (2x2) - tune these values
    private val R: Array<DoubleArray> = arrayOf(
        doubleArrayOf(5.0, 0.0),
        doubleArrayOf(0.0, 5.0)
    )

    // Prediction step: incorporate control input u = [ax, ay]
    fun predict(u: DoubleArray) {
        state = matrixVectorMultiply(A, state).let { predicted ->
            val Bu = matrixVectorMultiply(B, u)
            addVectors(predicted, Bu)
        }
        P = addMatrices(matrixMultiply(matrixMultiply(A, P), transpose(A)), Q)
    }

    // Update step with a measurement z = [measuredX, measuredY]
    fun update(z: DoubleArray) {
        val S = addMatrices(matrixMultiply(matrixMultiply(H, P), transpose(H)), R)
        val S_inv = invert2x2(S)
        val K = matrixMultiply(matrixMultiply(P, transpose(H)), S_inv)
        val y = subtractVectors(z, matrixVectorMultiply(H, state))
        state = addVectors(state, matrixVectorMultiply(K, y))
        val I = identityMatrix(4)
        P = matrixMultiply(subtractMatrices(I, matrixMultiply(K, H)), P)
    }

    fun getPosition(): Pair<Double, Double> {
        return Pair(state[0], state[1])
    }

    // --- Helper functions for basic matrix operations ---

    private fun matrixVectorMultiply(matrix: Array<DoubleArray>, vector: DoubleArray): DoubleArray {
        val result = DoubleArray(matrix.size)
        for (i in matrix.indices) {
            result[i] = 0.0
            for (j in vector.indices) {
                result[i] += matrix[i][j] * vector[j]
            }
        }
        return result
    }

    private fun matrixMultiply(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val rows = a.size
        val cols = b[0].size
        val result = Array(rows) { DoubleArray(cols) { 0.0 } }
        for (i in 0 until rows)
            for (j in 0 until cols)
                for (k in b.indices)
                    result[i][j] += a[i][k] * b[k][j]
        return result
    }

    private fun addMatrices(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val result = Array(a.size) { DoubleArray(a[0].size) }
        for (i in a.indices)
            for (j in a[0].indices)
                result[i][j] = a[i][j] + b[i][j]
        return result
    }

    private fun subtractMatrices(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val result = Array(a.size) { DoubleArray(a[0].size) }
        for (i in a.indices)
            for (j in a[0].indices)
                result[i][j] = a[i][j] - b[i][j]
        return result
    }

    private fun transpose(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val rows = matrix.size
        val cols = matrix[0].size
        val result = Array(cols) { DoubleArray(rows) }
        for (i in 0 until rows)
            for (j in 0 until cols)
                result[j][i] = matrix[i][j]
        return result
    }

    private fun invert2x2(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val a = matrix[0][0]
        val b = matrix[0][1]
        val c = matrix[1][0]
        val d = matrix[1][1]
        val det = a * d - b * c
        if (det == 0.0) throw IllegalArgumentException("Matrix is singular and cannot be inverted")
        val invDet = 1.0 / det
        return arrayOf(
            doubleArrayOf(d * invDet, -b * invDet),
            doubleArrayOf(-c * invDet, a * invDet)
        )
    }

    private fun identityMatrix(size: Int): Array<DoubleArray> {
        return Array(size) { i -> DoubleArray(size) { j -> if (i == j) 1.0 else 0.0 } }
    }

    private fun addVectors(a: DoubleArray, b: DoubleArray): DoubleArray {
        return DoubleArray(a.size) { i -> a[i] + b[i] }
    }

    private fun subtractVectors(a: DoubleArray, b: DoubleArray): DoubleArray {
        return DoubleArray(a.size) { i -> a[i] - b[i] }
    }
}
