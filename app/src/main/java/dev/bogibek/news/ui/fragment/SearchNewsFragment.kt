package dev.bogibek.news.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.bogibek.news.R
import dev.bogibek.news.adapter.NewsAdapter
import dev.bogibek.news.databinding.FragmentSearchNewsBinding
import dev.bogibek.news.ui.MainActivity
import dev.bogibek.news.ui.NewsViewModel
import dev.bogibek.news.utils.Constants
import dev.bogibek.news.utils.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import dev.bogibek.news.utils.RecyclerViewOnScrollListener
import dev.bogibek.news.utils.Resource
import dev.bogibek.news.utils.viewBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    private val binding by viewBinding { FragmentSearchNewsBinding.bind(it) }
    private lateinit var viewModel: NewsViewModel
    lateinit var newAdapter: NewsAdapter
    val TAG = javaClass.simpleName.toString()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()

        newAdapter.setOnClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }


        var job: Job? = null

        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newAdapter.differ.submitList(newsResponse.articles.toList())
                        Log.d(TAG, "${newsResponse.articles} ")
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        RecyclerViewOnScrollListener.isLastPage =
                            totalPages == viewModel.searchNewsPage

                        if (RecyclerViewOnScrollListener.isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }

                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("@@@", "An error accrued:$message ")
                    }

                }
            }


        })
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        RecyclerViewOnScrollListener.isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        RecyclerViewOnScrollListener.isLoading = true
    }

    private fun setupRecyclerView() {
        newAdapter = NewsAdapter()
        binding.apply {
            rvSearchNews.apply {
                adapter = newAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(RecyclerViewOnScrollListener.scrollListener)
                RecyclerViewOnScrollListener.isShouldPaginate = {
                    if (it) {
                        viewModel.searchNews(binding.etSearch.text.toString())
                    }
                }
            }
        }
    }

}