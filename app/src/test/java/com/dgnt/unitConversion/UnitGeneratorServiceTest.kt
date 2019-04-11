package com.dgnt.unitConversion

import android.util.Log
import com.dgnt.unitConversion.service.UnitGeneratorService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
@PrepareForTest(Log::class)
class UnitGeneratorServiceTest {

    private val sut: UnitGeneratorService = UnitGeneratorService()

    private val jsonString = """
        [
  {
    "group": {
      "name": "Currency"
    },
    "units": [
      {
        "name": "Penny",
        "shortName": "pn",
        "next": [
          {
            "name": "Nickle",
            "equivalence": 5.0
          }
        ]
      },
      {
        "name": "Nickle",
        "shortName": "nc",
        "group": "Currency",
        "next": [
          {
            "name": "Dime",
            "equivalence": 2.0
          }
        ]
      },
      {
        "name": "Dime",
        "shortName": "dm",
        "group": "Currency",
        "next": [
          {
            "name": "Quarter",
            "equivalence": 2.5
          }
        ]
      },
      {
        "name": "Quarter",
        "shortName": "qr",
        "group": "Currency",
        "next": [
          {
            "name": "Dollar",
            "equivalence": 4.0
          }
        ]
      },
      {
        "name": "Dollar",
        "shortName": "dl",
        "group": "Currency",
        "next": [
          {
            "name": "Toonie",
            "equivalence": 2.0
          }
        ]
      },
      {
        "name": "Toonie",
        "shortName": "tn",
        "group": "Currency"
      }
    ]
  }
]

    """.trimIndent()

    @Before
    fun setUp() {


    }

    @Test
    fun testGenerationFromJsonString() {
        sut.generateUnitsFromJsonString(jsonString)
    }


}