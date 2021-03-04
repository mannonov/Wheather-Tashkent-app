package uz.uzdroid.weatherappwithapi.ui

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import uz.uzdroid.weatherappwithapi.R
import uz.uzdroid.weatherappwithapi.databinding.ActivityMainBinding
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding

    var context = this
    var connectivity : ConnectivityManager? = null
    var info : NetworkInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE)

                as ConnectivityManager


        if ( connectivity != null)
        {
            info = connectivity!!.activeNetworkInfo

            if (info != null)
            {
                if (info!!.state == NetworkInfo.State.CONNECTED)
                {
                    Toast.makeText(context, "CONNECTED", Toast.LENGTH_LONG).show()
                    binding.networkType.text = "Online"
                }
            }
            else
            {
                Toast.makeText(context, "NOT CONNECTED", Toast.LENGTH_LONG).show()
                binding.networkType.text = "Offline"
            }
        }
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://api.openweathermap.org/data/2.5/weather?q=tashkent&appid=71357db69d7896ac88f9d18ce97dcbc8&units=metric")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                    val jsonObject = JSONObject(response.body?.string())
                    val main = jsonObject.getJSONObject("main")
                    val wind = jsonObject.getJSONObject("wind")
                    val weathers = jsonObject.getJSONArray("weather")

                    val windSpeed = wind.get("speed").toString()
                    val temp = main.get("temp").toString()
                    val humidity = main.get("humidity").toString()
                    val desc = weathers.getJSONObject(0)
                    val desc2 = desc.get("description").toString()
                    val iconType = desc.get("icon").toString()

                    runOnUiThread {
                        binding.tvWeatherGradus.text = "$temp°"
                        binding.tvWeatherWind.text = windSpeed
                        binding.tvWeatherHumidity.text = humidity
                        binding.dayType.text = desc2
                        binding.tvCurrentDate.text = currentDate()
                        replaceWheatherIcon(iconType)
                    }

                }
            }
        })


    }

    private fun replaceWheatherIcon(iconCode: String){

        when(iconCode){

            "01d" -> binding.weatherIcon.setImageResource(R.drawable.w_clear_sky)
            "02d" -> binding.weatherIcon.setImageResource(R.drawable.w_few_clouds)
            "03d" -> binding.weatherIcon.setImageResource(R.drawable.w_scattered_clouds)
            "04d" -> binding.weatherIcon.setImageResource(R.drawable.w_broken_clouds)
            "09d" -> binding.weatherIcon.setImageResource(R.drawable.w_shower_rain)
            "10d" -> binding.weatherIcon.setImageResource(R.drawable.w_rain)
            "11d" -> binding.weatherIcon.setImageResource(R.drawable.w_thunderstorm)
            "13d" -> binding.weatherIcon.setImageResource(R.drawable.w_snow)
            "50d" -> binding.weatherIcon.setImageResource(R.drawable.w_mist)

            "01n" -> binding.weatherIcon.setImageResource(R.drawable.w_clear_sky_night)
            "02n" -> binding.weatherIcon.setImageResource(R.drawable.w_few_clouds)
            "03n" -> binding.weatherIcon.setImageResource(R.drawable.w_scattered_clouds)
            "04n" -> binding.weatherIcon.setImageResource(R.drawable.w_broken_clouds)
            "09n" -> binding.weatherIcon.setImageResource(R.drawable.w_shower_rain)
            "10n" -> binding.weatherIcon.setImageResource(R.drawable.w_rain)
            "11n" -> binding.weatherIcon.setImageResource(R.drawable.w_thunderstorm)
            "13n" -> binding.weatherIcon.setImageResource(R.drawable.w_snow)
            "50n" -> binding.weatherIcon.setImageResource(R.drawable.w_mist)
        }

    }
    private fun currentDate(): String {
        val current: Calendar = Calendar.getInstance()
        val month = current.get(Calendar.MONTH)
        val day = current.get(Calendar.DAY_OF_MONTH)
        val name = current.get(Calendar.DAY_OF_WEEK)
        val year = current.get(Calendar.YEAR)
        val hour = current.get(Calendar.HOUR_OF_DAY)
        val minut = current.get(Calendar.MINUTE)

        val monthName = arrayOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"
        )
        val dayName = arrayOf(
            "Saturday", "Sunday", "Monday",
            "Tuesday", "Wednesday", "Thursday", "Friday"
        )
        return "${dayName[name]}, $day ${monthName[month]}  $year $hour:$minut"
    }
    private fun checkNetwork(): Boolean {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        binding.networkType.text = networkInfo!!.typeName
        return true
    }
    }
