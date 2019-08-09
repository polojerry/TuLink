package com.polotechnologies.tulink.fragments


import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
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
class HomeFragment : Fragment(),PopupMenu.OnMenuItemClickListener {

    lateinit var binding : FragmentHomeBinding
    lateinit var mAuth:FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    lateinit var popUpListener: PopupMenu.OnMenuItemClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        databaseReference = FirebaseDatabase.getInstance().reference
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false)
        
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_home) as NavHostFragment

        binding.btmNavHome
            .setupWithNavController(navController = navHostFragment.navController)

        setHasOptionsMenu(true)

        popUpListener = this

        mAuth = FirebaseAuth.getInstance()
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.tvFragmentTittle.text = destination.label
        }

        binding.actionLogOut.setOnClickListener{
            showMenu(it)
        }

        return binding.root
    }


    private fun signOut() {
        mAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        val currentUserId = mAuth.currentUser?.uid
        if(currentUserId?.isNotEmpty()!!){
            databaseReference
                .child("users")
                .child(currentUserId)
                .child("online")
                .setValue("online")
        }

    }

    override fun onStop() {
        super.onStop()
        val currentUserId = mAuth.currentUser?.uid
        if(currentUserId?.isNotEmpty()!!){
            databaseReference.child("users")
                .child(currentUserId)
                .child("online")
                .setValue(ServerValue.TIMESTAMP)
        }
    }

    fun showMenu(v: View) {
        PopupMenu(context!!, v).apply {
            // MainActivity implements OnMenuItemClickListener
            setOnMenuItemClickListener(this@HomeFragment.popUpListener)
            inflate(R.menu.menu_main)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logOut -> {
                signOut()
                true
            }
            R.id.action_settings -> {
                true
            }
            else -> false
        }
    }
}
