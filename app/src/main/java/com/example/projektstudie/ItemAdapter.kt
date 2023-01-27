package com.example.projektstudie

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class ItemAdapter (
    private val context: Context,
    private val items: ResponseArray,
    private val rcv: View
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View, private val picasso: Picasso, private val rcv: View): RecyclerView.ViewHolder(itemView) {
        fun bindData(name: String, distance: String, priceRangeStr: String, ratingsStr: String, ratingsCount: String, context: Context, item: ResponseObject) {
            (itemView.findViewById(R.id.txvItemName) as TextView).text = name
            (itemView.findViewById(R.id.txvDistance) as TextView).text = distance
            (itemView.findViewById(R.id.txvPriceRange) as TextView).text = priceRangeStr
            (itemView.findViewById(R.id.txvRating) as TextView).text = ratingsStr
            (itemView.findViewById(R.id.txvRatingCount) as TextView).text = ratingsCount
            if (item.highlight) itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.highlightYellow))
            picasso
                .load(item.logo)
                .into(itemView.findViewById(R.id.ivwLogo) as ImageView)

            itemView.setOnClickListener {
                rcv.requestFocus()
                context.startActivity(Intent(context, RestaurantActivity::class.java).apply {
                    putExtra("restaurant", item)
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(viewType, parent, false)
        return ItemViewHolder(view, Picasso.Builder(context).build(), rcv)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bindData(
            item.name,
            distance = formatDistance(item.distance),
            priceRangeStr = "€".repeat(item.price_range),
            ratingsStr = formatRating(item.avg_rating),
            ratingsCount = "#${item.ratings.size}",
            context,
            item
        )
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return R.layout.list_item
    }

    private fun formatDistance(distanceRaw: Double): String =

        if (distanceRaw < 1) {
            context.resources.getString(
                R.string.mString,
                distanceRaw.times(1000).roundToInt().toString()
            )
        } else {
            val roundVal =  (distanceRaw * 10.0).roundToInt().div(10.0)
            context.resources.getString(
                R.string.kmString,
                if (roundVal.mod(1.0) == 0.0) roundVal.roundToInt() else roundVal
            )
        }


    private fun formatRating(ratingRaw: Float): String {
        val ratingModRes = ratingRaw.mod(1.0)
        return context.resources.getString(R.string.star)
            .repeat(ratingRaw.toInt()) + when (ratingModRes) {
            in 0.25 .. 0.75 -> context.resources.getString(R.string.halfStar)
            in 0.75 .. 1.0 -> context.resources.getString(R.string.star)
            else -> ""
        }
    }
}