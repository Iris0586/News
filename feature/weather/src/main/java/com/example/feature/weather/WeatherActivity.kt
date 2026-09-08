package com.example.feature.weather

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.feature.weather.databinding.ActivityWeatherBinding

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "实时天气"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        initObserver()

        binding.btnSearch.setOnClickListener {
            val cityInput = binding.etCityInput.text.toString().trim()
            if (cityInput.isNotEmpty()) {
                viewModel.searchAndFetchWeather(cityInput)
            } else {
                Toast.makeText(this, "请输入城市名称", Toast.LENGTH_SHORT).show()
            }
        }

        // 默认进入先查北京
        viewModel.searchAndFetchWeather("北京")
    }

    private fun initObserver() {
        viewModel.weatherState.observe(this) { state ->
            when (state) {
                is WeatherUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is WeatherUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvCity.text = "城市：${state.cityName}"
                    val data = state.data
                    binding.tvTemp.text = "${data.temp}°C"
                    binding.tvCondition.text = data.text
                    binding.tvDetails.text = "体感: ${data.feelsLike}°C | ${data.windDir} ${data.windScale}级 | 湿度: ${data.humidity}%"
                }
                is WeatherUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}