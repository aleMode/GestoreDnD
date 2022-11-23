package com.example.gestorednd

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gestorednd.databinding.ActivityMenuMainBinding
import com.example.gestorednd.ui.login.SheetFragment

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)
        val fm : FragmentManager = supportFragmentManager
        val sheetFragment = SheetFragment()
        fm.beginTransaction().add(R.id.fragmentContainerView2, sheetFragment).commit()

    }

}