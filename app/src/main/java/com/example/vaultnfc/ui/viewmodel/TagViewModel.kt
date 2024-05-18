package com.example.vaultnfc.ui.viewmodel

import PasswordsViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TagViewModel(private val passwordsViewModel: PasswordsViewModel) : ViewModel() {

    private val _tags = MutableLiveData<List<String>>()
    val tags: LiveData<List<String>> get() = _tags

    init {
        fetchTags()
    }

    private fun fetchTags() {
        viewModelScope.launch {
            passwordsViewModel.passwordsList.observeForever { passwords ->
                var tags = passwords.map { it.tag.trim() }.toSet().toList()

                //filter empty tags
                tags = tags.filter { it.isNotEmpty() }

                _tags.postValue(tags)
                Log.d("TagViewModel", "Fetched tags: $tags")
            }
        }
    }
}


class TagViewModelFactory(private val passwordsViewModel: PasswordsViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TagViewModel(passwordsViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

