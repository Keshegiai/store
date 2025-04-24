package com.example.tobaccoalcoholshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tobaccoalcoholshop.R

/**
 * Адаптер для отображения списка продуктов в RecyclerView.
 *
 * @param products Список продуктов для отображения
 * @param favoriteService Сервис для управления избранными продуктами
 * @param listener Слушатель изменений статуса избранного
 */
class ProductAdapter(
    private val products: MutableList<Product>,
    private val favoriteService: FavoriteService,
    private val listener: OnFavoriteChangedListener? = null
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface OnFavoriteChangedListener {
        fun onFavoriteChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.productImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.productDescriptionTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        product.isFavorite = favoriteService.isFavorite(product.id)

        holder.nameTextView.text = product.name
        holder.descriptionTextView.text = product.description
        holder.priceTextView.text = "KZT ${product.price}"

        holder.imageView.setImageResource(
            when (product.category) {
                "alcohol" -> R.drawable.ic_launcher_background
                "tobacco" -> R.drawable.ic_launcher_background
                else -> R.drawable.ic_launcher_background
            }
        )

        updateFavoriteIcon(holder.favoriteButton, product.isFavorite)

        holder.favoriteButton.setOnClickListener {
            toggleFavorite(position)
        }

    }

    private fun toggleFavorite(position: Int) {
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
        if (isFavorite) {
            button.setImageResource(R.drawable.ic_favorite)
        } else {
            button.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    override fun getItemCount() = products.size
}
