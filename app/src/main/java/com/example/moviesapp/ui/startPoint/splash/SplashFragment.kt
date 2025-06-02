package com.example.moviesapp.ui.startPoint.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moviesapp.R
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.moviesapp.databinding.FragmentHomeBinding
import com.example.moviesapp.databinding.FragmentSplashBinding
import com.example.moviesapp.helper.base.BaseFragment

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override fun setupUI() {
                // Animate logo with Fade In and Scale
                binding.splashLogo.animate()
                    .alpha(1f)
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(1000)
                    .start()

                // Animate onboarding text
                binding.onboardingText.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .setStartDelay(500)
                    .start()

                // Animate sub text
                binding.subText.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .setStartDelay(700)
                    .start()

                // Animate enter button
                binding.enterButton.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .setStartDelay(900)
                    .start()

                // Keep splash screen visible for 2 seconds

        viewLifecycleOwner.lifecycleScope.launch {
                    delay(2000)
            binding.enterButton.setOnClickListener {
                navController.navigate(R.id.action_splashFragment_to_moviesListFragment)
            }
        }
                }




    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)    }
    }
