package com.vinodk.launcher.ui.reading

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BookItem(
    val id: String,
    val title: String,
    val author: String,
    val currentPage: Int,
    val totalPages: Int,
    val progressPercent: Float,
    val lastOpenedMs: Long = System.currentTimeMillis(),
)

data class ReadingModeUiState(
    val isReadingModeActive: Boolean = false,
    val currentBook: BookItem? = null,
    val recentBooks: List<BookItem> = emptyList(),
    val availableApps: List<String> = listOf(
        "Kindle",
        "Google Play Books",
        "Pocket",
        "Medium",
        "Instapaper"
    ),
    val currentTime: String = "",
    val distraction: String = "OFF",
)

class ReadingModeViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReadingModeUiState())
    val uiState: StateFlow<ReadingModeUiState> = _uiState.asStateFlow()

    private val mockBooks = listOf(
        BookItem(
            id = "1",
            title = "The Age of Surveillance Capitalism",
            author = "Shoshana Zuboff",
            currentPage = 342,
            totalPages = 520,
            progressPercent = 0.658f
        ),
        BookItem(
            id = "2",
            title = "Digital Minimalism",
            author = "Cal Newport",
            currentPage = 156,
            totalPages = 296,
            progressPercent = 0.527f
        ),
        BookItem(
            id = "3",
            title = "The Attention Merchants",
            author = "Tim Wu",
            currentPage = 89,
            totalPages = 512,
            progressPercent = 0.174f
        )
    )

    init {
        // Set the first book as current
        _uiState.value = _uiState.value.copy(
            currentBook = mockBooks.firstOrNull(),
            recentBooks = mockBooks
        )
        
        // Update time every minute
        updateCurrentTime()
    }

    fun toggleReadingMode() {
        _uiState.value = _uiState.value.copy(
            isReadingModeActive = !_uiState.value.isReadingModeActive
        )
    }

    fun activateReadingMode() {
        _uiState.value = _uiState.value.copy(isReadingModeActive = true)
    }

    fun deactivateReadingMode() {
        _uiState.value = _uiState.value.copy(isReadingModeActive = false)
    }

    fun selectBook(book: BookItem) {
        _uiState.value = _uiState.value.copy(currentBook = book)
    }

    fun updateCurrentTime() {
        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val currentTime = timeFormat.format(java.util.Date())
        _uiState.value = _uiState.value.copy(currentTime = currentTime)
    }

    fun setDistractionLevel(level: String) {
        _uiState.value = _uiState.value.copy(distraction = level)
    }

    fun updateBookProgress(bookId: String, newPage: Int) {
        val updated = _uiState.value.currentBook?.copy(
            currentPage = newPage,
            progressPercent = newPage.toFloat() / (_uiState.value.currentBook!!.totalPages)
        )
        if (updated != null) {
            _uiState.value = _uiState.value.copy(currentBook = updated)
        }
    }

    fun addCustomBook(title: String, author: String, totalPages: Int) {
        val newBook = BookItem(
            id = System.currentTimeMillis().toString(),
            title = title,
            author = author,
            currentPage = 0,
            totalPages = totalPages,
            progressPercent = 0f
        )
        val updated = _uiState.value.recentBooks.toMutableList()
        updated.add(0, newBook)
        _uiState.value = _uiState.value.copy(
            recentBooks = updated,
            currentBook = newBook
        )
    }
}
