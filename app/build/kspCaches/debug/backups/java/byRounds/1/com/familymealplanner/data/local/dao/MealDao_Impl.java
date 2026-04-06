package com.familymealplanner.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.familymealplanner.data.local.entity.MealEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MealDao_Impl implements MealDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MealEntity> __insertionAdapterOfMealEntity;

  private final EntityDeletionOrUpdateAdapter<MealEntity> __deletionAdapterOfMealEntity;

  private final EntityDeletionOrUpdateAdapter<MealEntity> __updateAdapterOfMealEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateImagePath;

  public MealDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMealEntity = new EntityInsertionAdapter<MealEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `meals` (`id`,`name`,`instructions`,`simple_instructions`,`prep_time_minutes`,`cook_time_minutes`,`servings`,`is_scraped`,`is_bundled`,`image_path`,`meal_type`,`dish_category`,`created_in_language`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getInstructions() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getInstructions());
        }
        if (entity.getSimpleInstructions() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSimpleInstructions());
        }
        if (entity.getPrepTimeMinutes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getPrepTimeMinutes());
        }
        if (entity.getCookTimeMinutes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCookTimeMinutes());
        }
        if (entity.getServings() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getServings());
        }
        final int _tmp = entity.isScraped() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isBundled() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        if (entity.getImagePath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getImagePath());
        }
        if (entity.getMealType() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getMealType());
        }
        if (entity.getDishCategory() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getDishCategory());
        }
        statement.bindString(13, entity.getCreatedInLanguage());
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfMealEntity = new EntityDeletionOrUpdateAdapter<MealEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `meals` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfMealEntity = new EntityDeletionOrUpdateAdapter<MealEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `meals` SET `id` = ?,`name` = ?,`instructions` = ?,`simple_instructions` = ?,`prep_time_minutes` = ?,`cook_time_minutes` = ?,`servings` = ?,`is_scraped` = ?,`is_bundled` = ?,`image_path` = ?,`meal_type` = ?,`dish_category` = ?,`created_in_language` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getInstructions() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getInstructions());
        }
        if (entity.getSimpleInstructions() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSimpleInstructions());
        }
        if (entity.getPrepTimeMinutes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getPrepTimeMinutes());
        }
        if (entity.getCookTimeMinutes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCookTimeMinutes());
        }
        if (entity.getServings() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getServings());
        }
        final int _tmp = entity.isScraped() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isBundled() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        if (entity.getImagePath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getImagePath());
        }
        if (entity.getMealType() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getMealType());
        }
        if (entity.getDishCategory() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getDishCategory());
        }
        statement.bindString(13, entity.getCreatedInLanguage());
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getUpdatedAt());
        statement.bindString(16, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateImagePath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE meals SET image_path = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MealEntity meal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMealEntity.insert(meal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MealEntity meal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMealEntity.handle(meal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MealEntity meal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMealEntity.handle(meal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateImagePath(final String mealId, final String imagePath, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateImagePath.acquire();
        int _argIndex = 1;
        if (imagePath == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, imagePath);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 3;
        _stmt.bindString(_argIndex, mealId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateImagePath.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id, final Continuation<? super MealEntity> $completion) {
    final String _sql = "SELECT * FROM meals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MealEntity>() {
      @Override
      @Nullable
      public MealEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfSimpleInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "simple_instructions");
          final int _cursorIndexOfPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "prep_time_minutes");
          final int _cursorIndexOfCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cook_time_minutes");
          final int _cursorIndexOfServings = CursorUtil.getColumnIndexOrThrow(_cursor, "servings");
          final int _cursorIndexOfIsScraped = CursorUtil.getColumnIndexOrThrow(_cursor, "is_scraped");
          final int _cursorIndexOfIsBundled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bundled");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfDishCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "dish_category");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final MealEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInstructions;
            if (_cursor.isNull(_cursorIndexOfInstructions)) {
              _tmpInstructions = null;
            } else {
              _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            }
            final String _tmpSimpleInstructions;
            if (_cursor.isNull(_cursorIndexOfSimpleInstructions)) {
              _tmpSimpleInstructions = null;
            } else {
              _tmpSimpleInstructions = _cursor.getString(_cursorIndexOfSimpleInstructions);
            }
            final Integer _tmpPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfPrepTimeMinutes)) {
              _tmpPrepTimeMinutes = null;
            } else {
              _tmpPrepTimeMinutes = _cursor.getInt(_cursorIndexOfPrepTimeMinutes);
            }
            final Integer _tmpCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfCookTimeMinutes)) {
              _tmpCookTimeMinutes = null;
            } else {
              _tmpCookTimeMinutes = _cursor.getInt(_cursorIndexOfCookTimeMinutes);
            }
            final Integer _tmpServings;
            if (_cursor.isNull(_cursorIndexOfServings)) {
              _tmpServings = null;
            } else {
              _tmpServings = _cursor.getInt(_cursorIndexOfServings);
            }
            final boolean _tmpIsScraped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsScraped);
            _tmpIsScraped = _tmp != 0;
            final boolean _tmpIsBundled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBundled);
            _tmpIsBundled = _tmp_1 != 0;
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final String _tmpDishCategory;
            if (_cursor.isNull(_cursorIndexOfDishCategory)) {
              _tmpDishCategory = null;
            } else {
              _tmpDishCategory = _cursor.getString(_cursorIndexOfDishCategory);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MealEntity(_tmpId,_tmpName,_tmpInstructions,_tmpSimpleInstructions,_tmpPrepTimeMinutes,_tmpCookTimeMinutes,_tmpServings,_tmpIsScraped,_tmpIsBundled,_tmpImagePath,_tmpMealType,_tmpDishCategory,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<MealEntity> observeById(final String id) {
    final String _sql = "SELECT * FROM meals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meals"}, new Callable<MealEntity>() {
      @Override
      @Nullable
      public MealEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfSimpleInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "simple_instructions");
          final int _cursorIndexOfPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "prep_time_minutes");
          final int _cursorIndexOfCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cook_time_minutes");
          final int _cursorIndexOfServings = CursorUtil.getColumnIndexOrThrow(_cursor, "servings");
          final int _cursorIndexOfIsScraped = CursorUtil.getColumnIndexOrThrow(_cursor, "is_scraped");
          final int _cursorIndexOfIsBundled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bundled");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfDishCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "dish_category");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final MealEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInstructions;
            if (_cursor.isNull(_cursorIndexOfInstructions)) {
              _tmpInstructions = null;
            } else {
              _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            }
            final String _tmpSimpleInstructions;
            if (_cursor.isNull(_cursorIndexOfSimpleInstructions)) {
              _tmpSimpleInstructions = null;
            } else {
              _tmpSimpleInstructions = _cursor.getString(_cursorIndexOfSimpleInstructions);
            }
            final Integer _tmpPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfPrepTimeMinutes)) {
              _tmpPrepTimeMinutes = null;
            } else {
              _tmpPrepTimeMinutes = _cursor.getInt(_cursorIndexOfPrepTimeMinutes);
            }
            final Integer _tmpCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfCookTimeMinutes)) {
              _tmpCookTimeMinutes = null;
            } else {
              _tmpCookTimeMinutes = _cursor.getInt(_cursorIndexOfCookTimeMinutes);
            }
            final Integer _tmpServings;
            if (_cursor.isNull(_cursorIndexOfServings)) {
              _tmpServings = null;
            } else {
              _tmpServings = _cursor.getInt(_cursorIndexOfServings);
            }
            final boolean _tmpIsScraped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsScraped);
            _tmpIsScraped = _tmp != 0;
            final boolean _tmpIsBundled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBundled);
            _tmpIsBundled = _tmp_1 != 0;
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final String _tmpDishCategory;
            if (_cursor.isNull(_cursorIndexOfDishCategory)) {
              _tmpDishCategory = null;
            } else {
              _tmpDishCategory = _cursor.getString(_cursorIndexOfDishCategory);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MealEntity(_tmpId,_tmpName,_tmpInstructions,_tmpSimpleInstructions,_tmpPrepTimeMinutes,_tmpCookTimeMinutes,_tmpServings,_tmpIsScraped,_tmpIsBundled,_tmpImagePath,_tmpMealType,_tmpDishCategory,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAll(final Continuation<? super List<MealEntity>> $completion) {
    final String _sql = "SELECT * FROM meals";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealEntity>>() {
      @Override
      @NonNull
      public List<MealEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfSimpleInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "simple_instructions");
          final int _cursorIndexOfPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "prep_time_minutes");
          final int _cursorIndexOfCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cook_time_minutes");
          final int _cursorIndexOfServings = CursorUtil.getColumnIndexOrThrow(_cursor, "servings");
          final int _cursorIndexOfIsScraped = CursorUtil.getColumnIndexOrThrow(_cursor, "is_scraped");
          final int _cursorIndexOfIsBundled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bundled");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfDishCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "dish_category");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<MealEntity> _result = new ArrayList<MealEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInstructions;
            if (_cursor.isNull(_cursorIndexOfInstructions)) {
              _tmpInstructions = null;
            } else {
              _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            }
            final String _tmpSimpleInstructions;
            if (_cursor.isNull(_cursorIndexOfSimpleInstructions)) {
              _tmpSimpleInstructions = null;
            } else {
              _tmpSimpleInstructions = _cursor.getString(_cursorIndexOfSimpleInstructions);
            }
            final Integer _tmpPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfPrepTimeMinutes)) {
              _tmpPrepTimeMinutes = null;
            } else {
              _tmpPrepTimeMinutes = _cursor.getInt(_cursorIndexOfPrepTimeMinutes);
            }
            final Integer _tmpCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfCookTimeMinutes)) {
              _tmpCookTimeMinutes = null;
            } else {
              _tmpCookTimeMinutes = _cursor.getInt(_cursorIndexOfCookTimeMinutes);
            }
            final Integer _tmpServings;
            if (_cursor.isNull(_cursorIndexOfServings)) {
              _tmpServings = null;
            } else {
              _tmpServings = _cursor.getInt(_cursorIndexOfServings);
            }
            final boolean _tmpIsScraped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsScraped);
            _tmpIsScraped = _tmp != 0;
            final boolean _tmpIsBundled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBundled);
            _tmpIsBundled = _tmp_1 != 0;
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final String _tmpDishCategory;
            if (_cursor.isNull(_cursorIndexOfDishCategory)) {
              _tmpDishCategory = null;
            } else {
              _tmpDishCategory = _cursor.getString(_cursorIndexOfDishCategory);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MealEntity(_tmpId,_tmpName,_tmpInstructions,_tmpSimpleInstructions,_tmpPrepTimeMinutes,_tmpCookTimeMinutes,_tmpServings,_tmpIsScraped,_tmpIsBundled,_tmpImagePath,_tmpMealType,_tmpDishCategory,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MealEntity>> observeAll() {
    final String _sql = "SELECT * FROM meals";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meals"}, new Callable<List<MealEntity>>() {
      @Override
      @NonNull
      public List<MealEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfSimpleInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "simple_instructions");
          final int _cursorIndexOfPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "prep_time_minutes");
          final int _cursorIndexOfCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cook_time_minutes");
          final int _cursorIndexOfServings = CursorUtil.getColumnIndexOrThrow(_cursor, "servings");
          final int _cursorIndexOfIsScraped = CursorUtil.getColumnIndexOrThrow(_cursor, "is_scraped");
          final int _cursorIndexOfIsBundled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bundled");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfDishCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "dish_category");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<MealEntity> _result = new ArrayList<MealEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInstructions;
            if (_cursor.isNull(_cursorIndexOfInstructions)) {
              _tmpInstructions = null;
            } else {
              _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            }
            final String _tmpSimpleInstructions;
            if (_cursor.isNull(_cursorIndexOfSimpleInstructions)) {
              _tmpSimpleInstructions = null;
            } else {
              _tmpSimpleInstructions = _cursor.getString(_cursorIndexOfSimpleInstructions);
            }
            final Integer _tmpPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfPrepTimeMinutes)) {
              _tmpPrepTimeMinutes = null;
            } else {
              _tmpPrepTimeMinutes = _cursor.getInt(_cursorIndexOfPrepTimeMinutes);
            }
            final Integer _tmpCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfCookTimeMinutes)) {
              _tmpCookTimeMinutes = null;
            } else {
              _tmpCookTimeMinutes = _cursor.getInt(_cursorIndexOfCookTimeMinutes);
            }
            final Integer _tmpServings;
            if (_cursor.isNull(_cursorIndexOfServings)) {
              _tmpServings = null;
            } else {
              _tmpServings = _cursor.getInt(_cursorIndexOfServings);
            }
            final boolean _tmpIsScraped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsScraped);
            _tmpIsScraped = _tmp != 0;
            final boolean _tmpIsBundled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBundled);
            _tmpIsBundled = _tmp_1 != 0;
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final String _tmpDishCategory;
            if (_cursor.isNull(_cursorIndexOfDishCategory)) {
              _tmpDishCategory = null;
            } else {
              _tmpDishCategory = _cursor.getString(_cursorIndexOfDishCategory);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MealEntity(_tmpId,_tmpName,_tmpInstructions,_tmpSimpleInstructions,_tmpPrepTimeMinutes,_tmpCookTimeMinutes,_tmpServings,_tmpIsScraped,_tmpIsBundled,_tmpImagePath,_tmpMealType,_tmpDishCategory,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MealEntity>> observeScrapedMeals() {
    final String _sql = "SELECT * FROM meals WHERE is_scraped = 1 ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meals"}, new Callable<List<MealEntity>>() {
      @Override
      @NonNull
      public List<MealEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfSimpleInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "simple_instructions");
          final int _cursorIndexOfPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "prep_time_minutes");
          final int _cursorIndexOfCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cook_time_minutes");
          final int _cursorIndexOfServings = CursorUtil.getColumnIndexOrThrow(_cursor, "servings");
          final int _cursorIndexOfIsScraped = CursorUtil.getColumnIndexOrThrow(_cursor, "is_scraped");
          final int _cursorIndexOfIsBundled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bundled");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfDishCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "dish_category");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<MealEntity> _result = new ArrayList<MealEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInstructions;
            if (_cursor.isNull(_cursorIndexOfInstructions)) {
              _tmpInstructions = null;
            } else {
              _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            }
            final String _tmpSimpleInstructions;
            if (_cursor.isNull(_cursorIndexOfSimpleInstructions)) {
              _tmpSimpleInstructions = null;
            } else {
              _tmpSimpleInstructions = _cursor.getString(_cursorIndexOfSimpleInstructions);
            }
            final Integer _tmpPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfPrepTimeMinutes)) {
              _tmpPrepTimeMinutes = null;
            } else {
              _tmpPrepTimeMinutes = _cursor.getInt(_cursorIndexOfPrepTimeMinutes);
            }
            final Integer _tmpCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfCookTimeMinutes)) {
              _tmpCookTimeMinutes = null;
            } else {
              _tmpCookTimeMinutes = _cursor.getInt(_cursorIndexOfCookTimeMinutes);
            }
            final Integer _tmpServings;
            if (_cursor.isNull(_cursorIndexOfServings)) {
              _tmpServings = null;
            } else {
              _tmpServings = _cursor.getInt(_cursorIndexOfServings);
            }
            final boolean _tmpIsScraped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsScraped);
            _tmpIsScraped = _tmp != 0;
            final boolean _tmpIsBundled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBundled);
            _tmpIsBundled = _tmp_1 != 0;
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final String _tmpDishCategory;
            if (_cursor.isNull(_cursorIndexOfDishCategory)) {
              _tmpDishCategory = null;
            } else {
              _tmpDishCategory = _cursor.getString(_cursorIndexOfDishCategory);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MealEntity(_tmpId,_tmpName,_tmpInstructions,_tmpSimpleInstructions,_tmpPrepTimeMinutes,_tmpCookTimeMinutes,_tmpServings,_tmpIsScraped,_tmpIsBundled,_tmpImagePath,_tmpMealType,_tmpDishCategory,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MealEntity>> observeNonScrapedMeals() {
    final String _sql = "SELECT * FROM meals WHERE is_scraped = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meals"}, new Callable<List<MealEntity>>() {
      @Override
      @NonNull
      public List<MealEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfSimpleInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "simple_instructions");
          final int _cursorIndexOfPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "prep_time_minutes");
          final int _cursorIndexOfCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cook_time_minutes");
          final int _cursorIndexOfServings = CursorUtil.getColumnIndexOrThrow(_cursor, "servings");
          final int _cursorIndexOfIsScraped = CursorUtil.getColumnIndexOrThrow(_cursor, "is_scraped");
          final int _cursorIndexOfIsBundled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bundled");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfDishCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "dish_category");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<MealEntity> _result = new ArrayList<MealEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInstructions;
            if (_cursor.isNull(_cursorIndexOfInstructions)) {
              _tmpInstructions = null;
            } else {
              _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            }
            final String _tmpSimpleInstructions;
            if (_cursor.isNull(_cursorIndexOfSimpleInstructions)) {
              _tmpSimpleInstructions = null;
            } else {
              _tmpSimpleInstructions = _cursor.getString(_cursorIndexOfSimpleInstructions);
            }
            final Integer _tmpPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfPrepTimeMinutes)) {
              _tmpPrepTimeMinutes = null;
            } else {
              _tmpPrepTimeMinutes = _cursor.getInt(_cursorIndexOfPrepTimeMinutes);
            }
            final Integer _tmpCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfCookTimeMinutes)) {
              _tmpCookTimeMinutes = null;
            } else {
              _tmpCookTimeMinutes = _cursor.getInt(_cursorIndexOfCookTimeMinutes);
            }
            final Integer _tmpServings;
            if (_cursor.isNull(_cursorIndexOfServings)) {
              _tmpServings = null;
            } else {
              _tmpServings = _cursor.getInt(_cursorIndexOfServings);
            }
            final boolean _tmpIsScraped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsScraped);
            _tmpIsScraped = _tmp != 0;
            final boolean _tmpIsBundled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBundled);
            _tmpIsBundled = _tmp_1 != 0;
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final String _tmpDishCategory;
            if (_cursor.isNull(_cursorIndexOfDishCategory)) {
              _tmpDishCategory = null;
            } else {
              _tmpDishCategory = _cursor.getString(_cursorIndexOfDishCategory);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MealEntity(_tmpId,_tmpName,_tmpInstructions,_tmpSimpleInstructions,_tmpPrepTimeMinutes,_tmpCookTimeMinutes,_tmpServings,_tmpIsScraped,_tmpIsBundled,_tmpImagePath,_tmpMealType,_tmpDishCategory,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object search(final String query,
      final Continuation<? super List<MealEntity>> $completion) {
    final String _sql = "SELECT * FROM meals WHERE name LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealEntity>>() {
      @Override
      @NonNull
      public List<MealEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfSimpleInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "simple_instructions");
          final int _cursorIndexOfPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "prep_time_minutes");
          final int _cursorIndexOfCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cook_time_minutes");
          final int _cursorIndexOfServings = CursorUtil.getColumnIndexOrThrow(_cursor, "servings");
          final int _cursorIndexOfIsScraped = CursorUtil.getColumnIndexOrThrow(_cursor, "is_scraped");
          final int _cursorIndexOfIsBundled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bundled");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfDishCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "dish_category");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<MealEntity> _result = new ArrayList<MealEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInstructions;
            if (_cursor.isNull(_cursorIndexOfInstructions)) {
              _tmpInstructions = null;
            } else {
              _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            }
            final String _tmpSimpleInstructions;
            if (_cursor.isNull(_cursorIndexOfSimpleInstructions)) {
              _tmpSimpleInstructions = null;
            } else {
              _tmpSimpleInstructions = _cursor.getString(_cursorIndexOfSimpleInstructions);
            }
            final Integer _tmpPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfPrepTimeMinutes)) {
              _tmpPrepTimeMinutes = null;
            } else {
              _tmpPrepTimeMinutes = _cursor.getInt(_cursorIndexOfPrepTimeMinutes);
            }
            final Integer _tmpCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfCookTimeMinutes)) {
              _tmpCookTimeMinutes = null;
            } else {
              _tmpCookTimeMinutes = _cursor.getInt(_cursorIndexOfCookTimeMinutes);
            }
            final Integer _tmpServings;
            if (_cursor.isNull(_cursorIndexOfServings)) {
              _tmpServings = null;
            } else {
              _tmpServings = _cursor.getInt(_cursorIndexOfServings);
            }
            final boolean _tmpIsScraped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsScraped);
            _tmpIsScraped = _tmp != 0;
            final boolean _tmpIsBundled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBundled);
            _tmpIsBundled = _tmp_1 != 0;
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final String _tmpDishCategory;
            if (_cursor.isNull(_cursorIndexOfDishCategory)) {
              _tmpDishCategory = null;
            } else {
              _tmpDishCategory = _cursor.getString(_cursorIndexOfDishCategory);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MealEntity(_tmpId,_tmpName,_tmpInstructions,_tmpSimpleInstructions,_tmpPrepTimeMinutes,_tmpCookTimeMinutes,_tmpServings,_tmpIsScraped,_tmpIsBundled,_tmpImagePath,_tmpMealType,_tmpDishCategory,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getChangedSince(final long timestamp,
      final Continuation<? super List<MealEntity>> $completion) {
    final String _sql = "SELECT * FROM meals WHERE updated_at > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealEntity>>() {
      @Override
      @NonNull
      public List<MealEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfSimpleInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "simple_instructions");
          final int _cursorIndexOfPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "prep_time_minutes");
          final int _cursorIndexOfCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cook_time_minutes");
          final int _cursorIndexOfServings = CursorUtil.getColumnIndexOrThrow(_cursor, "servings");
          final int _cursorIndexOfIsScraped = CursorUtil.getColumnIndexOrThrow(_cursor, "is_scraped");
          final int _cursorIndexOfIsBundled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bundled");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfDishCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "dish_category");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<MealEntity> _result = new ArrayList<MealEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInstructions;
            if (_cursor.isNull(_cursorIndexOfInstructions)) {
              _tmpInstructions = null;
            } else {
              _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            }
            final String _tmpSimpleInstructions;
            if (_cursor.isNull(_cursorIndexOfSimpleInstructions)) {
              _tmpSimpleInstructions = null;
            } else {
              _tmpSimpleInstructions = _cursor.getString(_cursorIndexOfSimpleInstructions);
            }
            final Integer _tmpPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfPrepTimeMinutes)) {
              _tmpPrepTimeMinutes = null;
            } else {
              _tmpPrepTimeMinutes = _cursor.getInt(_cursorIndexOfPrepTimeMinutes);
            }
            final Integer _tmpCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfCookTimeMinutes)) {
              _tmpCookTimeMinutes = null;
            } else {
              _tmpCookTimeMinutes = _cursor.getInt(_cursorIndexOfCookTimeMinutes);
            }
            final Integer _tmpServings;
            if (_cursor.isNull(_cursorIndexOfServings)) {
              _tmpServings = null;
            } else {
              _tmpServings = _cursor.getInt(_cursorIndexOfServings);
            }
            final boolean _tmpIsScraped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsScraped);
            _tmpIsScraped = _tmp != 0;
            final boolean _tmpIsBundled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBundled);
            _tmpIsBundled = _tmp_1 != 0;
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final String _tmpDishCategory;
            if (_cursor.isNull(_cursorIndexOfDishCategory)) {
              _tmpDishCategory = null;
            } else {
              _tmpDishCategory = _cursor.getString(_cursorIndexOfDishCategory);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MealEntity(_tmpId,_tmpName,_tmpInstructions,_tmpSimpleInstructions,_tmpPrepTimeMinutes,_tmpCookTimeMinutes,_tmpServings,_tmpIsScraped,_tmpIsBundled,_tmpImagePath,_tmpMealType,_tmpDishCategory,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
