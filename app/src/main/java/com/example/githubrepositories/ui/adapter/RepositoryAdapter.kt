package com.example.githubrepositories.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.githubrepositories.model.Repo

class RepositoryAdapter : ListAdapter<Repo, RepositoryViewHolder>(REPO_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repoItem = getItem(position)
        if(repoItem != null) {
            holder.bind(repoItem)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem.fullName == newItem.fullName

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem == newItem
        }
    }
}