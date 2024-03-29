package com.fshou.githubuser.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.fshou.githubuser.data.local.datastore.SettingPreferences
import com.fshou.githubuser.data.local.entity.FavoriteUser
import com.fshou.githubuser.data.local.room.FavoriteUserDao
import com.fshou.githubuser.data.remote.response.User
import com.fshou.githubuser.data.remote.response.UserDetailResponse
import com.fshou.githubuser.data.remote.retrofit.ApiService
import com.fshou.githubuser.utils.Event
import kotlinx.coroutines.flow.Flow


class AppRepository private constructor(
    private val apiService: ApiService,
    private val favoriteUserDao: FavoriteUserDao,
    private val settingPreferences: SettingPreferences

) {
    // Settings Preference
    fun getThemeSetting(): Flow<Boolean> = settingPreferences.getThemeSetting()
    suspend fun saveThemeSetting(isDarkMode: Boolean) =
        settingPreferences.saveThemeSetting(isDarkMode)

    // Room
    suspend fun addUser(user: FavoriteUser) = favoriteUserDao.addUser(user)
    suspend fun deleteUser(user: FavoriteUser) = favoriteUserDao.deleteUser(user)
    fun getFavoriteUsers(): LiveData<List<FavoriteUser>> = favoriteUserDao.getFavoriteUsers()

    suspend fun isFavoriteUser(username: String): Boolean = favoriteUserDao.isFavoriteUser(username)

    // Retrofit
    fun getUserDetail(username: String): LiveData<Result<UserDetailResponse>> = liveData {
        emit(Result.Loading)
        try {
            val userDetailResponse = apiService.getUserDetail(username)
            emit(Result.Success(userDetailResponse))
        } catch (e: Exception) {
            emit(Result.Error(Event(e.message.toString())))
        }
    }

    suspend fun getUsers(username: String): List<User> {
        val gitHubUserResponse = apiService.getUsers(username)
        val users = gitHubUserResponse.items
        return users as List<User>
    }

    fun getFollowers(username: String): LiveData<Result<List<User>>> = liveData {
        emit(Result.Loading)
        try {
            val followers = apiService.getUserFollowers(username)
            emit(Result.Success(followers))
        } catch (e: Exception) {
            emit(Result.Error(Event(e.message.toString())))
        }
    }

    fun getFollowing(username: String): LiveData<Result<List<User>>> = liveData {
        emit(Result.Loading)
        try {
            val followers: List<User> = apiService.getUserFollowing(username)
            emit(Result.Success(followers))
        } catch (e: Exception) {
            emit(Result.Error(Event(e.message.toString())))
        }
    }

    companion object {
        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteUserDao: FavoriteUserDao,
            settingPreferences: SettingPreferences
        ): AppRepository = instance ?: synchronized(this) {
            instance ?: AppRepository(apiService, favoriteUserDao, settingPreferences)
        }.also { instance = it }

    }

}