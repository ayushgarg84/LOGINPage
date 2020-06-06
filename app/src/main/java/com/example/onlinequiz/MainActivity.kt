package com.example.onlinequiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.model.User
import com.google.firebase.database.*
import com.rengwuxian.materialedittext.MaterialEditText

class MainActivity : AppCompatActivity() {
    lateinit var edtNewUser: MaterialEditText
    lateinit var edtNewPassword: MaterialEditText
    lateinit var edtNewEmail: MaterialEditText
    lateinit var edtUser: MaterialEditText
    lateinit var edtPassword: MaterialEditText
    lateinit var btnSignUp: Button
    lateinit var btnSignIn: Button

    //firebase variables
    var database: FirebaseDatabase? = null
    var users: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //firebase variables
        database = FirebaseDatabase.getInstance()
        users = database!!.getReference("Users")
        edtUser = findViewById(R.id.edtUser)
        edtPassword = findViewById(R.id.edtPassword)
        btnSignIn = findViewById(R.id.btn_sign_in)
        btnSignUp = findViewById(R.id.btn_sign_up)
        btnSignIn.setOnClickListener(View.OnClickListener { signIn(edtUser.getText().toString(), edtPassword.getText().toString()) })
        btnSignUp.setOnClickListener(View.OnClickListener { showSignUpDialog() })
    }

    private fun signIn(user: String, pwd: String) {
        users!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(user).exists()) {
                    if (!user.isEmpty()) {
                        val login = dataSnapshot.child(user).getValue(User::class.java)
                        if (login!!.password == pwd) Toast.makeText(this@MainActivity, "login ok!", Toast.LENGTH_SHORT).show() else Toast.makeText(this@MainActivity, "wrong password", Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(this@MainActivity, "enter username", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this@MainActivity, "User doesn't exists", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showSignUpDialog() {
        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setTitle("Sign up")
        alertDialog.setMessage("please fill full information")
        val inflater = this.layoutInflater
        val sign_up_layout = inflater.inflate(R.layout.sign_up_layout, null)
        edtNewUser = sign_up_layout.findViewById(R.id.edtNewUserName)
        edtNewPassword = sign_up_layout.findViewById(R.id.edtNewPassword)
        edtNewEmail = sign_up_layout.findViewById(R.id.edtNewEmail)
        alertDialog.setView(sign_up_layout)
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp)
        alertDialog.setNegativeButton("NO") { dialogInterface, i -> dialogInterface.dismiss() }
        alertDialog.setPositiveButton("yes") { dialogInterface, which ->
            val user = User(edtNewUser.getText().toString(), edtNewPassword.getText().toString(), edtNewEmail.getText().toString())
            users!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child(user.userName).exists()) Toast.makeText(this@MainActivity, "User already exists", Toast.LENGTH_SHORT).show() else {
                        users!!.child(user.userName)
                                .setValue(user)
                        Toast.makeText(this@MainActivity, "user registration success!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            dialogInterface.dismiss()
        }
        alertDialog.show()
    }
}