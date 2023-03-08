package com.example.gestorednd.StartFragments

import android.os.Bundle
import android.text.SpannableString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.gestorednd.R
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
            var email = view.findViewById<TextView>(R.id.txtEmail)
            var emailConf = view.findViewById<TextView>(R.id.txtEmailConf)
            var password = view.findViewById<TextView>(R.id.txtPwd)
            var passwordConf = view.findViewById<TextView>(R.id.txtPwdConf)

            if (email_pattern.matcher(email.text).matches()){
                if(password_pattern.matcher(password.text).matches()){
                    if(email.text.trim() == emailConf.text.trim()) {
                        if(password.text.trim() == passwordConf.text.trim()) {
                            auth.createUserWithEmailAndPassword(
                                SpannableString(email.text.trim()).toString(),
                                SpannableString(password.text.trim()).toString()
                            ).addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    // Create a new Toast object with the message you want to display
                                    Toast.makeText(activity, "Hello, world!",
                                        Toast.LENGTH_SHORT).show();

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
                            " diversi"}
                }else{view.findViewById<TextView>(R.id.txtError).text = "Password deve contenere " +
                        "almeno " + "una lettera minuscola, maiuscola e un numero"}
            }else{view.findViewById<TextView>(R.id.txtError).text = "Indirizzo email non valido"}




        }
        return view
    }
}