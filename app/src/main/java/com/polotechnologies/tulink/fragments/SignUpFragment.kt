package com.polotechnologies.tulink.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.databinding.FragmentSignUpBinding

/**
 * A simple [Fragment] subclass.
 *
 */
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_up, container, false
        )

        mAuth = FirebaseAuth.getInstance()

        binding.btnSignUpUser.setOnClickListener {
            signUpUser()
        }
        return binding.root
    }

    private fun signUpUser() {
        val isUserDetailsCorrect = verifyUserDetails()

        if (!isUserDetailsCorrect) return

        val userEmail = binding.etEmailSignUp.text.toString().trim()
        val userPass = binding.etPassSignUp.text.toString().trim()

        mAuth.createUserWithEmailAndPassword(userEmail, userPass)
            .addOnSuccessListener {

                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signUpFragment_to_profileFragment)

            }.addOnFailureListener(OnFailureListener {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
            })

    }

    private fun verifyUserDetails(): Boolean {
        if (binding.etEmailSignUp.text.toString().trim().isBlank()) {
            binding.etEmailSignUp.error = "Required"

            return false
        }

        if (binding.etPassSignUp.text.toString().trim().isBlank()) {
            binding.etPassSignUp.error = "Required"

            return false
        }

        if (binding.etConfirmPassSignUp.text.toString().trim().isBlank()) {
            binding.etConfirmPassSignUp.error = "Required"
            return false
        }

        return if (binding.etPassSignUp.text.toString().trim() ==
            binding.etConfirmPassSignUp.text.toString().trim()
        ) {

            true

        } else {
            Toast.makeText(context, "Password Don't Match", Toast.LENGTH_SHORT).show()
            binding.etPassSignUp.text?.clear()
            binding.etConfirmPassSignUp.text?.clear()

            binding.etPassSignUp.requestFocus()

            false
        }

    }

}
