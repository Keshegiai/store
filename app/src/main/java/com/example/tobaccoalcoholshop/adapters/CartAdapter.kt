package com.example.tobaccoalcoholshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tobaccoalcoholshop.data.CartItem
import com.example.tobaccoalcoholshop.R

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val listener: CartItemListener // Интерфейс для обратной связи с фрагментом
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // Интерфейс для обработки действий пользователя в элементе корзины
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
        // Проверка валидности позиции
        if (position < 0 || position >= cartItems.size) return

        val item = cartItems[position]

        holder.nameTextView.text = item.product.name
        // Отображаем цену за штуку и количество
        holder.priceTextView.text = "₸${item.product.price} x ${item.quantity}"
        holder.quantityTextView.text = item.quantity.toString()
        holder.imageView.setImageResource(R.drawable.ic_launcher_background) // Замените

        // Установка слушателей для кнопок
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

    // Метод для обновления данных в адаптере
    fun updateItems(newItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged() // Уведомляем RecyclerView об изменениях
    }
}