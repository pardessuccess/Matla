package com.sensomedi.matla.fragment

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.sensomedi.matla.DataActivity
import com.sensomedi.matla.MainViewModel
import com.sensomedi.adapter.DataAdapter
import com.sensomedi.data.User
import com.sensomedi.matla.MainActivity
import com.sensomedi.matla.databinding.FragmentDataBinding

class DataFragment : Fragment() {

    private lateinit var binding: FragmentDataBinding
    private var user = User()

    private val dataListAdapter = DataAdapter {
//        println(it)
        val intent = Intent(requireContext(), DataActivity::class.java)
        intent.putExtra("hi", it)
        intent.putExtra("user", user)
        startActivity(intent)
    }
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun bodyInfo(intent: Intent) {
        intent.putExtra("height", viewModel.user.value!!.height)
        intent.putExtra("weight", viewModel.user.value!!.weight)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDataBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binding.recyclerView.adapter = dataListAdapter

        initView()
        return binding.root
    }

    private fun initView() = with(binding) {
        val main = activity as MainActivity
        main.getMatlaData()
        viewModel.matlaDataList.observe(viewLifecycleOwner) {
            dataListAdapter.setUpdateList(it)
        }

        viewModel.user.observe(viewLifecycleOwner) {
            dataEmailTv.text = it.email
            user = it
        }

        viewModel.matlaListSize.observe(viewLifecycleOwner) {
            dataProgress.visibility = View.GONE
            if (it == 0) {
                noDataTv.visibility = View.VISIBLE
            }
        }
    }

}