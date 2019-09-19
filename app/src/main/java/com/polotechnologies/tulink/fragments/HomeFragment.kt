package com.polotechnologies.tulink.fragments


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.databinding.FragmentHomeBinding
import com.google.firebase.database.ServerValue


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    lateinit var mAuth:FirebaseAuth
    lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        databaseReference = FirebaseDatabase.getInstance().reference
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false)
        
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_home) as NavHostFragment

        binding.btmNavHome
            .setupWithNavController(navController = navHostFragment.navController)

        navHostFragment.navController.addOnDestinationChangedListener{_, destination,_ ->
            binding.homeFragmentToolbar.title = destination.label

        }

        mAuth = FirebaseAuth.getInstance()

        binding.homeFragmentToolbar.inflateMenu(R.menu.menu_main)
        binding.homeFragmentToolbar.setOnMenuItemClickListener { item ->
            when(item!!.itemId){
                R.id.action_logOut -> {
                    val currentUserId = mAuth.currentUser?.uid
                    if(currentUserId?.isNotEmpty()!!){
                        databaseReference.child("users")
                            .child(currentUserId)
                            .child("online")
                            .setValue(ServerValue.TIMESTAMP)
                    }

                    mAuth.signOut()
                    Toast.makeText(context, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                    true
                }
                R.id.action_settings->{
                    Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                else->{
                    false
                }

            }
        }
        return binding.root

    }



}
