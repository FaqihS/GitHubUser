package com.fshou.githubuser.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fshou.githubuser.data.response.UserDetailResponse
import com.fshou.githubuser.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailViewModel: ViewModel() {
    private var _userDetail = MutableLiveData<UserDetailResponse>()
    var userDetail : LiveData<UserDetailResponse> = _userDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "UserDetailViewModel"
    }

    init {
        getUserDetail(UserDetailActivity.username)
    }

    private fun getUserDetail(username: String){
        _isLoading.value = true
        ApiConfig.getApiService().getUserDetail(username).apply {
            enqueue(object : Callback<UserDetailResponse> {
                override fun onResponse(
                    call: Call<UserDetailResponse>,
                    response: Response<UserDetailResponse>
                ) {
                    _isLoading.value = false
                    if (!response.isSuccessful){
                        Log.e(TAG, "OnFailure: ${response.message()}")
                    }
                    _userDetail.value = response.body()
                }

                override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                    Log.e(TAG,"OnFailure: ${t.message}")                }

            })
        }

    }
}