package com.example.mobile_labs

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.mobile_labs.home.HomeFragment
import com.example.mobile_labs.model.User
import com.example.mobile_labs.onboard.OnboardFragment
import com.example.mobile_labs.signIn.SignInFragment
import com.example.mobile_labs.signUp.SignUpFragment

class MainActivity : FragmentActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            navigateToOnboard()
        }
    }

    fun navigateToOnboard() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, OnboardFragment())
            .commit()
    }

    fun navigateToSignIn(user: User? = null, email: String? = null, password: String? = null) {
        val fragment = SignInFragment.newInstance(user, email, password)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToSignUp() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignUpFragment())
            .addToBackStack(null)
            .commit()
    }

    fun navigateToHome(userName: String? = null) {
        val fragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putString("name", userName)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}