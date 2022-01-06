package com.edmundweather.android.logic


import androidx.lifecycle.liveData
import com.edmundweather.android.logic.dao.PlaceDao
import com.edmundweather.android.logic.model.Place
import com.edmundweather.android.logic.model.Weather
import com.edmundweather.android.logic.network.EdmundWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = EdmundWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("respones status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String,placeName: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                EdmundWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                EdmundWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" + "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place:Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() =PlaceDao.getSavedPlace()
    fun isPlaceSaved() =PlaceDao.isPlaceSaved()
}