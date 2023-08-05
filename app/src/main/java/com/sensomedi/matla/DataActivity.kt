package com.sensomedi.matla

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.sensomedi.data.Detail
import com.sensomedi.data.MatlaData
import com.sensomedi.data.User
import com.sensomedi.matla.databinding.ActivityDataBinding
import java.util.*
import kotlin.math.abs


class DataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataBinding
    private lateinit var colorList: IntArray

    private var data = mutableListOf<MatlaData>()
    private val entries = mutableListOf<BarEntry>()

    private val average = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    private val viewList: List<View> by lazy {
        with(binding) {
            listOf(
                view1,
                view2,
                view3,
                view4,
                view5,
                view6,
                view7,
                view8,
                view13,
                view14,
                view15,
                view10,
                view11,
                view12,
                view9,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        colorList = resources.getIntArray(R.array.color_list)

        val unix_seconds: Long = 1372339860
        //convert seconds to milliseconds
        //convert seconds to milliseconds
        val date = Date(unix_seconds * 1000L)


        val detailData = intent.getSerializableExtra("hi")
        val userData = intent.getSerializableExtra("user")

        val detail = detailData as Detail
        val user = userData as User
        val times = abs(detail.arrayList[0].date - detail.arrayList.last().date) / 1000
        println("@@@$detail")
        data = detail.arrayList as MutableList<MatlaData>

        val hours = times / 3600
        val mins = (times % 3600) / 60
        val secs = times / 60

        println("$hours hour $mins mins $secs secs")

        println(data + "@@@@@")

        binding.dataMeasureTimeTv.text = String.format("$hours : %02d", mins)
        binding.dataHeightTv.text = user.height.toString() + " \""
        binding.dataWeightTv.text = user.weight.toString() + " lb"

        println(user)
        initView()
    }

    private fun initView() = with(binding) {
        dataBackBtn.setOnClickListener { finish() }
        setMattress()
//        dataLogoIv.setOnClickListener {
//            setMattress()
//        }
    }

    private fun setMattress() {
        for ((k, i) in data.withIndex()) {
            entries.add(BarEntry(k.toFloat(), i.matlaData.sum() / 15f))
            for (n in 0..14) {
                average[n] += i.matlaData[n]
            }
        }
        println(entries)
        for (i in average.indices) {
            when (setStage(average[i])) {
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

        val lineDataSet = LineDataSet(entries as List<Entry>?, "matla")
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2f
        lineDataSet.setDrawFilled(true)
        lineDataSet.lineWidth = 1f
        lineDataSet.setDrawCircles(false)
        lineDataSet.circleRadius = 1f
        lineDataSet.color = R.color.main_color
        lineDataSet.fillColor = R.color.main_color
        lineDataSet.fillAlpha = 200
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawHighlightIndicators(false)
        lineDataSet.setDrawValues(false)
        val lineChart = binding.chart
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.isScaleXEnabled = true
        lineChart.isDragEnabled = true
        lineChart.animateX(1000)
        lineChart.animateY(1000)

        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)
        xAxis.mAxisRange = 10f

        val yLAxis: YAxis = lineChart.axisLeft
        yLAxis.textColor = Color.BLACK

        val yRAxis: YAxis = lineChart.getAxisRight()
        yRAxis.setDrawLabels(false)
        yRAxis.setDrawAxisLine(false)
        yRAxis.setDrawGridLines(false)

        val description = Description()
        description.text = ""

        lineChart.isDoubleTapToZoomEnabled = false
        lineChart.setDrawGridBackground(false)
        lineChart.description = description
        lineChart.invalidate()
        lineChart.setScaleMinima(8f, 1f)
        lineChart.scrollX


    }

    private fun setStage(value: Int): Int {
        return value / 23
    }
}