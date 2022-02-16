package com.example.githubrepositories.model

sealed class RepoSearchResult {
    data class Success(val data:List<Repo>):RepoSearchResult()
    data class Error(val error:Exception):RepoSearchResult()
}