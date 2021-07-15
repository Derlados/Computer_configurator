package com.derlados.computer_conf.providers

import android.content.Context
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.Build
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider

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

    override fun getCompatibilityErrors(error: Build.Companion.CompatibilityError): String {
        return when(error) {
            Build.Companion.CompatibilityError.WRONG_CPU_SOCKET -> context.getString(R.string.wrong_cpu_socket)
            Build.Companion.CompatibilityError.WRONG_RAM_COUNT -> context.getString(R.string.wrong_ram_count)
            Build.Companion.CompatibilityError.WRONG_RAM_TYPE -> context.getString(R.string.wrong_ram_type)
            Build.Companion.CompatibilityError.WRONG_RAM_SIZE -> context.getString(R.string.wrong_ram_size)
            Build.Companion.CompatibilityError.WRONG_CASE_FORM_FACTOR -> context.getString(R.string.wrong_case_form_factor)
            Build.Companion.CompatibilityError.NOT_ENOUGH_PS_POWER -> context.getString(R.string.not_enough_ps_power)
            Build.Companion.CompatibilityError.WRONG_SATA_COUNT -> context.getString(R.string.wrong_sata_count)
            Build.Companion.CompatibilityError.WRONG_M2_COUNT -> context.getString(R.string.wrong_m2_count)
        }
    }

    override fun getString(resString: ResourceProvider.ResString): String {
        return when (resString) {
            ResourceProvider.ResString.ADD_TO_FAVORITE -> context.getString(R.string.add_to_favorite)
            ResourceProvider.ResString.ADD_TO_BUILD -> context.getString(R.string.add_to_build)
            ResourceProvider.ResString.NOT_COMPATIBILITY -> context.getString(R.string.not_compatibility)
            ResourceProvider.ResString.NOT_COMPLETE -> context.getString(R.string.not_complete)
            ResourceProvider.ResString.COMPLETE -> context.getString(R.string.complete)
            ResourceProvider.ResString.SAVED -> context.getString(R.string.saved)
        }
    }

    override fun getColor(resColor: ResourceProvider.ResColor): Int {
        return when (resColor) {
            ResourceProvider.ResColor.GREEN -> context.getColor(R.color.green)
            ResourceProvider.ResColor.RED -> context.getColor(R.color.red)
        }
    }
}