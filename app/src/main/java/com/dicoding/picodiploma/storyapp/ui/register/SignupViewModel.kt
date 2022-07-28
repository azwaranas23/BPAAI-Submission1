package com.dicoding.picodiploma.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.network.ApiConfig
import com.dicoding.picodiploma.storyapp.ui.Event
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel : ViewModel() {

    companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRegisterSuccess = MutableLiveData<Boolean>()
    val isSignupSuccess: LiveData<Boolean> = _isRegisterSuccess

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    init {
        _isLoading.value = false
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { registerResponse ->
                        if (registerResponse.error) {
                            _toastText.value = Event(registerResponse.message)
                        } else {
                            _isRegisterSuccess.value = true
                        }
                    }

                } else {
                    Log.e(TAG, "onFailure x: ${response.message()}")
                    response.errorBody()?.let {
                        val jObjError = JSONObject(it.string())
                        _toastText.value = Event(jObjError.getString("message"))
                    } ?: let {
                        _toastText.value = Event(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure y: ${t.message}")
            }
        })
    }
}