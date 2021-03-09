package uz.uzdroid.weatherappwithapi.ui

import android.app.Service
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import uz.uzdroid.weatherappwithapi.R
import uz.uzdroid.weatherappwithapi.database.InfoDatabase
import uz.uzdroid.weatherappwithapi.databinding.ActivityMainBinding
import uz.uzdroid.weatherappwithapi.model.Info
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var context = this
    var connectivity: ConnectivityManager? = null
    var info: NetworkInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivity != null) {
            info = connectivity!!.activeNetworkInfo
            if (info != null) {
                if (info!!.state == NetworkInfo.State.CONNECTED) {
                    Toast.makeText(context, "CONNECTED", Toast.LENGTH_LONG).show()
                    binding.tvNetworkType.text = "Online"

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
                                val job = Job()
                                val uiScope = CoroutineScope(Dispatchers.Main + job)
                                val database = InfoDatabase.getInstance(application)
                                val infoDao = database.infoDao()

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
                                    binding.tvGradusWheather.text = temp
                                    binding.tvDayType.text = desc2
                                    binding.tvWeekDay.text = currentDay()
                                    binding.tvTime.text = currentDate()
                                    replaceWheatherIcon(iconType)
                                }
                                val info = Info(
                                    speed = windSpeed,
                                    temp = temp,
                                    humidity = humidity,
                                    description = desc2,
                                    icon = iconType
                                )

                                uiScope.launch {
                                    withContext(Dispatchers.IO) {

                                        infoDao.insertInfo(info)

                                    }
                                }

                            }
                        }
                    })
                }
            } else {
                val job =  Job()
                val uiScope = CoroutineScope(Dispatchers.Main + job)
                val database = InfoDatabase.getInstance(application)
                val infoDao = database.infoDao()
                Toast.makeText(context, "NOT CONNECTED", Toast.LENGTH_LONG).show()
                binding.tvNetworkType.text = "Offline"

               runOnUiThread {

                        infoDao.queryAllInfo().observe(this, androidx.lifecycle.Observer {
                            binding.tvGradusWheather.text = it.temp
                            binding.tvDayType.text = it.description
                            binding.tvTime.text = currentDate()
                            binding.tvWeekDay.text = currentDay()
                            replaceWheatherIcon(it.icon)
                        }
                        )
                    }
            }
        }
    }

    private fun replaceWheatherIcon(iconCode: String?) {

        when (iconCode) {

            "01d" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_clear_sky)
            "02d" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_few_clouds)
            "03d" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_scattered_clouds)
            "04d" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_broken_clouds)
            "09d" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_shower_rain)
            "10d" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_rain)
            "11d" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_thunderstorm)
            "13d" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_snow)
            "50d" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_mist)

            "01n" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_clear_sky)
            "02n" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_few_clouds)
            "03n" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_scattered_clouds)
            "04n" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_broken_clouds)
            "09n" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_shower_rain)
            "10n" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_rain)
            "11n" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_thunderstorm)
            "13n" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_snow)
            "50n" -> binding.imgWheatherIcon.setImageResource(R.drawable.ic_w_mist)
        }

    }

    private fun currentDay():String{
        val current: Calendar = Calendar.getInstance()
        val day= current.get(Calendar.DAY_OF_WEEK)

        val dayName = arrayOf(
            "Saturday", "Sunday", "Monday",
            "Tuesday", "Wednesday", "Thursday", "Friday"
        )

        return dayName[day]
    }

    private fun currentDate(): String {
        val current: Calendar = Calendar.getInstance()
        val hour = current.get(Calendar.HOUR_OF_DAY)
        val minut = current.get(Calendar.MINUTE)

        return "$hour:$minut"
    }
}
