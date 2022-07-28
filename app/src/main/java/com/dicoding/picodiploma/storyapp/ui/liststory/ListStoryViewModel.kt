package com.dicoding.picodiploma.storyapp.ui.liststory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyapp.network.ApiConfig
import com.dicoding.picodiploma.storyapp.network.StoryItem
import com.dicoding.picodiploma.storyapp.network.StoryResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListStoryViewmodelFactory(private val token: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ListStoryViewModel(token) as T
}

class ListStoryViewModel(token: String) : ViewModel() {

    companion object {
        private const val TAG = "ListStoryViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<List<StoryItem>>()
    val listStory: LiveData<List<StoryItem>> = _listStory

    private val _error = MutableLiveData<Error>()
    val error: LiveData<Error> = _error

    init {
        getStory(token, page = 1, size = 30)
    }

    fun getStory(token: String, page: Int = 1, size: Int = 30) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStories(token, page, size)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { storyResponse ->
                        storyResponse.listStory?.let {
                            if (it.isEmpty()) {
                                _error.value = Error(true, type = ErrorType.NO_DATA)
                                return
                            }
                            _error.value = Error(false)
                            _listStory.value = it
                        }
                    }

                } else {
                    Log.e(TAG, "onFailure x: ${response.message()}")
                    response.errorBody()?.let {
                        val jObjError = JSONObject(it.string())
                        _error.value = Error(true, jObjError.getString("message"))
                    } ?: let {
                        _error.value = Error(true, response.message())
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = Error(true, t.message.toString())
                Log.e(TAG, "onFailure y: ${t.message}")
            }
        })
    }

    inner class Error(
        val isError: Boolean,
        val errorMsg: String? = null,
        val type: ErrorType? = null
    )

    enum class ErrorType {
        NO_DATA
    }
}