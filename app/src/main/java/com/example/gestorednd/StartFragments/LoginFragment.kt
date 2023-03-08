package com.example.gestorednd.StartFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.gestorednd.Activities.MenuActivity
import com.example.gestorednd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment() : Fragment() {
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)

        auth = Firebase.auth                    //Initialize Firebase Auth
        val currentUser = auth.currentUser      // Check if user is signed in (non-null) and update
                                                //      UI accordingly.
        if(currentUser != null){
            // INSERISCI PAGINA DOPO
        }

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = view.findViewById<TextView>(R.id.txtEmailLogin).text.trim().toString()
            val password = view.findViewById<TextView>(R.id.txtPwdLogin).text.trim().toString()
            val error = view.findViewById<TextView>(R.id.txtErrorLogin)

            if(email != "" && password != ""){
                auth.signInWithEmailAndPassword(
                    email, password
                ).addOnCompleteListener(requireActivity()){ task ->
                    if(task.isSuccessful) {
                        val intent = Intent(context, MenuActivity::class.java)
                        startActivity(intent)
                    }else {
                        error.text = "Password o mail errate"
                    }
                }
            }else{
                error.text = "Inserire email e password"
            }
        }

        return view
    }
}