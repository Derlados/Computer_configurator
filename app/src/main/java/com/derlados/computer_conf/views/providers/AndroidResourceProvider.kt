package com.derlados.computer_conf.views.providers

import android.content.Context
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.interfaces.ResourceProvider
import java.util.*
import kotlin.collections.HashMap
import kotlin.contracts.contract

class AndroidResourceProvider(private val context: Context): ResourceProvider {
    override fun getDefaultImageByCategory(category: ComponentCategory): Int {
        return when (category) {
            ComponentCategory.CPU -> R.drawable.ic_cpu_24
            ComponentCategory.GPU -> R.drawable.ic_gpu_24
            ComponentCategory.MOTHERBOARD -> R.drawable.ic_motherboard_24
            ComponentCategory.HDD -> R.drawable.ic_hdd_24
            ComponentCategory.SSD -> R.drawable.ic_ssd_24
            ComponentCategory.RAM -> R.drawable.ic_ram_24
            ComponentCategory.POWER_SUPPLY -> R.drawable.ic_power_supply_24
            ComponentCategory.CASE -> R.drawable.ic_case_24
            else -> R.drawable.ic_default_image_24
        }
    }

    override fun getString(resString: ResourceProvider.ResString): String {
        return when (resString) {
            ResourceProvider.ResString.ADD_TO_FAVORITE -> context.getString(R.string.add_to_favorite)
            ResourceProvider.ResString.ADD_TO_BUILD -> context.getString(R.string.add_to_build)
            ResourceProvider.ResString.NOT_COMPATIBILITY -> context.getString(R.string.not_compatibility)
            ResourceProvider.ResString.NOT_COMPLETE -> context.getString(R.string.not_complete)
            ResourceProvider.ResString.COMPLETE -> context.getString(R.string.complete)
        }
    }
}