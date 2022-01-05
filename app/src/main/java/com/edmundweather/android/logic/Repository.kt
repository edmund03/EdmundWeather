package com.edmundweather.android.logic

import androidx.lifecycle.liveData
import com.edmundweather.android.logic.model.Place
import com.edmundweather.android.logic.network.EdmundWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

object Repository {
    fun searchPlaces(query:String) = liveData(Dispatchers.IO){
        val result =try {
        val placeResponse = EdmundWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("respones status is ${placeResponse.status}"))
        }
        } catch (e:Exception){
            Result.failure<List<Place>>(e)
        }
    emit(result)
    }
}