package com.dgnt.unitConversion.model.unit

data class Unit(var name: String, var shortName: String, var unitGroup: UnitGroup) {

    val nextUnits: MutableList<EquivalentUnit> = mutableListOf()
    val prevUnits: MutableList<EquivalentUnit> = mutableListOf()

    fun addNextUnits(vararg equivalentUnits: EquivalentUnit): kotlin.Unit {
        equivalentUnits.forEach { addNextUnit(it) }
        equivalentUnits.forEach { it.unit.addPrevUnit(EquivalentUnit(this, 1.0 / it.equivalentValue)) }
    }

    private fun addNextUnit(equivalentUnit: EquivalentUnit): kotlin.Unit {
        nextUnits.add(equivalentUnit)
    }

    fun addPrevUnits(vararg equivalentUnits: EquivalentUnit): kotlin.Unit {
        equivalentUnits.forEach { addPrevUnit(it) }
        equivalentUnits.forEach { it.unit.addNextUnit(EquivalentUnit(this, 1.0 / it.equivalentValue)) }
    }

    private fun addPrevUnit(equivalentUnit: EquivalentUnit): kotlin.Unit {
        prevUnits.add(equivalentUnit)
    }

}

