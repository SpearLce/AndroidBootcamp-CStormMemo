package com.illidancstormrage.cstormmemo.ui

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _selectedImageUri = MutableLiveData<Uri>()
    val selectedImageUri: LiveData<Uri>
        get() = _selectedImageUri

    fun setImageUri(uri: Uri) {
        _selectedImageUri.value = uri
    }

    private val _data = MutableLiveData<Intent>()
    val data: LiveData<Intent> get() = _data
    fun setData(data: Intent) {
        _data.value = data
    }

}