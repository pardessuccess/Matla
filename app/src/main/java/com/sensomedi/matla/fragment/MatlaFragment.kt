package com.sensomedi.matla.fragment

import android.app.AlertDialog
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.sensomedi.data.Temporary
import com.sensomedi.matla.MainActivity
import com.sensomedi.matla.MainViewModel
import com.sensomedi.matla.R
import com.sensomedi.matla.databinding.FragmentMatlaBinding


class MatlaFragment : Fragment() {

    private lateinit var binding: FragmentMatlaBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var colorList: IntArray

    private lateinit var lottieAnimation: LottieAnimationView

    private val viewList: List<View> by lazy {
        listOf(
            binding.view1,
            binding.view2,
            binding.view3,
            binding.view4,
            binding.view5,
            binding.view6,
            binding.view7,
            binding.view8,
            binding.view13,
            binding.view14,
            binding.view15,
            binding.view10,
            binding.view11,
            binding.view12,
            binding.view9,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatlaBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        colorList = resources.getIntArray(R.array.color_list)

        lottieAnimation = binding.lottieView
        lottieAnimation.setAnimation("measuring.json")
        lottieAnimation.loop(true)
        lottieAnimation.playAnimation()

        viewModel.isScanning.observe(viewLifecycleOwner) {
            binding.scanBtn.text =
                if (it) "Scanning..." else if (viewModel.isConnected.value!!) "MEASURE" else "START SCAN"
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isMeasuring.observe(viewLifecycleOwner) {
            if (it) {
                binding.scanBtn.setBackgroundResource(R.drawable.bg_logout_btn)
                lottieAnimation.visibility = View.VISIBLE
                binding.scanBtn.text = "STOP Measuring"
            } else {
                binding.scanBtn.setBackgroundResource(R.drawable.bg_scan_btn)
                lottieAnimation.visibility = View.INVISIBLE
                binding.scanBtn.text = "START SCAN"
            }
        }
        initView()

        return binding.root
    }

    private fun initView() = with(binding) {
        val main = activity as MainActivity

        scanBtn.setOnClickListener {
            if (viewModel.isMeasuring.value!!) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Measuring")
                    .setMessage("Do you want to finish measurement?")
                    .setPositiveButton(
                        "ok"
                    ) { _, _ ->
                        main.disconnect()
                        main.save()
                    }
                    .setNegativeButton(
                        "cancel"
                    ) { _, _ -> }
                    .create()
                    .show()
                return@setOnClickListener
            }
            if (viewModel.isConnected.value!!) {
                main.startMeasure()
                viewModel.setMeasure(true)
                return@setOnClickListener
            }
            if (!main.bluetoothAdapter.isEnabled) {
                main.promptEnableBluetooth()
                return@setOnClickListener
            }
            main.startBleScan()
        }
        viewModel.matlaData.observe(viewLifecycleOwner) {
            setData(it)
        }
        view8.setOnClickListener {
            setData(
                Temporary(
                    1L,
                    listOf(
                        100,
                        100,
                        100,
                        100,
                        100,
                        100,
                        100,
                        100,
                        100,
                        100,
                        100,
                        100,
                        100,
                        0,
                        100,
                    )
                )
            )
        }
    }

    private fun setData(data: Temporary) {
        for (i in data.matlaData.indices) {
            when (setStage(data.matlaData[i])) {
                0 -> viewList[i].setBackgroundResource(R.drawable.bg_1)
                1 -> viewList[i].setBackgroundResource(R.drawable.bg_2)
                2 -> viewList[i].setBackgroundResource(R.drawable.bg_3)
                3 -> viewList[i].setBackgroundResource(R.drawable.bg_4)
                4 -> viewList[i].setBackgroundResource(R.drawable.bg_5)
                5 -> viewList[i].setBackgroundResource(R.drawable.bg_6)
                6 -> viewList[i].setBackgroundResource(R.drawable.bg_7)
                7 -> viewList[i].setBackgroundResource(R.drawable.bg_8)
                8 -> viewList[i].setBackgroundResource(R.drawable.bg_9)
                9 -> viewList[i].setBackgroundResource(R.drawable.bg_10)
                10 -> viewList[i].setBackgroundResource(R.drawable.bg_11)
                else -> viewList[i].setBackgroundResource(R.drawable.bg_11)
            }
        }
    }

    private fun setStage(value: Int): Int {
        return value / 23
    }

}