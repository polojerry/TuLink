package com.polotechnologies.tulink


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_home) as NavHostFragment

        view.findViewById<BottomNavigationView>(R.id.btm_nav_home)
            .setupWithNavController(navController = navHostFragment.navController)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar_home)

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, arguments ->
            toolbar.title = destination.label
        }

        return view
    }


}
