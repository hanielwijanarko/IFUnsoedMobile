package com.example.ifunsoedmobile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ifunsoedmobile.data.model.BookDoc
import com.example.ifunsoedmobile.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _books = MutableLiveData<List<BookDoc>>()
    val books: LiveData<List<BookDoc>> = _books
    private val TAG = "MainViewModel"

    fun fetchBooks(query: String) {
        Log.d(TAG, "fetchBooks: Dipanggil dengan query = '$query'")
        viewModelScope.launch {
            Log.d(TAG, "fetchBooks: Memulai coroutine viewModelScope")
            try {
                Log.i(TAG, "fetchBooks: Mencoba mengambil data dari API...")

                val response = RetrofitInstance.api.searchBooks(query, limit = 10)

                Log.d(TAG, "fetchBooks: Response diterima. isSuccessful: ${response.isSuccessful}, Code: ${response.code()}, Message: ${response.message()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    val result = responseBody?.docs ?: emptyList()
                    Log.i(TAG, "fetchBooks: Parsing berhasil. Jumlah buku: ${result.size}")

                    if (result.isNotEmpty()) {

                        val firstBook = result[0]
                        Log.d(TAG, "fetchBooks: Buku pertama - Key: ${firstBook.key}, Title: ${firstBook.title}, Authors: ${firstBook.authorName?.joinToString()}, Year: ${firstBook.firstPublishYear}")
                    } else {
                        Log.w(TAG, "fetchBooks: Daftar buku yang diparsing dari API kosong.")
                    }

                    _books.postValue(result)
                    Log.i(TAG, "fetchBooks: _books LiveData diperbarui dengan ${result.size} buku.")

                } else {
                    Log.e(TAG, "fetchBooks: Panggilan API GAGAL. Code: ${response.code()}, Message: ${response.message()}")
                    _books.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchBooks: EXCEPTION saat mengambil/memproses data API!", e)
                _books.postValue(emptyList())
            }
        }
    }
}