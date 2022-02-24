package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBaseRefrence: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp =findViewById(R.id.btn_sign_up)

        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            signUp(name, email, password)
        }

    }

    private fun signUp(name:String, email:String, password:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDataBase(name,email,mAuth.currentUser?.uid!!)
                    finish()
                    startActivity(Intent(this@SignUpActivity, LogInActivity::class.java))

                } else {
                    Toast.makeText(this@SignUpActivity, "Too Lazy to fix the error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDataBase(name: String, email: String, uid: String) {
        mDataBaseRefrence = FirebaseDatabase.getInstance().reference
        mDataBaseRefrence.child("user").child(uid).setValue(User(name,email,uid))
    }

}