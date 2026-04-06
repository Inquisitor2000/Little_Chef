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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.familymealplanner.data.local.entity.IngredientEntity;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IngredientDao_Impl implements IngredientDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IngredientEntity> __insertionAdapterOfIngredientEntity;

  private final EntityDeletionOrUpdateAdapter<IngredientEntity> __deletionAdapterOfIngredientEntity;

  private final EntityDeletionOrUpdateAdapter<IngredientEntity> __updateAdapterOfIngredientEntity;

  public IngredientDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIngredientEntity = new EntityInsertionAdapter<IngredientEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ingredients` (`id`,`name`,`unit`,`category`,`subcategory`,`preferred_display_unit`,`created_in_language`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IngredientEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getUnit());
        if (entity.getCategory() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategory());
        }
        if (entity.getSubcategory() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSubcategory());
        }
        if (entity.getPreferredDisplayUnit() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPreferredDisplayUnit());
        }
        statement.bindString(7, entity.getCreatedInLanguage());
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfIngredientEntity = new EntityDeletionOrUpdateAdapter<IngredientEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `ingredients` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IngredientEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfIngredientEntity = new EntityDeletionOrUpdateAdapter<IngredientEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `ingredients` SET `id` = ?,`name` = ?,`unit` = ?,`category` = ?,`subcategory` = ?,`preferred_display_unit` = ?,`created_in_language` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IngredientEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getUnit());
        if (entity.getCategory() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategory());
        }
        if (entity.getSubcategory() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSubcategory());
        }
        if (entity.getPreferredDisplayUnit() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPreferredDisplayUnit());
        }
        statement.bindString(7, entity.getCreatedInLanguage());
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
        statement.bindString(10, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final IngredientEntity ingredient,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIngredientEntity.insert(ingredient);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final IngredientEntity ingredient,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIngredientEntity.handle(ingredient);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final IngredientEntity ingredient,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIngredientEntity.handle(ingredient);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id, final Continuation<? super IngredientEntity> $completion) {
    final String _sql = "SELECT * FROM ingredients WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IngredientEntity>() {
      @Override
      @Nullable
      public IngredientEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSubcategory = CursorUtil.getColumnIndexOrThrow(_cursor, "subcategory");
          final int _cursorIndexOfPreferredDisplayUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "preferred_display_unit");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final IngredientEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpSubcategory;
            if (_cursor.isNull(_cursorIndexOfSubcategory)) {
              _tmpSubcategory = null;
            } else {
              _tmpSubcategory = _cursor.getString(_cursorIndexOfSubcategory);
            }
            final String _tmpPreferredDisplayUnit;
            if (_cursor.isNull(_cursorIndexOfPreferredDisplayUnit)) {
              _tmpPreferredDisplayUnit = null;
            } else {
              _tmpPreferredDisplayUnit = _cursor.getString(_cursorIndexOfPreferredDisplayUnit);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new IngredientEntity(_tmpId,_tmpName,_tmpUnit,_tmpCategory,_tmpSubcategory,_tmpPreferredDisplayUnit,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<IngredientEntity> observeById(final String id) {
    final String _sql = "SELECT * FROM ingredients WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ingredients"}, new Callable<IngredientEntity>() {
      @Override
      @Nullable
      public IngredientEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSubcategory = CursorUtil.getColumnIndexOrThrow(_cursor, "subcategory");
          final int _cursorIndexOfPreferredDisplayUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "preferred_display_unit");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final IngredientEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpSubcategory;
            if (_cursor.isNull(_cursorIndexOfSubcategory)) {
              _tmpSubcategory = null;
            } else {
              _tmpSubcategory = _cursor.getString(_cursorIndexOfSubcategory);
            }
            final String _tmpPreferredDisplayUnit;
            if (_cursor.isNull(_cursorIndexOfPreferredDisplayUnit)) {
              _tmpPreferredDisplayUnit = null;
            } else {
              _tmpPreferredDisplayUnit = _cursor.getString(_cursorIndexOfPreferredDisplayUnit);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new IngredientEntity(_tmpId,_tmpName,_tmpUnit,_tmpCategory,_tmpSubcategory,_tmpPreferredDisplayUnit,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAll(final Continuation<? super List<IngredientEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredients";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientEntity>>() {
      @Override
      @NonNull
      public List<IngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSubcategory = CursorUtil.getColumnIndexOrThrow(_cursor, "subcategory");
          final int _cursorIndexOfPreferredDisplayUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "preferred_display_unit");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<IngredientEntity> _result = new ArrayList<IngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpSubcategory;
            if (_cursor.isNull(_cursorIndexOfSubcategory)) {
              _tmpSubcategory = null;
            } else {
              _tmpSubcategory = _cursor.getString(_cursorIndexOfSubcategory);
            }
            final String _tmpPreferredDisplayUnit;
            if (_cursor.isNull(_cursorIndexOfPreferredDisplayUnit)) {
              _tmpPreferredDisplayUnit = null;
            } else {
              _tmpPreferredDisplayUnit = _cursor.getString(_cursorIndexOfPreferredDisplayUnit);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IngredientEntity(_tmpId,_tmpName,_tmpUnit,_tmpCategory,_tmpSubcategory,_tmpPreferredDisplayUnit,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<IngredientEntity>> observeAll() {
    final String _sql = "SELECT * FROM ingredients";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ingredients"}, new Callable<List<IngredientEntity>>() {
      @Override
      @NonNull
      public List<IngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSubcategory = CursorUtil.getColumnIndexOrThrow(_cursor, "subcategory");
          final int _cursorIndexOfPreferredDisplayUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "preferred_display_unit");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<IngredientEntity> _result = new ArrayList<IngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpSubcategory;
            if (_cursor.isNull(_cursorIndexOfSubcategory)) {
              _tmpSubcategory = null;
            } else {
              _tmpSubcategory = _cursor.getString(_cursorIndexOfSubcategory);
            }
            final String _tmpPreferredDisplayUnit;
            if (_cursor.isNull(_cursorIndexOfPreferredDisplayUnit)) {
              _tmpPreferredDisplayUnit = null;
            } else {
              _tmpPreferredDisplayUnit = _cursor.getString(_cursorIndexOfPreferredDisplayUnit);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IngredientEntity(_tmpId,_tmpName,_tmpUnit,_tmpCategory,_tmpSubcategory,_tmpPreferredDisplayUnit,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByCategory(final String category,
      final Continuation<? super List<IngredientEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredients WHERE category = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientEntity>>() {
      @Override
      @NonNull
      public List<IngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSubcategory = CursorUtil.getColumnIndexOrThrow(_cursor, "subcategory");
          final int _cursorIndexOfPreferredDisplayUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "preferred_display_unit");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<IngredientEntity> _result = new ArrayList<IngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpSubcategory;
            if (_cursor.isNull(_cursorIndexOfSubcategory)) {
              _tmpSubcategory = null;
            } else {
              _tmpSubcategory = _cursor.getString(_cursorIndexOfSubcategory);
            }
            final String _tmpPreferredDisplayUnit;
            if (_cursor.isNull(_cursorIndexOfPreferredDisplayUnit)) {
              _tmpPreferredDisplayUnit = null;
            } else {
              _tmpPreferredDisplayUnit = _cursor.getString(_cursorIndexOfPreferredDisplayUnit);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IngredientEntity(_tmpId,_tmpName,_tmpUnit,_tmpCategory,_tmpSubcategory,_tmpPreferredDisplayUnit,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object search(final String query,
      final Continuation<? super List<IngredientEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredients WHERE name LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientEntity>>() {
      @Override
      @NonNull
      public List<IngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSubcategory = CursorUtil.getColumnIndexOrThrow(_cursor, "subcategory");
          final int _cursorIndexOfPreferredDisplayUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "preferred_display_unit");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<IngredientEntity> _result = new ArrayList<IngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpSubcategory;
            if (_cursor.isNull(_cursorIndexOfSubcategory)) {
              _tmpSubcategory = null;
            } else {
              _tmpSubcategory = _cursor.getString(_cursorIndexOfSubcategory);
            }
            final String _tmpPreferredDisplayUnit;
            if (_cursor.isNull(_cursorIndexOfPreferredDisplayUnit)) {
              _tmpPreferredDisplayUnit = null;
            } else {
              _tmpPreferredDisplayUnit = _cursor.getString(_cursorIndexOfPreferredDisplayUnit);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IngredientEntity(_tmpId,_tmpName,_tmpUnit,_tmpCategory,_tmpSubcategory,_tmpPreferredDisplayUnit,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByName(final String name,
      final Continuation<? super IngredientEntity> $completion) {
    final String _sql = "SELECT * FROM ingredients WHERE LOWER(name) = LOWER(?) LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IngredientEntity>() {
      @Override
      @Nullable
      public IngredientEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSubcategory = CursorUtil.getColumnIndexOrThrow(_cursor, "subcategory");
          final int _cursorIndexOfPreferredDisplayUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "preferred_display_unit");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final IngredientEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpSubcategory;
            if (_cursor.isNull(_cursorIndexOfSubcategory)) {
              _tmpSubcategory = null;
            } else {
              _tmpSubcategory = _cursor.getString(_cursorIndexOfSubcategory);
            }
            final String _tmpPreferredDisplayUnit;
            if (_cursor.isNull(_cursorIndexOfPreferredDisplayUnit)) {
              _tmpPreferredDisplayUnit = null;
            } else {
              _tmpPreferredDisplayUnit = _cursor.getString(_cursorIndexOfPreferredDisplayUnit);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new IngredientEntity(_tmpId,_tmpName,_tmpUnit,_tmpCategory,_tmpSubcategory,_tmpPreferredDisplayUnit,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getChangedSince(final long timestamp,
      final Continuation<? super List<IngredientEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredients WHERE updated_at > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientEntity>>() {
      @Override
      @NonNull
      public List<IngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSubcategory = CursorUtil.getColumnIndexOrThrow(_cursor, "subcategory");
          final int _cursorIndexOfPreferredDisplayUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "preferred_display_unit");
          final int _cursorIndexOfCreatedInLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "created_in_language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<IngredientEntity> _result = new ArrayList<IngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpSubcategory;
            if (_cursor.isNull(_cursorIndexOfSubcategory)) {
              _tmpSubcategory = null;
            } else {
              _tmpSubcategory = _cursor.getString(_cursorIndexOfSubcategory);
            }
            final String _tmpPreferredDisplayUnit;
            if (_cursor.isNull(_cursorIndexOfPreferredDisplayUnit)) {
              _tmpPreferredDisplayUnit = null;
            } else {
              _tmpPreferredDisplayUnit = _cursor.getString(_cursorIndexOfPreferredDisplayUnit);
            }
            final String _tmpCreatedInLanguage;
            _tmpCreatedInLanguage = _cursor.getString(_cursorIndexOfCreatedInLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IngredientEntity(_tmpId,_tmpName,_tmpUnit,_tmpCategory,_tmpSubcategory,_tmpPreferredDisplayUnit,_tmpCreatedInLanguage,_tmpCreatedAt,_tmpUpdatedAt);
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
