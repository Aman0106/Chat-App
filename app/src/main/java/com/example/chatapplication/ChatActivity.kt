 package com.example.chatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

 class ChatActivity : AppCompatActivity() {

     private lateinit var messageRecyclerView: RecyclerView
     private lateinit var messageBox: TextView
     private lateinit var sendButton: ImageView
     private lateinit var messageAdapter: MessageAdapter
     private lateinit var messageList: ArrayList<Message>
     private lateinit var mDatabaseReference: DatabaseReference

     var senderRoom: String? = null
     var receiverRoom: String? = null

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_chat)

         mDatabaseReference = FirebaseDatabase.getInstance().reference

         val senderUid = FirebaseAuth.getInstance().currentUser?.uid
         val name = intent.getStringExtra("name")
         val receiverUid = intent.getStringExtra("uid")

         supportActionBar?.title = name

         senderRoom = receiverUid + senderUid
         receiverRoom = senderUid + receiverUid

         messageRecyclerView = findViewById(R.id.chat_recycler_view)
         messageBox = findViewById(R.id.edt_message_box)
         sendButton = findViewById(R.id.send_button)
         messageList = ArrayList()
         messageAdapter = MessageAdapter(this,messageList)

         messageRecyclerView.layoutManager = LinearLayoutManager(this)
         messageRecyclerView.adapter = messageAdapter

         mDatabaseReference.child("chats").child(senderRoom!!).child("messages")
             .addValueEventListener(object : ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                     messageList.clear()
                     for(postSnapshot in snapshot.children){
                         val message = postSnapshot.getValue(Message::class.java)

                         messageList.add(message!!)
                     }
                     messageAdapter.notifyDataSetChanged()
                 }

                 override fun onCancelled(error: DatabaseError) {

                 }

             })


         sendButton.setOnClickListener {
             val message = messageBox.text.toString()
             val messagObject = Message(message, senderUid)
             mDatabaseReference.child("chats").child(senderRoom!!).child("messages").push()
                 .setValue(messagObject).addOnSuccessListener {
                     mDatabaseReference.child("chats").child(receiverRoom!!).child("messages").push()
                         .setValue(messagObject)
                     messageBox.text = ""
                 }
         }

     }
}