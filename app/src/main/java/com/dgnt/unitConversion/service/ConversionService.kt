package com.dgnt.unitConversion.service

import android.util.Log
import com.dgnt.unitConversion.exception.ConversionException
import com.dgnt.unitConversion.model.unit.Unit

class ConversionService {

    @Throws(ConversionException::class)
    fun getEquivalentValue(fromUnit: Unit, toUnit: Unit): Double {

        if (fromUnit == toUnit)
            return 1.0

        val visitedUnits: MutableSet<Unit> = mutableSetOf()

        fun getEquivalentValueInternal(fromUnit: Unit, toUnit: Unit): Double {

            (fromUnit.nextUnits + fromUnit.prevUnits).forEach {
                if (!visitedUnits.contains(it.unit)) {
                    visitedUnits.add(it.unit)

                    val accEquivalentValue = it.equivalentValue * when (it.unit == toUnit) {
                        true -> 1.0
                        false -> getEquivalentValueInternal(it.unit, toUnit);
                    }
                    if (accEquivalentValue > 0)
                        return accEquivalentValue
                }

            }
            return 0.0
        }

        val equivalentValue = getEquivalentValueInternal(fromUnit, toUnit)
        when (equivalentValue) {
            0.0 -> {
                val msg = "toUnit: $toUnit is not related to fromUnit: $fromUnit"
                val unitConversionException = ConversionException(msg)
                Log.e(ConversionService::class.java.toString(), msg, unitConversionException)
                throw unitConversionException
            }
            else -> {
                Log.i(ConversionService::class.java.toString(), "toUnit: $toUnit is equal to $equivalentValue fromUnit: $fromUnit")
                return equivalentValue
            }
        }
    }


}
