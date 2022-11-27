package com.white.meters.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.white.meters.R
import com.white.meters.base.view.setSuccessState
import com.white.meters.data.base.BaseMeterData
import com.white.meters.data.base.Consts
import com.white.meters.data.models.DetectArgs
import com.white.meters.data.models.MeterData
import com.white.meters.data.models.MeterType
import com.white.meters.databinding.FragmentHomeBinding
import com.white.meters.databinding.ViewAddMeterBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val model: HomeVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.mtbHome.inflateMenu(R.menu.home_menu)
        binding.mtbHome.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.mi_history -> {
                    findNavController().navigate(R.id.historyFragment)
                    true
                }
                else -> false
            }
        }

        binding.vHomeAddColdBath.tvAddMeterName.text = MeterType.COLD_BATH.meterName
        binding.vHomeAddHotBath.tvAddMeterName.text = MeterType.HOT_BATH.meterName
        binding.vHomeAddColdKitchen.tvAddMeterName.text = MeterType.COLD_KITCHEN.meterName
        binding.vHomeAddHotKitchen.tvAddMeterName.text = MeterType.HOT_KITCHEN.meterName

        binding.vHomeAddColdBath.root.setOnClickListener {
            findNavController().navigate(R.id.detectFragment,
                bundleOf("type" to DetectArgs(MeterType.COLD_BATH)))
        }

        binding.vHomeAddHotBath.root.setOnClickListener {
            findNavController().navigate(R.id.detectFragment,
                bundleOf("type" to DetectArgs(MeterType.HOT_BATH)))
        }

        binding.vHomeAddColdKitchen.root.setOnClickListener {
            findNavController().navigate(R.id.detectFragment,
                bundleOf("type" to DetectArgs(MeterType.COLD_KITCHEN)))
        }

        binding.vHomeAddHotKitchen.root.setOnClickListener {
            findNavController().navigate(R.id.detectFragment,
                bundleOf("type" to DetectArgs(MeterType.HOT_KITCHEN)))
        }

        model.meters.observe(viewLifecycleOwner) { data ->
            binding.vHomeAddColdBath.tvAddMeterText.text = data.coldBathMeterData?.value
            binding.vHomeAddHotBath.tvAddMeterText.text = data.hotBathMeterData?.value
            binding.vHomeAddColdKitchen.tvAddMeterText.text = data.coldKitchenMeterData?.value
            binding.vHomeAddHotKitchen.tvAddMeterText.text = data.hotKitchenMeterData?.value

            checkSuccessData(data)

            if (model.checkMetersData(data)) {
                binding.mbHomeSend.visibility = View.VISIBLE
            }

            binding.mbHomeSend.setOnClickListener {
                model.saveToHistory()
                openPicker(data)
            }
        }
    }



    private fun checkSuccessData(meterData: MeterData) {
        handleMeterView(meterData.coldBathMeterData, binding.vHomeAddColdBath)
        handleMeterView(meterData.hotBathMeterData, binding.vHomeAddHotBath)
        handleMeterView(meterData.coldKitchenMeterData, binding.vHomeAddColdKitchen)
        handleMeterView(meterData.hotKitchenMeterData, binding.vHomeAddHotKitchen)
    }

    private fun handleMeterView(meter: BaseMeterData?, binding: ViewAddMeterBinding) {
        if (meter != null) {
            binding.clAddMeter.setSuccessState()
        } else {
            binding.ivAddMeterIcon.setImageResource(R.drawable.add)
        }
    }

    private fun openPicker(meterData: MeterData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(Consts.EMAIL_ADDRESS))
            putExtra(Intent.EXTRA_SUBJECT, "Показания счётчиков Кв. ${Consts.FLAT_NUMBER}")
            putExtra(Intent.EXTRA_TEXT, generateMessage(meterData))
        }
        startActivity(Intent.createChooser(intent, "Send email"))
    }

    private fun generateMessage(meterData: MeterData): String {
        return "Холодная вода #1 ${meterData.coldBathMeterData?.value} \n" +
                "Холодная вода #2 ${meterData.coldKitchenMeterData?.value} \n" +
                "Горячая вода #1 ${meterData.hotBathMeterData?.value} \n" +
                "Горячая вода #2 ${meterData.hotKitchenMeterData?.value}"
    }
}