package com.example.composition.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings


class GameFinishedFragment : Fragment() {
    private val args by navArgs<GameFinishedFragmentArgs>()

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun retryGame() {
        findNavController().popBackStack()
    }

    private fun initViews() {
        setEmojiResult()
        binding.tvRequiredAnswers.text = String.format(
            requireActivity().resources.getString(R.string.required_score),
            args.gameResult.gameSettings.minCountOfRightAnswers)
        binding.tvScoreAnswers.text = String.format(
            requireActivity().resources.getString(R.string.score_answers),
            args.gameResult.countOfRightAnswers)
        binding.tvRequiredPercentage.text = String.format(
            requireActivity().resources.getString(R.string.required_percentage),
            args.gameResult.gameSettings.minPercentOfRightAnswers)
        binding.tvScorePercentage.text = String.format(
            requireActivity().resources.getString(R.string.score_percentage),
            calculatePercent())
    }

    private fun setEmojiResult() {
        val imageResId = if (args.gameResult.winner) {R.drawable.ic_smile} else {R.drawable.ic_sad}
        val imageDraw = ContextCompat.getDrawable(requireContext(), imageResId)
        binding.emojiResult.setImageDrawable(imageDraw)
    }

    private fun calculatePercent(): Int {
        val percent = if (args.gameResult.countOfQuestions == 0) { 0 }
        else {((args.gameResult.countOfRightAnswers.toDouble()
                    / args.gameResult.countOfQuestions) * 100).toInt()
        }
        return percent
    }

}