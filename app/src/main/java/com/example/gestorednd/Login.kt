package com.example.gestorednd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.gestorednd.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth //Initialize Firebase Auth
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser != null){
            // INSERISCI PAGINA DOPO
        }

        val registrami = findViewById<TextView>(R.id.txtRegistra)
        registrami.setOnClickListener {
           val intent = Intent(this, Registration::class.java)
           startActivity(intent)
        }

        val email = findViewById<TextView>(R.id.txtEmail)
        val password = findViewById<TextView>(R.id.txtPwd)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            auth.signInWithEmailAndPassword(
                email.text as String,
                password.text as String
            ).addOnCompleteListener(this){ task ->
                if(task.isSuccessful) {

                }else {

                }
            }
        }
    }
}