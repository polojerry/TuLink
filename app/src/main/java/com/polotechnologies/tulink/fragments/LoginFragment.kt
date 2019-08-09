package com.polotechnologies.tulink.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.databinding.FragmentLoginBinding

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

        return binding.root
    }

    private fun loginUser() {
        if(!verifyUserDetails()) return

        val userEmail = binding.etEmailLogin.text.toString().trim()
        val userPass  =  binding.etPassLogin.text.toString().trim()

        mAuth.signInWithEmailAndPassword(userEmail, userPass)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(context,"Login Successful",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }else{
                    Toast.makeText(context,"Incorrect Email/ Password",Toast.LENGTH_SHORT).show()
                    binding.etEmailLogin.text?.clear()
                    binding.etPassLogin.text?.clear()
                    binding.etEmailLogin.requestFocus()

                }
            }
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
