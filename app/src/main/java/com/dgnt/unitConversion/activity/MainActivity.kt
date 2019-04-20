package com.dgnt.unitConversion.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.dgnt.unitConversion.R
import com.dgnt.unitConversion.exception.ConversionException
import com.dgnt.unitConversion.model.unit.Unit
import com.dgnt.unitConversion.service.CalculatorService
import com.dgnt.unitConversion.service.ConversionService
import com.dgnt.unitConversion.service.UnitGeneratorService
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.AppCompatActivityInjector
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AppCompatActivityInjector {
    override val injector: KodeinInjector = KodeinInjector()

    private val conversionService: ConversionService by instance()

    private val calculatorService: CalculatorService by instance()
    private val unitGeneratorService: UnitGeneratorService by instance()
    private lateinit var arrayAdapter: ArrayAdapter<Unit>
    private lateinit var unitLayoutContainerVertical: LinearLayout
    private lateinit var focusedView:View

    private data class UnitLayout(val valueEditText: EditText, val unitSpinner: Spinner)

    private val unitLayoutMap = mutableMapOf<View, UnitLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeInjector()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        fab.setOnClickListener { _ ->
            addUnitLayout()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val jsonString = resources.openRawResource(R.raw.default_units).readBytes().toString(Charsets.UTF_8)

        val units = unitGeneratorService.generateUnitsFromJsonString(jsonString)

        unitLayoutContainerVertical = findViewById(R.id.unit_layout_container_ll)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

       val firstView:View = addUnitLayout()
        focusedView = firstView

        /*
        val firstUnit = units[4]
        val secondUnit = units[0]

        val conversion = conversionService.getEquivalentValue(firstUnit, secondUnit)


        textView3.setText("1 ${secondUnit.name} is equivalent to $conversion ${firstUnit.name}")
        */
    }

    private fun addUnitLayout():View {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_unit, null)
        val deleteUnitLayoutIV = view.findViewById<ImageView>(R.id.delete_unit_layout_iv)
        deleteUnitLayoutIV.setOnClickListener {
            unitLayoutContainerVertical.removeView(view)
            unitLayoutMap.remove(view)
        }

        val unitSpinner = view.findViewById<Spinner>(R.id.unit_spinner)
        unitSpinner.adapter = arrayAdapter

        val valueET = view.findViewById<EditText>(R.id.value_et)
        valueET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (currentFocus!=valueET)
                    return

                s?.let {
                    it.toString().toDoubleOrNull()?.let {theValue ->
                        convertEverything(theValue, unitSpinner.selectedItem as Unit)
                    }
                }

            }

        })
        valueET.setOnFocusChangeListener{_,isFocus ->
            if (isFocus){
                focusedView = view
            }
        }

        unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val unitLayoutOpt = unitLayoutMap[focusedView]
                unitLayoutOpt?.let {unitLayout->
                    unitLayout.valueEditText.text.toString().toDoubleOrNull()?.let {
                        convertEverything(it, unitLayout.unitSpinner.selectedItem as Unit)
                    }
                }


            }

        }

        unitLayoutContainerVertical.addView(view)
        unitLayoutMap[view] = UnitLayout(valueET, unitSpinner)

        return view

    }

    private fun convertEverything(value:Double, toUnit:Unit){
        for ((_, unitLayout) in unitLayoutMap) {
            val fromUnit =  unitLayout.unitSpinner.selectedItem as Unit
            var textValue = ""
            try {
                val convertedValue = conversionService.getEquivalentValue(fromUnit, toUnit)
                textValue = "${value * convertedValue}"

            } catch (e: ConversionException){

            }
            val currentValueEditText = unitLayout.valueEditText
            val hasFocus = currentValueEditText.hasFocus()
            if (!hasFocus)
                currentValueEditText.setText(textValue)

        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        destroyInjector()
        super.onDestroy()
    }
}
