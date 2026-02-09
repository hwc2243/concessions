import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'dart:async';
import 'package:provider/provider.dart';
import '/models/dto/order_dto.dart';
import '/providers/kitchen_provider.dart';
import '/providers/client_config_provider.dart';
import '/providers/server_config_provider.dart';
import '/providers/security_provider.dart';

class KitchenOrderCard extends StatefulWidget {
  final OrderDTO order;
  const KitchenOrderCard({super.key, required this.order});

  @override
  State<KitchenOrderCard> createState() => _KitchenOrderCardState();
}

class _KitchenOrderCardState extends State<KitchenOrderCard> {
  late Timer _timer;
  late String _elapsed;
  bool _isProcessing = false; // For button feedback

  @override
  void initState() {
    super.initState();
    _updateElapsed();
    _timer = Timer.periodic(
      const Duration(seconds: 1),
      (timer) => _updateElapsed(),
    );
  }

  void _updateElapsed() {
    final diff = DateTime.now().difference(widget.order.startTs);
    final duration = diff.isNegative ? Duration.zero : diff;

    setState(() {
      String twoDigits(int n) => n.toString().padLeft(2, '0');
      final minutes = twoDigits(duration.inMinutes.remainder(60));
      final seconds = twoDigits(duration.inSeconds.remainder(60));

      if (duration.inHours > 0) {
        _elapsed = "${duration.inHours}:$minutes:$seconds";
      } else {
        _elapsed = "$minutes:$seconds";
      }
    });
  }

  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 300,
      margin: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: Colors.white, // Brighter white for better contrast
        border: Border.all(color: Colors.black54, width: 2),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Items List
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(12.0),
              child: ListView.builder(
                itemCount: widget.order.orderItems.length,
                itemBuilder: (context, i) => Padding(
                  padding: const EdgeInsets.symmetric(vertical: 4),
                  child: Text(
                    widget.order.orderItems[i].name,
                    style: const TextStyle(
                      fontSize: 22, // Bigger for kitchen readability
                      fontWeight: FontWeight.bold,
                      color: Colors.black,
                    ),
                  ),
                ),
              ),
            ),
          ),

          const Divider(color: Colors.black54, height: 1, thickness: 2),

          // Time Info Section
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      "Started: ${DateFormat('HH:mm:ss').format(widget.order.startTs)}",
                      style: const TextStyle(
                        fontSize: 14,
                        color: Colors.black87,
                      ),
                    ),
                    Text(
                      "Elapsed: $_elapsed",
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: Colors.red, // Makes it pop
                      ),
                    ),
                  ],
                ),
                Text(
                  "Items: ${widget.order.orderItems.length}",
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
              ],
            ),
          ),

          // Action Button
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: SizedBox(
              width: double.infinity,
              height: 60,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.green.shade700,
                  foregroundColor: Colors.white,
                  disabledBackgroundColor: Colors.grey,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(4),
                  ),
                ),
                onPressed: _isProcessing ? null : _handleComplete,
                child: _isProcessing
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text(
                        "COMPLETE",
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _handleComplete() async {
    setState(() => _isProcessing = true);

    try {
      final kitchenProvider = context.read<KitchenProvider>();
      final success = await kitchenProvider.completeOrder(
        order: widget.order,
        serverConfig: context.read<ServerConfigProvider>(),
        clientConfig: context.read<ClientConfigProvider>(),
        security: context.read<SecurityProvider>(),
      );

      if (!success && mounted) {
        setState(() => _isProcessing = false);
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text("Error: Could not complete order."),
            backgroundColor: Colors.red,
          ),
        );
      }
    } catch (e) {
      if (mounted) setState(() => _isProcessing = false);
    }
  }
}
