package com.example.tobaccoalcoholshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tobaccoalcoholshop.data.CartItem
import com.example.tobaccoalcoholshop.R

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val listener: CartItemListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface CartItemListener {
        fun onIncrementClicked(item: CartItem)
        fun onDecrementClicked(item: CartItem)
        fun onRemoveClicked(item: CartItem)
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.cartItemImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.cartItemNameTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.cartItemPriceTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.cartItemQuantityTextView)
        val incrementButton: ImageButton = itemView.findViewById(R.id.cartItemIncrementButton)
        val decrementButton: ImageButton = itemView.findViewById(R.id.cartItemDecrementButton)
        val removeButton: ImageButton = itemView.findViewById(R.id.cartItemRemoveButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        if (position < 0 || position >= cartItems.size) return

        val item = cartItems[position]
        val product = item.product
        val context = holder.itemView.context

        holder.nameTextView.text = product.name
        holder.priceTextView.text = "₸${product.price} x ${item.quantity}"
        holder.quantityTextView.text = item.quantity.toString()

        val placeholderResId = when (product.category) {
            "alcohol" -> R.drawable.ic_bottle_placeholder
            "tobacco" -> R.drawable.ic_cigarette_placeholder
            else -> R.drawable.ic_bottle_placeholder
        }
        holder.imageView.setImageResource(placeholderResId)

        holder.imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.background_light))
        holder.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        val padding = (context.resources.displayMetrics.density * 8).toInt()
        holder.imageView.setPadding(padding, padding, padding, padding)
        holder.imageView.imageTintList = null


        holder.incrementButton.setOnClickListener {
            listener.onIncrementClicked(item)
        }
        holder.decrementButton.setOnClickListener {
            listener.onDecrementClicked(item)
        }
        holder.removeButton.setOnClickListener {
            listener.onRemoveClicked(item)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged()
    }
}