package android.example.caloriesburn.ui.fragments


import android.content.Intent
import android.example.caloriesburn.R
import android.example.caloriesburn.databinding.FragmentTrackingBinding
import android.example.caloriesburn.db.Run
import android.example.caloriesburn.other.Constants.ACTION_PAUSE_SERVICE
import android.example.caloriesburn.other.Constants.ACTION_START_OR_RESUME_SERVICE
import android.example.caloriesburn.other.Constants.ACTION_STOP_SERVICE
import android.example.caloriesburn.other.Constants.MAP_ZOOM
import android.example.caloriesburn.other.Constants.POLYLINE_COLOR
import android.example.caloriesburn.other.Constants.POLYLINE_WIDTH
import android.example.caloriesburn.other.TrackingUtility
import android.example.caloriesburn.services.PolyLine
import android.example.caloriesburn.services.TrackingServices
import android.example.caloriesburn.ui.viewmodels.RunViewModel
import android.os.Bundle
import android.util.Log

import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking){
    private lateinit var binding: FragmentTrackingBinding

    private val viewModel : RunViewModel by viewModels()
    private var isTracking = false
    private var pathPoints = mutableListOf<PolyLine>()

    private var map: GoogleMap? = null

    private var curTimeInMillis = 0L
    private var menu: Menu? = null

    @set:Inject
    var weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)

        binding.btnToggleRun.setOnClickListener{
            toggleRun()
        }

        binding.btnFinishRun.setOnClickListener{
            zoomToSeeWholeTrack()
            endRunSaveToDb()
        }

        binding.mapView.getMapAsync{
            map = it
            addAllPolylines()
        }

        subscribeToObservers()
    }

    /* SUBSCRIBE TO OUR OBSERVER TO SUBSCRIBE TO THE LIVE DATA OBJECTS*/
    private fun subscribeToObservers() {
        TrackingServices.isTracking.observe(viewLifecycleOwner,  {
            updateTracking(it)
        })

        TrackingServices.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingServices.timeRunInMillis.observe(viewLifecycleOwner, {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        })
    }


     /*TOGGLE OUR RUN SO JUST TO START OUR TRACKING SERVICE
     IF IT IS CURRENTLY IN THE STOPPED OR PAUSE STATE*/
    private fun toggleRun(){
      if(isTracking){
          menu?.getItem(0)?.isVisible = true
          sendCommandToService(ACTION_PAUSE_SERVICE)
      }else{
          sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
      }
    }


   /* WANT TO UPDATE OUR UI REGARDING THIS IS TRACKING STATE*/
   private fun updateTracking(isTracking: Boolean) {
       this.isTracking = isTracking
       if (!isTracking) {
           binding.btnToggleRun.text = "Start"
           binding.btnFinishRun.visibility = View.VISIBLE
       } else {
           binding.btnToggleRun.text = "Stop"
           menu?.getItem(0)?.isVisible = true
           binding.btnFinishRun.visibility = View.GONE
       }
   }


    /*function that moves the camera to the users position whenever
    * there is a new position in our polyline list in our path point
    * list */
    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
               CameraUpdateFactory.newLatLngZoom(
                  pathPoints.last().last(),
                  MAP_ZOOM
               )
            )
        }
    }


    private fun zoomToSeeWholeTrack(){
        Log.d("zoom", "starting")
       val bounds =  LatLngBounds.Builder()
        for(polyline in pathPoints){
           for(pos in polyline){
               bounds.include(pos)
           }
            Log.d("zoom", "For loop")
        }
        Log.d("zoom", "end")

        val width = binding.mapView.width
        val height = binding.mapView.height

        /*val latLngBounds = try {

        } catch (e: IllegalStateException) {
            Timber.e(e, "Cannot find any path points, associated with this run")
            return
        }*/

        map?.moveCamera(
           CameraUpdateFactory.newLatLngBounds(
               bounds.build(),
               width,
               height,
               (height * 0.05f).toInt()
           )
        )
        Log.d("zoom", "map")
    }

    private fun endRunSaveToDb(){
       map?.snapshot { bmp ->
          var distanceInMeters = 0
           for(polyline in pathPoints){
               distanceInMeters += TrackingUtility.CalculatePolyLineDistanceLength(polyline).toInt()
           }
           val avgSpeed = round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60 ) * 10) / 10f
           val dateTimeStamp = Calendar.getInstance().timeInMillis
           val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
           val run = Run(bmp, dateTimeStamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)
           viewModel.insertRun(run)
           Snackbar.make(
               requireActivity().findViewById(R.id.rootView),
               "Run saved successfully",
               Snackbar.LENGTH_LONG
           ).show()
           stopRun()
       }
    }

    // add coordinates when user rotate the screen
    private fun addAllPolylines() {
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)

                map?.addPolyline(polylineOptions)
        }
    }

    //tO ADD LAST points for our paths
    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
           val preLastLatlng = pathPoints.last() [pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatlng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }


    private fun sendCommandToService(action: String){
        Intent(requireContext(), TrackingServices::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    override fun onResume()
    {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart(){
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop(){
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause(){
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

   override fun onPrepareOptionsMenu(menu: Menu){
       super.onPrepareOptionsMenu(menu)
       if(curTimeInMillis > 0L){
           this.menu?.getItem(0)?.isVisible = true
       }
   }

    private fun showCancelTrackingDialog(){
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete_new)
            .setPositiveButton("Yes") { _, _->
                stopRun()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }

    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    //show the cancel dialog
    //8:14
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
        R.id.miCloseTracking-> {
                showCancelTrackingDialog()
          }
        }
        return super.onOptionsItemSelected(item)
    }

}