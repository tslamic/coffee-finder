package me.tadej.coffeefinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coffeefinder.Finder

class CoffeeShopAdapter(private val listener: Listener) : RecyclerView.Adapter<CoffeeShopViewHolder>() {
    private var shops = emptyList<Finder.CoffeeShop>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.coffee_shop, parent, false)
        return CoffeeShopViewHolder(view)
    }

    override fun getItemCount(): Int = shops.size

    override fun onBindViewHolder(holder: CoffeeShopViewHolder, position: Int) {
        val shop = shops[position]
        holder.set(listener, shop)
    }

    fun update(s: List<Finder.CoffeeShop>) {
        shops = s
        notifyDataSetChanged()
    }

    interface Listener {
        fun onClick(coffeeShop: Finder.CoffeeShop)
    }
}
