package dev.bogibek.news.ui.fragment

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dev.bogibek.news.R
import dev.bogibek.news.databinding.FragmentArticleBinding
import dev.bogibek.news.ui.MainActivity
import dev.bogibek.news.ui.NewsViewModel
import dev.bogibek.news.utils.viewBinding


class ArticleFragment : Fragment(R.layout.fragment_article) {
    private val binding by viewBinding { FragmentArticleBinding.bind(it) }
    private lateinit var viewModel: NewsViewModel
    val TAG = javaClass.simpleName.toString()
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        viewModel = (activity as MainActivity).viewModel
        val article = args.article
        binding.apply {
            webView.apply {
                webViewClient = WebViewClient()
                loadUrl(article.url)
            }
            fab.setOnClickListener {
                viewModel.saveNews(article)
                Snackbar.make(binding.root, "Article saved successfully", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

}