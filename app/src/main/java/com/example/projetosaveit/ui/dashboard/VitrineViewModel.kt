package com.example.projetosaveit.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VitrineViewModel : ViewModel() {
    private val mText = MutableLiveData<String?>()

    init {
        mText.value = "This is dashboard fragment"
    }

    val text: LiveData<String?>
        get() = mText
}