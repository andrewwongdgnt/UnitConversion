package com.dgnt.unitConversion.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import com.dgnt.unitConversion.R
import com.dgnt.unitConversion.service.CalculatorService

private const val CALCULATOR_SERVICE_PARAM = "calculatorServiceParam"
class CalculatorFragment : Fragment() {

    private lateinit var calculatorService: CalculatorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            calculatorService = it.get(CALCULATOR_SERVICE_PARAM) as CalculatorService
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val calculatorEditText = view.findViewById<EditText>(R.id.calculatorEditText)
        calculatorEditText.showSoftInputOnFocus = false;

        fun appendToEditText(value:String){
            calculatorEditText.text?.let{
                val selectionStart = calculatorEditText.selectionStart
                val selectionEnd = calculatorEditText.selectionEnd
                val currentText = it.toString()
                val leftSideText = currentText.slice(0.until(selectionStart))
                val rightSideText = currentText.slice(selectionEnd.until(currentText.length))
                val newText = leftSideText + value + rightSideText
                calculatorEditText.setText(newText)
                calculatorEditText.setSelection(selectionStart+value.length);
            }
        }

        fun deleteFromEditText(){
            calculatorEditText.text?.let{

                val currentText = it.toString()
                if (currentText.isNotEmpty()) {
                    val selectionStart = calculatorEditText.selectionStart
                    val selectionEnd = calculatorEditText.selectionEnd
                    val newSelectionStart = when (selectionStart == selectionEnd) {
                        true -> selectionStart - 1
                        else -> selectionStart
                    }
                    val leftSideText = currentText.slice(0.until(newSelectionStart))
                    val rightSideText = currentText.slice(selectionEnd.until(currentText.length))
                    val newText = leftSideText + rightSideText
                    calculatorEditText.setText(newText)
                    calculatorEditText.setSelection(newSelectionStart);
                }
            }
        }

        view.findViewById<Button>(R.id.buttonZero).setOnClickListener {
            appendToEditText("0")
        }
        view.findViewById<Button>(R.id.buttonOne).setOnClickListener {
            appendToEditText("1")
        }
        view.findViewById<Button>(R.id.buttonTwo).setOnClickListener {
            appendToEditText("2")
        }
        view.findViewById<Button>(R.id.buttonThree).setOnClickListener {
            appendToEditText("3")
        }
        view.findViewById<Button>(R.id.buttonFour).setOnClickListener {
            appendToEditText("4")
        }
        view.findViewById<Button>(R.id.buttonFive).setOnClickListener {
            appendToEditText("5")
        }
        view.findViewById<Button>(R.id.buttonSix).setOnClickListener {
            appendToEditText("6")
        }
        view.findViewById<Button>(R.id.buttonSeven).setOnClickListener {
            appendToEditText("7")
        }
        view.findViewById<Button>(R.id.buttonEight).setOnClickListener {
            appendToEditText("8")
        }
        view.findViewById<Button>(R.id.buttonNine).setOnClickListener {
            appendToEditText("9")
        }
        view.findViewById<Button>(R.id.buttonDivide).setOnClickListener {
            appendToEditText("/")
        }
        view.findViewById<Button>(R.id.buttonMultiply).setOnClickListener {
            appendToEditText("*")
        }
        view.findViewById<Button>(R.id.buttonSubtract).setOnClickListener {
            appendToEditText("-")
        }
        view.findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            appendToEditText("+")
        }
        view.findViewById<Button>(R.id.buttonDot).setOnClickListener {
            appendToEditText(".")
        }

        view.findViewById<Button>(R.id.buttonDel).setOnClickListener {
            deleteFromEditText()
        }

        view.findViewById<Button>(R.id.buttonClear).setOnClickListener {
            calculatorEditText.text.clear()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(calculatorService: CalculatorService) =
                CalculatorFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(CALCULATOR_SERVICE_PARAM, calculatorService)
                    }
                }
    }
}
