package com.dgnt.unitConversion.service

import com.dgnt.unitConversion.exception.UnitCalculatorException
import com.dgnt.unitConversion.exception.UnitConversionException
import com.dgnt.unitConversion.model.unit.Unit
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class UnitCalculatorService(private val unitConversionService: UnitConversionService) {

    private val engine: ScriptEngine = ScriptEngineManager().getEngineByName("rhino")


    @Throws(UnitCalculatorException::class)
    fun calculate(rawExpression:String, referenceUnits: List<Unit>, finalUnit: Unit): Double {
        var newExpression: String = rawExpression
        referenceUnits.forEach {
            newExpression = newExpression.replace("'${it.shortName}'", "*${unitConversionService.getEquivalentValue(finalUnit, it)}")
        }

        return eval(newExpression)
    }

    @Throws(UnitCalculatorException::class)
    private fun eval(expr: String): Double {

        return try {
            engine.eval(expr) as Double
        } catch (ex: ScriptException) {
            throw UnitCalculatorException("not a valid expression")
        }

    }
}