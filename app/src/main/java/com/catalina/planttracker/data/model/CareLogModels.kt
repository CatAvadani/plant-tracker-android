package com.catalina.planttracker.data.model

import androidx.annotation.StringRes
import com.catalina.planttracker.R

enum class CareLogEntryType(val value: Int, @param:StringRes val labelRes: Int) {
    WATERED(0, R.string.care_log_type_watered),
    FERTILIZED(1, R.string.care_log_type_fertilized),
    REPOTTED(2, R.string.care_log_type_repotted),
    PRUNED(3, R.string.care_log_type_pruned),
    TREATED_FOR_PESTS(4, R.string.care_log_type_treated_for_pests),
    HEALTH_CHECK(5, R.string.care_log_type_health_check),
    OTHER(6, R.string.care_log_type_other);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: OTHER
    }
}

data class CreateCareLogRequest(
    val entryType: Int,
    val notes: String? = null
)

data class CareLogResponse(
    val id: Int,
    val plantId: Int,
    val entryType: Int,
    val notes: String? = null,
    val createdAt: String
)
