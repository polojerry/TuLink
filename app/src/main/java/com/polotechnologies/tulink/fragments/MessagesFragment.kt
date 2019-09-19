package com.polotechnologies.tulink.fragments


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.emoji.widget.EmojiTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.polotechnologies.tulink.utils.GlideApp
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.adapters.MessageRecyclerAdapter
import com.polotechnologies.tulink.utils.TimeAgo
import com.polotechnologies.tulink.dataModels.Message
import com.polotechnologies.tulink.dataModels.Profile
import com.polotechnologies.tulink.databinding.FragmentMessagesBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass.
 *
 */
class MessagesFragment : Fragment() {

    lateinit var binding: FragmentMessagesBinding
    lateinit var databaseReference: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var adapter: MessageRecyclerAdapter
    lateinit var receiverUid: String
    lateinit var currentUserId:String
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_messages, container, false
        )

        databaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        receiverUid = MessagesFragmentArgs.fromBundle(arguments!!).uId
        currentUserId = mAuth.currentUser!!.uid
        layoutManager = LinearLayoutManager(context)
        binding.rvMessages.layoutManager = layoutManager
        adapter = MessageRecyclerAdapter()

        loadProfile(receiverUid)

        loadMessages(receiverUid)

        binding.fabSendMessage.setOnClickListener {
            mAuth.currentUser?.uid?.let { it1 -> createChatIfNotExist(it1, receiverUid) }
            sendMessage(receiverUid)
        }

        binding.constraintBack.setOnClickListener {
            findNavController().navigate(R.id.action_messagesFragment_to_homeFragment)
        }

        return binding.root
    }

    private fun loadMessages(receiverUid: String) {
        val currentUid = mAuth.currentUser!!.uid

        val query: Query = databaseReference
            .child("messages")
            .child(currentUid)
            .child(receiverUid)


        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

                return if (viewType == MESSAGE_TYPE_RECEIVED) {
                    MessageViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                            R.layout.item_message_received,
                            parent, false
                        )
                    )
                } else {
                    MessageViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                            R.layout.item_message_sent,
                            parent, false
                        )
                    )
                }
            }

            override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
                holder.bind(model)
            }

            override fun getItemViewType(position: Int): Int {
                val message: Message = getItem(position)

                return if (message.from == receiverUid) {
                    MESSAGE_TYPE_RECEIVED
                } else {
                    MESSAGE_TYPE_SENT
                }

            }
        }

        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                val messagesCount = adapter.itemCount
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

                if(lastVisiblePosition == -1 ||
                    (positionStart >= (messagesCount-1)&&
                            lastVisiblePosition == (positionStart-1))){
                    binding.rvMessages.scrollToPosition(positionStart)
                }
            }
        })

        binding.rvMessages.adapter = adapter

    }


    private fun sendMessage(receiverUid: String) {

        if (binding.etMessageInput.text.toString().isEmpty()) {
            Toast.makeText(context, "Cant Send Empty Message",Toast.LENGTH_SHORT).show()
            return
        }

        val message = binding.etMessageInput.text.toString().trim()

        binding.etMessageInput.text?.clear()

        val currentUserId: String = mAuth.currentUser?.uid!!

        createChatIfNotExist(currentUserId,receiverUid)

        val userReference = "messages/$currentUserId/$receiverUid"
        val receiverReference = "messages/$receiverUid/$currentUserId"

        val messageMap = HashMap<String, Any>()
        messageMap["message"] = message
        messageMap["isSeen"] = false
        messageMap["type"] = "text"
        messageMap["from"] = currentUserId
        messageMap["timeStamp"] = ServerValue.TIMESTAMP


        val messagePush: DatabaseReference = databaseReference
            .child("messages")
            .child(currentUserId)
            .child(receiverUid)
            .push()

        val messagePushKey = messagePush.key

        val messageUsersMap = HashMap<String, Any>()
        messageUsersMap["$userReference/$messagePushKey"] = messageMap
        messageUsersMap["$receiverReference/$messagePushKey"] = messageMap

        databaseReference.updateChildren(messageUsersMap).addOnFailureListener { exception ->
            Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {

            val addChatMap = HashMap<String, Any>()
            addChatMap["seen"] = false
            addChatMap["timeStamp"] = ServerValue.TIMESTAMP

            val userMap = HashMap<String, Any>()
            userMap["chats/$currentUserId/$receiverUid"] = addChatMap
            userMap["chats/$receiverUid/$currentUserId"] = addChatMap

            databaseReference.updateChildren(userMap)
        }

    }

    private fun createChatIfNotExist(currentUid: String, receiverUid: String) {

        val query: Query = FirebaseDatabase.getInstance().reference
            .child("chats")
            .child(currentUid)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(receiverUid)) {

                    val addChatMap = HashMap<String, Any>()
                    addChatMap["seen"] = false
                    addChatMap["timeStamp"] = ServerValue.TIMESTAMP

                    val userMap = HashMap<String, Any>()
                    userMap["chats/$currentUid/$receiverUid"] = addChatMap
                    userMap["chats/$receiverUid/$currentUid"] = addChatMap

                    databaseReference.updateChildren(userMap)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun loadProfile(id: String) {

        val query: Query = FirebaseDatabase.getInstance()
            .reference.child("users")
            .child(id)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val profile = dataSnapshot.getValue(Profile::class.java)
                displayUserProfile(profile)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun displayUserProfile(profile: Profile?) {

        context?.let {
            GlideApp.with(it)
                .load(profile?.imageUrl)
                .into(binding.imgProfilePicMessages)
        }

        binding.tvProfileNameMessages.text = profile?.userName
        if (profile?.online == "online") {
            binding.tvOnlineMessages.text = getString(R.string.status_online)
        } else {
            val timeAgoConverter = TimeAgo()

            val stringTimeAgo = timeAgoConverter.getTimeAgo(profile?.online as Long)
            binding.tvOnlineMessages.text = stringTimeAgo
        }

    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val messageReceivedSent: EmojiTextView = itemView.findViewById(R.id.tv_message)
        private val messageSentTime: AppCompatTextView = itemView.findViewById(R.id.sentTime)

        fun bind(message: Message) {
            val timeStampLong = message.timeStamp.toString().toLong()

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val sdf = SimpleDateFormat("HH:mm a", Locale.ENGLISH)

            val dateString = sdf.format(timeStampLong)

            messageReceivedSent.text = message.message
            messageSentTime.text = dateString.toString()
        }

    }

    companion object {
        private const val MESSAGE_TYPE_SENT = 0
        private const val MESSAGE_TYPE_RECEIVED = 1
    }
}

