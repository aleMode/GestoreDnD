package com.example.gestorednd

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class RegistrationFragment() : Fragment() {
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_registration, container, false)

        auth = Firebase.auth //Initialize Firebase Auth

        var register = view.findViewById<Button>(R.id.btnLogin);
        var email = view.findViewById<TextView>(R.id.txtEmail)
        var emailConf = view.findViewById<TextView>(R.id.txtEmailConf)
        var password = view.findViewById<TextView>(R.id.txtPwd)
        var passwordConf = view.findViewById<TextView>(R.id.txtPwdConf)
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
                            ).addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    view.findViewById<TextView>(R.id.txtError).text = ""
                                    //Sign in success, update UI with the signed-in user's
                                    //      information
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
                        }else{view.findViewById<TextView>(R.id.txtError).text = "Password diverse"}
                    }else{view.findViewById<TextView>(R.id.txtError).text = "Indirizzi email" +
                            " diversi" }
                }else{view.findViewById<TextView>(R.id.txtError).text = "Password deve contenere " +
                        "almeno " + "una lettera minuscola, maiuscola e un numero"}
            }else{view.findViewById<TextView>(R.id.txtError).text = "Indirizzo email non valido"}




        }
        return view
    }
}