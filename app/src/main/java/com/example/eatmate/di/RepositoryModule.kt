package com.example.eatmate.di

import com.example.eatmate.data.repository.FoodAnalysisRepositoryImpl
import com.example.eatmate.data.repository.MealRepositoryImpl
import com.example.eatmate.domain.repository.FoodAnalysisRepository
import com.example.eatmate.domain.repository.MealRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMealRepository(impl: MealRepositoryImpl): MealRepository

    @Binds
    @Singleton
    abstract fun bindFoodAnalysisRepository(impl: FoodAnalysisRepositoryImpl): FoodAnalysisRepository
}
