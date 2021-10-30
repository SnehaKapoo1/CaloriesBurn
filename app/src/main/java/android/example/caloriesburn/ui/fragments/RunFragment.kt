package android.example.caloriesburn.ui.fragments

import android.Manifest
import android.example.caloriesburn.R
import android.example.caloriesburn.adapters.RunAdapter
import android.example.caloriesburn.databinding.FragmentRunFragmentBinding
import android.example.caloriesburn.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import android.example.caloriesburn.other.SortType
import android.example.caloriesburn.other.TrackingUtility
import android.example.caloriesburn.ui.viewmodels.RunViewModel
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run_fragment), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentRunFragmentBinding

    private val viewModel: RunViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requestLocationPermission()

        binding = FragmentRunFragmentBinding.inflate(inflater, container, false)

        setupRecyclerView()

        when (viewModel.sortType) {
            SortType.DATE -> binding.spFilter.setSelection(0)
            SortType.RUNNING_TIME -> binding.spFilter.setSelection(1)
            SortType.DISTANCE -> binding.spFilter.setSelection(2)
            SortType.AVG_SPEED -> binding.spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> binding.spFilter.setSelection(4)
        }

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                when (pos) {
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                    3 -> viewModel.sortRuns(SortType.AVG_SPEED)
                    4 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }


        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
        return binding?.root
    }

    private fun setupRecyclerView() = binding.rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestLocationPermission() {
        if (TrackingUtility.hasLocationPermission(requireContext())) {
            return
        }
        EasyPermissions.requestPermissions(
            this,
            "This application cannot work without Location Permission.",
            REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        requestBackgroundPermission()
        /* fun shouldShowRequestPermissionRationale(permission: String): Boolean {

             return super.shouldShowRequestPermissionRationale(permission)
         }*/
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundPermission() {
        val backgroundPermission = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        AlertDialog.Builder(requireContext()).setTitle("Background Location Permission")
            .setMessage("Allow location permission to access this device's location all the time")
            .setPositiveButton("allow") { _, _ ->
                requestPermissions(backgroundPermission, REQUEST_CODE_LOCATION_PERMISSION)
            }.setNegativeButton("deny") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()

    }

    /*private fun requestBackgroundPermission() {
        // optional implementation of shouldShowRequestPermissionRationale
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            AlertDialog.Builder(requireContext())
                .setMessage("Need location permission to get current place")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // ActivityCompat.requestPermissions(activity!!, locationPermissions, REQUEST_LOCATION_PERMISSION)
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQUEST_CODE_LOCATION_PERMISSION)
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        else {
            // ActivityCompat.requestPermissions(activity!!, locationPermissions, REQUEST_LOCATION_PERMISSION)
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION_PERMISSION)
        }}
*/
    /*private fun requestPermissions(){
        when {
            ContextCompat.checkSelfPermission(
                CONTEXT,
                Manifest.permission.REQUESTED_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                performAction(...)
            }
            shouldShowRequestPermissionRationale(...) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            showInContextUI(...)
        }
            else -> {
                // You can directly ask for the permission.
                requestPermissions(CONTEXT,
                    arrayOf(Manifest.permission.REQUESTED_PERMISSION),
                    REQUEST_CODE)
            }
        }
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /*private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermission(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{

            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }*/


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }
}










