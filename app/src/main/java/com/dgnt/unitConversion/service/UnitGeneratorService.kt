package com.dgnt.unitConversion.service

import com.dgnt.unitConversion.model.unit.EquivalentUnit
import com.dgnt.unitConversion.model.unit.Unit
import com.dgnt.unitConversion.model.unit.UnitGroup
import org.json.JSONArray

class UnitGeneratorService() {

    data class EquivalentUnitInfo(val name: String, val equivalence: Double)

    fun generateUnitsFromJsonString(json: String): List<Unit> {

        val allUnits = mutableListOf<Unit>()

        val unitAndGroupsJsonArray = JSONArray(json)
        for (i in 0 until unitAndGroupsJsonArray.length()) {
            val units = mutableListOf<Unit>()
            val unitMap = mutableMapOf<String, Unit>()

            val unitAndGroupJsonObject = unitAndGroupsJsonArray.getJSONObject(i);
            val groupJsonObject = unitAndGroupJsonObject.getJSONObject("group")
            val unitGroup = UnitGroup(groupJsonObject.getString("name"))

            val equivalentUnitMap = mutableMapOf<String, List<EquivalentUnitInfo>>()
            val unitsJsonArray = unitAndGroupJsonObject.getJSONArray("units")
            for (j in 0 until unitsJsonArray.length()) {
                val unitJsonObject = unitsJsonArray.getJSONObject(j)
                val unit = Unit(unitJsonObject.getString("name"), unitJsonObject.getString("shortName"), unitGroup)
                units.add(unit)
                unitMap[unit.name] = unit

                val equivalentUnitInfos = mutableListOf<EquivalentUnitInfo>()
                val nextJsonArrayOpt = unitJsonObject.optJSONArray("next")
                nextJsonArrayOpt?.let {nextJsonArray ->
                    for (k in 0 until nextJsonArray.length()) {
                        val equivalentUnitJsonObject = nextJsonArray.getJSONObject(k)
                        equivalentUnitInfos.add(EquivalentUnitInfo(equivalentUnitJsonObject.getString("name"), equivalentUnitJsonObject.getDouble("equivalence")))
                    }
                    equivalentUnitMap[unit.name] = equivalentUnitInfos

                }
            }

            units.forEach { unitElement ->
                val unitName = unitElement.name;
                val equivalentUnitInfos = equivalentUnitMap[unitName]
                val nextUnitsOpt = equivalentUnitInfos?.let {
                    it.map { equivalentUnitInfoElement ->

                        val ret = unitMap[equivalentUnitInfoElement.name]?.let { unitInner ->
                            EquivalentUnit(unitInner, equivalentUnitInfoElement.equivalence)
                        }
                        ret
                    }
                }
                nextUnitsOpt?.let{nextUnits ->

                    nextUnits.forEach{nextUnitOpt ->
                        nextUnitOpt?.let {nextUnit ->
                            unitElement.addNextUnits(nextUnit)
                        }
                    }
                }
            }

            allUnits.addAll(units)
        }



        return allUnits
    }
}