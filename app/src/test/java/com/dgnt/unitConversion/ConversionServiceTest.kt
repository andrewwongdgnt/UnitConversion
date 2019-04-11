package com.dgnt.unitConversion

import android.util.Log
import com.dgnt.unitConversion.exception.ConversionException
import com.dgnt.unitConversion.model.unit.EquivalentUnit
import com.dgnt.unitConversion.model.unit.Unit
import com.dgnt.unitConversion.model.unit.UnitGroup
import com.dgnt.unitConversion.service.ConversionService
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Log::class)
class ConversionServiceTest {

    private var currency: UnitGroup = UnitGroup("currency")
    private var penny: Unit = Unit("penny", "pn", currency)
    private var shinyNickle: Unit = Unit("shinyNickle", "Snc", currency)
    private var nickle: Unit = Unit("nickle", "nc", currency)
    private var dime: Unit = Unit("dime", "dim", currency)
    private var quarter: Unit = Unit("quarter", "qr", currency)
    private var dollar: Unit = Unit("dollar", "$", currency)
    private var toonie: Unit = Unit("toonie", "$2", currency)
    private var fiveDollars: Unit = Unit("fiveDollars", "$5", currency)
    private var halfDollar: Unit = Unit("half dollar", "hlf", currency)

    private var distance: UnitGroup = UnitGroup("distance")
    private var millimeter: Unit = Unit("millimeter", "mm", distance)
    private var centimeter: Unit = Unit("centimeter", "cm", distance)
    private var meter: Unit = Unit("meter", "m", distance)

    private var mass: UnitGroup = UnitGroup("mass")
    private var milligram: Unit = Unit("milligram", "mg", mass)
    private var gram: Unit = Unit("gram", "g", mass)
    private var kilogram: Unit = Unit("kilogram", "kg", mass)
    private var pound: Unit = Unit("pound", "lbs", mass)
    private var stone: Unit = Unit("stone", "st", mass)
    private var qr: Unit = Unit("quarter", "qr", mass)

    private var sut: ConversionService = ConversionService()

    @Before
    fun setUp() {
        PowerMockito.mockStatic(Log::class.java)

        penny.addNextUnits(EquivalentUnit(nickle, 5.0), EquivalentUnit(shinyNickle, 5.0))
        nickle.addNextUnits(EquivalentUnit(dime, 2.0))
        dime.addNextUnits(EquivalentUnit(quarter, 2.5))
        quarter.addNextUnits(EquivalentUnit(dollar, 4.0))
        dollar.addNextUnits(EquivalentUnit(toonie, 2.0), EquivalentUnit(fiveDollars, 5.0))

        millimeter.addNextUnits(EquivalentUnit(meter, 1000.0))
        centimeter.addNextUnits(EquivalentUnit(meter, 100.0))

        milligram.addNextUnits(EquivalentUnit(gram, 1000.0))
        gram.addNextUnits(EquivalentUnit(kilogram, 1000.0))
        pound.addNextUnits(EquivalentUnit(stone, 14.0), EquivalentUnit(kilogram, 2.20462))
        stone.addNextUnits(EquivalentUnit(qr, 2.0))


    }

    @Test
    fun testConversionFromPennyToPenny() {
        val equivalentValue = sut.getEquivalentValue(penny, penny)
        assertEquals(1.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromPennyToNickel() {
        val equivalentValue = sut.getEquivalentValue(penny, nickle)
        assertEquals(5.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromPennyToDollar() {
        val equivalentValue = sut.getEquivalentValue(penny, dollar)
        assertEquals(100.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromNickleToQuarter() {
        val equivalentValue = sut.getEquivalentValue(nickle, quarter)
        assertEquals(5.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromNickelToPenny() {
        val equivalentValue = sut.getEquivalentValue(nickle, penny)
        assertEquals(1.0 / 5.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromDollarToPenny() {
        val equivalentValue = sut.getEquivalentValue(dollar, penny)
        assertEquals(1.0 / 100.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromQuarterToNickle() {
        val equivalentValue = sut.getEquivalentValue(quarter, nickle)
        assertEquals(1.0 / 5.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromPennyToFiveDollars() {
        val equivalentValue = sut.getEquivalentValue(penny, fiveDollars)
        assertEquals(500.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromPennyToToonie() {
        val equivalentValue = sut.getEquivalentValue(penny, toonie)
        assertEquals(200.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromPennyToShinyNickle() {
        val equivalentValue = sut.getEquivalentValue(penny, shinyNickle)
        assertEquals(5.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromShinyNickleToNickle() {
        val equivalentValue = sut.getEquivalentValue(shinyNickle, nickle)
        assertEquals(1.0, equivalentValue, 0.001)
    }

    @Test(expected = ConversionException::class)
    fun testConversionFromPennyToHalfDollar() {
        sut.getEquivalentValue(penny, halfDollar)
    }

    @Test
    fun testConversionFromMillimeterToCentimeter() {
        val equivalentValue = sut.getEquivalentValue(millimeter, centimeter)
        assertEquals(10.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromCentimeterToMillimeter() {
        val equivalentValue = sut.getEquivalentValue(centimeter, millimeter)
        assertEquals(1.0 / 10.0, equivalentValue, 0.001)
    }

    @Test
    fun testConversionFromMilligramToQr() {
        val equivalentValue = sut.getEquivalentValue(milligram, qr)
        assertEquals(12700601.45, equivalentValue, 0.05)
    }

    @Test
    fun testConversionFromPoundToGram() {
        val equivalentValue = sut.getEquivalentValue(pound, gram)
        assertEquals(0.00220462, equivalentValue, 0.001)
    }

}