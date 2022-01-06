package com.edmundweather.android.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.edmundweather.android.logic.Repository
import com.edmundweather.android.logic.model.Location

class WeatherViewModel: ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()
    var locationlng = ""
    var locationLat = ""
    var placeName =""
    val weatherLiveData = Transformations.switchMap(locationLiveData) { location ->
        Repository.refreshWeather(location.lng, location.lat, placeName)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
    }
}