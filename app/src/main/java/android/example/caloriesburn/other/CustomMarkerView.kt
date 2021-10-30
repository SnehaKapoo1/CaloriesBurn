package android.example.caloriesburn.other

import android.annotation.SuppressLint
import android.content.Context
import android.example.caloriesburn.databinding.MarkerViewBinding
import android.example.caloriesburn.db.Run
import android.view.LayoutInflater
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class CustomMarkerView(
    val runs: List<Run>,
    c: Context,
    layoutId: Int
): MarkerView(c, layoutId){

    private var _binding: MarkerViewBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = MarkerViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null){
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar = Calendar.getInstance().apply{
            timeInMillis = run.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
       binding.markerTvDate.text= dateFormat.format(calendar.time)

        val avgSpeed = "${run.avgSpeedInKMH}km/h"
        binding.markerTvAvgSpeed.text = avgSpeed

        val distanceInKm = "${run.distanceInMaters/ 1000f}km"
        binding.markerTvDistance.text = distanceInKm

        binding.markerTvDuration.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMills)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        binding.markerTvCaloriesBurned.text = caloriesBurned
    }
}



