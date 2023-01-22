package com.example.projektstudie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodMenuAdapter (
    private val context: Context,
    private val menu: ArrayList<MenuEntry>,
) : RecyclerView.Adapter<FoodMenuAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindData(menuEntry: MenuEntry) {
            (itemView.findViewById(R.id.txvFoodItem) as TextView).text = menuEntry.name
            (itemView.findViewById(R.id.txvItemPrice) as TextView).text = menuEntry.price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(viewType, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val menuEntry = menu[position]
        holder.bindData(menuEntry)
    }

    override fun getItemCount(): Int = menu.size

    override fun getItemViewType(position: Int): Int {
        return R.layout.food_menu_item
    }
}