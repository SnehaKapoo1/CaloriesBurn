package android.example.caloriesburn.ui.viewmodels

import android.example.caloriesburn.repository.RunRepository
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    runRepository: RunRepository
): ViewModel(){

    var totalTimeRun = runRepository.getTotalTimeInMillis()
    var totalDistance = runRepository.getTotalDistance()
    var totalCaloriesBurned = runRepository.getTotalCaloriesBurned()
    var totalAvgSpeed = runRepository.getTotalAvgSpeed()

    var runsSortedByDate = runRepository.getAllRunsSortedDate()
}