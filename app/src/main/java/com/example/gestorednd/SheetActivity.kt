package com.example.gestorednd

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gestorednd.databinding.ActivitySheetBinding

class SheetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet)
        val fm : FragmentManager = supportFragmentManager
        val navFrag = SheetNavFragment()
        val statsFrag = StatsFragment()

        fm.beginTransaction().add(R.id.navFragContainer, navFrag).commit()
        fm.beginTransaction().add(R.id.sheetPortionContainer, statsFrag).commit()
    }

}