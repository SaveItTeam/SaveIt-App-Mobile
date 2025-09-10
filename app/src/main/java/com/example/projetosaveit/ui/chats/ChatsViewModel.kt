package com.example.projetosaveit.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatsViewModel : ViewModel() {
    private val mText = MutableLiveData<String?>()

    init {
        mText.value = "This is notifications fragment"
    }

    val text: LiveData<String?>
        get() = mText
}