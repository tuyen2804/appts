package com.example.testbackend.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.testbackend.R
import java.io.BufferedReader
import java.io.InputStreamReader

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE Locations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                city TEXT,
                city_code TEXT,
                district TEXT,
                district_code TEXT,
                ward TEXT,
                ward_code TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Load data from CSV
        loadCSVIntoDatabase(db)
    }

    private fun loadCSVIntoDatabase(db: SQLiteDatabase) {
        val inputStream = context.resources.openRawResource(R.raw.tinhhuyenxa)
        val buffer = BufferedReader(InputStreamReader(inputStream))

        db.beginTransaction()
        try {
            buffer.useLines { lines ->
                lines.drop(1).forEach { line ->
                    val columns = line.split(",")
                    if (columns.size == 6) {
                        val city = columns[0].trim()
                        val cityCode = columns[1].trim()
                        val district = columns[2].trim()
                        val districtCode = columns[3].trim()
                        val ward = columns[4].trim()
                        val wardCode = columns[5].trim()

                        val insertQuery = """
                            INSERT INTO Locations (city, city_code, district, district_code, ward, ward_code)
                            VALUES (?, ?, ?, ?, ?, ?)
                        """.trimIndent()

                        db.execSQL(insertQuery, arrayOf(city, cityCode, district, districtCode, ward, wardCode))
                    }
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        Log.d("DatabaseHelper", "Data imported from CSV successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Locations")
        onCreate(db)
    }

    fun getCities(): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT city FROM Locations", null)
        val cities = mutableListOf<String>()
        while (cursor.moveToNext()) {
            cities.add(cursor.getString(0))
        }
        cursor.close()
        return cities
    }
    fun getDistricts(city: String): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT district FROM Locations WHERE city = ?", arrayOf(city))
        val districts = mutableListOf<String>()
        while (cursor.moveToNext()) {
            districts.add(cursor.getString(0))
        }
        cursor.close()
        return districts
    }

    fun getWards(district: String): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT ward FROM Locations WHERE district = ?", arrayOf(district))
        val wards = mutableListOf<String>()
        while (cursor.moveToNext()) {
            wards.add(cursor.getString(0))
        }
        cursor.close()
        return wards
    }

    companion object {
        private const val DATABASE_NAME = "locations.db"
        private const val DATABASE_VERSION = 1
    }
}
