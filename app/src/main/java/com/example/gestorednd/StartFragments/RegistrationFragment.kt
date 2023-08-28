package com.example.gestorednd.StartFragments

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestorednd.Activities.MenuActivity
import com.example.gestorednd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.util.regex.Pattern

class RegistrationFragment() : Fragment() {
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_registration, container, false)

        var email = view.findViewById<TextView>(R.id.txtEmail)
        var emailConf = view.findViewById<TextView>(R.id.txtEmailConf)
        var password = view.findViewById<TextView>(R.id.txtPwd)
        var passwordConf = view.findViewById<TextView>(R.id.txtPwdConf)
        var error = view.findViewById<TextView>(R.id.txtError)

        auth = Firebase.auth //Initialize Firebase Auth
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val ref: DatabaseReference = database.reference.child("profile")

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
            error.text = ""

            val emailTxt = SpannableString(email.text.trim()).toString()
            val passTxt = SpannableString(password.text.trim()).toString()
            val emailConfTxt = SpannableString(emailConf.text.trim()).toString()
            val passConfTxt = SpannableString(passwordConf.text.trim()).toString()

            if (email_pattern.matcher(emailTxt).matches()){
                if(password_pattern.matcher(passTxt).matches()){
                    if(emailTxt == emailConfTxt) {
                        if(passTxt == passConfTxt) {
                            auth.createUserWithEmailAndPassword(
                                emailTxt, passTxt
                            ).addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    auth.signInWithEmailAndPassword(emailTxt, passTxt)
                                        .addOnCompleteListener(requireActivity()) { task ->
                                            if (task.isSuccessful) {
                                                loginCleanup(emailTxt)
                                                val intent =
                                                    Intent(context, MenuActivity::class.java)
                                                //impedisce di tornare indietro se non
                                                // dall'apposito pulsante
                                                intent.addFlags(
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                                )
                                                startActivity(intent)
                                            } else {
                                                Toast.makeText(
                                                    context, getText(R.string.AuthFail),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                    //creazione file con la lista dei personaggi
                                    val file = File(context?.filesDir, "characters.json")
                                    file.createNewFile()

                                    /*potenziale upgrade con impostazione di username*/
                                } else {
                                    // If sign in fails, display a message to the user.
                                    // Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                    Toast.makeText(context, getText(R.string.RegFail),
                                    Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{error.text = "Password diverse"}
                    }else{error.text = "Indirizzi email" + " diversi"}
                }else{error.text = "Password deve contenere almeno una lettera minuscola, una " +
                        "maiuscola e un numero"}
            }else{error.text = "Indirizzo email non valido"}

        }
        return view
    }

    //funzione per pulire i files locali ed evitare conflitti nel caso di login con nuovo account
    fun loginCleanup(email : String){
        val dir = File(context?.filesDir?.path)
        val lastlog = File(context?.filesDir, "lastLogin.txt")
        if(!lastlog.exists()){
            lastlog.createNewFile()
            lastlog.writeText(email)
        }else {
            val reader = BufferedReader(FileReader(lastlog))
            val line = reader.readLine()
            reader.close()
            if (!line.contains(email)) {
                dir.listFiles()?.forEach {
                    if (it.isFile && it.name.endsWith(".json")) {
                        it.delete()
                    }
                }
                lastlog.writeText(email)
            }
        }
    }

    //funzione per pulire semplicemente i files nella directory filesdir
    fun hardCleanup(){
        val dir = File(context?.filesDir?.path)
        dir.listFiles()?.forEach {
            if (it.isFile && it.name.endsWith(".json")) {
                it.delete()
            }
        }
        File(dir,"lastLogin.txt").delete()
    }
}