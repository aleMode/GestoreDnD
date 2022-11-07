package com.example.gestorednd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.gestorednd.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Registration : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth //Initialize Firebase Auth

        val login = findViewById<TextView>(R.id.txtLogin)
        login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        var register = findViewById<Button>(R.id.btnRegistra);
        var email = findViewById<TextView>(R.id.txtEmail)
        var emailConf = findViewById<TextView>(R.id.txtEmailConf)
        var password = findViewById<TextView>(R.id.txtPwd)
        var passwordConf = findViewById<TextView>(R.id.txtPwdConf)

        register.setOnClickListener {
            //INSERISCI CONTROLLI
            if(email.text != emailConf.text) findViewById<TextView>(R.id.txtError).text = "Indirizzi email diversi"
            else{
                if(password.text != password.text) findViewById<TextView>(R.id.txtError).text = "Password diversw"
                else{
                    auth.createUserWithEmailAndPassword(email.text as String, password.text as String)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "createUserWithEmail:success")
                                //val user = auth.currentUser
                                //updateUI(user)
                            } else {
                                // If sign in fails, display a message to the user.
                               // Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                //Toast.makeText(baseContext, "Authentication failed.",
                                    //Toast.LENGTH_SHORT).show()
                                //updateUI(null)
                            }
                        }
                }
                }
            }

        }





}