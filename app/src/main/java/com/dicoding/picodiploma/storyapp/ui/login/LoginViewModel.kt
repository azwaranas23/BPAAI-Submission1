package com.dicoding.picodiploma.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.network.ApiConfig
import com.dicoding.picodiploma.storyapp.preference.SessionModel
import com.dicoding.picodiploma.storyapp.ui.Event
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _session = MutableLiveData<SessionModel>()
    val session: LiveData<SessionModel> = _session

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    init {
        _isLoading.value = false
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        loginResponse.loginResult?.let {
                            _session.value = SessionModel(it.userId, it.name, it.token)
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

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure y: ${t.message}")
            }
        })
    }
}