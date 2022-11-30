package com.example.gestorednd.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.gestorednd.R
import com.example.gestorednd.MainMenuFragments.SheetNavFragment
import com.example.gestorednd.CharacterSheetFragments.StatsFragment

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