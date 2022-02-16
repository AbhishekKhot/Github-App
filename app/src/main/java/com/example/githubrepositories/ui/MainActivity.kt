package com.example.githubrepositories.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositories.Injection
import com.example.githubrepositories.databinding.ActivityMainBinding
import com.example.githubrepositories.model.RepoSearchResult
import com.example.githubrepositories.ui.adapter.RepositoryAdapter
import com.example.githubrepositories.ui.viewmodel.SearchRepositoriesViewModel
import com.example.githubrepositories.ui.viewmodel.UiAction
import com.example.githubrepositories.ui.viewmodel.UiState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this))
            .get(SearchRepositoriesViewModel::class.java)

        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        binding.bindState(
            uiState = viewModel.state,
            uiActions = viewModel.accept
        )
    }

    private fun ActivityMainBinding.bindState(
        uiState: LiveData<UiState>,
        uiActions: (UiAction) -> Unit,
    ) {
        val repoAdapter = RepositoryAdapter()
        list.adapter = repoAdapter

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            repoAdapter = repoAdapter,
            uiState = uiState,
            onScrollChanged = uiActions
        )
    }

    private fun ActivityMainBinding.bindSearch(
        uiState: LiveData<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit,
    ) {
        searchRepo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchRepo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        uiState
            .map(UiState::query)
            .distinctUntilChanged()
            .observe(this@MainActivity, searchRepo::setText)
    }

    private fun ActivityMainBinding.updateRepoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun ActivityMainBinding.bindList(
        repoAdapter: RepositoryAdapter,
        uiState: LiveData<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit,
    ) {
        setupScrollListener(onScrollChanged)

        uiState
            .map(UiState::searchResult)
            .distinctUntilChanged()
            .observe(this@MainActivity) { result ->
                when (result) {
                    is RepoSearchResult.Success -> {
                        showEmptyList(result.data.isEmpty())
                        repoAdapter.submitList(result.data)
                    }
                    is RepoSearchResult.Error -> {
                        Toast.makeText(
                            this@MainActivity,
                            "\uD83D\uDE28 Wooops $result.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    private fun ActivityMainBinding.showEmptyList(show: Boolean) {
        emptyList.isVisible = show
        list.isVisible = !show
    }

    private fun ActivityMainBinding.setupScrollListener(
        onScrollChanged: (UiAction.Scroll) -> Unit,
    ) {
        val layoutManager = list.layoutManager as LinearLayoutManager
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                onScrollChanged(
                    UiAction.Scroll(
                        visibleItemCount = visibleItemCount,
                        lastVisibleItemPosition = lastVisibleItem,
                        totalItemCount = totalItemCount
                    )
                )
            }
        })
    }
}