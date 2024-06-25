package com.example.roomies

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.data.Chat
import com.example.roomies.data.Group
import com.example.roomies.data.GroupChat
import com.example.roomies.data.History
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class ChatMain : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var inputBox: TextView
    private lateinit var sendButton: Button

    private lateinit var chatDisplay: RecyclerView
    private lateinit var chatArray: MutableList<Chat>
    private lateinit var ChatAdapter: ChatAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatmain)

        inputBox = findViewById(R.id.chatInputBox)
        sendButton = findViewById(R.id.chatSendButton)

        chatDisplay = findViewById(R.id.chatDisplay)
        chatDisplay.layoutManager = LinearLayoutManager(this)
        chatDisplay.setHasFixedSize(true)
        chatArray = mutableListOf()
        ChatAdapter = ChatAdapter(chatArray)
        chatDisplay.adapter = ChatAdapter

        auth = Firebase.auth
        val groupID = getGroupFromIntent().id

        //get chat, create if does not exist
        val groupChatRef = Firebase.firestore.collection("chats").document(groupID.toString()) //bad practice?
        groupChatRef.get().addOnCompleteListener{docRef ->
            if (docRef.isSuccessful()) {
                val document: DocumentSnapshot = docRef.getResult()
                if (document.exists()) {
                    Log.d("LOOKING FOR CHAT", "Document exists!")
                } else {
                    Log.d("LOOKING FOR CHAT", "Document does not exist!")
                    //create document!
                    createGroupChat(groupChatRef);
                }
            } else {
                Log.d("LOOKING FOR CHAT", "Failed with: ", docRef.getException())
            }
        }

        //update chat
        realtimeUpdateListener(groupChatRef)

        //send messages
        sendButton.setOnClickListener {
            sendMessage(groupChatRef)
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    private fun getGroupFromIntent(): Group {

        return Group(
            id = intent.getStringExtra("id"),
            //name = intent.getStringExtra("name"),
            //owner = intent.getStringExtra("owner"),
            members = intent.getStringExtra("members").toString()
                .removePrefix("[").removeSuffix("]")
                .split(", ").toMutableList()
        )
    }

    //get changes to chat
    private fun realtimeUpdateListener(groupChatRef: DocumentReference) {
        groupChatRef.addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> {}//Log.e("ERROR", e.message)
                documentSnapshot != null && documentSnapshot.exists() -> {
                    val obj = documentSnapshot.toObject<GroupChat>() //parse data
                    if (obj != null) {
                        displayMessages(obj) //update display
                    }
                }
            }
        }
    }

    //create new group chat with welcome message
    private fun createGroupChat(groupChatRef: DocumentReference) {
        val chatData = GroupChat(chat = MutableList<Chat>(1) { Chat(
            id = "0",
            name = "Roomies",
            time = java.util.Calendar.getInstance().time.toString(),
            text = "The group chat was created") })

        groupChatRef.set(chatData)
            .addOnSuccessListener { Log.d("MAKING GROUP CHAT", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("MAKING GROUP CHAT", "Error writing document", e) }
    }

    //send new message to database
    private fun sendMessage(groupChatRef: DocumentReference) {
        val newMessage = mapOf(
            "id" to auth.currentUser?.uid,
            "name" to auth.currentUser?.displayName,
            "time" to java.util.Calendar.getInstance().time.toString(),
            "text" to inputBox.text.toString())
        groupChatRef.update("chat", FieldValue.arrayUnion(newMessage))
            .addOnSuccessListener {
                inputBox.setText("")
            }
            .addOnFailureListener {}
    }

    //displays all messages based on GroupChat object
    private fun displayMessages(obj: GroupChat) {
        chatArray = obj.chat!!
        ChatAdapter = ChatAdapter(chatArray)
        chatDisplay.adapter = ChatAdapter
    }
}
