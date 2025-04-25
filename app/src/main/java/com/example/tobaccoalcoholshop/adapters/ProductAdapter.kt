package com.example.tobaccoalcoholshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tobaccoalcoholshop.services.FavoriteService
import com.example.tobaccoalcoholshop.R
import com.example.tobaccoalcoholshop.data.Product
import com.example.tobaccoalcoholshop.services.CartService
import android.widget.Button
import android.widget.Toast

class ProductAdapter(
    private val products: MutableList<Product>,
    private val favoriteService: FavoriteService,
    private val cartService: CartService,
    private val listener: OnFavoriteChangedListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_LIST = 1
        const val VIEW_TYPE_GRID = 2
    }

    private var currentViewType = VIEW_TYPE_LIST

    interface OnFavoriteChangedListener {
        fun onFavoriteChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.productImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.productDescriptionTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton) // Добавлено
    }

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.productImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.productDescriptionTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton) // Добавлено
    }

    fun setViewType(viewType: Int) {
        if (currentViewType != viewType) {
            currentViewType = viewType
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentViewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GRID -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product_grid, parent, false)
                GridViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product, parent, false)
                ListViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < 0 || position >= products.size) return

        val product = products[position]
        product.isFavorite = favoriteService.isFavorite(product.id)

        when (holder) {
            is ListViewHolder -> bindListViewHolder(holder, product, position)
            is GridViewHolder -> bindGridViewHolder(holder, product, position)
        }
    }

    private fun bindListViewHolder(holder: ListViewHolder, product: Product, position: Int) {
        holder.nameTextView.text = product.name
        holder.descriptionTextView.text = product.description
        holder.priceTextView.text = "₸${product.price}"

        holder.imageView.setImageResource(R.drawable.ic_launcher_background) // Замените
        updateFavoriteIcon(holder.favoriteButton, product.isFavorite)

        holder.favoriteButton.setOnClickListener {
            toggleFavorite(position)
        }
        holder.addToCartButton.setOnClickListener {
            cartService.addItem(product)
            Toast.makeText(holder.itemView.context, "${product.name} добавлен в корзину", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindGridViewHolder(holder: GridViewHolder, product: Product, position: Int) {
        holder.nameTextView.text = product.name
        holder.descriptionTextView.text = product.description
        holder.priceTextView.text = "₸${product.price}"

        holder.imageView.setImageResource(R.drawable.ic_launcher_background)
        updateFavoriteIcon(holder.favoriteButton, product.isFavorite)

        holder.favoriteButton.setOnClickListener {
            toggleFavorite(position)
        }

        holder.addToCartButton.setOnClickListener {
            cartService.addItem(product)
            Toast.makeText(holder.itemView.context, "${product.name} добавлен в корзину", Toast.LENGTH_SHORT).show()
        }

    }

    private fun toggleFavorite(position: Int) {
        if (position < 0 || position >= products.size) return
        val product = products[position]
        product.isFavorite = !product.isFavorite
        if (product.isFavorite) {
            favoriteService.addToFavorites(product.id)
        } else {
            favoriteService.removeFromFavorites(product.id)
        }
        notifyItemChanged(position)
        listener?.onFavoriteChanged()
    }

    private fun updateFavoriteIcon(button: ImageButton, isFavorite: Boolean) {
        button.setImageResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
    }

    override fun getItemCount() = products.size
}