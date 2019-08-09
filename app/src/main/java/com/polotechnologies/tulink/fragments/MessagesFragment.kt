package com.polotechnologies.tulink.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.polotechnologies.tulink.utils.GlideApp
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.adapters.MessageRecyclerAdapter
import com.polotechnologies.tulink.utils.TimeAgo
import com.polotechnologies.tulink.dataModels.Message
import com.polotechnologies.tulink.dataModels.Profile
import com.polotechnologies.tulink.databinding.FragmentMessagesBinding
import kotlinx.android.synthetic.main.fragment_messages.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MessagesFragment : Fragment() {

    lateinit var binding: FragmentMessagesBinding
    lateinit var databaseReference: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var adapter : MessageRecyclerAdapter
    lateinit var receiverUid : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_messages, container, false)

        receiverUid = MessagesFragmentArgs.fromBundle(arguments!!).uId

        databaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        binding.rvMessages.layoutManager = LinearLayoutManager(context)
        adapter = MessageRecyclerAdapter()

        loadProfile(receiverUid)

        loadMessages(receiverUid)

        binding.fabSendMessage.setOnClickListener {
            mAuth.currentUser?.uid?.let { it1 -> createChatIfNotExist(it1, receiverUid) }
            sendMessage(receiverUid)
        }

        binding.actionMessagesBack.setOnClickListener {
            findNavController().navigate(R.id.action_messagesFragment_to_homeFragment)
        }

        return binding.root
    }

    private fun sendMessage(receiverUid: String) {

        var message = ""

        if (binding.etMessageInput.text.toString().isNotEmpty()) {
            message = binding.etMessageInput.text.toString().trim()
        }

        binding.etMessageInput.text?.clear()

        val currentUserId: String = mAuth.currentUser?.uid!!

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
        }

    }

    private fun loadMessages(receiverUid: String) {
        val currentUid = mAuth.currentUser!!.uid

        val query: Query = databaseReference
            .child("messages")
            .child(currentUid)
            .child(receiverUid)

        query.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val message:Message = dataSnapshot.getValue(Message::class.java)!!

                adapter.swapList(message)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        binding.rvMessages.adapter = adapter
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
}

