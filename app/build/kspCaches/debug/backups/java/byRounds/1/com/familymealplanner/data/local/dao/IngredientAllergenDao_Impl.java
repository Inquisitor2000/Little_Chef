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
import com.familymealplanner.data.local.entity.IngredientAllergenEntity;
import java.lang.Class;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IngredientAllergenDao_Impl implements IngredientAllergenDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IngredientAllergenEntity> __insertionAdapterOfIngredientAllergenEntity;

  private final EntityDeletionOrUpdateAdapter<IngredientAllergenEntity> __deletionAdapterOfIngredientAllergenEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByIngredientId;

  public IngredientAllergenDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIngredientAllergenEntity = new EntityInsertionAdapter<IngredientAllergenEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ingredient_allergens` (`id`,`ingredient_id`,`allergen_id`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IngredientAllergenEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getIngredientId());
        statement.bindString(3, entity.getAllergenId());
      }
    };
    this.__deletionAdapterOfIngredientAllergenEntity = new EntityDeletionOrUpdateAdapter<IngredientAllergenEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `ingredient_allergens` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IngredientAllergenEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByIngredientId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ingredient_allergens WHERE ingredient_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final IngredientAllergenEntity ingredientAllergen,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIngredientAllergenEntity.insert(ingredientAllergen);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<IngredientAllergenEntity> ingredientAllergens,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIngredientAllergenEntity.insert(ingredientAllergens);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final IngredientAllergenEntity ingredientAllergen,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIngredientAllergenEntity.handle(ingredientAllergen);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByIngredientId(final String ingredientId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByIngredientId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, ingredientId);
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
          __preparedStmtOfDeleteByIngredientId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getByIngredientId(final String ingredientId,
      final Continuation<? super List<IngredientAllergenEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredient_allergens WHERE ingredient_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ingredientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientAllergenEntity>>() {
      @Override
      @NonNull
      public List<IngredientAllergenEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfAllergenId = CursorUtil.getColumnIndexOrThrow(_cursor, "allergen_id");
          final List<IngredientAllergenEntity> _result = new ArrayList<IngredientAllergenEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientAllergenEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final String _tmpAllergenId;
            _tmpAllergenId = _cursor.getString(_cursorIndexOfAllergenId);
            _item = new IngredientAllergenEntity(_tmpId,_tmpIngredientId,_tmpAllergenId);
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
  public Object getByAllergenId(final String allergenId,
      final Continuation<? super List<IngredientAllergenEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredient_allergens WHERE allergen_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, allergenId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientAllergenEntity>>() {
      @Override
      @NonNull
      public List<IngredientAllergenEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfAllergenId = CursorUtil.getColumnIndexOrThrow(_cursor, "allergen_id");
          final List<IngredientAllergenEntity> _result = new ArrayList<IngredientAllergenEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientAllergenEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final String _tmpAllergenId;
            _tmpAllergenId = _cursor.getString(_cursorIndexOfAllergenId);
            _item = new IngredientAllergenEntity(_tmpId,_tmpIngredientId,_tmpAllergenId);
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
  public Object getAll(final Continuation<? super List<IngredientAllergenEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredient_allergens";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientAllergenEntity>>() {
      @Override
      @NonNull
      public List<IngredientAllergenEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfAllergenId = CursorUtil.getColumnIndexOrThrow(_cursor, "allergen_id");
          final List<IngredientAllergenEntity> _result = new ArrayList<IngredientAllergenEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientAllergenEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final String _tmpAllergenId;
            _tmpAllergenId = _cursor.getString(_cursorIndexOfAllergenId);
            _item = new IngredientAllergenEntity(_tmpId,_tmpIngredientId,_tmpAllergenId);
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
      final Continuation<? super List<IngredientAllergenEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredient_allergens WHERE id > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientAllergenEntity>>() {
      @Override
      @NonNull
      public List<IngredientAllergenEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfAllergenId = CursorUtil.getColumnIndexOrThrow(_cursor, "allergen_id");
          final List<IngredientAllergenEntity> _result = new ArrayList<IngredientAllergenEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientAllergenEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final String _tmpAllergenId;
            _tmpAllergenId = _cursor.getString(_cursorIndexOfAllergenId);
            _item = new IngredientAllergenEntity(_tmpId,_tmpIngredientId,_tmpAllergenId);
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
