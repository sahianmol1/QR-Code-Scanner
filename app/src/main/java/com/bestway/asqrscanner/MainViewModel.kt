package com.bestway.asqrscanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val currentAppVersion = BuildConfig.VERSION_CODE.toLong()

    private val _flexibleUpdateDownloaded = MutableStateFlow(false)
    val flexibleUpdateDownloaded: StateFlow<Boolean> = _flexibleUpdateDownloaded.asStateFlow()

    private val _appUpdateState = MutableStateFlow(Pair(AppUpdateState.LOADING, currentAppVersion))
    val appUpdateState: StateFlow<Pair<AppUpdateState, Long>> = _appUpdateState.asStateFlow()

    private val _isForceUpdatePending = MutableStateFlow(false)
    val isForceUpdatePending: StateFlow<Boolean> = _isForceUpdatePending.asStateFlow()

    fun fetchUpdateTypeFromRemoteConfig() {
        _appUpdateState.value = Pair(AppUpdateState.LOADING, currentAppVersion)
        viewModelScope.launch {
            _appUpdateState.value = Pair(AppUpdateState.FORCE, 1000)
        }
    }

    fun handleDownloadedFlexibleAppUpdate(isDownloaded: Boolean) {
        _flexibleUpdateDownloaded.value = isDownloaded
    }

    fun forceUpdateTriggered() {
        _isForceUpdatePending.value = true
    }

    fun updateComplete() {
        _isForceUpdatePending.value = false
    }
}

enum class AppUpdateState {
    FORCE, FLEXIBLE, NO_UPDATE, LOADING
}
