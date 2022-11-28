package com.example.gestorednd

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gestorednd.databinding.ActivityMenuMainBinding
import com.example.gestorednd.ui.login.SheetFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)

        val fm : FragmentManager = supportFragmentManager
        val sheetFragment = SheetFragment()
        val campFragment = CampaignsFragment()
        val txtTitle = findViewById<TextView>(R.id.mainTitle)
        val chipGrp = findViewById<ChipGroup>(R.id.selectChips)
        val chipChars = findViewById<Chip>(R.id.sheetChip)
        val chipCamps = findViewById<Chip>(R.id.campaignChip)

        fm.beginTransaction().add(R.id.fragmentContainerView2, sheetFragment).commit()
        txtTitle.text = "Characters"
        chipChars.isChecked = true


        chipCamps.setOnClickListener(){
            val intent = Intent(this@MenuActivity, SheetActivity::class.java)
            startActivity(intent)
            //fm.beginTransaction().replace(R.id.fragmentContainerView2, campFragment).commit()
            //txtTitle.text = "Campaigns"
        }
        chipChars.setOnClickListener(){
            fm.beginTransaction().replace(R.id.fragmentContainerView2, sheetFragment).commit()
            txtTitle.text = "Characters"
        }
    }

}