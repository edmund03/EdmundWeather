package com.edmundweather.android.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.edmundweather.android.R
import com.edmundweather.android.databinding.*
import com.edmundweather.android.logic.model.Weather
import com.edmundweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding
    val viewModel by lazy { ViewModelProviders.of(this).get(WeatherViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(viewModel.locationlng.isEmpty()){
        viewModel.locationlng = intent.getStringExtra("location_lng") ?: ""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?:""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?:""
        }
        viewModel.weatherLiveData.observe(this, Observer { result -> val weather = result.getOrNull()
            if(weather != null) {
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.refreshWeather(viewModel.locationlng,viewModel.locationLat)
    }
    private lateinit var binding1: NowBinding
    private lateinit var binding2: ForecastBinding
    private lateinit var binding3: ForecastItemBinding
    private lateinit var binding4: LifeIndexBinding

    private fun showWeatherInfo(weather: Weather){
        binding1 = NowBinding.inflate(layoutInflater)
        binding2 = ForecastBinding.inflate(layoutInflater)
        binding3 = ForecastItemBinding.inflate(layoutInflater)
        binding4 = LifeIndexBinding.inflate(layoutInflater)

        binding1.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        binding1.currentTemp.text = currentTempText
        binding1.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding1.currentAQI.text = currentPM25Text
        binding1.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        binding2.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, binding2.forecastLayout, false)
            val dateInfo = binding3.dateInfo as TextView
            val skyIcon = binding3.skyIcon as ImageView
            val skyInfo = binding3.skyInfo as TextView
            val temperatureInfo = binding3.temperatureInfo as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            binding2.forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        binding4.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding4.dressingText.text = lifeIndex.dressing[0].desc
        binding4.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding4.carWashingText.text = lifeIndex.carWashing[0].desc
        binding.weatherLayout.visibility = View.VISIBLE
    }
}