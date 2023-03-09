package com.example.gestorednd.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.gestorednd.StartFragments.LoginFragment
import com.example.gestorednd.R
import com.example.gestorednd.StartFragments.RegistrationFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fm : FragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val regFragment = RegistrationFragment()
        fm.beginTransaction().add(R.id.fragmentContainerView, loginFragment).addToBackStack(null).commit()
        //questo serve per uno strano bug visivo con gli hint delle caselle di testo a cui non ho
        //trovato alcuna soluzione o documentazione
        fm.beginTransaction().replace(R.id.fragmentContainerView, loginFragment).addToBackStack(null).commit()

        var login : Boolean = true
        val regLog = findViewById<TextView>(R.id.txtSwap)

        //Swap per i fragment di registrazione e login
        regLog.setOnClickListener{
            if(login) {
                val newReg = RegistrationFragment()
                fm.beginTransaction().replace(R.id.fragmentContainerView, newReg).addToBackStack(null).commit()

                regLog.text = getString(R.string.Login)
            }
            else {
                val newLog = LoginFragment()
                fm.beginTransaction().replace(R.id.fragmentContainerView, newLog).addToBackStack(null).commit()

                regLog.text = getString(R.string.Register)
            }
            login = !login
        }

    }

}