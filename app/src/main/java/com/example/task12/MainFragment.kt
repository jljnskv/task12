package com.example.task12

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.task12.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding
    private var lastResult = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setRetainInstance(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel //передача класса MainViewModel в вёрстку
        binding.lifecycleOwner = viewLifecycleOwner //для работы с корутинами

        binding.cancelButton.setOnClickListener {
            viewModel.onCancel()
        }

        stateFromViewModelProcessing()
    }

    private fun stateFromViewModelProcessing() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            States.Initial -> {
                                binding.lastRequestResult = getString(R.string.hint_request_result)
                                lastResult = getString(R.string.hint_request_result)
                            }

                            States.Canceled -> {
                                binding.lastRequestResult = getString(R.string.hint_request_result)
                                lastResult = getString(R.string.hint_request_result)
                                binding.searchEditText.setText("")
                                Snackbar.make(
                                    requireView(),
                                    getString(R.string.cancel_message),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                            States.Loading -> {
                                binding.lastRequestResult = lastResult
                            }

                            is States.Success -> {
                                binding.lastRequestResult =
                                    getString(R.string.request_result, state.curRequest)
                                lastResult = getString(R.string.request_result, state.curRequest)

                            }

                        }
                    }

            }
            binding.invalidateAll()
        }
    }

}