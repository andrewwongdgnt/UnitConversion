package com.dgnt.unitConversion.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.dgnt.unitConversion.R
import com.dgnt.unitConversion.fragment.CalculatorFragment
import com.dgnt.unitConversion.fragment.ConverterFragment
import com.dgnt.unitConversion.fragment.EditorFragment
import com.dgnt.unitConversion.service.CalculatorService
import com.dgnt.unitConversion.service.ConversionService
import com.dgnt.unitConversion.service.UnitGeneratorService
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.AppCompatActivityInjector
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.content.Context
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AppCompatActivityInjector {


    override val injector: KodeinInjector = KodeinInjector()

    private val conversionService: ConversionService by instance()

    private val calculatorService: CalculatorService by instance()
    private val unitGeneratorService: UnitGeneratorService by instance()
    private lateinit var converterFragment: ConverterFragment
    private lateinit var calculatorFragment: CalculatorFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeInjector()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        fab.setOnClickListener { _ ->
            getConverterFragment().addUnitLayoutS()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, getConverterFragment()).commit()

    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        fab.alpha = 1.0f
        when (item.itemId) {
            R.id.nav_converter -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, getConverterFragment()).commit()
            }
            R.id.nav_calculator -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, getCalculatorFragment()).commit()
                fab.alpha = 0.0f
                val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus.windowToken,0)
            }

            R.id.nav_editor -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EditorFragment()).commit()
            }
            R.id.nav_contact -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getConverterFragment(): ConverterFragment {
        if (!::converterFragment.isInitialized) {
            converterFragment = ConverterFragment.newInstance(conversionService, unitGeneratorService)
        }
        return converterFragment
    }

    private fun getCalculatorFragment(): CalculatorFragment {
        if (!::calculatorFragment.isInitialized) {
            calculatorFragment = CalculatorFragment.newInstance(calculatorService)
        }
        return calculatorFragment
    }

    override fun onDestroy() {
        destroyInjector()
        super.onDestroy()
    }
}
