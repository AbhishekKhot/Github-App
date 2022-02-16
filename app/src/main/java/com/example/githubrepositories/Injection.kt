package com.example.githubrepositories

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.githubrepositories.api.GithubService
import com.example.githubrepositories.data.GithubRepository
import com.example.githubrepositories.db.RepoDatabase
import com.example.githubrepositories.ui.viewmodel.ViewModelProviderFactory

object Injection {

//    private fun provideGithubRepository():GithubRepository {
//        return GithubRepository(GithubService.create())
//    }
//
//    fun provideViewModelFactory(owner:SavedStateRegistryOwner):ViewModelProvider.Factory {
//        return ViewModelProviderFactory(owner, provideGithubRepository())
//    }

    private fun provideGithubRepository(context: Context): GithubRepository {
        return GithubRepository(GithubService.create(), RepoDatabase.getInstance(context))
    }

    fun provideViewModelFactory(context: Context, owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelProviderFactory(owner, provideGithubRepository(context))
    }
}