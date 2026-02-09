import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import '/models/location_model.dart';
import '/models/menu_model.dart';
import '/models/organization_model.dart';
import '../providers/app_config_provider.dart';
import '/providers/security_provider.dart';
import '/services/location_rest_service.dart';
import '/services/menu_database_service.dart';
import '/services/menu_rest_service.dart';
import '/services/organization_rest_service.dart';

class LocalServerSetupScreen extends StatefulWidget {
  const LocalServerSetupScreen({super.key});

  @override
  State<LocalServerSetupScreen> createState() => _LocalServerSetupScreenState();
}

class _LocalServerSetupScreenState extends State<LocalServerSetupScreen> {
  final _formKey = GlobalKey<FormState>();
  bool _isSyncingLocalDb = false;

  // for organizations
  final OrganizationRestService _orgService = OrganizationRestService();
  List<Organization> _organizations = [];
  Organization? _selectedOrg; // Now an Object
  bool _isLoadingOrgs = true;

  // for locations
  List<Location> _locations = [];
  Location? _selectedLocation;
  bool _isLoadingLocations = false;

  // for menus
  List<Menu> _menus = [];
  Menu? _selectedMenu;
  bool _isLoadingMenus = false;

  // Form State
  final TextEditingController _pinController = TextEditingController();
  final TextEditingController _verifyPinController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadOrganizations();
  }

  Future<void> _loadOrganizations() async {
    try {
      final orgs = await _orgService.fetchOrganizations();
      setState(() {
        _organizations = orgs;
        _isLoadingOrgs = false;
      });
    } catch (e) {
      setState(() => _isLoadingOrgs = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error loading organizations: $e")),
      );
    }
  }

  // Method to fetch locations based on selected Org
  Future<void> _loadLocations() async {
    setState(() {
      _isLoadingLocations = true;
      _locations = []; // Clear current list
      _selectedLocation = null; // Reset selection
    });

    try {
      final locationService = LocationRestService(
        context.read<AppConfigProvider>(),
      );
      final locations = await locationService.fetchLocations();
      setState(() {
        _locations = locations;
        _isLoadingLocations = false;
      });
    } catch (e) {
      setState(() => _isLoadingLocations = false);
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text("Error loading locations: $e")));
    }
  }

  Future<void> _loadMenus() async {
    setState(() => _isLoadingMenus = true);
    try {
      final menuService = MenuRestService(context.read<AppConfigProvider>());

      final menus = await menuService.fetchMenus();
      setState(() {
        _menus = menus;
        _isLoadingMenus = false;
      });
    } catch (e) {
      setState(() => _isLoadingMenus = false);
      // Handle error...
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Local Server Setup"),
        centerTitle: true,
      ),
      // We use a Stack to place the Syncing Overlay on top of the Form
      body: Stack(
        children: [
          SingleChildScrollView(
            padding: const EdgeInsets.all(24.0),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  _buildSectionHeader(
                    "Security PIN",
                    "This PIN will be use for system security.",
                  ),
                  const SizedBox(height: 24),

                  // PIN Field
                  TextFormField(
                    controller: _pinController,
                    obscureText: true,
                    keyboardType: TextInputType.number,
                    maxLength: 6,
                    decoration: const InputDecoration(
                      labelText: "Enter System PIN",
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.lock_outline),
                    ),
                    inputFormatters: [
                      FilteringTextInputFormatter.digitsOnly,
                      LengthLimitingTextInputFormatter(6),
                    ],
                    validator: (val) {
                      if (val == null || val.isEmpty) return "PIN is required";
                      if (!RegExp(r'^[0-9]{6}$').hasMatch(val))
                        return "PIN must be 6 digits";
                      return null;
                    },
                  ),
                  const SizedBox(height: 20),

                  // Verify PIN Field
                  TextFormField(
                    controller: _verifyPinController,
                    obscureText: true,
                    keyboardType: TextInputType.number,
                    maxLength: 6,
                    decoration: const InputDecoration(
                      labelText: "Verify System PIN",
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.lock_clock_outlined),
                    ),
                    inputFormatters: [
                      FilteringTextInputFormatter.digitsOnly,
                      LengthLimitingTextInputFormatter(6),
                    ],
                    validator: (val) {
                      if (val == null || val.isEmpty)
                        return "Please verify your PIN";
                      if (val != _pinController.text)
                        return "PINs do not match";
                      return null;
                    },
                  ),

                  const Divider(height: 64),
                  _buildSectionHeader(
                    "Configure Location",
                    "Select the following to configure this location.",
                  ),
                  const SizedBox(height: 32),

                  // Organization Dropdown
                  _isLoadingOrgs
                      ? const Center(child: CircularProgressIndicator())
                      : DropdownButtonFormField<Organization>(
                          decoration: const InputDecoration(
                            labelText: "Organization",
                            border: OutlineInputBorder(),
                          ),
                          value: _selectedOrg,
                          items: _organizations.map((org) {
                            return DropdownMenuItem(
                              value: org,
                              child: Text(org.name),
                            );
                          }).toList(),
                          onChanged: (org) {
                            setState(() => _selectedOrg = org);
                            context
                                .read<AppConfigProvider>()
                                .setActiveOrganization(org);
                            if (org != null) {
                              _loadLocations();
                              _loadMenus();
                            }
                          },
                          validator: (org) => org == null ? "Required" : null,
                        ),

                  const SizedBox(height: 20),

                  // Location Dropdown
                  _isLoadingLocations
                      ? const Center(child: LinearProgressIndicator())
                      : DropdownButtonFormField<Location>(
                          decoration: const InputDecoration(
                            labelText: "Location",
                            border: OutlineInputBorder(),
                          ),
                          value: _selectedLocation,
                          disabledHint: const Text(
                            "Select an Organization first",
                          ),
                          items: _locations.map((loc) {
                            return DropdownMenuItem(
                              value: loc,
                              child: Text(loc.name),
                            );
                          }).toList(),
                          onChanged: _selectedOrg == null
                              ? null
                              : (loc) =>
                                    setState(() => _selectedLocation = loc),
                          validator: (val) => val == null ? "Required" : null,
                        ),
                  const SizedBox(height: 20),

                  // Menu Dropdown
                  _isLoadingMenus
                      ? const Center(child: LinearProgressIndicator())
                      : DropdownButtonFormField<Menu>(
                          decoration: const InputDecoration(
                            labelText: "Initial Menu",
                            border: OutlineInputBorder(),
                          ),
                          value: _selectedMenu,
                          disabledHint: const Text(
                            "Select an Organization first",
                          ),
                          items: _menus.map((menu) {
                            return DropdownMenuItem(
                              value: menu,
                              child: Text(menu.name),
                            );
                          }).toList(),
                          onChanged: _selectedOrg == null
                              ? null
                              : (menu) => setState(() => _selectedMenu = menu),
                          validator: (val) => val == null ? "Required" : null,
                        ),

                  const SizedBox(height: 48),

                  Row(
                    children: [
                      // Cancel Button
                      Expanded(
                        child: OutlinedButton(
                          onPressed: _isSyncingLocalDb ? null : _handleCancel,
                          style: OutlinedButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 16),
                            side: BorderSide(color: Colors.blueGrey.shade800),
                            foregroundColor: Colors.blueGrey.shade800,
                          ),
                          child: const Text(
                            "Cancel",
                            style: TextStyle(fontSize: 16),
                          ),
                        ),
                      ),
                      const SizedBox(width: 16),
                      // Complete Setup Button
                      Expanded(
                        flex: 2, // Gives the primary action more visual weight
                        child: ElevatedButton(
                          onPressed: _isSyncingLocalDb ? null : _submitForm,
                          style: ElevatedButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 16),
                            backgroundColor: Colors.blueGrey.shade800,
                            foregroundColor: Colors.white,
                          ),
                          child: const Text(
                            "Complete Setup",
                            style: TextStyle(fontSize: 16),
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),

          // --- THE SYNCING OVERLAY ---
          if (_isSyncingLocalDb)
            Positioned.fill(
              child: Container(
                color: Colors.black.withOpacity(0.6), // Dim the background
                child: Center(
                  child: Card(
                    elevation: 8,
                    margin: const EdgeInsets.symmetric(horizontal: 40),
                    child: Padding(
                      padding: const EdgeInsets.all(24.0),
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const CircularProgressIndicator(),
                          const SizedBox(height: 24),
                          const Text(
                            "Finalizing Setup...",
                            style: TextStyle(
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            "Syncing menu with ${_selectedMenu?.menuItems.length ?? 0} items locally.",
                            textAlign: TextAlign.center,
                            style: TextStyle(color: Colors.grey.shade700),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }

  // Helper to build headers
  Widget _buildSectionHeader(String title, String subtitle) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        ),
        Text(
          subtitle,
          style: TextStyle(fontSize: 14, color: Colors.grey.shade600),
        ),
      ],
    );
  }

  Widget _buildDropdown({
    required String label,
    required String? value,
    required List<String> items,
    required ValueChanged<String?> onChanged,
  }) {
    return DropdownButtonFormField<String>(
      decoration: InputDecoration(
        labelText: label,
        border: const OutlineInputBorder(),
      ),
      value: value,
      items: items
          .map((item) => DropdownMenuItem(value: item, child: Text(item)))
          .toList(),
      onChanged: onChanged,
      validator: (val) => val == null ? "Required" : null,
    );
  }

  void _submitForm() async {
    if (_formKey.currentState!.validate()) {
      setState(() => _isSyncingLocalDb = true);
      try {
        await context.read<SecurityProvider>().savePin(_pinController.text);
        await context.read<AppConfigProvider>().saveServerConfiguration(
          orgId: _selectedOrg!.id,
          locationId: _selectedLocation!.id,
          menuId: _selectedMenu!.id,
        );

        final menuDb = MenuDatabaseService();
        await menuDb.persistMenu(_selectedMenu!);

        if (mounted) {
          ScaffoldMessenger.of(
            context,
          ).showSnackBar(const SnackBar(content: Text("Setup Complete!")));
        }
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Error saving configuration: $e")),
        );
      } finally {
        if (mounted) setState(() => _isSyncingLocalDb = false);
      }
    }
  }

  void _handleCancel() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Discard Setup?"),
        content: const Text("All entered configuration will be lost."),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text("NO"),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text("YES"),
          ),
        ],
      ),
    );

    if (confirm == true) {
      context.read<AppConfigProvider>().setRole(POSRole.undecided);
      if (Navigator.canPop(context)) Navigator.pop(context);
    }
  }
}
