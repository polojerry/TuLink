package com.polotechnologies.tulink


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.polotechnologies.tulink.adapters.ChatsRecyclerAdapter


/**
 * A simple [Fragment] subclass.
 *
 */
class ChatsFragment : Fragment() {

    lateinit var chatsRecyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_chats, container, false)

        chatsRecyclerView = view.findViewById(R.id.rv_chats)
        chatsRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }


}
