package com.polotechnologies.tulink.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.viewModels.Chats

class ChatsRecyclerAdapter(private val context: Context, private val chatList: List<Chats>) :
    RecyclerView.Adapter<ChatsRecyclerAdapter.ViewHolder>() {

    private val layoutInflater  =  LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_chats,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = chatList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]

        //holder.profilePictureChats.setImageDrawable(R.drawable.chat.profilePictureChats)
        holder.profileNameChats.text = chat.profileNameChats
        holder.lastMessageChats.text = chat.lastMessageChats
        //holder.lastMessageChatsTime.text = chat.lastMessageChatsTime.toString()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val profilePictureChats  = itemView.findViewById<ImageView>(R.id.img_profile_pic_chats)
        val profileNameChats  = itemView.findViewById<AppCompatTextView>(R.id.tv_profile_name_chats)
        val lastMessageChats  = itemView.findViewById<AppCompatTextView>(R.id.tv_last_message_chat)
        val lastMessageChatsTime  = itemView.findViewById<AppCompatTextView>(R.id.tv_last_message_chat_time)


    }
}