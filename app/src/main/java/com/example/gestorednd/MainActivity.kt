package com.example.gestorednd

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.fragment.app.FragmentManager
import com.example.gestorednd.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fm : FragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val registerFragment = RegistrationFragment()
        fm.beginTransaction().add(R.id.fragmentContainerView, loginFragment).commit()

        var login : Boolean = true
        val regLog = findViewById<TextView>(R.id.txtSwap)
        //Swap per i fragment di registrazione e login
        regLog.setOnClickListener{
            //temp
            val intent = Intent(this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
            //

            if(login) {
                fm.beginTransaction().replace(R.id.fragmentContainerView, registerFragment).commit()
                regLog.text = "Login"
            }
            else {
                fm.beginTransaction().replace(R.id.fragmentContainerView, loginFragment).commit()
                regLog.text = "Registrami"
            }
            login = !login
        }

    }

}