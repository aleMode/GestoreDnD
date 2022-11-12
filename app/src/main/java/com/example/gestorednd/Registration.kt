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
import java.util.regex.Pattern

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

        var register = findViewById<Button>(R.id.btnLogin);
        var email = findViewById<TextView>(R.id.txtEmail)
        var emailConf = findViewById<TextView>(R.id.txtEmailConf)
        var password = findViewById<TextView>(R.id.txtPwd)
        var passwordConf = findViewById<TextView>(R.id.txtPwdConf)
        val email_pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        val password_pattern = Pattern.compile(
            "^(?=.*[0-9])"
                    + "(?=.*[a-z])(?=.*[A-Z])"
                    + "(?=\\S+$).{8,20}"
        )

        register.setOnClickListener {
            if (email_pattern.matcher(email.text).matches()){
                if(password_pattern.matcher(password.text).matches()){
                    if(email.text == emailConf.text) {
                        if(password.text == passwordConf.text) {
                            auth.createUserWithEmailAndPassword(
                                email.text as String,
                                password.text as String
                            ).addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    findViewById<TextView>(R.id.txtError).text = ""
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
                        }else{findViewById<TextView>(R.id.txtError).text = "Password diverse"}
                    }else{findViewById<TextView>(R.id.txtError).text = "Indirizzi email diversi"}
                }else{findViewById<TextView>(R.id.txtError).text = "Password deve contenere almeno una lettera minuscola, maiuscola e un numero"}
            }else{findViewById<TextView>(R.id.txtError).text = "Indirizzo email non valido"}




        }





    }
}