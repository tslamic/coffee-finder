package me.tadej.coffeefinder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coffeefinder.Finder

class CoffeeShopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun set(listener: CoffeeShopAdapter.Listener, coffeeShop: Finder.CoffeeShop) {
        itemView.findViewById<TextView>(R.id.name).text = coffeeShop.name
        itemView.findViewById<TextView>(R.id.rating).text =
            itemView.resources.getString(R.string.coffee_rating, coffeeShop.rating)
        itemView.setOnClickListener {
            listener.onClick(coffeeShop)
        }
    }
}
