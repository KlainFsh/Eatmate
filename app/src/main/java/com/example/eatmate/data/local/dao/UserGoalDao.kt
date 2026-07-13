package com.example.eatmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eatmate.data.local.entity.UserGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserGoalDao {
    @Query("SELECT * FROM user_goal WHERE id = 1")
    fun observeGoal(): Flow<UserGoalEntity?>

    @Query("SELECT * FROM user_goal WHERE id = 1")
    suspend fun getGoal(): UserGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoal(goal: UserGoalEntity)
}
