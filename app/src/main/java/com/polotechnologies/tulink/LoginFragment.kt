package com.polotechnologies.tulink


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.set
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.polotechnologies.tulink.databinding.FragmentLoginBinding

/**
 * A simple [Fragment] subclass.
 *
 */
class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login,container,false)

        binding.btnSignUp.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_signUpFragment)
        )

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        return binding.root
    }

    private fun loginUser() {
        val userName = binding.etUsernameLogin.text.toString().trim()
        val userPass  =  binding.etPassLogin.text.toString().trim()

        if(userName.isNotEmpty() && userPass.isNotEmpty()){
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }else{
            Toast.makeText(context,"Invalid Username or Password",Toast.LENGTH_SHORT).show()
            binding.etUsernameLogin.text?.clear()
            binding.etUsernameLogin.requestFocus()
            binding.etPassLogin.text?.clear()
        }

    }

}
