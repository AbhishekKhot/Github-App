package com.example.githubrepositories.model

sealed class Resource {
    data class Success(val data:List<Repo>):Resource()
    data class Error(val error:Exception):Resource()
}