import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '/models/dto/menu_item_dto.dart';
import '/models/dto/simple_response.dart';
import '/network/messenger_service.dart';
import '/network/network_constants.dart';
import '/providers/app_config_provider.dart';
import '/providers/client_config_provider.dart';
import '/providers/journal_provider.dart';
import '/providers/menu_provider.dart';
import '/providers/order_provider.dart';
import '/providers/server_config_provider.dart';
import '/providers/security_provider.dart';

class OrderEntryScreen extends StatefulWidget {
  const OrderEntryScreen({super.key});

  @override
  State<OrderEntryScreen> createState() => _OrderEntryScreenState();
}

class _OrderEntryScreenState extends State<OrderEntryScreen> {
  String selectedCategory = "DRINK";

  bool _isProcessing = false;
  void _setLoading(bool value) => setState(() => _isProcessing = value);

  @override
  void initState() {
    super.initState();
  }

  Future<void> _refreshMenu() async {
    try {
      await context.read<MenuProvider>().loadMenu(
        serverConfig: context.read<ServerConfigProvider>(),
        clientConfig: context.read<ClientConfigProvider>(),
        security: context.read<SecurityProvider>(),
      );
    } catch (e) {
      if (mounted) {
        showDialog(
          context: context,
          builder: (ctx) => AlertDialog(
            title: const Text("Fatal Error"),
            content: Text("Failed to retrieve menu: $e"),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(ctx),
                child: const Text("OK"),
              ),
            ],
          ),
        );
      }
    }
  }

  void _addToOrder(MenuItemDTO item) {
    // Access the OrderProvider and add the selected item
    context.read<OrderProvider>().addItem(item);

    // Optional: Provide haptic feedback or a brief toast/snackbar
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text("Added ${item.name} to order"),
        duration: const Duration(milliseconds: 500),
        behavior: SnackBarBehavior.floating,
        width: 200,
      ),
    );
  }

  Future<void> _processCheckout() async {
    final orderProvider = context.read<OrderProvider>();
    final journal = context.read<JournalProvider>().journal;
    final appConfig = context.read<AppConfigProvider>();
    final serverConfig = context.read<ServerConfigProvider>();
    final clientConfig = context.read<ClientConfigProvider>();
    final security = context.read<SecurityProvider>();

    final int? effectiveMenuId = journal?.menuId ?? appConfig.menuId;

    if (orderProvider.itemCount == 0) return;

    if (effectiveMenuId == null || journal?.id == null) {
      _showError("Configuration missing. Cannot submit order.");
      return;
    }
    if (appConfig.menuId == null) {
      _showError("Menu ID is missing. Please restart the app.");
      return;
    }

    if (journal == null || journal.id == null) {
      _showError("No active Journal found. Please check connection.");
      return;
    }

    bool? confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Confirm Order"),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text("Items: ${orderProvider.itemCount}"),
            const SizedBox(height: 8),
            Text(
              "Total: \$${orderProvider.totalPrice.toStringAsFixed(2)}",
              style: const TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: Colors.deepPurple,
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text("CANCEL"),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.deepPurple,
              foregroundColor: Colors.white,
            ),
            onPressed: () => Navigator.pop(context, true),
            child: const Text("SUBMIT"),
          ),
        ],
      ),
    );

    if (confirmed != true) return;

    _setLoading(true);

    try {
      final orderRequest = orderProvider.createOrderRequest(
        pin: security.systemPin ?? "",
        deviceId: clientConfig.deviceId ?? "",
        menuId: effectiveMenuId,
        journalId: journal!.id!,
      );

      // Show loading overlay if you have one
      await MessengerService.sendRequest<SimpleResponseDTO>(
        serverIp: serverConfig.serverIp!,
        serverPort: serverConfig.serverPort!,
        service: NetworkConstants.orderService, // You'll need to define this
        action: NetworkConstants.orderSubmitAction,
        payload: orderRequest.toJson(),
        fromJson: (json) => SimpleResponseDTO.fromJson(json),
      );

      // Success! Clear the order and show a confirmation
      orderProvider.clearOrder();
      _showToast("Order Submitted!", Colors.green);
    } catch (e) {
      _showToast("Checkout failed: $e", Colors.red);
    } finally {
      _setLoading(false);
    }
  }

  Future<void> _refreshJournal() async {
    try {
      await context.read<JournalProvider>().loadJournal(
        serverConfig: context.read<ServerConfigProvider>(),
        clientConfig: context.read<ClientConfigProvider>(),
        security: context.read<SecurityProvider>(),
      );
    } catch (e) {
      debugPrint("Manual journal refresh failed: $e");
    }
  }

  void _showError(String message) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }

  @override
  Widget build(BuildContext context) {
    // Watch the journal provider for status changes (Open/Locked)
    final journalProvider = context.watch<JournalProvider>();

    return Scaffold(
      backgroundColor: const Color(0xFFF3E5F5),
      body: OrientationBuilder(
        builder: (context, orientation) {
          return Stack(
            children: [
              // LAYER 1: The Main UI
              // AbsorbPointer blocks interaction if we are processing a checkout OR if the journal is locked
              AbsorbPointer(
                absorbing: _isProcessing || journalProvider.isLocked,
                child: orientation == Orientation.portrait
                    ? _buildPortraitLayout()
                    : _buildLandscapeLayout(),
              ),

              // LAYER 2: The Loading Overlay (For Checkout)
              if (_isProcessing)
                Positioned.fill(
                  child: Container(
                    color: Colors.black.withOpacity(
                      0.3,
                    ), // Lighter dimming for processing
                    child: Center(
                      child: Card(
                        elevation: 8,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(16),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(32.0),
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            children: const [
                              CircularProgressIndicator(
                                color: Colors.deepPurple,
                              ),
                              SizedBox(height: 20),
                              Text(
                                "Submitting Order...",
                                style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: 16,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                  ),
                ),

              // LAYER 3: The Journal Lock Overlay (For Management control)
              if (journalProvider.isLocked)
                Positioned.fill(
                  child: _buildJournalLockOverlay(journalProvider),
                ),
            ],
          );
        },
      ),
    );
  }

  /// Helper to build the Lock UI to keep the build method clean
  Widget _buildJournalLockOverlay(JournalProvider journalProvider) {
    return Container(
      color: Colors.black.withOpacity(0.8), // Darker dimming for locked state
      child: Center(
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 30),
          margin: const EdgeInsets.symmetric(horizontal: 20),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: Colors.red, width: 2),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Icon(Icons.lock, color: Colors.red, size: 64),
              const SizedBox(height: 20),
              Text(
                journalProvider.lockMessage.toUpperCase(),
                style: const TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.bold,
                  color: Colors.red,
                ),
              ),
              const SizedBox(height: 12),
              const Text(
                "This terminal is currently disabled.\nPlease contact a manager to open the session.",
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16, color: Colors.black87),
              ),
              const SizedBox(height: 24),
              ElevatedButton.icon(
                onPressed: _isProcessing ? null : () => _refreshJournal(),
                icon: const Icon(Icons.refresh),
                label: const Text("Check System Status"),
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 24,
                    vertical: 12,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildPortraitLayout() {
    return Column(
      children: [
        _buildCategoryBar(Axis.horizontal),
        Expanded(child: _buildItemGrid()),
        _buildOrderSummary(heightFactor: 0.35),
      ],
    );
  }

  Widget _buildLandscapeLayout() {
    return Row(
      children: [
        SizedBox(width: 200, child: _buildCategoryBar(Axis.vertical)),
        Expanded(flex: 3, child: _buildItemGrid()),
        const VerticalDivider(width: 1),
        Expanded(flex: 2, child: _buildOrderSummary()),
      ],
    );
  }

  // UI COMPONENTS

  Widget _buildCategoryBar(Axis axis) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            "Categories",
            style: TextStyle(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 10),
          Wrap(
            direction: axis,
            spacing: 8,
            runSpacing: 8,
            children: ["DRINK", "ENTREE", "SIDE"].map((cat) {
              bool isSelected = selectedCategory == cat;
              return ChoiceChip(
                label: Text(cat),
                selected: isSelected,
                selectedColor: Colors.deepPurple,
                labelStyle: TextStyle(
                  color: isSelected ? Colors.white : Colors.grey,
                ),
                onSelected: (val) => setState(() => selectedCategory = cat),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }

  Widget _buildItemGrid() {
    final menuProvider = context.watch<MenuProvider>();

    if (menuProvider.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    final items =
        menuProvider.menu?.menuItems
            .where((item) => item.category == selectedCategory)
            .toList() ??
        [];

    return GridView.builder(
      padding: const EdgeInsets.all(16),
      gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
        maxCrossAxisExtent: 220,
        childAspectRatio: 2.2,
        mainAxisSpacing: 12,
        crossAxisSpacing: 12,
      ),
      itemCount: items.length,
      itemBuilder: (context, index) {
        final item = items[index];
        return OutlinedButton(
          style: OutlinedButton.styleFrom(
            backgroundColor: Colors.white.withOpacity(0.5),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30),
            ),
          ),
          onPressed: () => _addToOrder(item),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                item.name,
                style: const TextStyle(
                  color: Colors.indigo,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text(
                "(\$${item.price.toStringAsFixed(2)})",
                style: const TextStyle(color: Colors.indigo),
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildOrderSummary({double? heightFactor}) {
    // Use watch to ensure the UI rebuilds whenever notifyListeners() is called
    final orderProvider = context.watch<OrderProvider>();

    return Container(
      // In portrait, we use a percentage of screen height. In landscape, it fills the Row.
      height: heightFactor != null
          ? MediaQuery.of(context).size.height * heightFactor
          : null,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.5),
        border: const Border(
          left: BorderSide(color: Colors.black12), // Divider for landscape
          top: BorderSide(color: Colors.black12), // Divider for portrait
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          const Text(
            "Current Order",
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const Divider(height: 20),

          // 1. The Scrollable List of Items
          Expanded(
            child: orderProvider.items.isEmpty
                ? const Center(
                    child: Text(
                      "No items added yet",
                      style: TextStyle(
                        color: Colors.grey,
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                  )
                : ListView.separated(
                    itemCount: orderProvider.items.length,
                    separatorBuilder: (context, index) =>
                        const Divider(height: 1),
                    itemBuilder: (context, index) {
                      final item = orderProvider.items[index];
                      return ListTile(
                        contentPadding: EdgeInsets.zero,
                        dense: true,
                        title: Text(
                          item.name,
                          style: const TextStyle(fontWeight: FontWeight.w500),
                        ),
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text("\$${item.price.toStringAsFixed(2)}"),
                            IconButton(
                              icon: const Icon(
                                Icons.remove_circle_outline,
                                color: Colors.red,
                                size: 20,
                              ),
                              onPressed: () => orderProvider.removeItem(index),
                            ),
                          ],
                        ),
                      );
                    },
                  ),
          ),

          const Divider(height: 20, thickness: 1),

          // 2. The Totals Section
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 8.0),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      "Items: ${orderProvider.itemCount}",
                      style: const TextStyle(fontSize: 16),
                    ),
                    Text(
                      "\$${orderProvider.totalPrice.toStringAsFixed(2)}",
                      style: const TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                        color: Colors.deepPurple,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),

          const SizedBox(height: 12),

          // 3. Action Buttons
          Row(
            children: [
              // CLEAR BUTTON
              Expanded(
                child: OutlinedButton(
                  onPressed: orderProvider.items.isEmpty
                      ? null
                      : () => orderProvider.clearOrder(),
                  style: OutlinedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    side: const BorderSide(color: Colors.deepPurple),
                    shape: const StadiumBorder(),
                  ),
                  child: const Text("Clear"),
                ),
              ),
              const SizedBox(width: 12),
              // CHECKOUT BUTTON
              Expanded(
                child: ElevatedButton(
                  onPressed: orderProvider.items.isEmpty
                      ? null
                      : _processCheckout,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.deepPurple,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: const StadiumBorder(),
                    elevation: 2,
                  ),
                  child: const Text(
                    "Checkout",
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  void _showToast(String message, Color color) {
  if (!mounted) return;
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(
      content: Text(message),
      backgroundColor: color,
      behavior: SnackBarBehavior.floating,
      duration: const Duration(seconds: 2),
    ),
  );
}
}
