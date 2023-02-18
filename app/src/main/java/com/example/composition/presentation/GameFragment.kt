package com.example.composition.presentation


import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level


class GameFragment : Fragment() {
    private lateinit var level: Level

    private val gameViewModelFactory by lazy {
        GameViewModelFactory(level, requireActivity().application)
    }
    private val gameViewModel: GameViewModel by lazy {
        ViewModelProvider(this, gameViewModelFactory).get(GameViewModel::class.java)
    }

    private val optionsList by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
        }
    }

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
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

        observeViewModels()
        setClickListenersOptions()

    }

    private fun setClickListenersOptions() {
        for (options in optionsList) {
            options.setOnClickListener {
                gameViewModel.chooseAnswer(Integer.parseInt(options.text.toString()))
            }
        }
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

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null).commit()
    }

    private fun observeViewModels() {
        gameViewModel.timerLD.observe(viewLifecycleOwner, Observer {
                binding.tvTimer.text = it
        })
        gameViewModel.questionLD.observe(viewLifecycleOwner, Observer {
                binding.tvSum.text = it.sum.toString()
                binding.tvVisibleNumber.text = it.visibleNumber.toString()
            for (i in 0 until optionsList.size) {
                optionsList[i].text = it.options[i].toString()
            }
        })
        gameViewModel.percentOfRightAnswersLD.observe(viewLifecycleOwner, Observer {
                binding.progressBar.setProgress(it, true)
        })
        gameViewModel.progressAnswersLD.observe(viewLifecycleOwner, Observer {
                binding.tvPercentCorrect.text = it
        })
        gameViewModel.isEnoughCountLD.observe(viewLifecycleOwner, Observer {
            binding.tvPercentCorrect.setTextColor(getColorByState(it))
        })
        gameViewModel.isEnoughPercentLD.observe(viewLifecycleOwner, Observer {
            val color = getColorByState(it)
            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        })
        gameViewModel.minPercentLD.observe(viewLifecycleOwner, Observer {
            binding.progressBar.secondaryProgress = it
        })
        gameViewModel.gameResultLD.observe(viewLifecycleOwner, Observer {
                launchGameFinishedFragment(it)
        })
    }

    private fun getColorByState(state : Boolean): Int {
        val resId = if (state) {android.R.color.holo_green_light}
        else {android.R.color.holo_red_light}
        val color = ContextCompat.getColor(requireContext(), resId)
        return color
    }

}