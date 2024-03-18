package com.example.modul3_2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.modul3_2.R
import com.example.modul3_2.data.local.datastore.SettingPreferences
import com.example.modul3_2.data.local.datastore.dataStore
import com.example.modul3_2.databinding.FragmentSettingBinding
import com.example.modul3_2.ui.viewmodel.SettingViewModel
import com.example.modul3_2.utils.SettingViewModelFactory

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding ?= null
    private val binding get() = _binding!!

    private lateinit var viewModel: SettingViewModel

    private lateinit var pref: SettingPreferences

    private lateinit var switchSetting: SwitchCompat
    private lateinit var tvSetting: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(layoutInflater, container, false)

        binding.apply {
            switchSetting = switch1
            tvSetting = tvMode
        }

        pref = SettingPreferences.getInstance(requireActivity().application.dataStore)

        viewModel = ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        viewModel.getThemeSettings().observe(viewLifecycleOwner){ isDarkModeActive ->
            if (isDarkModeActive){
                switchSetting.isChecked = true
                tvSetting.text = resources.getString(R.string.lightMode_on)
            }else{
                switchSetting.isChecked = false
                tvSetting.text = resources.getString(R.string.darkMode_on)
            }
        }

        switchSetting.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }

        return binding.root
    }
}