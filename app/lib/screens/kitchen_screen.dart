import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '/providers/kitchen_provider.dart';
import "kitchen_order_card.dart";

class KitchenScreen extends StatelessWidget {
  const KitchenScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<KitchenProvider>();

    return Scaffold(
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              itemCount: provider.activeOrders.length,
              itemBuilder: (context, index) {
                return KitchenOrderCard(
                  key: ValueKey(provider.activeOrders[index].id),
                  order: provider.activeOrders[index]
                );
              },
            ),
          ),
          // Bottom Status Bar
          Container(
            padding: const EdgeInsets.all(12),
            color: Colors.grey[100],
            width: double.infinity,
            child: Text(
              "Total Orders: ${provider.activeOrders.length}",
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }
}