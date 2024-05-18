package com.example.vaultnfc.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.data.repository.TagRepository
import com.example.vaultnfc.model.Tag
import kotlinx.coroutines.launch

class TagViewModel(private val repository: TagRepository) : ViewModel() {

    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> get() = _tags

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        fetchTags()
    }

    fun fetchTags() {
        viewModelScope.launch {
            try {
                val tagList = repository.getAllTags()
                _tags.postValue(tagList)
            } catch (e: Exception) {
                _error.postValue("Error fetching tags: ${e.message}")
            }
        }
    }

    fun addTag(tag: Tag) {
        viewModelScope.launch {
            try {
                repository.addTag(tag)
                fetchTags() // Refresh the tag list after adding a new tag
            } catch (e: Exception) {
                _error.postValue("Error adding tag: ${e.message}")
            }
        }
    }
}
