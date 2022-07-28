package com.dicoding.picodiploma.storyapp.ui.addstory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyapp.network.ApiConfig
import com.dicoding.picodiploma.storyapp.network.FileUploadResponse
import com.dicoding.picodiploma.storyapp.ui.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModelFactory(private val token: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AddStoryViewmodel(token) as T
}

class AddStoryViewmodel(private val token: String) : ViewModel() {
    companion object {
        private const val TAG = "AddStoryViewModel"
    }

    private val _file = MutableLiveData<File>()
    val file: LiveData<File> = _file

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    private val _isSucceed = MutableLiveData<Event<Boolean>>()
    val isSucceed: LiveData<Event<Boolean>> = _isSucceed

    fun uploadImage(imageMultipart: MultipartBody.Part, description: RequestBody) {

        val service = ApiConfig.getApiService().uploadStory(token, imageMultipart, description)
        service.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isSucceed.value = Event(true)
                    }
                } else {
                    _toastText.value = Event(response.message())
                    Log.e(TAG, "onFailure x: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure y: ${t.message}")
            }
        })

    }

    fun setFile(file: File) {
        _file.value = file
    }
}