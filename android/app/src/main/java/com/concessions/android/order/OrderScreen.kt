package com.concessions.android.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.concessions.dto.CategoryType
import com.concessions.dto.MenuItemDTO
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel = viewModel(),
    onCheckout: () -> Unit
) {
    val uiState by orderViewModel.uiState.collectAsState()

    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight

        if (isLandscape) {
            // Keep the original 3-column layout for landscape
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryPane(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategoryClick = { orderViewModel.selectCategory(it) },
                    modifier = Modifier.weight(1f)
                )
                MenuItemPane(
                    items = uiState.itemsForSelectedCategory,
                    onItemClick = { orderViewModel.addItemToOrder(it) },
                    modifier = Modifier.weight(2.5f)
                )
                CurrentOrderPane(
                    orderItems = uiState.currentOrderItems,
                    orderTotal = uiState.orderTotal,
                    onRemoveItem = { orderViewModel.removeItemFromOrder(it) },
                    onClearOrder = { orderViewModel.clearOrder() },
                    onCheckout = onCheckout,
                    modifier = Modifier.weight(1.5f)
                )
            }
        } else {
            // Use the new stacked layout for portrait
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PortraitCategoryPane(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategoryClick = { orderViewModel.selectCategory(it) }
                )
                MenuItemPane(
                    items = uiState.itemsForSelectedCategory,
                    onItemClick = { orderViewModel.addItemToOrder(it) },
                    modifier = Modifier.weight(1f) // Takes up remaining space
                )
                PortraitCurrentOrderPane(
                    orderItems = uiState.currentOrderItems,
                    orderTotal = uiState.orderTotal,
                    onRemoveItem = { orderViewModel.removeItemFromOrder(it) },
                    onClearOrder = { orderViewModel.clearOrder() },
                    onCheckout = onCheckout
                )
            }
        }
    }
}


@Composable
private fun PortraitCategoryPane(
    categories: List<CategoryType>,
    selectedCategory: CategoryType?,
    onCategoryClick: (CategoryType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Categories", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    Button(
                        onClick = { onCategoryClick(category) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (category == selectedCategory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(category.name)
                    }
                }
            }
        }
    }
}

@Composable
private fun PortraitCurrentOrderPane(
    orderItems: List<MenuItemDTO>,
    orderTotal: BigDecimal,
    onRemoveItem: (MenuItemDTO) -> Unit,
    onClearOrder: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Current Order", style = MaterialTheme.typography.titleMedium)
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            LazyColumn(
                modifier = Modifier
                    .height(150.dp) // Set a fixed or weighted height
                    .fillMaxWidth()
            ) {
                items(orderItems) { item ->
                    TextButton(onClick = { onRemoveItem(item) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.name, modifier = Modifier.weight(1f))
                            Text(formatCurrency(item.price))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total:", style = MaterialTheme.typography.titleLarge)
                Text(formatCurrency(orderTotal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onClearOrder, modifier = Modifier.weight(1f)) {
                    Text("Clear")
                }
                Button(onClick = onCheckout, modifier = Modifier.weight(1f)) {
                    Text("Checkout")
                }
            }
        }
    }
}


@Composable
private fun CategoryPane(
    categories: List<CategoryType>,
    selectedCategory: CategoryType?,
    onCategoryClick: (CategoryType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxHeight()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Categories", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    Button(
                        onClick = { onCategoryClick(category) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (category == selectedCategory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(category.name)
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItemPane(
    items: List<MenuItemDTO>,
    onItemClick: (MenuItemDTO) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxHeight()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                OutlinedButton(onClick = { onItemClick(item) }, modifier = Modifier.height(80.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(item.name, textAlign = TextAlign.Center)
                        Text("(${formatCurrency(item.price)})", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentOrderPane(
    orderItems: List<MenuItemDTO>,
    orderTotal: BigDecimal,
    onRemoveItem: (MenuItemDTO) -> Unit,
    onClearOrder: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxHeight()) {
        Column(modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)) {
            Text("Current Order", style = MaterialTheme.typography.titleMedium)
            Divider(modifier = Modifier.padding(vertical = 4.dp))

            // Order Items List
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(orderItems) { item ->
                    TextButton(onClick = { onRemoveItem(item) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.name, modifier = Modifier.weight(1f))
                            Text(formatCurrency(item.price))
                        }
                    }
                }
            }

            // Total Display
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total:", style = MaterialTheme.typography.titleLarge)
                Text(formatCurrency(orderTotal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            // Action Buttons
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onClearOrder, modifier = Modifier.weight(1f)) {
                    Text("Clear")
                }
                Button(onClick = onCheckout, modifier = Modifier.weight(1f)) {
                    Text("Checkout")
                }
            }
        }
    }
}

private fun formatCurrency(price: BigDecimal): String {
    return NumberFormat.getCurrencyInstance().format(price)
}
