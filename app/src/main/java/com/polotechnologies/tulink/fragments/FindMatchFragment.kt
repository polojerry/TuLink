package com.polotechnologies.tulink.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.polotechnologies.tulink.utils.GlideApp
import com.polotechnologies.tulink.fragments.HomeFragmentDirections
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.dataModels.Profile
import com.polotechnologies.tulink.databinding.FragmentFindMatchBinding
import de.hdodenhof.circleimageview.CircleImageView


/**
 * A simple [Fragment] subclass.
 *
 */
class FindMatchFragment : Fragment() {

    lateinit var binding:FragmentFindMatchBinding
    lateinit var mAuth : FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_find_match, container, false)
        binding.rvFindMatch.layoutManager = GridLayoutManager(context,2)
        mAuth = FirebaseAuth.getInstance()
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        loadMatches()

        return binding.root
    }

    private fun loadMatches() {
        val query = FirebaseDatabase.getInstance()
            .reference
            .child("users")
            .limitToFirst(10)

        val options  = FirebaseRecyclerOptions.Builder<Profile>()
            .setQuery(query, Profile::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = object  : FirebaseRecyclerAdapter<Profile, ProfileHolder>(options){


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHolder {

                return ProfileHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_find_match,
                        parent, false
                    )
                )
            }

            override fun onBindViewHolder(holder: ProfileHolder, position: Int, model: Profile) {

                holder.bind(model)

                holder.itemView.setOnClickListener {

                    val action =
                        HomeFragmentDirections.actionHomeFragmentToUserProfileFragment(model.uId!!)
                    activity?.findNavController(R.id.mainNavFragment)?.navigate(action)

                }
            }
        }

        binding.rvFindMatch.adapter= adapter

    }

    class ProfileHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        var profilePicture: AppCompatImageView = itemView.findViewById(R.id.img_profile_pic_find_match)
        var profileName: AppCompatTextView = itemView.findViewById(R.id.tv_profile_name_find_match)
        var profileOnline: View = itemView.findViewById(R.id.find_match_online_status)

        fun bind(model: Profile) {


            GlideApp.with(itemView.context)
                    .load(model.imageUrl)
                    .into(profilePicture)


            profileName.text = model.userName

            if(model.online == "online"){
                profileOnline.setBackgroundResource(R.drawable.online_dot)
            }

        }

    }
}
