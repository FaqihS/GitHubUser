package com.fshou.githubuser.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fshou.githubuser.data.response.User
import com.fshou.githubuser.data.response.GitHubUserResponse
import com.fshou.githubuser.data.retrofit.ApiConfig
import com.fshou.githubuser.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private var _searchedUser = MutableLiveData<List<User>>()
    var searchedUser: LiveData<List<User>> = _searchedUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        searchUser()
    }


    fun searchUser(username: String = "the") {
        _isLoading.value = true
        ApiConfig.getApiService().getUsers(username)
            .enqueue(object : Callback<GitHubUserResponse> {

                override fun onResponse(call: Call<GitHubUserResponse>, response: Response<GitHubUserResponse>) {
                    _isLoading.value = false
                    if (!response.isSuccessful) {
                        Log.e(TAG, "OnFailure: ${response.message()}")
                    }
                    _searchedUser.value = response.body()?.items as List<User>?
                }

                override fun onFailure(call: Call<GitHubUserResponse>, t: Throwable) {
                    _isLoading.value = false
                    _toastText.value = Event("Failed to Load User")
                    Log.e(TAG, "OnFailure: ${t.cause}")
                }

            })
    }
}