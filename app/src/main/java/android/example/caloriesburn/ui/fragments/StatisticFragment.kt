package android.example.caloriesburn.ui.fragments

import android.example.caloriesburn.R
import android.example.caloriesburn.databinding.FragmentStatisticBinding
import android.example.caloriesburn.other.CustomMarkerView
import android.example.caloriesburn.other.TrackingUtility
import android.example.caloriesburn.ui.viewmodels.StatisticViewModel
import android.graphics.Color
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round
import com.github.mikephil.charting.data.BarEntry as BarEntry

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.fragment_statistic)
{

    private val viewModel : StatisticViewModel by viewModels()

    lateinit var binding: FragmentStatisticBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
     
        binding = FragmentStatisticBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setUpBarChart()
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, {
              it?.let{
                  val km = it / 1000f
                  val totalDistance = round(km * 10f) / 10f
                  val totalDistanceString = "${totalDistance}km"
                  binding.tvTotalDistance.text = totalDistanceString
              }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, {
            it?.let{
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "${avgSpeed}km/h"
                binding.tvAverageSpeed.text = avgSpeedString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, {
            it?.let{
                val totalCaloriesBurned = "${it}kcal"
                binding.tvTotalCalories.text = totalCaloriesBurned
            }

        })

        viewModel.runsSortedByDate.observe(viewLifecycleOwner, {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(),it[i].avgSpeedInKMH) }
                val bardataSet = BarDataSet(allAvgSpeeds, "Avg Speed Over Time").apply {
                    valueTextColor = Color.RED
                    valueTextSize = 8f
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                binding.barChart.data = BarData(bardataSet)
                binding.barChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                binding.barChart.invalidate()
            }
        })
    }

    private fun setUpBarChart(){
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.RED
            textColor = Color.RED
            textSize = 12f
            setDrawGridLines(false)
        }
        binding.barChart.axisLeft.apply {
            axisLineColor = Color.RED
            textColor = Color.RED
            textSize = 12f
            setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply {
            axisLineColor = Color.RED
            textColor = Color.RED
            textSize = 12f
            setDrawGridLines(false)
        }
        binding.barChart.apply{

            description.text = requireContext().getString(R.string.your_records)
            description.textColor = Color.RED
            legend.isEnabled = true
        }
    }
}

















