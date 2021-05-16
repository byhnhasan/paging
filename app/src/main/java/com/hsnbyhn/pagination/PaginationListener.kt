package com.hsnbyhn.pagination

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by hasanbayhan on 16.05.2021
 **/
abstract class PaginationListener : RecyclerView.OnScrollListener() {

    abstract fun isLoading(): Boolean

    abstract fun loadMore()
}