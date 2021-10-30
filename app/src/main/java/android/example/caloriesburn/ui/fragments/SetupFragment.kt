package android.example.caloriesburn.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.example.caloriesburn.R
import android.example.caloriesburn.databinding.FragmentSetupBinding
import android.example.caloriesburn.other.Constants.KEY_FIRST_TIME_TOGGLE
import android.example.caloriesburn.other.Constants.KEY_NAME
import android.example.caloriesburn.other.Constants.KEY_WEIGHT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    private var _binding: FragmentSetupBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSetupBinding.inflate(inflater, container, false)

        if(!isFirstAppOpen){
          val navOptions = NavOptions.Builder()
              .setPopUpTo(R.id.setupFragment, true)
              .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }

        _binding?.tvContinue?.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if(success){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }else{
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT).show()
            }
            
        }

        return _binding?.root
    }


    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().navigate(R.id.action_setupFragment_to_runFragment)
    }*/

    @SuppressLint("CommitPrefEdits")
    fun writePersonalDataToSharedPref(): Boolean {
        val name = _binding?.edtName?.text.toString()
        val weight = _binding?.weightEditText?.text.toString()
        if(name.isEmpty() || weight.isEmpty()){
           return false
        }
            sharedPref.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight.toFloat())
                .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
                .apply()
        val toolbarText = "Let's go, $name!"
        requireActivity().findViewById<MaterialTextView>(R.id.tvToolbarTitle).text = toolbarText
        return true
    }
}












