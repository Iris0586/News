package com.example.feature.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _weatherState = MutableLiveData<WeatherUiState>()
    val weatherState: LiveData<WeatherUiState> = _weatherState

    // 填入 32 位 API Key
    private val apiKey = ""

    fun searchAndFetchWeather(inputName: String) {
        _weatherState.value = WeatherUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. 从本地离线 CSV 字典快速检索城市 ID
                val localCity = CityLookupHelper.findCityId(getApplication(), inputName)

                if (localCity == null) {
                    _weatherState.postValue(WeatherUiState.Error("未找到城市：$inputName，请检查输入"))
                    return@launch
                }

                // 2. 直接根据匹配到的 LocationID 发起天气请求
                val weatherRes = WeatherApiService.instance.getNowWeather(localCity.id, apiKey)
                if (weatherRes.code == "200" && weatherRes.now != null) {
                    val displayName = if (localCity.province != localCity.name) {
                        "${localCity.province} · ${localCity.name}"
                    } else {
                        localCity.name
                    }
                    _weatherState.postValue(WeatherUiState.Success(displayName, weatherRes.now))
                } else {
                    _weatherState.postValue(WeatherUiState.Error("获取天气失败，状态码: ${weatherRes.code}"))
                }
            } catch (e: Exception) {
                _weatherState.postValue(WeatherUiState.Error("网络异常: ${e.localizedMessage}"))
            }
        }
    }
}

// 确保最底部的这个 sealed class 没有被漏掉
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val cityName: String, val data: WeatherNow) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}