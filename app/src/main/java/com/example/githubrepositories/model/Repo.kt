package com.example.githubrepositories.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "repos")
data class Repo(
    @field:SerializedName("id") var id: Long,
    @field:SerializedName("name") var name: String,
    @field:SerializedName("full_name") var fullName: String,
    @field:SerializedName("description") var description: String?,
    @field:SerializedName("html_url") var url: String,
    @field:SerializedName("stargazers_count") val stars: Int,
    @field:SerializedName("forks_count") var forks: Int,
    @field:SerializedName("language") var language: String?

)
