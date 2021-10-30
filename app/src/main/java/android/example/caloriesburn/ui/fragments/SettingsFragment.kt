package android.example.caloriesburn.ui.fragments

import android.content.SharedPreferences
import android.example.caloriesburn.R
import android.example.caloriesburn.databinding.FragmentSettingsBinding
import android.example.caloriesburn.other.Constants.KEY_NAME
import android.example.caloriesburn.other.Constants.KEY_WEIGHT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings){

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFieldsFromSharedPref()
        binding.applyChanges.setOnClickListener{
            val success = applyChangesToSharedPref()
            if(success){
                Snackbar.make(requireView(), "Saved changes", Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(requireView(), "Please fill out all the fields", Snackbar.LENGTH_LONG).show()
            }
        }

    }
    private fun loadFieldsFromSharedPref(){
        val name = sharedPreferences.getString(KEY_NAME, "")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 80f)
        binding.edtNameStgFrag.setText(name)
        binding.edtWeightStgFrag.setText(weight.toString())
    }

    private fun applyChangesToSharedPref(): Boolean{

        val nameText = binding.edtNameStgFrag.text.toString()
        val weightText = binding.edtWeightStgFrag.text.toString()
        if(nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME,nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Let's go $nameText"
        requireActivity().findViewById<MaterialTextView>(R.id.tvToolbarTitle).text = toolbarText
        return true
    }
}