package com.example.githubrepositories.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.githubrepositories.data.GithubRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.githubrepositories.model.RepoSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchRepositoriesViewModel(
    private val repository: GithubRepository,
    private val saveStateHandle: SavedStateHandle,
) : ViewModel() {
    val state: LiveData<UiState>
    val accept: (UiAction) -> Unit

    init {
        val queryLiveData = MutableLiveData(saveStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY)

        state = queryLiveData
            .distinctUntilChanged()
            .switchMap { queryString ->
                liveData {
                    val uiState = repository.getSearchResultStream(queryString)
                        .map {
                            UiState(
                                query = queryString,
                                searchResult = it
                            )
                        }
                        .asLiveData(Dispatchers.Main)
                    emitSource(uiState)
                }
            }

        accept = { action ->
            when (action) {
                is UiAction.Search -> queryLiveData.postValue(action.query)
                is UiAction.Scroll -> if (action.shouldFetchMore) {
                    val immutableQuery = queryLiveData.value
                    if (immutableQuery != null) {
                        viewModelScope.launch {
                            repository.requestMore(immutableQuery)
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        saveStateHandle[LAST_SEARCH_QUERY] = state.value?.query
        super.onCleared()
    }
}


private val UiAction.Scroll.shouldFetchMore
    get() = visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(
        val visibleItemCount: Int,
        val lastVisibleItemPosition: Int,
        val totalItemCount: Int,
    ) : UiAction()
}

data class UiState(
    val query: String,
    val searchResult: RepoSearchResult,
)

private const val VISIBLE_THRESHOLD = 5
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "Android"