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
import com.familymealplanner.data.local.entity.IngredientSubstituteEntity;
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
public final class IngredientSubstituteDao_Impl implements IngredientSubstituteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IngredientSubstituteEntity> __insertionAdapterOfIngredientSubstituteEntity;

  private final EntityDeletionOrUpdateAdapter<IngredientSubstituteEntity> __deletionAdapterOfIngredientSubstituteEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByIngredientId;

  public IngredientSubstituteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIngredientSubstituteEntity = new EntityInsertionAdapter<IngredientSubstituteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ingredient_substitutes` (`id`,`ingredient_id`,`substitute_id`,`notes`,`created_at`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IngredientSubstituteEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getIngredientId());
        statement.bindString(3, entity.getSubstituteId());
        if (entity.getNotes() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNotes());
        }
        statement.bindLong(5, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfIngredientSubstituteEntity = new EntityDeletionOrUpdateAdapter<IngredientSubstituteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `ingredient_substitutes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IngredientSubstituteEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByIngredientId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ingredient_substitutes WHERE ingredient_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final IngredientSubstituteEntity substitute,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIngredientSubstituteEntity.insert(substitute);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final IngredientSubstituteEntity substitute,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIngredientSubstituteEntity.handle(substitute);
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
      final Continuation<? super List<IngredientSubstituteEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredient_substitutes WHERE ingredient_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ingredientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientSubstituteEntity>>() {
      @Override
      @NonNull
      public List<IngredientSubstituteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfSubstituteId = CursorUtil.getColumnIndexOrThrow(_cursor, "substitute_id");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<IngredientSubstituteEntity> _result = new ArrayList<IngredientSubstituteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientSubstituteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final String _tmpSubstituteId;
            _tmpSubstituteId = _cursor.getString(_cursorIndexOfSubstituteId);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new IngredientSubstituteEntity(_tmpId,_tmpIngredientId,_tmpSubstituteId,_tmpNotes,_tmpCreatedAt);
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
  public Object getBySubstituteId(final String substituteId,
      final Continuation<? super List<IngredientSubstituteEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredient_substitutes WHERE substitute_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, substituteId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientSubstituteEntity>>() {
      @Override
      @NonNull
      public List<IngredientSubstituteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfSubstituteId = CursorUtil.getColumnIndexOrThrow(_cursor, "substitute_id");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<IngredientSubstituteEntity> _result = new ArrayList<IngredientSubstituteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientSubstituteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final String _tmpSubstituteId;
            _tmpSubstituteId = _cursor.getString(_cursorIndexOfSubstituteId);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new IngredientSubstituteEntity(_tmpId,_tmpIngredientId,_tmpSubstituteId,_tmpNotes,_tmpCreatedAt);
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
  public Object getAll(final Continuation<? super List<IngredientSubstituteEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredient_substitutes";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientSubstituteEntity>>() {
      @Override
      @NonNull
      public List<IngredientSubstituteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfSubstituteId = CursorUtil.getColumnIndexOrThrow(_cursor, "substitute_id");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<IngredientSubstituteEntity> _result = new ArrayList<IngredientSubstituteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientSubstituteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final String _tmpSubstituteId;
            _tmpSubstituteId = _cursor.getString(_cursorIndexOfSubstituteId);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new IngredientSubstituteEntity(_tmpId,_tmpIngredientId,_tmpSubstituteId,_tmpNotes,_tmpCreatedAt);
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
      final Continuation<? super List<IngredientSubstituteEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredient_substitutes WHERE created_at > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientSubstituteEntity>>() {
      @Override
      @NonNull
      public List<IngredientSubstituteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfSubstituteId = CursorUtil.getColumnIndexOrThrow(_cursor, "substitute_id");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<IngredientSubstituteEntity> _result = new ArrayList<IngredientSubstituteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientSubstituteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final String _tmpSubstituteId;
            _tmpSubstituteId = _cursor.getString(_cursorIndexOfSubstituteId);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new IngredientSubstituteEntity(_tmpId,_tmpIngredientId,_tmpSubstituteId,_tmpNotes,_tmpCreatedAt);
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
