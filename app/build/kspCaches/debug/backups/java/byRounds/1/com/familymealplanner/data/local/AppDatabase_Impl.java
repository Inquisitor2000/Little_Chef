package com.familymealplanner.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.familymealplanner.data.local.dao.AllergenDao;
import com.familymealplanner.data.local.dao.AllergenDao_Impl;
import com.familymealplanner.data.local.dao.GroceryItemDao;
import com.familymealplanner.data.local.dao.GroceryItemDao_Impl;
import com.familymealplanner.data.local.dao.IngredientAllergenDao;
import com.familymealplanner.data.local.dao.IngredientAllergenDao_Impl;
import com.familymealplanner.data.local.dao.IngredientDao;
import com.familymealplanner.data.local.dao.IngredientDao_Impl;
import com.familymealplanner.data.local.dao.IngredientSubstituteDao;
import com.familymealplanner.data.local.dao.IngredientSubstituteDao_Impl;
import com.familymealplanner.data.local.dao.InventoryTransactionDao;
import com.familymealplanner.data.local.dao.InventoryTransactionDao_Impl;
import com.familymealplanner.data.local.dao.MealDao;
import com.familymealplanner.data.local.dao.MealDao_Impl;
import com.familymealplanner.data.local.dao.MealIngredientDao;
import com.familymealplanner.data.local.dao.MealIngredientDao_Impl;
import com.familymealplanner.data.local.dao.MealPlanDao;
import com.familymealplanner.data.local.dao.MealPlanDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile AllergenDao _allergenDao;

  private volatile IngredientDao _ingredientDao;

  private volatile IngredientAllergenDao _ingredientAllergenDao;

  private volatile IngredientSubstituteDao _ingredientSubstituteDao;

  private volatile MealDao _mealDao;

  private volatile MealIngredientDao _mealIngredientDao;

  private volatile MealPlanDao _mealPlanDao;

  private volatile InventoryTransactionDao _inventoryTransactionDao;

  private volatile GroceryItemDao _groceryItemDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `allergens` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ingredients` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `unit` TEXT NOT NULL, `category` TEXT, `subcategory` TEXT, `preferred_display_unit` TEXT, `created_in_language` TEXT NOT NULL DEFAULT 'en', `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ingredient_allergens` (`id` TEXT NOT NULL, `ingredient_id` TEXT NOT NULL, `allergen_id` TEXT NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`ingredient_id`) REFERENCES `ingredients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`allergen_id`) REFERENCES `allergens`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ingredient_allergens_ingredient_id` ON `ingredient_allergens` (`ingredient_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ingredient_allergens_allergen_id` ON `ingredient_allergens` (`allergen_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ingredient_substitutes` (`id` TEXT NOT NULL, `ingredient_id` TEXT NOT NULL, `substitute_id` TEXT NOT NULL, `notes` TEXT, `created_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`ingredient_id`) REFERENCES `ingredients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`substitute_id`) REFERENCES `ingredients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ingredient_substitutes_ingredient_id` ON `ingredient_substitutes` (`ingredient_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ingredient_substitutes_substitute_id` ON `ingredient_substitutes` (`substitute_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `meals` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `instructions` TEXT, `simple_instructions` TEXT, `prep_time_minutes` INTEGER, `cook_time_minutes` INTEGER, `servings` INTEGER, `is_scraped` INTEGER NOT NULL DEFAULT 0, `is_bundled` INTEGER NOT NULL DEFAULT 0, `image_path` TEXT, `meal_type` TEXT, `dish_category` TEXT, `created_in_language` TEXT NOT NULL DEFAULT 'en', `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `meal_ingredients` (`id` TEXT NOT NULL, `meal_id` TEXT NOT NULL, `ingredient_id` TEXT NOT NULL, `quantity` REAL NOT NULL, `unit` TEXT, `is_star_ingredient` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`), FOREIGN KEY(`meal_id`) REFERENCES `meals`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`ingredient_id`) REFERENCES `ingredients`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_meal_ingredients_meal_id` ON `meal_ingredients` (`meal_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_meal_ingredients_ingredient_id` ON `meal_ingredients` (`ingredient_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `meal_plans` (`id` TEXT NOT NULL, `meal_id` TEXT NOT NULL, `planned_date` INTEGER NOT NULL, `meal_type` TEXT NOT NULL, `status` TEXT NOT NULL, `started_at` INTEGER, `completed_at` INTEGER, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `ingredient_substitutions` TEXT NOT NULL DEFAULT '{}', `planned_servings` INTEGER, `adjusted_prep_time_minutes` INTEGER, `adjusted_cook_time_minutes` INTEGER, PRIMARY KEY(`id`), FOREIGN KEY(`meal_id`) REFERENCES `meals`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_meal_plans_meal_id` ON `meal_plans` (`meal_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `inventory_transactions` (`id` TEXT NOT NULL, `ingredient_id` TEXT NOT NULL, `quantity_change` REAL NOT NULL, `status` TEXT NOT NULL, `reason` TEXT NOT NULL, `meal_plan_id` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`ingredient_id`) REFERENCES `ingredients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_inventory_transactions_ingredient_id` ON `inventory_transactions` (`ingredient_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_inventory_transactions_meal_plan_id` ON `inventory_transactions` (`meal_plan_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `grocery_items` (`id` TEXT NOT NULL, `ingredientName` TEXT NOT NULL, `ingredientId` TEXT, `category` TEXT, `quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `mealName` TEXT NOT NULL, `mealType` TEXT, `plannedDate` INTEGER, `isChecked` INTEGER NOT NULL, `checkedAt` INTEGER, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd94cac3b26f15adcda98df02d4a683a2')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `allergens`");
        db.execSQL("DROP TABLE IF EXISTS `ingredients`");
        db.execSQL("DROP TABLE IF EXISTS `ingredient_allergens`");
        db.execSQL("DROP TABLE IF EXISTS `ingredient_substitutes`");
        db.execSQL("DROP TABLE IF EXISTS `meals`");
        db.execSQL("DROP TABLE IF EXISTS `meal_ingredients`");
        db.execSQL("DROP TABLE IF EXISTS `meal_plans`");
        db.execSQL("DROP TABLE IF EXISTS `inventory_transactions`");
        db.execSQL("DROP TABLE IF EXISTS `grocery_items`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAllergens = new HashMap<String, TableInfo.Column>(4);
        _columnsAllergens.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllergens.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllergens.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllergens.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAllergens = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAllergens = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAllergens = new TableInfo("allergens", _columnsAllergens, _foreignKeysAllergens, _indicesAllergens);
        final TableInfo _existingAllergens = TableInfo.read(db, "allergens");
        if (!_infoAllergens.equals(_existingAllergens)) {
          return new RoomOpenHelper.ValidationResult(false, "allergens(com.familymealplanner.data.local.entity.AllergenEntity).\n"
                  + " Expected:\n" + _infoAllergens + "\n"
                  + " Found:\n" + _existingAllergens);
        }
        final HashMap<String, TableInfo.Column> _columnsIngredients = new HashMap<String, TableInfo.Column>(9);
        _columnsIngredients.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("subcategory", new TableInfo.Column("subcategory", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("preferred_display_unit", new TableInfo.Column("preferred_display_unit", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("created_in_language", new TableInfo.Column("created_in_language", "TEXT", true, 0, "'en'", TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIngredients = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIngredients = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIngredients = new TableInfo("ingredients", _columnsIngredients, _foreignKeysIngredients, _indicesIngredients);
        final TableInfo _existingIngredients = TableInfo.read(db, "ingredients");
        if (!_infoIngredients.equals(_existingIngredients)) {
          return new RoomOpenHelper.ValidationResult(false, "ingredients(com.familymealplanner.data.local.entity.IngredientEntity).\n"
                  + " Expected:\n" + _infoIngredients + "\n"
                  + " Found:\n" + _existingIngredients);
        }
        final HashMap<String, TableInfo.Column> _columnsIngredientAllergens = new HashMap<String, TableInfo.Column>(3);
        _columnsIngredientAllergens.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredientAllergens.put("ingredient_id", new TableInfo.Column("ingredient_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredientAllergens.put("allergen_id", new TableInfo.Column("allergen_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIngredientAllergens = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysIngredientAllergens.add(new TableInfo.ForeignKey("ingredients", "CASCADE", "NO ACTION", Arrays.asList("ingredient_id"), Arrays.asList("id")));
        _foreignKeysIngredientAllergens.add(new TableInfo.ForeignKey("allergens", "CASCADE", "NO ACTION", Arrays.asList("allergen_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIngredientAllergens = new HashSet<TableInfo.Index>(2);
        _indicesIngredientAllergens.add(new TableInfo.Index("index_ingredient_allergens_ingredient_id", false, Arrays.asList("ingredient_id"), Arrays.asList("ASC")));
        _indicesIngredientAllergens.add(new TableInfo.Index("index_ingredient_allergens_allergen_id", false, Arrays.asList("allergen_id"), Arrays.asList("ASC")));
        final TableInfo _infoIngredientAllergens = new TableInfo("ingredient_allergens", _columnsIngredientAllergens, _foreignKeysIngredientAllergens, _indicesIngredientAllergens);
        final TableInfo _existingIngredientAllergens = TableInfo.read(db, "ingredient_allergens");
        if (!_infoIngredientAllergens.equals(_existingIngredientAllergens)) {
          return new RoomOpenHelper.ValidationResult(false, "ingredient_allergens(com.familymealplanner.data.local.entity.IngredientAllergenEntity).\n"
                  + " Expected:\n" + _infoIngredientAllergens + "\n"
                  + " Found:\n" + _existingIngredientAllergens);
        }
        final HashMap<String, TableInfo.Column> _columnsIngredientSubstitutes = new HashMap<String, TableInfo.Column>(5);
        _columnsIngredientSubstitutes.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredientSubstitutes.put("ingredient_id", new TableInfo.Column("ingredient_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredientSubstitutes.put("substitute_id", new TableInfo.Column("substitute_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredientSubstitutes.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredientSubstitutes.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIngredientSubstitutes = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysIngredientSubstitutes.add(new TableInfo.ForeignKey("ingredients", "CASCADE", "NO ACTION", Arrays.asList("ingredient_id"), Arrays.asList("id")));
        _foreignKeysIngredientSubstitutes.add(new TableInfo.ForeignKey("ingredients", "CASCADE", "NO ACTION", Arrays.asList("substitute_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIngredientSubstitutes = new HashSet<TableInfo.Index>(2);
        _indicesIngredientSubstitutes.add(new TableInfo.Index("index_ingredient_substitutes_ingredient_id", false, Arrays.asList("ingredient_id"), Arrays.asList("ASC")));
        _indicesIngredientSubstitutes.add(new TableInfo.Index("index_ingredient_substitutes_substitute_id", false, Arrays.asList("substitute_id"), Arrays.asList("ASC")));
        final TableInfo _infoIngredientSubstitutes = new TableInfo("ingredient_substitutes", _columnsIngredientSubstitutes, _foreignKeysIngredientSubstitutes, _indicesIngredientSubstitutes);
        final TableInfo _existingIngredientSubstitutes = TableInfo.read(db, "ingredient_substitutes");
        if (!_infoIngredientSubstitutes.equals(_existingIngredientSubstitutes)) {
          return new RoomOpenHelper.ValidationResult(false, "ingredient_substitutes(com.familymealplanner.data.local.entity.IngredientSubstituteEntity).\n"
                  + " Expected:\n" + _infoIngredientSubstitutes + "\n"
                  + " Found:\n" + _existingIngredientSubstitutes);
        }
        final HashMap<String, TableInfo.Column> _columnsMeals = new HashMap<String, TableInfo.Column>(15);
        _columnsMeals.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("instructions", new TableInfo.Column("instructions", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("simple_instructions", new TableInfo.Column("simple_instructions", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("prep_time_minutes", new TableInfo.Column("prep_time_minutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("cook_time_minutes", new TableInfo.Column("cook_time_minutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("servings", new TableInfo.Column("servings", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("is_scraped", new TableInfo.Column("is_scraped", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("is_bundled", new TableInfo.Column("is_bundled", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("image_path", new TableInfo.Column("image_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("meal_type", new TableInfo.Column("meal_type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("dish_category", new TableInfo.Column("dish_category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("created_in_language", new TableInfo.Column("created_in_language", "TEXT", true, 0, "'en'", TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeals.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMeals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMeals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMeals = new TableInfo("meals", _columnsMeals, _foreignKeysMeals, _indicesMeals);
        final TableInfo _existingMeals = TableInfo.read(db, "meals");
        if (!_infoMeals.equals(_existingMeals)) {
          return new RoomOpenHelper.ValidationResult(false, "meals(com.familymealplanner.data.local.entity.MealEntity).\n"
                  + " Expected:\n" + _infoMeals + "\n"
                  + " Found:\n" + _existingMeals);
        }
        final HashMap<String, TableInfo.Column> _columnsMealIngredients = new HashMap<String, TableInfo.Column>(6);
        _columnsMealIngredients.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealIngredients.put("meal_id", new TableInfo.Column("meal_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealIngredients.put("ingredient_id", new TableInfo.Column("ingredient_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealIngredients.put("quantity", new TableInfo.Column("quantity", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealIngredients.put("unit", new TableInfo.Column("unit", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealIngredients.put("is_star_ingredient", new TableInfo.Column("is_star_ingredient", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMealIngredients = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMealIngredients.add(new TableInfo.ForeignKey("meals", "CASCADE", "NO ACTION", Arrays.asList("meal_id"), Arrays.asList("id")));
        _foreignKeysMealIngredients.add(new TableInfo.ForeignKey("ingredients", "RESTRICT", "NO ACTION", Arrays.asList("ingredient_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMealIngredients = new HashSet<TableInfo.Index>(2);
        _indicesMealIngredients.add(new TableInfo.Index("index_meal_ingredients_meal_id", false, Arrays.asList("meal_id"), Arrays.asList("ASC")));
        _indicesMealIngredients.add(new TableInfo.Index("index_meal_ingredients_ingredient_id", false, Arrays.asList("ingredient_id"), Arrays.asList("ASC")));
        final TableInfo _infoMealIngredients = new TableInfo("meal_ingredients", _columnsMealIngredients, _foreignKeysMealIngredients, _indicesMealIngredients);
        final TableInfo _existingMealIngredients = TableInfo.read(db, "meal_ingredients");
        if (!_infoMealIngredients.equals(_existingMealIngredients)) {
          return new RoomOpenHelper.ValidationResult(false, "meal_ingredients(com.familymealplanner.data.local.entity.MealIngredientEntity).\n"
                  + " Expected:\n" + _infoMealIngredients + "\n"
                  + " Found:\n" + _existingMealIngredients);
        }
        final HashMap<String, TableInfo.Column> _columnsMealPlans = new HashMap<String, TableInfo.Column>(13);
        _columnsMealPlans.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("meal_id", new TableInfo.Column("meal_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("planned_date", new TableInfo.Column("planned_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("meal_type", new TableInfo.Column("meal_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("started_at", new TableInfo.Column("started_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("completed_at", new TableInfo.Column("completed_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("ingredient_substitutions", new TableInfo.Column("ingredient_substitutions", "TEXT", true, 0, "'{}'", TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("planned_servings", new TableInfo.Column("planned_servings", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("adjusted_prep_time_minutes", new TableInfo.Column("adjusted_prep_time_minutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMealPlans.put("adjusted_cook_time_minutes", new TableInfo.Column("adjusted_cook_time_minutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMealPlans = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMealPlans.add(new TableInfo.ForeignKey("meals", "CASCADE", "NO ACTION", Arrays.asList("meal_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMealPlans = new HashSet<TableInfo.Index>(1);
        _indicesMealPlans.add(new TableInfo.Index("index_meal_plans_meal_id", false, Arrays.asList("meal_id"), Arrays.asList("ASC")));
        final TableInfo _infoMealPlans = new TableInfo("meal_plans", _columnsMealPlans, _foreignKeysMealPlans, _indicesMealPlans);
        final TableInfo _existingMealPlans = TableInfo.read(db, "meal_plans");
        if (!_infoMealPlans.equals(_existingMealPlans)) {
          return new RoomOpenHelper.ValidationResult(false, "meal_plans(com.familymealplanner.data.local.entity.MealPlanEntity).\n"
                  + " Expected:\n" + _infoMealPlans + "\n"
                  + " Found:\n" + _existingMealPlans);
        }
        final HashMap<String, TableInfo.Column> _columnsInventoryTransactions = new HashMap<String, TableInfo.Column>(8);
        _columnsInventoryTransactions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInventoryTransactions.put("ingredient_id", new TableInfo.Column("ingredient_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInventoryTransactions.put("quantity_change", new TableInfo.Column("quantity_change", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInventoryTransactions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInventoryTransactions.put("reason", new TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInventoryTransactions.put("meal_plan_id", new TableInfo.Column("meal_plan_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInventoryTransactions.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInventoryTransactions.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInventoryTransactions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysInventoryTransactions.add(new TableInfo.ForeignKey("ingredients", "CASCADE", "NO ACTION", Arrays.asList("ingredient_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesInventoryTransactions = new HashSet<TableInfo.Index>(2);
        _indicesInventoryTransactions.add(new TableInfo.Index("index_inventory_transactions_ingredient_id", false, Arrays.asList("ingredient_id"), Arrays.asList("ASC")));
        _indicesInventoryTransactions.add(new TableInfo.Index("index_inventory_transactions_meal_plan_id", false, Arrays.asList("meal_plan_id"), Arrays.asList("ASC")));
        final TableInfo _infoInventoryTransactions = new TableInfo("inventory_transactions", _columnsInventoryTransactions, _foreignKeysInventoryTransactions, _indicesInventoryTransactions);
        final TableInfo _existingInventoryTransactions = TableInfo.read(db, "inventory_transactions");
        if (!_infoInventoryTransactions.equals(_existingInventoryTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "inventory_transactions(com.familymealplanner.data.local.entity.InventoryTransactionEntity).\n"
                  + " Expected:\n" + _infoInventoryTransactions + "\n"
                  + " Found:\n" + _existingInventoryTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsGroceryItems = new HashMap<String, TableInfo.Column>(12);
        _columnsGroceryItems.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("ingredientName", new TableInfo.Column("ingredientName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("ingredientId", new TableInfo.Column("ingredientId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("quantity", new TableInfo.Column("quantity", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("mealName", new TableInfo.Column("mealName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("mealType", new TableInfo.Column("mealType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("plannedDate", new TableInfo.Column("plannedDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("isChecked", new TableInfo.Column("isChecked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("checkedAt", new TableInfo.Column("checkedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroceryItems.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGroceryItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGroceryItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGroceryItems = new TableInfo("grocery_items", _columnsGroceryItems, _foreignKeysGroceryItems, _indicesGroceryItems);
        final TableInfo _existingGroceryItems = TableInfo.read(db, "grocery_items");
        if (!_infoGroceryItems.equals(_existingGroceryItems)) {
          return new RoomOpenHelper.ValidationResult(false, "grocery_items(com.familymealplanner.data.local.entity.GroceryItemEntity).\n"
                  + " Expected:\n" + _infoGroceryItems + "\n"
                  + " Found:\n" + _existingGroceryItems);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "d94cac3b26f15adcda98df02d4a683a2", "4373b7b92fe159f9f67defdcaac4fee9");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "allergens","ingredients","ingredient_allergens","ingredient_substitutes","meals","meal_ingredients","meal_plans","inventory_transactions","grocery_items");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `allergens`");
      _db.execSQL("DELETE FROM `ingredients`");
      _db.execSQL("DELETE FROM `ingredient_allergens`");
      _db.execSQL("DELETE FROM `ingredient_substitutes`");
      _db.execSQL("DELETE FROM `meals`");
      _db.execSQL("DELETE FROM `meal_ingredients`");
      _db.execSQL("DELETE FROM `meal_plans`");
      _db.execSQL("DELETE FROM `inventory_transactions`");
      _db.execSQL("DELETE FROM `grocery_items`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AllergenDao.class, AllergenDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IngredientDao.class, IngredientDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IngredientAllergenDao.class, IngredientAllergenDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IngredientSubstituteDao.class, IngredientSubstituteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MealDao.class, MealDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MealIngredientDao.class, MealIngredientDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MealPlanDao.class, MealPlanDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(InventoryTransactionDao.class, InventoryTransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GroceryItemDao.class, GroceryItemDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AllergenDao allergenDao() {
    if (_allergenDao != null) {
      return _allergenDao;
    } else {
      synchronized(this) {
        if(_allergenDao == null) {
          _allergenDao = new AllergenDao_Impl(this);
        }
        return _allergenDao;
      }
    }
  }

  @Override
  public IngredientDao ingredientDao() {
    if (_ingredientDao != null) {
      return _ingredientDao;
    } else {
      synchronized(this) {
        if(_ingredientDao == null) {
          _ingredientDao = new IngredientDao_Impl(this);
        }
        return _ingredientDao;
      }
    }
  }

  @Override
  public IngredientAllergenDao ingredientAllergenDao() {
    if (_ingredientAllergenDao != null) {
      return _ingredientAllergenDao;
    } else {
      synchronized(this) {
        if(_ingredientAllergenDao == null) {
          _ingredientAllergenDao = new IngredientAllergenDao_Impl(this);
        }
        return _ingredientAllergenDao;
      }
    }
  }

  @Override
  public IngredientSubstituteDao ingredientSubstituteDao() {
    if (_ingredientSubstituteDao != null) {
      return _ingredientSubstituteDao;
    } else {
      synchronized(this) {
        if(_ingredientSubstituteDao == null) {
          _ingredientSubstituteDao = new IngredientSubstituteDao_Impl(this);
        }
        return _ingredientSubstituteDao;
      }
    }
  }

  @Override
  public MealDao mealDao() {
    if (_mealDao != null) {
      return _mealDao;
    } else {
      synchronized(this) {
        if(_mealDao == null) {
          _mealDao = new MealDao_Impl(this);
        }
        return _mealDao;
      }
    }
  }

  @Override
  public MealIngredientDao mealIngredientDao() {
    if (_mealIngredientDao != null) {
      return _mealIngredientDao;
    } else {
      synchronized(this) {
        if(_mealIngredientDao == null) {
          _mealIngredientDao = new MealIngredientDao_Impl(this);
        }
        return _mealIngredientDao;
      }
    }
  }

  @Override
  public MealPlanDao mealPlanDao() {
    if (_mealPlanDao != null) {
      return _mealPlanDao;
    } else {
      synchronized(this) {
        if(_mealPlanDao == null) {
          _mealPlanDao = new MealPlanDao_Impl(this);
        }
        return _mealPlanDao;
      }
    }
  }

  @Override
  public InventoryTransactionDao inventoryTransactionDao() {
    if (_inventoryTransactionDao != null) {
      return _inventoryTransactionDao;
    } else {
      synchronized(this) {
        if(_inventoryTransactionDao == null) {
          _inventoryTransactionDao = new InventoryTransactionDao_Impl(this);
        }
        return _inventoryTransactionDao;
      }
    }
  }

  @Override
  public GroceryItemDao groceryItemDao() {
    if (_groceryItemDao != null) {
      return _groceryItemDao;
    } else {
      synchronized(this) {
        if(_groceryItemDao == null) {
          _groceryItemDao = new GroceryItemDao_Impl(this);
        }
        return _groceryItemDao;
      }
    }
  }
}
