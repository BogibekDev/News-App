package dev.bogibek.news.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dev.bogibek.news.R
import dev.bogibek.news.adapter.NewsAdapter
import dev.bogibek.news.databinding.FragmentSavedNewsBinding
import dev.bogibek.news.ui.MainActivity
import dev.bogibek.news.ui.NewsViewModel
import dev.bogibek.news.utils.viewBinding


class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    val binding by viewBinding { FragmentSavedNewsBinding.bind(it) }

    private lateinit var viewModel: NewsViewModel
    val TAG = javaClass.simpleName.toString()
    lateinit var newAdapter: NewsAdapter

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
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newAdapter.differ.currentList[position]
                viewModel.deleteSavedNews(article)
                Snackbar.make(binding.root, "Successfully deleted Article", Snackbar.LENGTH_LONG)
                    .apply {
                        setAction("Undo") {
                            viewModel.saveNews(article)
                        }
                        show()
                    }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)

        }

        viewModel.getAllNews().observe(viewLifecycleOwner, Observer { articles ->
            newAdapter.differ.submitList(articles)

        })
    }

    private fun setupRecyclerView() {
        newAdapter = NewsAdapter()
        binding.apply {
            rvSavedNews.apply {
                adapter = newAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
    }

}