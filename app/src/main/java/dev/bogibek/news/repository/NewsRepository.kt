package dev.bogibek.news.repository

import dev.bogibek.news.data.local.ArticleDatabase
import dev.bogibek.news.data.remote.ApiClient
import dev.bogibek.news.model.Article

class NewsRepository(val database: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        ApiClient.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        ApiClient.api.searchForNews(searchQuery, pageNumber)

    suspend fun saveNews(article: Article) = database.getArticleDao().addArticle(article)

    fun getSavedNews() = database.getArticleDao().getAllArticles()

    suspend fun deleteSavedNews(article: Article) = database.getArticleDao().deleteArticle(article)
}