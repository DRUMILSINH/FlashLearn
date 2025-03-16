package com.rana.flashlearn.utils

import java.util.Calendar
import java.util.Date

object SpacedRepetitionUtil {

    fun calculateNextReviewDate(previousReviewDate: Date, quality: Int, repetitions: Int, easeFactor: Double): Pair<Date, Double> {
        val newEaseFactor = calculateEaseFactor(easeFactor, quality)
        val interval = calculateInterval(repetitions, newEaseFactor)

        val calendar = Calendar.getInstance()
        calendar.time = previousReviewDate
        calendar.add(Calendar.DAY_OF_YEAR, interval)

        return Pair(calendar.time, newEaseFactor)
    }

    private fun calculateEaseFactor(easeFactor: Double, quality: Int): Double {
        val newEaseFactor = easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
        return if (newEaseFactor < 1.3) 1.3 else newEaseFactor
    }

    private fun calculateInterval(repetitions: Int, easeFactor: Double): Int {
        return when (repetitions) {
            0 -> 1
            1 -> 6
            else -> (repetitions * easeFactor).toInt()
        }
    }
} 