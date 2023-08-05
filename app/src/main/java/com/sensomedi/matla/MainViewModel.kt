package com.sensomedi.matla

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensomedi.data.Detail
import com.sensomedi.data.MatlaData
import com.sensomedi.data.Temporary
import com.sensomedi.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _isScanning = MutableLiveData(false)
    var isScanning: LiveData<Boolean> = _isScanning

    fun setScanning(bool: Boolean) {
        _isScanning.value = bool
    }

    private val _isConnected = MutableLiveData(false)
    var isConnected: LiveData<Boolean> = _isConnected

    fun setConnected(bool: Boolean) {
        _isConnected.value = bool
    }

    private val _isMeasuring = MutableLiveData(false)
    var isMeasuring: LiveData<Boolean> = _isMeasuring

    fun setMeasure(bool: Boolean) {
        _isMeasuring.value = bool
    }

    private val _measureTime = MutableLiveData(1000 * 60 * 60 * 2L)
    var measureTime: LiveData<Long> = _measureTime

    fun setMeasureTime(time: Long) {
        _measureTime.value = time
    }

    private val _user = MutableLiveData(User())
    var user: LiveData<User> = _user

    fun setUser(user: User) {
        viewModelScope.launch {
            _user.value = user
        }
    }

    private val _matlaDataList = MutableLiveData(arrayListOf<Detail>())
    var matlaDataList: LiveData<ArrayList<Detail>> = _matlaDataList

    fun setMatlaDataList(datas: ArrayList<Detail>) {
        _matlaDataList.value = datas
    }

    private val _matlaListSize = MutableLiveData(100000)
    var matlaListSize: LiveData<Int> = _matlaListSize

    fun setMatlaListSize(size: Int){
        _matlaListSize.value = size
    }

    private val _matlaData = MutableLiveData(Temporary(0L, listOf()))
    var matlaData: LiveData<Temporary> = _matlaData

    fun setMatlaData(matla: Temporary) {
        CoroutineScope(Dispatchers.IO).launch {
            _matlaData.postValue(matla)
        }
    }

}