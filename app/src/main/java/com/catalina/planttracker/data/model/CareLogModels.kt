package com.catalina.planttracker.data.model

enum class CareLogEntryType(val value: Int, val label: String) {
    WATERED(0, "Watered"),
    FERTILIZED(1, "Fertilized"),
    REPOTTED(2, "Repotted"),
    PRUNED(3, "Pruned"),
    TREATED_FOR_PESTS(4, "Treated for Pests"),
    HEALTH_CHECK(5, "Health Check"),
    OTHER(6, "Other");

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
