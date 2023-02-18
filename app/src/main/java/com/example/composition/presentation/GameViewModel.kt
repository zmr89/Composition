package com.example.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(private val level: Level, private val application: Application) : ViewModel() {

    private lateinit var gameSettings: GameSettings

    private val repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null

    private val _timerLD = MutableLiveData<String>()
    val timerLD: LiveData<String>
        get() = _timerLD

    private val _questionLD = MutableLiveData<Question>()
    val questionLD: LiveData<Question>
        get() = _questionLD

    private val _percentOfRightAnswersLD = MutableLiveData<Int>()
    val percentOfRightAnswersLD: LiveData<Int>
        get() = _percentOfRightAnswersLD

    private val _progressAnswersLD = MutableLiveData<String>()
    val progressAnswersLD: LiveData<String>
        get() = _progressAnswersLD

    private val _isEnoughCountLD = MutableLiveData<Boolean>()
    val isEnoughCountLD: LiveData<Boolean>
        get() = _isEnoughCountLD

    private val _isEnoughPercentLD = MutableLiveData<Boolean>()
    val isEnoughPercentLD: LiveData<Boolean>
        get() = _isEnoughPercentLD

    private val _minPercentLD = MutableLiveData<Int>()
    val minPercentLD: LiveData<Int>
        get() = _minPercentLD

    private val _gameResultLD = MutableLiveData<GameResult>()
    val gameResultLD: LiveData<GameResult>
        get() = _gameResultLD

    private var countRightAnswers = 0
    private var countTotalAnswers = 0

    init {
        startGame()
    }

    private fun startGame() {
        getSettings()
        startTimer()
        generateQuestion()
        updateProgress()
    }

    private fun getSettings() {
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercentLD.value = gameSettings.minPercentOfRightAnswers
    }

    private fun startTimer() {
        timer = object :
            CountDownTimer(gameSettings.gameTimeSeconds * SECOND_TO_MILLIS, SECOND_TO_MILLIS) {
            override fun onTick(millisUntilFinish: Long) {
                _timerLD.value = formatTime(millisUntilFinish)
            }
            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    fun formatTime(millisUntilFinish: Long): String {
        val seconds = millisUntilFinish / SECOND_TO_MILLIS
        val minutes = seconds / SECOND_TO_MINUTES
        val leftSeconds = seconds - (minutes * SECOND_TO_MINUTES)
        val strFormTime = String.format("%02d:%02d", minutes, leftSeconds)
        return strFormTime
    }

    fun finishGame() {
        val isWinner = _isEnoughCountLD.value == true && _isEnoughPercentLD.value == true
        val gameResult = GameResult(isWinner, countRightAnswers, countTotalAnswers, gameSettings)
        _gameResultLD.value = gameResult
    }

    private fun generateQuestion() {
        _questionLD.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    fun chooseAnswer(chooseAnswer: Int) {
        checkChooseAnswer(chooseAnswer)
        updateProgress()
        generateQuestion()
    }

    private fun updateProgress() {
        val percentRightAnswer = if (countTotalAnswers == 0) { 0 }
        else {((countRightAnswers.toDouble() / countTotalAnswers.toDouble()) * 100).toInt()}
        _percentOfRightAnswersLD.value = percentRightAnswer
        _isEnoughPercentLD.value = percentRightAnswer >= gameSettings.minPercentOfRightAnswers
        _isEnoughCountLD.value = countRightAnswers >= gameSettings.minCountOfRightAnswers
        val progressAnswersString = application.resources.getString(R.string.progress_answers)
        _progressAnswersLD.value = String.format(progressAnswersString,
            countRightAnswers, gameSettings.minCountOfRightAnswers)
    }

    private fun checkChooseAnswer(chooseAnswer: Int) {
        val correctAnswer = _questionLD.value?.correctAnswer
        if (chooseAnswer == correctAnswer) {
            countRightAnswers++
        }
        countTotalAnswers++
    }

    companion object {
        const val SECOND_TO_MILLIS = 1000L
        const val SECOND_TO_MINUTES = 60
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}