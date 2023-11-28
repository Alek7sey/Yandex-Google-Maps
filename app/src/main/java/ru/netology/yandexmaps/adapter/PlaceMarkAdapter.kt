package ru.netology.yandexmaps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.databinding.PlacemarkItemBinding
import ru.netology.yandexmaps.dto.PlaceMark

interface OnInteractionListener {
    fun onClick(placeMark: PlaceMark) {}
    fun onDelete(placeMark: PlaceMark) {}
    fun onEdit(placeMark: PlaceMark) {}
}

class PlaceMarkAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<PlaceMark, PlaceMarkHolder>(DiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceMarkHolder {
        val binding = PlacemarkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceMarkHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PlaceMarkHolder, position: Int) {
        val placemark = getItem(position)
        holder.bind(placemark)
    }
}

class PlaceMarkHolder(
    private val binding: PlacemarkItemBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(placemark: PlaceMark) {
        binding.apply {
            title.text = placemark.title
            type.setImageResource(R.drawable.ic_placemark)
            binding.menu.isVisible = true

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.place_mark_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onDelete(placemark)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(placemark)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            root.setOnClickListener {
                onInteractionListener.onClick(placemark)
            }
        }
    }


}

class DiffCallBack : DiffUtil.ItemCallback<PlaceMark>() {
    override fun areItemsTheSame(oldItem: PlaceMark, newItem: PlaceMark): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaceMark, newItem: PlaceMark): Boolean {
        return oldItem == newItem
    }
}