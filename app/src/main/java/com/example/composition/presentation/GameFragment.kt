package com.example.composition.presentation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase


class GameFragment : Fragment() {
    lateinit var level: Level

    private var _binding: FragmentGameBinding? = null
    val binding: FragmentGameBinding
    get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseLevel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val repository = GameRepositoryImpl
//        val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
//        val generateQuestionUseCase = GenerateQuestionUseCase(repository)
//        val gameSettings = getGameSettingsUseCase.invoke(level)
//        val question = generateQuestionUseCase.invoke(gameSettings.maxSumValue)
        val gameSettings = GetGameSettingsUseCase(GameRepositoryImpl).invoke(level)
        val question = GenerateQuestionUseCase(GameRepositoryImpl).invoke(gameSettings.maxSumValue)

        binding.tvTimer.text = gameSettings.gameTimeSeconds.toString()
        binding.tvSum.text = question.sum.toString()
        binding.tvVisibleNumber.text = question.visibleNumber.toString()
        binding.tvOption1.text = question.options[0].toString()
        binding.tvOption2.text = question.options[1].toString()
        binding.tvOption3.text = question.options[2].toString()
        binding.tvOption4.text = question.options[3].toString()
        binding.tvOption5.text = question.options[4].toString()
        binding.tvOption6.text = question.options[5].toString()

        val rightAnswer = (question.sum - question.visibleNumber).toString()
        binding.tvOption1.setOnClickListener {
//            if (rightAnswer == binding.tvOption1.text) {
//                binding.progressBar.progress = 20
//            }

            launchGameFinishedFragment(GameResult(true, 3, 6, gameSettings))

        }
        binding.tvOption2.setOnClickListener {  }
        binding.tvOption3.setOnClickListener {  }
        binding.tvOption4.setOnClickListener {  }
        binding.tvOption5.setOnClickListener {  }
        binding.tvOption6.setOnClickListener {  }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseLevel() {
        requireArguments().getParcelable<Level>(LEVEL_ARGUMENT)?.let {
            level = it
        }
    }

    companion object {
        const val LEVEL_ARGUMENT = "level"
        const val NAME = "GameFragment"

        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(LEVEL_ARGUMENT, level)
                }
            }
        }
    }

    fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null).commit()
    }
}