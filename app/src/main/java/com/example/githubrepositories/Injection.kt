package com.example.githubrepositories

import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.githubrepositories.api.GithubService
import com.example.githubrepositories.data.GithubRepository
import com.example.githubrepositories.ui.viewmodel.ViewModelProviderFactory

object Injection {

    private fun provideGithubRepository():GithubRepository {
        return GithubRepository(GithubService.create())
    }

    fun provideViewModelFactory(owner:SavedStateRegistryOwner):ViewModelProvider.Factory {
        return ViewModelProviderFactory(owner, provideGithubRepository())
    }
}