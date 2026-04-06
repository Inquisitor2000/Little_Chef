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
import com.familymealplanner.data.local.entity.MealPlanEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class MealPlanDao_Impl implements MealPlanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MealPlanEntity> __insertionAdapterOfMealPlanEntity;

  private final EntityDeletionOrUpdateAdapter<MealPlanEntity> __deletionAdapterOfMealPlanEntity;

  private final EntityDeletionOrUpdateAdapter<MealPlanEntity> __updateAdapterOfMealPlanEntity;

  public MealPlanDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMealPlanEntity = new EntityInsertionAdapter<MealPlanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `meal_plans` (`id`,`meal_id`,`planned_date`,`meal_type`,`status`,`started_at`,`completed_at`,`created_at`,`updated_at`,`ingredient_substitutions`,`planned_servings`,`adjusted_prep_time_minutes`,`adjusted_cook_time_minutes`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealPlanEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getMealId());
        statement.bindLong(3, entity.getPlannedDate());
        statement.bindString(4, entity.getMealType());
        statement.bindString(5, entity.getStatus());
        if (entity.getStartedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getStartedAt());
        }
        if (entity.getCompletedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCompletedAt());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
        statement.bindString(10, entity.getIngredientSubstitutions());
        if (entity.getPlannedServings() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPlannedServings());
        }
        if (entity.getAdjustedPrepTimeMinutes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getAdjustedPrepTimeMinutes());
        }
        if (entity.getAdjustedCookTimeMinutes() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getAdjustedCookTimeMinutes());
        }
      }
    };
    this.__deletionAdapterOfMealPlanEntity = new EntityDeletionOrUpdateAdapter<MealPlanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `meal_plans` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealPlanEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfMealPlanEntity = new EntityDeletionOrUpdateAdapter<MealPlanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `meal_plans` SET `id` = ?,`meal_id` = ?,`planned_date` = ?,`meal_type` = ?,`status` = ?,`started_at` = ?,`completed_at` = ?,`created_at` = ?,`updated_at` = ?,`ingredient_substitutions` = ?,`planned_servings` = ?,`adjusted_prep_time_minutes` = ?,`adjusted_cook_time_minutes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealPlanEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getMealId());
        statement.bindLong(3, entity.getPlannedDate());
        statement.bindString(4, entity.getMealType());
        statement.bindString(5, entity.getStatus());
        if (entity.getStartedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getStartedAt());
        }
        if (entity.getCompletedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCompletedAt());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
        statement.bindString(10, entity.getIngredientSubstitutions());
        if (entity.getPlannedServings() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPlannedServings());
        }
        if (entity.getAdjustedPrepTimeMinutes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getAdjustedPrepTimeMinutes());
        }
        if (entity.getAdjustedCookTimeMinutes() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getAdjustedCookTimeMinutes());
        }
        statement.bindString(14, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final MealPlanEntity mealPlan,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMealPlanEntity.insert(mealPlan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MealPlanEntity mealPlan,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMealPlanEntity.handle(mealPlan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MealPlanEntity mealPlan,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMealPlanEntity.handle(mealPlan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id, final Continuation<? super MealPlanEntity> $completion) {
    final String _sql = "SELECT * FROM meal_plans WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MealPlanEntity>() {
      @Override
      @Nullable
      public MealPlanEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIngredientSubstitutions = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_substitutions");
          final int _cursorIndexOfPlannedServings = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_servings");
          final int _cursorIndexOfAdjustedPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_prep_time_minutes");
          final int _cursorIndexOfAdjustedCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_cook_time_minutes");
          final MealPlanEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final long _tmpPlannedDate;
            _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpIngredientSubstitutions;
            _tmpIngredientSubstitutions = _cursor.getString(_cursorIndexOfIngredientSubstitutions);
            final Integer _tmpPlannedServings;
            if (_cursor.isNull(_cursorIndexOfPlannedServings)) {
              _tmpPlannedServings = null;
            } else {
              _tmpPlannedServings = _cursor.getInt(_cursorIndexOfPlannedServings);
            }
            final Integer _tmpAdjustedPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedPrepTimeMinutes)) {
              _tmpAdjustedPrepTimeMinutes = null;
            } else {
              _tmpAdjustedPrepTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedPrepTimeMinutes);
            }
            final Integer _tmpAdjustedCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedCookTimeMinutes)) {
              _tmpAdjustedCookTimeMinutes = null;
            } else {
              _tmpAdjustedCookTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedCookTimeMinutes);
            }
            _result = new MealPlanEntity(_tmpId,_tmpMealId,_tmpPlannedDate,_tmpMealType,_tmpStatus,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIngredientSubstitutions,_tmpPlannedServings,_tmpAdjustedPrepTimeMinutes,_tmpAdjustedCookTimeMinutes);
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
  public Flow<MealPlanEntity> observeById(final String id) {
    final String _sql = "SELECT * FROM meal_plans WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meal_plans"}, new Callable<MealPlanEntity>() {
      @Override
      @Nullable
      public MealPlanEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIngredientSubstitutions = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_substitutions");
          final int _cursorIndexOfPlannedServings = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_servings");
          final int _cursorIndexOfAdjustedPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_prep_time_minutes");
          final int _cursorIndexOfAdjustedCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_cook_time_minutes");
          final MealPlanEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final long _tmpPlannedDate;
            _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpIngredientSubstitutions;
            _tmpIngredientSubstitutions = _cursor.getString(_cursorIndexOfIngredientSubstitutions);
            final Integer _tmpPlannedServings;
            if (_cursor.isNull(_cursorIndexOfPlannedServings)) {
              _tmpPlannedServings = null;
            } else {
              _tmpPlannedServings = _cursor.getInt(_cursorIndexOfPlannedServings);
            }
            final Integer _tmpAdjustedPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedPrepTimeMinutes)) {
              _tmpAdjustedPrepTimeMinutes = null;
            } else {
              _tmpAdjustedPrepTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedPrepTimeMinutes);
            }
            final Integer _tmpAdjustedCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedCookTimeMinutes)) {
              _tmpAdjustedCookTimeMinutes = null;
            } else {
              _tmpAdjustedCookTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedCookTimeMinutes);
            }
            _result = new MealPlanEntity(_tmpId,_tmpMealId,_tmpPlannedDate,_tmpMealType,_tmpStatus,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIngredientSubstitutions,_tmpPlannedServings,_tmpAdjustedPrepTimeMinutes,_tmpAdjustedCookTimeMinutes);
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
  public Object getAll(final Continuation<? super List<MealPlanEntity>> $completion) {
    final String _sql = "SELECT * FROM meal_plans";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealPlanEntity>>() {
      @Override
      @NonNull
      public List<MealPlanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIngredientSubstitutions = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_substitutions");
          final int _cursorIndexOfPlannedServings = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_servings");
          final int _cursorIndexOfAdjustedPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_prep_time_minutes");
          final int _cursorIndexOfAdjustedCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_cook_time_minutes");
          final List<MealPlanEntity> _result = new ArrayList<MealPlanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealPlanEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final long _tmpPlannedDate;
            _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpIngredientSubstitutions;
            _tmpIngredientSubstitutions = _cursor.getString(_cursorIndexOfIngredientSubstitutions);
            final Integer _tmpPlannedServings;
            if (_cursor.isNull(_cursorIndexOfPlannedServings)) {
              _tmpPlannedServings = null;
            } else {
              _tmpPlannedServings = _cursor.getInt(_cursorIndexOfPlannedServings);
            }
            final Integer _tmpAdjustedPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedPrepTimeMinutes)) {
              _tmpAdjustedPrepTimeMinutes = null;
            } else {
              _tmpAdjustedPrepTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedPrepTimeMinutes);
            }
            final Integer _tmpAdjustedCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedCookTimeMinutes)) {
              _tmpAdjustedCookTimeMinutes = null;
            } else {
              _tmpAdjustedCookTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedCookTimeMinutes);
            }
            _item = new MealPlanEntity(_tmpId,_tmpMealId,_tmpPlannedDate,_tmpMealType,_tmpStatus,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIngredientSubstitutions,_tmpPlannedServings,_tmpAdjustedPrepTimeMinutes,_tmpAdjustedCookTimeMinutes);
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
  public Flow<List<MealPlanEntity>> observeAll() {
    final String _sql = "SELECT * FROM meal_plans";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meal_plans"}, new Callable<List<MealPlanEntity>>() {
      @Override
      @NonNull
      public List<MealPlanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIngredientSubstitutions = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_substitutions");
          final int _cursorIndexOfPlannedServings = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_servings");
          final int _cursorIndexOfAdjustedPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_prep_time_minutes");
          final int _cursorIndexOfAdjustedCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_cook_time_minutes");
          final List<MealPlanEntity> _result = new ArrayList<MealPlanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealPlanEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final long _tmpPlannedDate;
            _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpIngredientSubstitutions;
            _tmpIngredientSubstitutions = _cursor.getString(_cursorIndexOfIngredientSubstitutions);
            final Integer _tmpPlannedServings;
            if (_cursor.isNull(_cursorIndexOfPlannedServings)) {
              _tmpPlannedServings = null;
            } else {
              _tmpPlannedServings = _cursor.getInt(_cursorIndexOfPlannedServings);
            }
            final Integer _tmpAdjustedPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedPrepTimeMinutes)) {
              _tmpAdjustedPrepTimeMinutes = null;
            } else {
              _tmpAdjustedPrepTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedPrepTimeMinutes);
            }
            final Integer _tmpAdjustedCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedCookTimeMinutes)) {
              _tmpAdjustedCookTimeMinutes = null;
            } else {
              _tmpAdjustedCookTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedCookTimeMinutes);
            }
            _item = new MealPlanEntity(_tmpId,_tmpMealId,_tmpPlannedDate,_tmpMealType,_tmpStatus,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIngredientSubstitutions,_tmpPlannedServings,_tmpAdjustedPrepTimeMinutes,_tmpAdjustedCookTimeMinutes);
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
  public Object getByDateRange(final long startDate, final long endDate,
      final Continuation<? super List<MealPlanEntity>> $completion) {
    final String _sql = "SELECT * FROM meal_plans WHERE planned_date BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealPlanEntity>>() {
      @Override
      @NonNull
      public List<MealPlanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIngredientSubstitutions = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_substitutions");
          final int _cursorIndexOfPlannedServings = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_servings");
          final int _cursorIndexOfAdjustedPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_prep_time_minutes");
          final int _cursorIndexOfAdjustedCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_cook_time_minutes");
          final List<MealPlanEntity> _result = new ArrayList<MealPlanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealPlanEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final long _tmpPlannedDate;
            _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpIngredientSubstitutions;
            _tmpIngredientSubstitutions = _cursor.getString(_cursorIndexOfIngredientSubstitutions);
            final Integer _tmpPlannedServings;
            if (_cursor.isNull(_cursorIndexOfPlannedServings)) {
              _tmpPlannedServings = null;
            } else {
              _tmpPlannedServings = _cursor.getInt(_cursorIndexOfPlannedServings);
            }
            final Integer _tmpAdjustedPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedPrepTimeMinutes)) {
              _tmpAdjustedPrepTimeMinutes = null;
            } else {
              _tmpAdjustedPrepTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedPrepTimeMinutes);
            }
            final Integer _tmpAdjustedCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedCookTimeMinutes)) {
              _tmpAdjustedCookTimeMinutes = null;
            } else {
              _tmpAdjustedCookTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedCookTimeMinutes);
            }
            _item = new MealPlanEntity(_tmpId,_tmpMealId,_tmpPlannedDate,_tmpMealType,_tmpStatus,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIngredientSubstitutions,_tmpPlannedServings,_tmpAdjustedPrepTimeMinutes,_tmpAdjustedCookTimeMinutes);
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
  public Object getByDate(final long date,
      final Continuation<? super List<MealPlanEntity>> $completion) {
    final String _sql = "SELECT * FROM meal_plans WHERE planned_date = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealPlanEntity>>() {
      @Override
      @NonNull
      public List<MealPlanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIngredientSubstitutions = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_substitutions");
          final int _cursorIndexOfPlannedServings = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_servings");
          final int _cursorIndexOfAdjustedPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_prep_time_minutes");
          final int _cursorIndexOfAdjustedCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_cook_time_minutes");
          final List<MealPlanEntity> _result = new ArrayList<MealPlanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealPlanEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final long _tmpPlannedDate;
            _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpIngredientSubstitutions;
            _tmpIngredientSubstitutions = _cursor.getString(_cursorIndexOfIngredientSubstitutions);
            final Integer _tmpPlannedServings;
            if (_cursor.isNull(_cursorIndexOfPlannedServings)) {
              _tmpPlannedServings = null;
            } else {
              _tmpPlannedServings = _cursor.getInt(_cursorIndexOfPlannedServings);
            }
            final Integer _tmpAdjustedPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedPrepTimeMinutes)) {
              _tmpAdjustedPrepTimeMinutes = null;
            } else {
              _tmpAdjustedPrepTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedPrepTimeMinutes);
            }
            final Integer _tmpAdjustedCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedCookTimeMinutes)) {
              _tmpAdjustedCookTimeMinutes = null;
            } else {
              _tmpAdjustedCookTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedCookTimeMinutes);
            }
            _item = new MealPlanEntity(_tmpId,_tmpMealId,_tmpPlannedDate,_tmpMealType,_tmpStatus,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIngredientSubstitutions,_tmpPlannedServings,_tmpAdjustedPrepTimeMinutes,_tmpAdjustedCookTimeMinutes);
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
  public Object getByStatus(final String status,
      final Continuation<? super List<MealPlanEntity>> $completion) {
    final String _sql = "SELECT * FROM meal_plans WHERE status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealPlanEntity>>() {
      @Override
      @NonNull
      public List<MealPlanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIngredientSubstitutions = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_substitutions");
          final int _cursorIndexOfPlannedServings = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_servings");
          final int _cursorIndexOfAdjustedPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_prep_time_minutes");
          final int _cursorIndexOfAdjustedCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_cook_time_minutes");
          final List<MealPlanEntity> _result = new ArrayList<MealPlanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealPlanEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final long _tmpPlannedDate;
            _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpIngredientSubstitutions;
            _tmpIngredientSubstitutions = _cursor.getString(_cursorIndexOfIngredientSubstitutions);
            final Integer _tmpPlannedServings;
            if (_cursor.isNull(_cursorIndexOfPlannedServings)) {
              _tmpPlannedServings = null;
            } else {
              _tmpPlannedServings = _cursor.getInt(_cursorIndexOfPlannedServings);
            }
            final Integer _tmpAdjustedPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedPrepTimeMinutes)) {
              _tmpAdjustedPrepTimeMinutes = null;
            } else {
              _tmpAdjustedPrepTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedPrepTimeMinutes);
            }
            final Integer _tmpAdjustedCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedCookTimeMinutes)) {
              _tmpAdjustedCookTimeMinutes = null;
            } else {
              _tmpAdjustedCookTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedCookTimeMinutes);
            }
            _item = new MealPlanEntity(_tmpId,_tmpMealId,_tmpPlannedDate,_tmpMealType,_tmpStatus,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIngredientSubstitutions,_tmpPlannedServings,_tmpAdjustedPrepTimeMinutes,_tmpAdjustedCookTimeMinutes);
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
      final Continuation<? super List<MealPlanEntity>> $completion) {
    final String _sql = "SELECT * FROM meal_plans WHERE updated_at > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealPlanEntity>>() {
      @Override
      @NonNull
      public List<MealPlanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfPlannedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIngredientSubstitutions = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_substitutions");
          final int _cursorIndexOfPlannedServings = CursorUtil.getColumnIndexOrThrow(_cursor, "planned_servings");
          final int _cursorIndexOfAdjustedPrepTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_prep_time_minutes");
          final int _cursorIndexOfAdjustedCookTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "adjusted_cook_time_minutes");
          final List<MealPlanEntity> _result = new ArrayList<MealPlanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealPlanEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final long _tmpPlannedDate;
            _tmpPlannedDate = _cursor.getLong(_cursorIndexOfPlannedDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpIngredientSubstitutions;
            _tmpIngredientSubstitutions = _cursor.getString(_cursorIndexOfIngredientSubstitutions);
            final Integer _tmpPlannedServings;
            if (_cursor.isNull(_cursorIndexOfPlannedServings)) {
              _tmpPlannedServings = null;
            } else {
              _tmpPlannedServings = _cursor.getInt(_cursorIndexOfPlannedServings);
            }
            final Integer _tmpAdjustedPrepTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedPrepTimeMinutes)) {
              _tmpAdjustedPrepTimeMinutes = null;
            } else {
              _tmpAdjustedPrepTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedPrepTimeMinutes);
            }
            final Integer _tmpAdjustedCookTimeMinutes;
            if (_cursor.isNull(_cursorIndexOfAdjustedCookTimeMinutes)) {
              _tmpAdjustedCookTimeMinutes = null;
            } else {
              _tmpAdjustedCookTimeMinutes = _cursor.getInt(_cursorIndexOfAdjustedCookTimeMinutes);
            }
            _item = new MealPlanEntity(_tmpId,_tmpMealId,_tmpPlannedDate,_tmpMealType,_tmpStatus,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIngredientSubstitutions,_tmpPlannedServings,_tmpAdjustedPrepTimeMinutes,_tmpAdjustedCookTimeMinutes);
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
