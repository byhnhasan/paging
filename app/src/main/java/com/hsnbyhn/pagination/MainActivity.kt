package com.hsnbyhn.pagination

import DataSource
import FetchCompletionHandler
import FetchError
import FetchResponse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hsnbyhn.pagination.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private lateinit var dataSource: DataSource
    private var isLoading = false

    private var next: String? = null
    private var retryEnabledOnError = true

    private var recyclerViewAdapter: RecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        dataSource = DataSource()
        initViews()
        setContentView(binding!!.root)
    }

    private fun initViews() {
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            recyclerViewAdapter = RecyclerViewAdapter()
            adapter = recyclerViewAdapter
            addOnScrollListener(object : PaginationListener() {

                override fun isLoading(): Boolean {
                    return isLoading
                }

                override fun loadMore() {
                    recyclerViewAdapter?.addLoading()
                    fetchData()
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = layoutManager!!.childCount
                    val totalItemCount = layoutManager!!.itemCount

                    var firstVisibleItemPosition =
                        (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                    if (!isLoading()) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                            isLoading = true
                            loadMore()
                        }
                    }

                }

            })


        }
        fetchData()
        binding?.swipeLayout?.setOnRefreshListener {
            fetchData()
        }
    }

    private fun fetchData() {
        dataSource.fetch(next, object : FetchCompletionHandler {
            override fun invoke(p1: FetchResponse?, p2: FetchError?) {
                binding?.swipeLayout?.isRefreshing = false
                if (p1 != null) {
                    retryEnabledOnError = true
                    next = p1.next
                    if (p1.people.isEmpty()) {
                        binding?.apply {
                            recyclerView.setVisibility(false)
                            noDataText.setVisibility(true)
                        }
                    } else {
                        binding?.apply {
                            recyclerView.setVisibility(true)
                            noDataText.setVisibility(false)
                        }
                        isLoading = false
                        recyclerViewAdapter?.removeLoading()
                        recyclerViewAdapter?.setData(p1.people)
                    }
                } else {
                    Log.e("MainActivity", p2?.errorDescription.orEmpty())
                    if (retryEnabledOnError) {
                        fetchData()
                        retryEnabledOnError = false
                    }
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}


fun View?.setVisibility(visible: Boolean) {
    this?.visibility = if (visible) View.VISIBLE
    else View.GONE
}