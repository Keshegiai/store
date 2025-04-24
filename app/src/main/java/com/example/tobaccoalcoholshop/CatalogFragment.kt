package com.example.tobaccoalcoholshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tobaccoalcoholshop.R

class CatalogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var favoriteService: FavoriteService
    private val productsList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteService = FavoriteService(requireContext())

        recyclerView = view.findViewById(R.id.productsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        productsList.addAll(createSampleProducts())

        updateFavoriteStatus()

        productAdapter = ProductAdapter(
            productsList,
            favoriteService,
            object : ProductAdapter.OnFavoriteChangedListener {
                override fun onFavoriteChanged() {
                    (activity as? MainActivity)?.refreshFavorites()
                }
            }
        )

        recyclerView.adapter = productAdapter
    }

    fun updateFavoriteStatus() {
        val favoriteIds = favoriteService.getFavoriteIds()
        for (product in productsList) {
            product.isFavorite = favoriteIds.contains(product.id)
        }
        if (::productAdapter.isInitialized) {
            productAdapter.notifyDataSetChanged()
        }
    }

    private fun createSampleProducts(): List<Product> {
        return listOf(
            Product(
                1,
                "Marlboro Red",
                "Классические сигареты с насыщенным вкусом",
                170.0,
                "",
                "tobacco"
            ),
            Product(
                2,
                "Jack Daniel's Tennessee Whiskey",
                "Американский виски с нотами дуба, ванили и карамели",
                2500.0,
                "",
                "alcohol"
            ),
            Product(
                3,
                "Chivas Regal 12",
                "Шотландский купажированный виски 12-летней выдержки",
                3200.0,
                "",
                "alcohol"
            ),
            Product(
                4,
                "Parliament Aqua Blue",
                "Легкие сигареты с угольным фильтром",
                190.0,
                "",
                "tobacco"
            ),
            Product(
                5,
                "Hennessy VS",
                "Французский коньяк с фруктовыми и дубовыми нотами",
                3800.0,
                "",
                "alcohol"
            ),
            Product(
                6,
                "Winston Blue",
                "Легкие сигареты с классическим вкусом",
                150.0,
                "",
                "tobacco"
            )
        )
    }
}
