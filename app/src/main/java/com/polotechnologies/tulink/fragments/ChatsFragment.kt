package com.polotechnologies.tulink.fragments


import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.emoji.widget.EmojiTextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.polotechnologies.tulink.utils.GlideApp
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.dataModels.Chat
import com.polotechnologies.tulink.dataModels.Profile
import com.polotechnologies.tulink.databinding.FragmentChatsBinding
import com.polotechnologies.tulink.utils.TimeAgo
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class ChatsFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var currentUid : String
    lateinit var mDatabaseReference: DatabaseReference
    lateinit var binding: FragmentChatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chats, container, false)

        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        currentUid = mAuth.currentUser?.uid.toString()

        val layoutManager = LinearLayoutManager(context)
        binding.rvChats.layoutManager = layoutManager


        loadChats()

        return binding.root
    }

    private fun loadChats() {

        val chatsQuery : Query = FirebaseDatabase.getInstance().reference
            .child("chats")
            .child(currentUid)
            .orderByChild("timestamp")


        val options  = FirebaseRecyclerOptions.Builder<Chat>()
            .setQuery(chatsQuery, Chat::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = object  : FirebaseRecyclerAdapter<Chat, ChatHolder>(options){


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {

                return ChatHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_chat,
                        parent, false
                    )
                )
            }

            override fun onBindViewHolder(holder: ChatHolder, position: Int, model: Chat) {

                val chatUid = getRef(position).key!!

                val lastMessageQuery : Query = FirebaseDatabase.getInstance().reference
                    .child("messages")
                    .child(currentUid).child(chatUid)
                    .limitToLast(1)

                lastMessageQuery.addChildEventListener(object : ChildEventListener{

                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        val lastMessage = dataSnapshot.child("message").value.toString()
                        var messageSentTime= 0L
                        if(dataSnapshot.child("timeStamp").value != null){
                            messageSentTime = dataSnapshot.child("timeStamp").value.toString().toLong()
                        }

                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        val sdf = SimpleDateFormat("HH:mm a", Locale.ENGLISH)

                        val dateString = sdf.format(messageSentTime)

                        val isSeen = model.isSeen!!

                        holder.setLastMessage(lastMessage, isSeen,dateString)
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        val lastMessage = dataSnapshot.child("message").value.toString()
                        var messageSentTime= 0L
                        if(dataSnapshot.child("timeStamp").value != null){
                            messageSentTime = dataSnapshot.child("timeStamp").value.toString().toLong()
                        }

                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        val sdf = SimpleDateFormat("HH:mm a", Locale.ENGLISH)

                        val dateString = sdf.format(messageSentTime)

                        val isSeen = model.isSeen!!

                        holder.setLastMessage(lastMessage, isSeen,dateString)
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                        val lastMessage = dataSnapshot.child("message").value.toString()
                        var messageSentTime= 0L
                        if(dataSnapshot.child("timeStamp").value != null){
                            messageSentTime = dataSnapshot.child("timeStamp").value.toString().toLong()
                        }

                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        val sdf = SimpleDateFormat("HH:mm a", Locale.ENGLISH)

                        val dateString = sdf.format(messageSentTime)

                        val isSeen = model.isSeen!!

                        holder.setLastMessage(lastMessage, isSeen,dateString)
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                        val lastMessage = dataSnapshot.child("message").value.toString()
                        var messageSentTime= 0L
                        if(dataSnapshot.child("timeStamp").value != null){
                            messageSentTime = dataSnapshot.child("timeStamp").value.toString().toLong()
                        }

                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        val sdf = SimpleDateFormat("HH:mm a", Locale.ENGLISH)

                        val dateString = sdf.format(messageSentTime)

                        val isSeen = model.isSeen!!

                        holder.setLastMessage(lastMessage, isSeen,dateString)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }


                })

                //Load user Profile
                val userProfileQuery : Query = FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(chatUid)
                userProfileQuery.addValueEventListener(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val profile: Profile = dataSnapshot.getValue(Profile::class.java)!!

                        holder.setProfile(profile)

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })

                holder.itemView.setOnClickListener {
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToMessagesFragment(chatUid)
                    activity?.findNavController(R.id.mainNavFragment)?.navigate(action)
                }

            }

        }

        binding.rvChats.adapter = adapter

    }

    class ChatHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var uId = ""
        var profilePicture : CircleImageView = itemView.findViewById(R.id.img_profile_pic_chats)
        var profileName : AppCompatTextView = itemView.findViewById(R.id.tv_profile_name_chats)
        var lastMessage : EmojiTextView = itemView.findViewById(R.id.tv_last_message_chat)
        var messageSentTime : AppCompatTextView = itemView.findViewById(R.id.tv_last_message_chat_time)
        var onlineStatus : View = itemView.findViewById(R.id.chat_is_online)

        fun setLastMessage(message: String, isSeen: Boolean, messageSentTimeString: String?) {

            if(isSeen){
                lastMessage.text = message
            }else{
                lastMessage.text = message
                lastMessage.typeface = Typeface.DEFAULT_BOLD
            }
            messageSentTime.text = messageSentTimeString

        }

        fun setProfile(profile: Profile) {
            uId = profile.uId!!
            GlideApp.with(itemView)
                .load(profile.imageUrl)
                .into(profilePicture)

            profileName.text = profile.userName

            if(profile.online == "online"){
                onlineStatus.setBackgroundResource(R.drawable.online_dot)
            }

        }

    }

}
