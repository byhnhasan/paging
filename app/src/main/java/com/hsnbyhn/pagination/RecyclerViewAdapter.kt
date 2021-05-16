package com.hsnbyhn.pagination

import Person
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hsnbyhn.pagination.databinding.ProgressViewBinding
import com.hsnbyhn.pagination.databinding.ViewPersonItemBinding

/**
 * Created by hasanbayhan on 12.05.2021
 **/

private const val VIEW_TYPE_LOADING = 0
private const val VIEW_TYPE_NORMAL = 1

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val personList = arrayListOf<Person>()

    private var isLoadingVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> ProgressBarViewHolder(
                ProgressViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ItemViewHolder(
                ViewPersonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    }

    override fun getItemCount() = personList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != personList.size - 1) {
            (holder as ItemViewHolder).bind(personList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingVisible) {
            if (position == personList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    fun addLoading() {
        isLoadingVisible = true
        personList.add(Person(id = 0, fullName = ""))
        notifyItemInserted(personList.size - 1)
    }

    fun removeLoading() {
        isLoadingVisible = false
        val pos = personList.size - 1
        if (personList.getOrNull(pos) != null) {
            personList.removeAt(pos)
            notifyItemRemoved(pos)
        }

    }

    fun setData(data: List<Person>) {
        data.forEach { item ->
            if (personList.firstOrNull { it.id == item.id } == null) {
                personList.add(item)
            }
        }
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: ViewPersonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Person?) {
            item?.let {
                binding.name.text = "${it.fullName} (${it.id})"
            }

        }
    }

    inner class ProgressBarViewHolder(binding: ProgressViewBinding) :
        RecyclerView.ViewHolder(binding.root)

}