package com.example.tobaccoalcoholshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tobaccoalcoholshop.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CatalogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var filterChipGroup: ChipGroup
    private lateinit var sortPriceButton: Button
    private lateinit var viewModeButton: ImageButton
    private lateinit var productAdapter: ProductAdapter
    private lateinit var favoriteService: FavoriteService

    private val allProducts: MutableList<Product> by lazy { createSampleProducts().toMutableList() }
    private val displayedProducts = mutableListOf<Product>()

    private var currentFilter: String? = null
    private var isAscendingSortOrder = true
    private var isListView = true

    private var recyclerViewState: Bundle? = null
    private val KEY_RECYCLER_STATE = "recycler_state"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getBundle(KEY_RECYCLER_STATE)
        }
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteService = FavoriteService(requireContext())

        recyclerView = view.findViewById(R.id.productsRecyclerView)
        emptyTextView = view.findViewById(R.id.emptyFilterResultTextView)
        filterChipGroup = view.findViewById(R.id.filterChipGroup)
        sortPriceButton = view.findViewById(R.id.sortPriceButton)
        viewModeButton = view.findViewById(R.id.viewModeButton)

        setupLayoutManager()

        if (!::productAdapter.isInitialized) {
            productAdapter = ProductAdapter(
                displayedProducts,
                favoriteService,
                object : ProductAdapter.OnFavoriteChangedListener {
                    override fun onFavoriteChanged() {
                        updateFavoriteStatusInAllProducts()
                        (activity as? MainActivity)?.refreshFavorites()
                    }
                }
            )
        }
        recyclerView.adapter = productAdapter

        setupFilterListeners()
        setupSortButtonListener()
        setupViewModeButtonListener()

        updateFavoriteStatusInAllProducts()

        applyFilterAndSort()

        productAdapter.setViewType(if (isListView) ProductAdapter.VIEW_TYPE_LIST else ProductAdapter.VIEW_TYPE_GRID)
        updateViewModeButtonIcon()

        if (recyclerViewState != null) {
            recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState?.getParcelable("LAYOUT_MANAGER_STATE"))
            recyclerViewState = null
        }

        updateEmptyViewVisibility()
    }

    override fun onPause() {
        super.onPause()
        recyclerViewState = Bundle()
        val layoutManagerState = recyclerView.layoutManager?.onSaveInstanceState()
        recyclerViewState?.putParcelable("LAYOUT_MANAGER_STATE", layoutManagerState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBundle(KEY_RECYCLER_STATE, recyclerViewState)
    }

    private fun setupLayoutManager() {
        recyclerView.layoutManager = if (isListView) {
            LinearLayoutManager(requireContext())
        } else {
            GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupViewModeButtonListener() {
        viewModeButton.setOnClickListener {
            toggleViewMode()
        }
        updateViewModeButtonIcon()
    }


    private fun updateViewModeButtonIcon() {
        viewModeButton.setImageResource(
            if (isListView) R.drawable.ic_view_grid else R.drawable.ic_view_list
        )
    }

    private fun toggleViewMode() {
        isListView = !isListView
        updateViewModeButtonIcon()
        productAdapter.setViewType(
            if (isListView) ProductAdapter.VIEW_TYPE_LIST else ProductAdapter.VIEW_TYPE_GRID
        )
        setupLayoutManager()
        recyclerView.adapter = productAdapter
    }

    private fun setupFilterListeners() {
        filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val newFilter = when (checkedId) {
                R.id.chipTobacco -> "tobacco"
                R.id.chipAlcohol -> "alcohol"
                else -> null
            }

            if (newFilter != currentFilter) {
                currentFilter = newFilter
                applyFilterAndSort()
            }

            if (checkedId == -1 && group.checkedChipId == -1) {
                view?.findViewById<Chip>(R.id.chipAll)?.isChecked = true
            }
        }
        val initialChipId = when(currentFilter) {
            "tobacco" -> R.id.chipTobacco
            "alcohol" -> R.id.chipAlcohol
            else -> R.id.chipAll
        }
        if (filterChipGroup.checkedChipId != initialChipId) {
            filterChipGroup.check(initialChipId)
        }
    }

    private fun setupSortButtonListener() {
        sortPriceButton.setOnClickListener {
            toggleSortOrder()
        }
        updateSortButtonText()
    }

    private fun updateSortButtonText() {
        sortPriceButton.text = if (isAscendingSortOrder) "По цене ↑" else "По цене ↓"
    }

    private fun applyFilterAndSort() {
        displayedProducts.clear()

        val filteredList = if (currentFilter == null) {
            allProducts
        } else {
            allProducts.filter { it.category == currentFilter }
        }

        val sortedList = if (isAscendingSortOrder) {
            filteredList.sortedBy { it.price }
        } else {
            filteredList.sortedByDescending { it.price }
        }

        displayedProducts.addAll(sortedList)

        if (::productAdapter.isInitialized) {
            productAdapter.notifyDataSetChanged()
        }

        updateEmptyViewVisibility()
    }


    private fun toggleSortOrder() {
        isAscendingSortOrder = !isAscendingSortOrder
        updateSortButtonText()
        applyFilterAndSort()
    }

    private fun updateFavoriteStatusInAllProducts() {
        val favoriteIds = favoriteService.getFavoriteIds()
        var changed = false
        for (product in allProducts) {
            val oldStatus = product.isFavorite
            product.isFavorite = favoriteIds.contains(product.id)
            if (oldStatus != product.isFavorite) {
                changed = true
            }
        }
        if (changed && isResumed && ::productAdapter.isInitialized) {
            applyFilterAndSort()
        }
    }

    fun refreshDataAndView() {
        if (!isAdded || view == null) return

        updateFavoriteStatusInAllProducts()
        applyFilterAndSort()
    }
    private fun updateEmptyViewVisibility() {
        if (view == null) return
        if (displayedProducts.isEmpty()) {
            emptyTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun createSampleProducts(): List<Product> {
        return listOf(
            Product(1,"Marlboro Red","Классические сигареты с насыщенным вкусом",170.0,"","tobacco"),
            Product(2,"Jack Daniel's Tennessee Whiskey","Американский виски с нотами дуба, ванили и карамели",2500.0,"","alcohol"),
            Product(3,"Chivas Regal 12","Шотландский купажированный виски 12-летней выдержки",3200.0,"","alcohol"),
            Product(4,"Parliament Aqua Blue","Легкие сигареты с угольным фильтром",190.0,"","tobacco"),
            Product(5,"Hennessy VS","Французский коньяк с фруктовыми и дубовыми нотами",3800.0,"","alcohol"),
            Product(6,"Winston Blue","Легкие сигареты с классическим вкусом",150.0,"","tobacco"),
            Product(7,"Beluga Noble","Премиальная водка с мягким вкусом",2100.0,"","alcohol"),
            Product(8,"Kent HD","Сигареты с пониженным содержанием никотина",180.0,"","tobacco"),
            Product(9,"Macallan 12","Односолодовый шотландский виски 12 лет выдержки",7500.0,"","alcohol"),
            Product(10,"Camel Blue","Легкие сигареты с мягким вкусом",160.0,"","tobacco")
        )
    }
}

