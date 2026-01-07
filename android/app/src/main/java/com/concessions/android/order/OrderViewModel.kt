package com.concessions.android.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concessions.dto.CategoryType
import com.concessions.dto.MenuDTO
import com.concessions.dto.MenuItemDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

// Represents the overall state of the ordering UI
data class OrderUiState(
    val menu: MenuDTO? = null,
    val categories: List<CategoryType> = emptyList(),
    val itemsForSelectedCategory: List<MenuItemDTO> = emptyList(),
    val selectedCategory: CategoryType? = null,
    val currentOrderItems: List<MenuItemDTO> = emptyList(),
    val orderTotal: BigDecimal = BigDecimal.ZERO
)

class OrderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    fun setMenu(menu: MenuDTO) {
        viewModelScope.launch {
            // Group items by category and extract the sorted list of categories
            val groupedItems = menu.menuItems.groupBy { it.category }
            val categories = groupedItems.keys.sortedBy { it.name }
            val initialCategory = categories.firstOrNull()

            _uiState.update {
                it.copy(
                    menu = menu,
                    categories = categories,
                    selectedCategory = initialCategory,
                    // Show items for the first category initially
                    itemsForSelectedCategory = initialCategory?.let { cat -> groupedItems[cat] } ?: emptyList()
                )
            }
        }
    }

    fun selectCategory(category: CategoryType) {
        val groupedItems = _uiState.value.menu?.menuItems?.groupBy { it.category }
        _uiState.update {
            it.copy(
                selectedCategory = category,
                itemsForSelectedCategory = groupedItems?.get(category) ?: emptyList()
            )
        }
    }

    fun addItemToOrder(item: MenuItemDTO) {
        _uiState.update {
            val updatedItems = it.currentOrderItems + item
            it.copy(
                currentOrderItems = updatedItems,
                orderTotal = updatedItems.sumOf { i -> i.price }
            )
        }
    }

    fun removeItemFromOrder(item: MenuItemDTO) {
        _uiState.update {
            val updatedItems = it.currentOrderItems.toMutableList()
            updatedItems.remove(item) // Removes the first occurrence
            it.copy(
                currentOrderItems = updatedItems,
                orderTotal = updatedItems.sumOf { i -> i.price }
            )
        }
    }

    fun clearOrder() {
        _uiState.update {
            it.copy(
                currentOrderItems = emptyList(),
                orderTotal = BigDecimal.ZERO
            )
        }
    }
}
