package android.example.caloriesburn.repository

import android.example.caloriesburn.db.Run
import android.example.caloriesburn.db.RunDao
import javax.inject.Inject

class RunRepository @Inject constructor(
    private val runDao: RunDao
) {

    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedDate() = runDao.getAllRunsSortedDate()

    fun getAllRunsSortedTimeInMilliSec() = runDao.getAllRunsSortedTimeInMilliSec()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

}