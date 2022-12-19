package com.derlados.computer_configurator.providers

import android.content.Context
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.entities.build.Build
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider

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
            else -> ""
        }
    }

    override fun getString(resString: ResourceProvider.ResString): String {
        return when (resString) {
            ResourceProvider.ResString.ADD_TO_FAVOURITE -> context.getString(R.string.add_to_favorite)
            ResourceProvider.ResString.DELETE_FROM_FAVOURITE -> context.getString(R.string.delete_from_favorite)
            ResourceProvider.ResString.ADD_TO_BUILD -> context.getString(R.string.add_to_build)
            ResourceProvider.ResString.NOT_COMPATIBILITY -> context.getString(R.string.not_compatibility)
            ResourceProvider.ResString.NOT_COMPLETE -> context.getString(R.string.not_complete)
            ResourceProvider.ResString.COMPLETE -> context.getString(R.string.complete)
            ResourceProvider.ResString.SAVED -> context.getString(R.string.saved)
            ResourceProvider.ResString.INVALID_AUTH_DATA -> context.getString(R.string.invalid_auth_data)
            ResourceProvider.ResString.PASSWORD_DO_NOT_MATCH -> context.getString(R.string.pass_do_not_match)
            ResourceProvider.ResString.NICKNAME_TAKEN -> context.getString(R.string.NICKNAME_TAKEN)
            ResourceProvider.ResString.GOOGLE_ACCOUNT_ALREADY_USED -> context.getString(R.string.GOOGLE_ACCOUNT_ALREADY_USED)
            ResourceProvider.ResString.LOGIN_USER_NOT_FOUND -> context.getString(R.string.LOGIN_USER_NOT_FOUND)
            ResourceProvider.ResString.NO_CONNECTION -> context.getString(R.string.no_connection)
            ResourceProvider.ResString.INTERNAL_SERVER_ERROR -> context.getString(R.string.internal_server_error)
            ResourceProvider.ResString.UNEXPECTED_ERROR -> context.getString(R.string.unexpected_error)
            ResourceProvider.ResString.INCORRECT_FIELDS_LENGTH -> context.getString(R.string.incorrect_fields_length)
            ResourceProvider.ResString.BUILD_MUST_BE_COMPLETED -> context.getString(R.string.build_must_be_completed)
            ResourceProvider.ResString.BUILD_WILL_BE_SAVED_ON_SERVER -> context.getString(R.string.build_will_be_saved_on_server)
            ResourceProvider.ResString.CANNOT_ADD_MORE -> context.getString(R.string.cannot_add_more)
            ResourceProvider.ResString.INVALID_USERNAME -> context.getString(R.string.invalid_username)
            ResourceProvider.ResString.LOGIN_SUCCESS -> context.getString(R.string.login_success)
            ResourceProvider.ResString.LOGOUT_SUCCESS -> context.getString(R.string.logout_success)
            ResourceProvider.ResString.INCORRECT_USERNAME_OR_SECRET_WORD -> context.getString(R.string.incorrect_username_or_secret_word)
            ResourceProvider.ResString.ENTER_VALUE -> context.getString(R.string.enter_value_to_the_field)
            ResourceProvider.ResString.SUCCESS_CHANGE_PASSWORD -> context.getString(R.string.success_change_password)
            ResourceProvider.ResString.DELETE_FROM_BUILD -> context.getString(R.string.delete_from_build)
            ResourceProvider.ResString.BUILD_NOT_FOUND -> context.getString(R.string.build_not_found)
            ResourceProvider.ResString.YOU_MUST_BE_AUTHORIZED -> context.getString(R.string.you_must_be_authorized)
            ResourceProvider.ResString.LOCAL_USER_NOT_FOUND -> context.getString(R.string.local_user_not_found)
            ResourceProvider.ResString.ONLY_FOR_AUTHORIZED -> context.getString(R.string.only_for_authorized)
            ResourceProvider.ResString.REPORT_WAS_SENDED -> context.getString(R.string.report_was_sended)
        }
    }

    override fun getColor(resColor: ResourceProvider.ResColor): Int {
        return when (resColor) {
            ResourceProvider.ResColor.GREEN -> context.getColor(R.color.green)
            ResourceProvider.ResColor.RED -> context.getColor(R.color.red)
        }
    }
}