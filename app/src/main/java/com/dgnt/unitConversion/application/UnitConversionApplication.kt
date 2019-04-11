package com.dgnt.unitConversion.application

import android.app.Application
import com.dgnt.unitConversion.service.CalculatorService
import com.dgnt.unitConversion.service.ConversionService
import com.dgnt.unitConversion.service.UnitGeneratorService
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton

class UnitConversionApplication : Application(), KodeinAware {

    override val kodein: Kodein = Kodein {


        var unitConversionService = ConversionService()

        bind<ConversionService>() with singleton {
            unitConversionService
        }
        bind<CalculatorService>() with singleton {
            CalculatorService(unitConversionService)
        }
        bind<UnitGeneratorService>() with singleton {
            UnitGeneratorService()
        }
    }

}