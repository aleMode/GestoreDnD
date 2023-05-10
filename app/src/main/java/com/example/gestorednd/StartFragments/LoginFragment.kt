package com.example.gestorednd.StartFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import com.example.gestorednd.Activities.MainActivity
import com.example.gestorednd.Activities.MenuActivity
import com.example.gestorednd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


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
            val intent = Intent(context, MenuActivity::class.java)
            startActivity(intent)
        }

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            //hardCleanup()
            val email = view.findViewById<TextView>(R.id.txtEmailLogin).text.trim().toString()
            val password = view.findViewById<TextView>(R.id.txtPwdLogin).text.trim().toString()
            val error = view.findViewById<TextView>(R.id.txtErrorLogin)

            if(email != "" && password != ""){
                auth.signInWithEmailAndPassword(
                        email, password
                ).addOnCompleteListener(requireActivity()){ task ->
                    if(task.isSuccessful) {
                        loginCleanup(email)
                        val intent = Intent(context, MenuActivity::class.java)
                        startActivity(intent)
                    }else {
                        error.text = "Password o mail errate"
                    }
                }

                MainActivity.offline = false
            }else{
                error.text = "Inserire email e password"
            }
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
