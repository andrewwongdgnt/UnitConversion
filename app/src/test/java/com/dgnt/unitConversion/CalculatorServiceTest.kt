package com.dgnt.unitConversion

import android.util.Log
import com.dgnt.unitConversion.exception.CalculatorException
import com.dgnt.unitConversion.model.unit.EquivalentUnit
import com.dgnt.unitConversion.model.unit.Unit
import com.dgnt.unitConversion.model.unit.UnitGroup
import com.dgnt.unitConversion.service.CalculatorService
import com.dgnt.unitConversion.service.ConversionService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
@PrepareForTest(Log::class, ConversionService::class)
class CalculatorServiceTest {

    private val currency: UnitGroup = UnitGroup("currency")
    private val penny: Unit = Unit("penny", "pn", currency)
    private val nickle: Unit = Unit("nickle", "nc", currency)
    private val dime: Unit = Unit("dime", "dim", currency)
    private val quarter: Unit = Unit("quarter", "qr", currency)
    private val dollar: Unit = Unit("dollar", "$", currency)
    private val toonie: Unit = Unit("toonie", "$2", currency)
    private val fiveDollars: Unit = Unit("fiveDollars", "$5", currency)

    private val mockConversionService = PowerMockito.mock(ConversionService::class.java)

    private val sut: CalculatorService = CalculatorService(mockConversionService)

    @Before
    fun setUp() {
        PowerMockito.mockStatic(Log::class.java)

        penny.addNextUnits(EquivalentUnit(nickle, 5.0))
        nickle.addNextUnits(EquivalentUnit(dime, 2.0))
        dime.addNextUnits(EquivalentUnit(quarter, 2.5))
        quarter.addNextUnits(EquivalentUnit(dollar, 4.0))
        dollar.addNextUnits(EquivalentUnit(toonie, 2.0), EquivalentUnit(fiveDollars, 5.0))


        PowerMockito.`when`(mockConversionService.getEquivalentValue(penny, penny)).thenReturn(1.0)
        PowerMockito.`when`(mockConversionService.getEquivalentValue(penny, nickle)).thenReturn(5.0)
        PowerMockito.`when`(mockConversionService.getEquivalentValue(dollar, nickle)).thenReturn(0.05)
        PowerMockito.`when`(mockConversionService.getEquivalentValue(dollar, quarter)).thenReturn(0.25)
        PowerMockito.`when`(mockConversionService.getEquivalentValue(penny, penny)).thenReturn(1.0)
        PowerMockito.`when`(mockConversionService.getEquivalentValue(nickle, penny)).thenReturn(1.0/5.0)
        PowerMockito.`when`(mockConversionService.getEquivalentValue(nickle, nickle)).thenReturn(1.0)
        PowerMockito.`when`(mockConversionService.getEquivalentValue(dollar, penny)).thenReturn(1.0/100.0)
    }

    @Test
    fun testAddition() {
        val retVal = sut.calculate("6'pn' + 1'nc'", mutableListOf(penny, nickle), penny)
        Assert.assertEquals(11.0, retVal, 0.001)
    }

    @Test
    fun testAddition2() {
        val retVal = sut.calculate("6'nc' + 1'qr'", mutableListOf(quarter, nickle), dollar)
        Assert.assertEquals(0.55, retVal, 0.001)
    }

    @Test
    fun testAdditionWithNegative() {
        val retVal = sut.calculate("6'nc' + - 1'qr'", mutableListOf(quarter, nickle), dollar)
        Assert.assertEquals(0.05, retVal, 0.001)
    }

    @Test
    fun testSubtraction() {
        val retVal = sut.calculate("6'pn' - 1'nc'", mutableListOf(penny, nickle), nickle)
        Assert.assertEquals(0.2, retVal, 0.001)
    }

    @Test
    fun testMultiplication() {
        val retVal = sut.calculate("6'pn'*7", mutableListOf(penny), nickle)
        Assert.assertEquals(8.4, retVal, 0.001)
    }

    @Test
    fun testDivision() {
        val retVal = sut.calculate("100'pn'/5", mutableListOf(penny), nickle)
        Assert.assertEquals(4.0, retVal, 0.001)
    }

    @Test
    fun testBrackets() {
        val retVal = sut.calculate("(5'nc'+92'pn')*8", mutableListOf(penny, nickle), dollar)
        Assert.assertEquals(9.36, retVal, 0.001)
    }


    @Test(expected = CalculatorException::class)
    fun testInvalidExpression() {
        sut.calculate("6'pn'++7'pn'", mutableListOf(penny), dollar)
    }

    @Test(expected = CalculatorException::class)
    fun testInvalidExpression2() {
        sut.calculate("((6'pn')*8+7'pn'", mutableListOf(penny), dollar)
    }


}