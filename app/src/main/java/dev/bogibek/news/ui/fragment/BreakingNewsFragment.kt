package dev.bogibek.news.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.bogibek.news.R
import dev.bogibek.news.adapter.NewsAdapter
import dev.bogibek.news.databinding.FragmentBreakingNewsBinding
import dev.bogibek.news.ui.MainActivity
import dev.bogibek.news.ui.NewsViewModel
import dev.bogibek.news.utils.Constants.Companion.QUERY_PAGE_SIZE
import dev.bogibek.news.utils.RecyclerViewOnScrollListener
import dev.bogibek.news.utils.Resource
import dev.bogibek.news.utils.viewBinding


class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private val binding by viewBinding { FragmentBreakingNewsBinding.bind(it) }
    lateinit var viewModel: NewsViewModel
    lateinit var newAdapter: NewsAdapter
    var TAG =javaClass.simpleName.toString()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        viewModel = (activity as MainActivity).viewModel
        viewModel.getBreakingNews("us")
        setupRecyclerView()

        newAdapter.setOnClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }


        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        RecyclerViewOnScrollListener.isLastPage =
                            totalPages == viewModel.breakingNewsPage
                        Log.d(TAG, "initViews: ${newsResponse.articles}")
                        if (RecyclerViewOnScrollListener.isLastPage) {
                            binding.rvBreakingNews.setPadding(0, 0, 0, 0)
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
            rvBreakingNews.apply {
                adapter = newAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(RecyclerViewOnScrollListener.scrollListener)
                RecyclerViewOnScrollListener.isShouldPaginate = {
                    if (it) {
                        viewModel.getBreakingNews("us")
                    }
                }
            }
        }
    }

}