package com.example.oya.newsreader.ui.main


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.appcompat.app.AppCompatActivity
import com.example.oya.newsreader.R
import com.example.oya.newsreader.data.Interactors
import com.example.oya.newsreader.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    @Inject lateinit var interactors : Interactors

    private var animatingViewCount = 0

    private lateinit var animationList : List<Pair<View, Int>>

    private var animsEnded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Prepare animation with views
        val fadeAnimation = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.fade_anim)
        binding.splashTitle.startAnimation(fadeAnimation)

        animationList = listOf(binding.viewFromLeft to R.anim.translate_from_left,
            binding.viewFromBottom to R.anim.translate_from_bottom,
            binding.viewFromRight to R.anim.translate_from_right,
            binding.viewFromTop to R.anim.translate_from_top)

        val firstAnim = animationList[0]
        animateView(firstAnim.first, firstAnim.second)

        GlobalScope.launch {
            interactors.refreshAllData()
        }
    }

    private fun animateView(view: View, @AnimRes animResource : Int){
        val translation = AnimationUtils.loadAnimation(this@SplashActivity, animResource)
        translation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                if (animatingViewCount == 3) {
                    animsEnded = true
                    startMainActivity()
                    return
                }

                animatingViewCount++
                val currentAnim = animationList[animatingViewCount]
                animateView(currentAnim.first, currentAnim.second)
            }
        })
        view.startAnimation(translation)
        view.visibility = View.VISIBLE
    }

    private fun startMainActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}