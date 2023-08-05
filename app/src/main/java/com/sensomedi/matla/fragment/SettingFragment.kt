package com.sensomedi.matla.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sensomedi.data.User
import com.sensomedi.matla.MainViewModel
import com.sensomedi.matla.MainActivity
import com.sensomedi.matla.R
import com.sensomedi.matla.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var viewModel: MainViewModel

    private val times = arrayOf(
        "1 hour",
        "2 hours",
        "3 hours",
        "4 hours",
        "5 hours",
        "6 hours",
        "7 hours",
        "8 hours",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        initView()


        return binding.root
    }

    private fun initView() = with(binding) {
        val main = activity as MainActivity

        val signOutDialog = AlertDialog.Builder(requireContext())
            .setTitle("Sign Out")
            .setMessage("Do you want to sign out")
            .setPositiveButton(
                "ok"
            ) { _, _ ->
                main.signOut()
            }
            .setNegativeButton(
                "cancel"
            ) { _, _ ->

            }.create()

        signOutTv.setOnClickListener {
            signOutDialog.show()
        }

        saveDataBtn.setOnClickListener {

            if (settingHeightEt.text.isEmpty() || settingWeightEt.text.isEmpty() || settingAgeEt.text.isEmpty()) {
                Toast.makeText(requireContext(), "Invalid Data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            main.saveData(
                settingHeightEt.text.toString().toInt(),
                settingWeightEt.text.toString().toInt(),
                settingAgeEt.text.toString().toInt()
            )

        }

        val txtEdit = EditText(requireContext())
        txtEdit.hint = viewModel.user.value?.email
        txtEdit.maxLines = 1
        txtEdit.setPadding(65, 0, 0, 30)

        val alert = AlertDialog.Builder(requireContext())
            .setTitle("Withdraw")
            .setView(txtEdit)
            .setMessage("Are you sure you want to withdraw?\nWrite your email")
            .setPositiveButton(
                "ok"
            ) { _, _ ->
                if (viewModel.user.value?.email != txtEdit.text.toString()) {
                    Toast.makeText(requireContext(), "Email is not correct", Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }
                main.withdraw()
            }
            .setNegativeButton(
                "cancel"
            ) { _, _ ->

            }.create()

        withdrawBtn.setOnClickListener {
            txtEdit.setText("")
            alert.show()
        }

        viewModel.user.observe(viewLifecycleOwner) {
            settingHeightEt.setText(it.height.toString())
            settingWeightEt.setText(it.weight.toString())
            settingAgeEt.setText(it.age.toString())
        }

        val spinner: Spinner = binding.settingMeasureTimeSp
        // 글자 하나만 표시되는 ArrayAdapter의 경우 기본 제공되는 Adapter를 사용할 수 있음
        // 글자 하나만 표시되는 ArrayAdapter의 경우 기본 제공되는 Adapter를 사용할 수 있음
        val adapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(requireContext(), R.layout.spinner_measure_time, times)
        adapter.setDropDownViewResource(R.layout.spinner_measure_time)
        spinner.adapter = adapter
        spinner.setSelection(1)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                when (position) {
                    0 -> {
                        viewModel.setMeasureTime(1000 * 60 * 60 * 1)
                    }
                    1 -> {
                        viewModel.setMeasureTime(1000 * 60 * 60 * 2)
                    }
                    2 -> {
                        viewModel.setMeasureTime(1000 * 60 * 60 * 3)
                    }
                    3 -> {
                        viewModel.setMeasureTime(1000 * 60 * 60 * 4)
                    }
                    4 -> {
                        viewModel.setMeasureTime(1000 * 60 * 60 * 5)
                    }
                    5 -> {
                        viewModel.setMeasureTime(1000 * 60 * 60 * 6)
                    }
                    6 -> {
                        viewModel.setMeasureTime(1000 * 60 * 60 * 7)
                    }
                    7 -> {
                        viewModel.setMeasureTime(1000 * 60 * 60 * 8)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }
    }

}