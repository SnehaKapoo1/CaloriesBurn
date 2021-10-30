package android.example.caloriesburn.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.example.caloriesburn.R
import android.example.caloriesburn.databinding.ActivityMainBinding
import android.example.caloriesburn.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var findNavController: NavController

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)

        /* val runFragment = RunFragment()
        val statisticsFragment = StatisticFragment()
        val settingsFragment = SettingsFragment()
        setCurrentFragment(runFragment)

        binding.bottomNavigationView.onTabSelected = {
            when (it.id) {
                R.id.runFragment -> setCurrentFragment(runFragment)
                R.id.statisticFragment -> setCurrentFragment(statisticsFragment)
                R.id.settingsFragment -> setCurrentFragment(settingsFragment)
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
*/
       /* val navController: NavController =
            Navigation.findNavController(this, android.example.caloriesburn.R.id.nav_host_fragment)
        val bottomNavigationView =
            findViewById<BottomNavigationView>(android.example.caloriesburn.R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        navController.addOnDestinationChangedListener {
                _, destination, _ ->
            Log.d("MAIN","Main activity is running successfully ")

            when(destination.id){
                R.id.settingsFragment, R.id.runFragment, R.id.statisticFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    binding.tvToolbarTitle.visibility = View.VISIBLE
                }
                else -> {
                    bottomNavigationView.visibility = View.GONE
                    binding.tvToolbarTitle.visibility = View.GONE
                }
            }

        }*/

        findNavController = findNavController(R.id.nav_host_fragment)

       binding.bottomNavigationView.setupWithNavController(findNavController)
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /* NO-OP */ }

        navigateToTrackingFragmentIfNeeded(intent)

        findNavController.addOnDestinationChangedListener {
                _, destination, _ ->
            Log.d("MAIN","Main activity is running successfully ")

            when(destination.id){
                R.id.setupFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.appBarLayout.visibility = View.GONE
                }
                R.id.trackingFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.appBarLayout.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.appBarLayout.visibility = View.VISIBLE
                }
            }

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }


    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            findNavController.navigate(R.id.action_global_trackingFragment)
        }
    }

}



