package com.example.projektstudie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RatingsAdapter (
    private val context: Context,
    private val ratings: ArrayList<Rating>,
) : RecyclerView.Adapter<RatingsAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View, private val context: Context): RecyclerView.ViewHolder(itemView) {
        fun bindData(rating: Rating) {
            (itemView.findViewById(R.id.txvRating) as TextView).text =
                context.resources.getString(R.string.star).repeat(rating.rating)
            (itemView.findViewById(R.id.txvText) as TextView).text =
                rating.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(viewType, parent, false)
        return ItemViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val rating = ratings[position]
        holder.bindData(rating)
    }

    override fun getItemCount(): Int = ratings.size

    override fun getItemViewType(position: Int): Int {
        return R.layout.rating_item
    }
}