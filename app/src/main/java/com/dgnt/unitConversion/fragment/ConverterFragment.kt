package com.dgnt.unitConversion.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.dgnt.unitConversion.R
import com.dgnt.unitConversion.exception.ConversionException
import com.dgnt.unitConversion.model.unit.Unit
import com.dgnt.unitConversion.service.ConversionService
import com.dgnt.unitConversion.service.UnitGeneratorService

private const val CONVERSION_SERVICE_PARAM = "conversionServiceParam"
private const val UNIT_GENERATOR_SERVICE_PARAM = "param2"

class ConverterFragment : Fragment() {

    private lateinit var conversionService: ConversionService
    private lateinit var unitGeneratorService: UnitGeneratorService

    private lateinit var arrayAdapter: ArrayAdapter<Unit>
    private lateinit var unitLayoutContainerVertical: LinearLayout
    private lateinit var focusedView: View

    private data class UnitLayout(val valueEditText: EditText, val unitSpinner: Spinner)

    private val unitLayoutMap = mutableMapOf<View, UnitLayout>()

    private lateinit var fragContext:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            conversionService = it.get(CONVERSION_SERVICE_PARAM) as ConversionService
            unitGeneratorService = it.get(UNIT_GENERATOR_SERVICE_PARAM) as UnitGeneratorService
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragContext = view.context
        val jsonString = resources.openRawResource(R.raw.default_units).readBytes().toString(Charsets.UTF_8)

        val units = unitGeneratorService.generateUnitsFromJsonString(jsonString)

        unitLayoutContainerVertical = view.findViewById(R.id.unit_layout_container_ll)

        arrayAdapter = ArrayAdapter(fragContext, android.R.layout.simple_spinner_item, units)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val firstView: View = addUnitLayout(fragContext)
        focusedView = firstView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_converter, container, false)
    }

    fun addUnitLayoutS(){
        addUnitLayout(fragContext)
    }

    private fun addUnitLayout(context:Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_unit, null)
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

                if (activity?.currentFocus?.equals(valueET)!=true)
                    return

                s?.let {
                    it.toString().toDoubleOrNull()?.let { theValue ->
                        convertEverything(theValue, unitSpinner.selectedItem as Unit)
                    }
                }

            }

        })
        valueET.setOnFocusChangeListener { _, isFocus ->
            if (isFocus) {
                focusedView = view
            }
        }

        unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val unitLayoutOpt = unitLayoutMap[focusedView]
                unitLayoutOpt?.let { unitLayout ->
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

    private fun convertEverything(value: Double, toUnit: Unit) {
        for ((_, unitLayout) in unitLayoutMap) {
            val fromUnit = unitLayout.unitSpinner.selectedItem as Unit
            var textValue = ""
            try {
                val convertedValue = conversionService.getEquivalentValue(fromUnit, toUnit)
                textValue = "${value * convertedValue}"

            } catch (e: ConversionException) {

            }
            val currentValueEditText = unitLayout.valueEditText
            val hasFocus = currentValueEditText.hasFocus()
            if (!hasFocus)
                currentValueEditText.setText(textValue)

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(conversionService: ConversionService, unitGeneratorService: UnitGeneratorService) =
                ConverterFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(CONVERSION_SERVICE_PARAM, conversionService)
                        putParcelable(UNIT_GENERATOR_SERVICE_PARAM, unitGeneratorService)
                    }
                }
    }

}
