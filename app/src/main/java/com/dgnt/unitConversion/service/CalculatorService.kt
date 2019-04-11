package com.dgnt.unitConversion.service

import com.dgnt.unitConversion.exception.CalculatorException
import com.dgnt.unitConversion.model.unit.Unit
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class CalculatorService(private val conversionService: ConversionService) {

    private val engine: ScriptEngine = ScriptEngineManager().getEngineByName("rhino")


    @Throws(CalculatorException::class)
    fun calculate(rawExpression:String, referenceUnits: List<Unit>, finalUnit: Unit): Double {
        var newExpression: String = rawExpression
        referenceUnits.forEach {
            newExpression = newExpression.replace("'${it.shortName}'", "*${conversionService.getEquivalentValue(finalUnit, it)}")
        }

        return eval(newExpression)
    }

    @Throws(CalculatorException::class)
    private fun eval(expr: String): Double {

        return try {
            engine.eval(expr) as Double
        } catch (ex: ScriptException) {
            throw CalculatorException("not a valid expression")
        }

    }
}