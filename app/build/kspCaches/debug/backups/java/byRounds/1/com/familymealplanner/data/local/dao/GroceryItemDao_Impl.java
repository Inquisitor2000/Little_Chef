package com.familymealplanner.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.familymealplanner.data.local.entity.GroceryItemEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
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
public final class GroceryItemDao_Impl implements GroceryItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GroceryItemEntity> __insertionAdapterOfGroceryItemEntity;

  private final EntityDeletionOrUpdateAdapter<GroceryItemEntity> __updateAdapterOfGroceryItemEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCheckedItems;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldCheckedItems;

  public GroceryItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGroceryItemEntity = new EntityInsertionAdapter<GroceryItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `grocery_items` (`id`,`ingredientName`,`ingredientId`,`category`,`quantity`,`unit`,`mealName`,`mealType`,`plannedDate`,`isChecked`,`checkedAt`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GroceryItemEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getIngredientName());
        if (entity.getIngredientId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getIngredientId());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategory());
        }
        statement.bindDouble(5, entity.getQuantity());
        statement.bindString(6, entity.getUnit());
        statement.bindString(7, entity.getMealName());
        if (entity.getMealType() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getMealType());
        }
        if (entity.getPlannedDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getPlannedDate());
        }
        final int _tmp = entity.isChecked() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getCheckedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getCheckedAt());
        }
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__updateAdapterOfGroceryItemEntity = new EntityDeletionOrUpdateAdapter<GroceryItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `grocery_items` SET `id` = ?,`ingredientName` = ?,`ingredientId` = ?,`category` = ?,`quantity` = ?,`unit` = ?,`mealName` = ?,`mealType` = ?,`plannedDate` = ?,`isChecked` = ?,`checkedAt` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GroceryItemEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getIngredientName());
        if (entity.getIngredientId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getIngredientId());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategory());
        }
        statement.bindDouble(5, entity.getQuantity());
        statement.bindString(6, entity.getUnit());
        statement.bindString(7, entity.getMealName());
        if (entity.getMealType() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getMealType());
        }
        if (entity.getPlannedDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getPlannedDate());
        }
        final int _tmp = entity.isChecked() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getCheckedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getCheckedAt());
        }
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindString(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM grocery_items WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteCheckedItems = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM grocery_items WHERE isChecked = 1";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldCheckedItems = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM grocery_items WHERE isChecked = 1 AND checkedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final GroceryItemEntity item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGroceryItemEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<GroceryItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGroceryItemEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final GroceryItemEntity item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfGroceryItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String itemId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, itemId);
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCheckedItems(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCheckedItems.acquire();
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
          __preparedStmtOfDeleteCheckedItems.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldCheckedItems(final long cutoffTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldCheckedItems.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffTime);
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
          __preparedStmtOfDeleteOldCheckedItems.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<GroceryItemEntity>> $completion) {
    final String _sql = "SELECT * FROM grocery_items ORDER BY isChecked ASC, createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GroceryItemEntity>>() {
      @Override
      @NonNull
      public List<GroceryItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientName = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredientName");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredientId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfMealName = CursorUtil.getColumnIndexOrThrow(_cursor, "mealName");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedDate");
          final int _cursorIndexOfIsChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecked");
          final int _cursorIndexOfCheckedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "checkedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<GroceryItemEntity> _result = new ArrayList<GroceryItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GroceryItemEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientName;
            _tmpIngredientName = _cursor.getString(_cursorIndexOfIngredientName);
            final String _tmpIngredientId;
            if (_cursor.isNull(_cursorIndexOfIngredientId)) {
              _tmpIngredientId = null;
            } else {
              _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpMealName;
            _tmpMealName = _cursor.getString(_cursorIndexOfMealName);
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final Long _tmpPlannedDate;
            if (_cursor.isNull(_cursorIndexOfPlannedDate)) {
              _tmpPlannedDate = null;
            } else {
              _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            }
            final boolean _tmpIsChecked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsChecked);
            _tmpIsChecked = _tmp != 0;
            final Long _tmpCheckedAt;
            if (_cursor.isNull(_cursorIndexOfCheckedAt)) {
              _tmpCheckedAt = null;
            } else {
              _tmpCheckedAt = _cursor.getLong(_cursorIndexOfCheckedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new GroceryItemEntity(_tmpId,_tmpIngredientName,_tmpIngredientId,_tmpCategory,_tmpQuantity,_tmpUnit,_tmpMealName,_tmpMealType,_tmpPlannedDate,_tmpIsChecked,_tmpCheckedAt,_tmpCreatedAt);
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
  public Flow<List<GroceryItemEntity>> observeAll() {
    final String _sql = "SELECT * FROM grocery_items ORDER BY isChecked ASC, createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"grocery_items"}, new Callable<List<GroceryItemEntity>>() {
      @Override
      @NonNull
      public List<GroceryItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientName = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredientName");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredientId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfMealName = CursorUtil.getColumnIndexOrThrow(_cursor, "mealName");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedDate");
          final int _cursorIndexOfIsChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecked");
          final int _cursorIndexOfCheckedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "checkedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<GroceryItemEntity> _result = new ArrayList<GroceryItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GroceryItemEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientName;
            _tmpIngredientName = _cursor.getString(_cursorIndexOfIngredientName);
            final String _tmpIngredientId;
            if (_cursor.isNull(_cursorIndexOfIngredientId)) {
              _tmpIngredientId = null;
            } else {
              _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpMealName;
            _tmpMealName = _cursor.getString(_cursorIndexOfMealName);
            final String _tmpMealType;
            if (_cursor.isNull(_cursorIndexOfMealType)) {
              _tmpMealType = null;
            } else {
              _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            }
            final Long _tmpPlannedDate;
            if (_cursor.isNull(_cursorIndexOfPlannedDate)) {
              _tmpPlannedDate = null;
            } else {
              _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            }
            final boolean _tmpIsChecked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsChecked);
            _tmpIsChecked = _tmp != 0;
            final Long _tmpCheckedAt;
            if (_cursor.isNull(_cursorIndexOfCheckedAt)) {
              _tmpCheckedAt = null;
            } else {
              _tmpCheckedAt = _cursor.getLong(_cursorIndexOfCheckedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new GroceryItemEntity(_tmpId,_tmpIngredientName,_tmpIngredientId,_tmpCategory,_tmpQuantity,_tmpUnit,_tmpMealName,_tmpMealType,_tmpPlannedDate,_tmpIsChecked,_tmpCheckedAt,_tmpCreatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
