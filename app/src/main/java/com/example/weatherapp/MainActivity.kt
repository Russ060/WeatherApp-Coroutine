package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val apiKey = "44352c365a5cf20cd679eae8b356c7e1"
    var zipCode = "0"
    lateinit var apiCall: String


    // variables to be used for changing location
    lateinit var location: TextView
    lateinit var updateTimeStamp: TextView
    lateinit var weatherDes: TextView
    lateinit var temp: TextView
    lateinit var lowTemp: TextView
    lateinit var maxTemp: TextView
    lateinit var sunrise: TextView
    lateinit var sunset: TextView
    lateinit var windVel: TextView
    lateinit var pressureTV: TextView
    lateinit var humidityTV: TextView
    lateinit var refreshView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        location = findViewById(R.id.locationTV)
        updateTimeStamp = findViewById(R.id.updateTimeStamp)
        weatherDes = findViewById(R.id.weatherCondition)
        temp = findViewById(R.id.temp)
        lowTemp = findViewById(R.id.lowTemp)
        maxTemp = findViewById(R.id.highTemp)
        sunrise = findViewById(R.id.sunRiseTimeTV)
        sunset = findViewById(R.id.sunsetTimeTV)
        windVel = findViewById(R.id.windSpeed)
        pressureTV = findViewById(R.id.pressureValueTV)
        humidityTV = findViewById(R.id.humidityValueTV)
        refreshView = findViewById(R.id.refreshImageView)


        //OnClick listener to change the location
        location.setOnClickListener { getZipCode() }
        zipCode = intent.getStringExtra("zipCode").toString()
        apiCall =
            "https://api.openweathermap.org/data/2.5/weather?zip=$zipCode,us&appid=${apiKey}&units=metric"
        requestAPI()
        refreshView.setOnClickListener { requestAPI() }

    }// add all functions below this line

    private fun getZipCode() {
        val intent = Intent(this, GetZipCode::class.java)
        startActivity(intent)
    }


    private fun requestAPI() {
        // We are using Coroutine to fetch the data in the background, then update our recycler view if the data is valid
        CoroutineScope(Dispatchers.IO).launch {
            val data = async { fetchData() }.await()
            // once the data is received, we populate the recyclerView
            if (data.isNotEmpty()) {
                populateWeatherData(data)
            }
        }
    }


    private fun fetchData(): String {
        var response = ""
        try {
            response = URL(apiCall).readText()
            Log.d("URLResponse", "res: $response")
        } catch (e: Exception) {
            Log.d("MAIN", "ISSUE: $e")
        }
        // our response is saved as a string and returned
        return response
    }

    private suspend fun populateWeatherData(result: String) {
        withContext(Dispatchers.Main) {
            // we create JSON Array from the data
            val jsonObject = JSONObject(result)

            val city = jsonObject.getString("name")
            Log.d("weatherInfo", city)

            val country = jsonObject.getJSONObject("sys").getString("country")
            Log.d("weatherInfo", country)

            val date = jsonObject.getString("dt")
            Log.d("weatherInfo", date)

            val weatherDescription =
                jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
            Log.d("weatherInfo", weatherDescription)

            val currentTemp = jsonObject.getJSONObject("main").getString("temp")
            Log.d("weatherInfo", currentTemp)

            val lowestTemp = jsonObject.getJSONObject("main").getString("temp_min")
            Log.d("weatherInfo", lowestTemp)

            val highestTemp = jsonObject.getJSONObject("main").getString("temp_max")
            Log.d("weatherInfo", highestTemp)

            val pressure = jsonObject.getJSONObject("main").getString("pressure")
            Log.d("weatherInfo", pressure)

            val humidity = jsonObject.getJSONObject("main").getString("humidity")
            Log.d("weatherInfo", humidity)

            val windSpeed = jsonObject.getJSONObject("wind").getString("speed")
            Log.d("weatherInfo", windSpeed)

            val sunriseTime = jsonObject.getJSONObject("sys").getString("sunrise")
            Log.d("weatherInfo", sunriseTime)

            val sunsetTime = jsonObject.getJSONObject("sys").getString("sunset")
            Log.d("weatherInfo", sunsetTime)

            location.text = "$city, $country"
            updateTimeStamp.text =
                "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
                    .format(Date(date.toLong() * 1000))
            weatherDes.text = weatherDescription

            temp.text = "${String.format("%.0f", currentTemp.toFloat())}" + "°C"
            lowTemp.text = "Low: " + "${String.format("%.1f", lowestTemp.toFloat())}" + "°C"
            maxTemp.text = "High: " + "${String.format("%.1f", highestTemp.toFloat())}" + "°C"
            sunrise.text = SimpleDateFormat(
                "HH:mm a",
                Locale.ENGLISH
            ).format(Date(sunriseTime.toLong() * 1000))
            sunset.text =
                SimpleDateFormat("HH:mm a", Locale.ENGLISH).format(Date(sunsetTime.toLong() * 1000))
            windVel.text = windSpeed
            pressureTV.text = pressure
            humidityTV.text = humidity

            temp.setOnClickListener {
                if(temp.text.contains("℃")){
                    val fahrenheit = currentTemp.toFloat() * 1.800 + 32
                    val tempMaxF = highestTemp.toFloat() * 1.800 + 32
                    val tempMinF = lowestTemp.toFloat() * 1.800 + 32

                    val solution = String.format("%.0f", fahrenheit)
                    val solutiontempMax = String.format("%.1f", tempMaxF)
                    val solutiontempMin = String.format("%.1f", tempMinF)

                    temp.text= solution+" F"
                    maxTemp.text="High: "+solutiontempMax+" F"
                    lowTemp.text="Low: "+solutiontempMin+" F"
                }else{
                    val celsius = currentTemp.toFloat()
                    val tempMax = highestTemp.toFloat()
                    val tempMin = lowestTemp.toFloat()


                    val solution = String.format("%.0f", celsius)
                    val solutiontempMax = String.format("%.1f", tempMax)
                    val solutiontempMin = String.format("%.1f", tempMin)
                    temp.text= solution+"℃"
                    maxTemp.text="High: "+solutiontempMax+"℃"
                    lowTemp.text="Low: "+solutiontempMin+"℃"
                }

            }

        }
    }

}