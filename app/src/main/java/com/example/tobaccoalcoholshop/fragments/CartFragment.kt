package com.example.tobaccoalcoholshop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tobaccoalcoholshop.R
import com.example.tobaccoalcoholshop.adapters.CartAdapter
import com.example.tobaccoalcoholshop.data.CartItem
import com.example.tobaccoalcoholshop.services.CartService

class CartFragment : Fragment(), CartAdapter.CartItemListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartService: CartService
    private lateinit var totalPriceTextView: TextView
    private lateinit var emptyCartTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var clearCartButton: Button
    private lateinit var totalPriceLabelTextView: TextView

    private val cartItems = mutableListOf<CartItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartService = CartService(requireContext())

        recyclerView = view.findViewById(R.id.cartRecyclerView)
        totalPriceTextView = view.findViewById(R.id.totalPriceValueTextView)
        emptyCartTextView = view.findViewById(R.id.emptyCartTextView)
        checkoutButton = view.findViewById(R.id.checkoutButton)
        clearCartButton = view.findViewById(R.id.clearCartButton)
        totalPriceLabelTextView = view.findViewById(R.id.totalPriceLabelTextView)

        setupRecyclerView()
        loadCartDataAndUpdateUI()

        checkoutButton.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                Toast.makeText(requireContext(), "Переход к оформлению заказа...", Toast.LENGTH_SHORT).show()
                cartService.clearCart()
                loadCartDataAndUpdateUI()
            } else {
                Toast.makeText(requireContext(), "Корзина пуста", Toast.LENGTH_SHORT).show()
            }
        }

        clearCartButton.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                cartService.clearCart()
                loadCartDataAndUpdateUI()
                Toast.makeText(requireContext(), "Корзина очищена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadCartDataAndUpdateUI()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems, this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun loadCartDataAndUpdateUI() {
        if (!isAdded || context == null) {
            return
        }

        val currentCartItems = cartService.getCartItems()
        cartItems.clear()
        cartItems.addAll(currentCartItems)
        if (::cartAdapter.isInitialized) {
            cartAdapter.notifyDataSetChanged()
        }

        val totalPrice = cartService.getTotalPrice()
        if (::totalPriceTextView.isInitialized) {
            totalPriceTextView.text = "₸${totalPrice}"
        }
        if (cartItems.isEmpty()) {
            if (::emptyCartTextView.isInitialized) emptyCartTextView.visibility = View.VISIBLE
            if (::recyclerView.isInitialized) recyclerView.visibility = View.GONE
            if (::totalPriceLabelTextView.isInitialized) totalPriceLabelTextView.visibility = View.GONE // <-- Проверка добавлена
            if (::totalPriceTextView.isInitialized) totalPriceTextView.visibility = View.GONE          // <-- Проверка добавлена
            if (::clearCartButton.isInitialized) clearCartButton.visibility = View.GONE
            if (::checkoutButton.isInitialized) checkoutButton.isEnabled = false
        } else {
            if (::emptyCartTextView.isInitialized) emptyCartTextView.visibility = View.GONE
            if (::recyclerView.isInitialized) recyclerView.visibility = View.VISIBLE
            if (::totalPriceLabelTextView.isInitialized) totalPriceLabelTextView.visibility = View.VISIBLE // <-- Проверка добавлена
            if (::totalPriceTextView.isInitialized) totalPriceTextView.visibility = View.VISIBLE          // <-- Проверка добавлена
            if (::clearCartButton.isInitialized) clearCartButton.visibility = View.VISIBLE
            if (::checkoutButton.isInitialized) checkoutButton.isEnabled = true
        }
    }

    override fun onIncrementClicked(item: CartItem) {
        cartService.incrementQuantity(item.product.id)
        loadCartDataAndUpdateUI()
    }

    override fun onDecrementClicked(item: CartItem) {
        cartService.decrementQuantity(item.product.id)
        loadCartDataAndUpdateUI()
    }

    override fun onRemoveClicked(item: CartItem) {
        cartService.removeItem(item.product.id)
        loadCartDataAndUpdateUI()
        Toast.makeText(requireContext(), "${item.product.name} удален из корзины", Toast.LENGTH_SHORT).show()
    }
}