package com.dicoding.asclepius.data.remote.response

import com.google.gson.annotations.SerializedName

data class Response(
	@field:SerializedName("articles")
	val articles: List<ArticlesItem>? = null,
)

data class ArticlesItem(
	@field:SerializedName("publishedAt")
	val publishedAt: String? = null,

	@field:SerializedName("urlToImage")
	val urlToImage: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)
