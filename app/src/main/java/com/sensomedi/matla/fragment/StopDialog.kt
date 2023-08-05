package com.sensomedi.matla.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.sensomedi.matla.databinding.DialogStopBinding

class StopDialog : DialogFragment() {

    private lateinit var binding: DialogStopBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogStopBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initView()

        return view
    }

    private fun initView() = with(binding) {

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()

        val params = dialog?.window?.attributes
        val windowManager = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = windowManager.currentWindowMetricsPointCompat()
        val deviceWidth = size.x

        params?.width = (deviceWidth * 0.75).toInt()

        dialog?.window?.attributes = params as WindowManager.LayoutParams

    }

    private fun WindowManager.currentWindowMetricsPointCompat(): Point {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsets = currentWindowMetrics.windowInsets
            var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
            windowInsets.displayCutout?.run {
                insets = Insets.max(
                    insets, Insets.of(0, 0, 500, 500)
                )
            }
            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom
            Point(
                currentWindowMetrics.bounds.width() - insetsWidth,
                currentWindowMetrics.bounds.height() - insetsHeight
            )
        } else {
            Point().apply {
                defaultDisplay.getSize(this)
            }
        }
    }
}