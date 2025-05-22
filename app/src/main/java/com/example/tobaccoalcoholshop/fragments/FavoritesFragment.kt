package com.example.tobaccoalcoholshop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tobaccoalcoholshop.services.FavoriteService
import com.example.tobaccoalcoholshop.MainActivity
import com.example.tobaccoalcoholshop.adapters.ProductAdapter
import com.example.tobaccoalcoholshop.R
import com.example.tobaccoalcoholshop.data.Product
import com.example.tobaccoalcoholshop.services.CartService

class FavoritesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var favoriteService: FavoriteService
    private lateinit var cartService: CartService

    private val favoriteProducts = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteService = FavoriteService(requireContext())
        cartService = CartService(requireContext())

        recyclerView = view.findViewById(R.id.favoritesRecyclerView)
        emptyTextView = view.findViewById(R.id.emptyFavoritesTextView)

        setupRecyclerView()
        loadFavoriteProducts()
    }

    override fun onResume() {
        super.onResume()
        loadFavoriteProducts()
    }

    private fun setupRecyclerView() {
        if (!::productAdapter.isInitialized) {
            productAdapter = ProductAdapter(
                favoriteProducts,
                favoriteService,
                cartService,
                object : ProductAdapter.OnFavoriteChangedListener {
                    override fun onFavoriteChanged() {
                        loadFavoriteProducts()
                        (activity as? MainActivity)?.refreshCatalog()
                    }
                }
            )
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    fun loadFavoriteProducts() {
        if (!isAdded || context == null) return

        favoriteProducts.clear()
        val favoriteIds = favoriteService.getFavoriteIds()
        val allProducts = createSampleProducts()
        favoriteProducts.addAll(allProducts.filter { favoriteIds.contains(it.id) })
        if (::productAdapter.isInitialized) {
            productAdapter.notifyDataSetChanged()
        }

        updateEmptyViewVisibility()
    }

    private fun updateEmptyViewVisibility() {
        if (!isAdded || view == null) return
        if (::emptyTextView.isInitialized && ::recyclerView.isInitialized) {
            if (favoriteProducts.isEmpty()) {
                emptyTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun createSampleProducts(): List<Product> {
        return listOf(
            Product(1,"Marlboro Red","Классические сигареты с насыщенным вкусом",950.0,"","tobacco"),
            Product(2,"Jack Daniel's Tennessee Whiskey","Американский виски с нотами дуба, ванили и карамели",18500.0,"","alcohol"),
            Product(3,"Chivas Regal 12","Шотландский купажированный виски 12-летней выдержки",24000.0,"","alcohol"),
            Product(4,"Parliament Aqua Blue","Легкие сигареты с угольным фильтром",1050.0,"","tobacco"),
            Product(5,"Hennessy VS","Французский коньяк с фруктовыми и дубовыми нотами",26000.0,"","alcohol"),
            Product(6,"Winston Blue","Легкие сигареты с классическим вкусом",800.0,"","tobacco"),
            Product(7,"Beluga Noble","Премиальная водка с мягким вкусом",15000.0,"","alcohol"),
            Product(8,"Kent HD","Сигареты с пониженным содержанием никотина",980.0,"","tobacco"),
            Product(9,"Macallan 12","Односолодовый шотландский виски 12 лет выдержки",48000.0,"","alcohol"),
            Product(10,"Camel Blue","Легкие сигареты с мягким вкусом",850.0,"","tobacco")
        )
    }
}