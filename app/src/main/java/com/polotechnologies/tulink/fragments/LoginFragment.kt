package com.polotechnologies.tulink.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.provider.FontRequest
import androidx.databinding.DataBindingUtil
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.databinding.FragmentLoginBinding
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 *
 */
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login,container,false)

        /*binding.loginProgressBar.visibility = View.GONE*/

        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser!=null){
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

        binding.btnSignUp.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_signUpFragment)
        )

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        requestEmoji()

        return binding.root
    }

    private fun requestEmoji() {
        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs
        )
        val config = FontRequestEmojiCompatConfig(context!!, fontRequest)
            .setReplaceAll(true)
        EmojiCompat.init(config)

    }

    private fun loginUser() {
        if(!verifyUserDetails()) return

        val userEmail = binding.etEmailLogin.text.toString().trim()
        val userPass  =  binding.etPassLogin.text.toString().trim()
        /*binding.loginProgressBar.visibility = View.VISIBLE*/

        mAuth.signInWithEmailAndPassword(userEmail, userPass)
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                Toast.makeText(context,"Login Successful",Toast.LENGTH_SHORT).show()

            }.addOnFailureListener(object : OnFailureListener{
                override fun onFailure(p0: Exception) {

                }

            })
    }

    private fun verifyUserDetails(): Boolean {
        if(binding.etEmailLogin.text.toString().trim().isBlank()){
            binding.etEmailLogin.error = "Required"

            return false
        }
        if(binding.etPassLogin.text.toString().trim().isBlank()){
            binding.etPassLogin.error = "Required"

            return false
        }
        return true
    }


}
