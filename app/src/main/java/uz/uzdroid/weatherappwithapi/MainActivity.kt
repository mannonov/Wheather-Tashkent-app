package uz.uzdroid.weatherappwithapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.*
import org.json.JSONObject
import uz.uzdroid.weatherappwithapi.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://api.openweathermap.org/data/2.5/weather?q=tashkent&appid=71357db69d7896ac88f9d18ce97dcbc8&units=metric")
            .build()

        client.newCall(request).enqueue(object : Callback  {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful){

                    val jsonObject = JSONObject(response.body?.string())
                    val main = jsonObject.getJSONObject("main")
                    val wind = jsonObject.getJSONObject("wind")
                    val weathers = jsonObject.getJSONArray("weather")

                    val windSpeed = wind.get("speed").toString()
                    val temp = main.get("temp").toString()
                    val humidity = main.get("humidity").toString()
                    val desc = weathers.getJSONObject(0)
                    val desc2 = desc.get("description").toString()

                    runOnUiThread {
                        binding.tvWeatherGradus.text = "$temp°"
                        binding.tvWeatherWind.text = windSpeed
                        binding.tvWeatherHumidity.text = humidity
                        binding.dayType.text = desc2
                    }



                }
            }
        })





    }
}