package com.example.gestorednd.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.fragment.app.FragmentManager
import com.example.gestorednd.StartFragments.LoginFragment
import com.example.gestorednd.R
import com.example.gestorednd.StartFragments.RegistrationFragment
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    companion object{
        var offline = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Fragmentmanager per gestire i frammenti di login e registrazione
        val fm : FragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val regFragment = RegistrationFragment()
        fm.beginTransaction().add(R.id.fragmentContainerView, loginFragment)
            .addToBackStack(null).commit()
        //questo serve per uno strano bug visivo con gli hint delle caselle di testo a cui non ho
        //trovato alcuna soluzione o documentazione
        fm.beginTransaction().replace(R.id.fragmentContainerView, loginFragment)
            .addToBackStack(null).commit()

        var login : Boolean = true
        val regLog = findViewById<TextView>(R.id.txtSwap)

        //Swap per i fragment di registrazione e login
        regLog.setOnClickListener{
            if(login) {
                swapToReg(fm)
                regLog.text = getString(R.string.Login)
            }
            else {
                swapToLog(fm)
                regLog.text = getString(R.string.Register)
            }
            login = !login
        }

        //Accesso alla modalità offline
        var txtOffline = findViewById<TextView>(R.id.txtOffline)
        txtOffline.setOnClickListener{
            MainActivity.offline = true
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    //funzione per swappare il frammento da quello di login a quello di registrazione
    fun swapToReg(fm : FragmentManager){
        val newReg = RegistrationFragment()
        fm.beginTransaction().replace(R.id.fragmentContainerView, newReg)
            .addToBackStack(null).commit()
    }

    //funzione per swappare il frammento da quello di login a quello di registrazione
    fun swapToLog(fm : FragmentManager){
        val newLog = LoginFragment()
        fm.beginTransaction().replace(R.id.fragmentContainerView, newLog)
            .addToBackStack(null).commit()
    }

    //funzione per pulire i files locali ed evitare conflitti nel caso di login con nuovo account
    fun loginCleanup(email : String){
        val dir = File(this.filesDir.path)
        val lastlog = File(this.filesDir, "lastLogin.txt")
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
        val dir = File(this.filesDir.path)
        dir.listFiles()?.forEach {
            if (it.isFile && it.name.endsWith(".json")) {
                it.delete()
            }
        }
        File(dir,"lastLogin.txt").delete()
    }

}