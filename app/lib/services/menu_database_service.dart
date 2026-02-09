import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import '../models/menu_model.dart';
import '../models/menu_item_model.dart';

class MenuDatabaseService {
  static final MenuDatabaseService _instance = MenuDatabaseService._internal();
  static Database? _database;

  MenuDatabaseService._internal();

  factory MenuDatabaseService() => _instance;

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  Future<Database> _initDatabase() async {
    String path = join(await getDatabasesPath(), 'pos_menu.db');
    return await openDatabase(
      path,
      version: 1,
      onCreate: (db, version) async {
        // Menu Header Table
        await db.execute('''
          CREATE TABLE menus(
            id INTEGER PRIMARY KEY,
            name TEXT,
            description TEXT,
            organizationId INTEGER
          )
        ''');
        // Menu Items Table
        await db.execute('''
          CREATE TABLE menu_items(
            id INTEGER PRIMARY KEY,
            menu_id INTEGER,
            name TEXT,
            description TEXT,
            category TEXT,
            price TEXT, 
            organizationId INTEGER,
            FOREIGN KEY (menu_id) REFERENCES menus (id) ON DELETE CASCADE
          )
        ''');
      },
    );
  }

  /// Syncs the remote MenuDTO structure to the local SQLite tables
  Future<void> persistMenu(Menu menu) async {
    final db = await database;

    await db.transaction((txn) async {
      // 1. Clear previous menu (Standard for fixed-terminal sync)
      await txn.delete('menus');
      await txn.delete('menu_items');

      // 2. Insert Menu Header
      await txn.insert('menus', {
        'id': menu.id,
        'name': menu.name,
        'description': menu.description,
        'organizationId': menu.organizationId,
      });

      // 3. Batch insert items for better performance
      for (var item in menu.menuItems) {
        await txn.insert('menu_items', {
          'id': item.id,
          'menu_id': menu.id,
          'name': item.name,
          'description': item.description,
          'category': item.category.jsonKey,
          'price': item.price.toString(),
          'organizationId': item.organizationId,
        });
      }
    });
  }

  Future<Menu?> getLocalMenu() async {
    final db = await database;
    final List<Map<String, dynamic>> menuMaps = await db.query('menus', limit: 1);
    
    if (menuMaps.isEmpty) return null;

    final List<Map<String, dynamic>> itemMaps = await db.query('menu_items');
    
    List<MenuItem> items = itemMaps.map((m) => MenuItem.fromJson(m)).toList();

    return Menu(
      id: menuMaps[0]['id'],
      name: menuMaps[0]['name'],
      description: menuMaps[0]['description'],
      organizationId: menuMaps[0]['organizationId'],
      menuItems: items,
    );
  }
}