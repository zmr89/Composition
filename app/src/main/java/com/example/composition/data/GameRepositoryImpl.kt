package com.example.composition.data

import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.repository.GameRepository
import kotlin.random.Random

object GameRepositoryImpl : GameRepository {

    private const val MIN_VISIBLE_ANSWER = 1
    private const val MIN_SUM = 2


    override fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question {
        val sum = Random.nextInt(MIN_SUM, maxSumValue + 1)
        val visibleNumber = Random.nextInt(MIN_VISIBLE_ANSWER, sum)
        val rightAnswer = sum - visibleNumber
        val  options = hashSetOf<Int>()
        options.add(rightAnswer)
        val from = Random.nextInt(rightAnswer - countOfOptions, MIN_VISIBLE_ANSWER)
        val to = Random.nextInt(rightAnswer + countOfOptions, maxSumValue)
        while(options.size < countOfOptions) {
            options.add(Random.nextInt(from, to))
        }
        return Question(sum, visibleNumber, options.toList())
    }

    override fun getGameSettings(level: Level): GameSettings {
        return when(level) {
            Level.TEST -> GameSettings(10, 3, 50, 8)
            Level.EASY -> GameSettings(10, 10, 70, 60)
            Level.NORMAL -> GameSettings(20, 20, 80, 40)
            Level.HARD -> GameSettings(30, 30, 90, 40)
        }
    }

}