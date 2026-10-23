package com.example.ess_mich_ki.util

import kotlin.math.roundToInt

/**
 * Core nutritional multipliers:
 * 1g Protein = 4 kcal
 * 1g Carbohydrates = 4 kcal
 * 1g Fat = 9 kcal
 */
object NutritionalGoalMath {

    const val PROTEIN_KCAL_PER_G = 4.0
    const val CARBS_KCAL_PER_G = 4.0
    const val FAT_KCAL_PER_G = 9.0

    data class NutritionalGoalState(
        val totalKcal: Int = 1581,
        val proteinGrams: Int = 113,
        val carbsGrams: Int = 163,
        val fatGrams: Int = 53,
        val isKcalLocked: Boolean = false,
        val isProteinLocked: Boolean = false,
        val isCarbsLocked: Boolean = false,
        val isFatLocked: Boolean = false,
        val isFastingDay: Boolean = false
    ) {
        /**
         * Effective values when Fasting Day ("Heute ist Fastentag") is active.
         * Reduces baseline target values to 50%.
         */
        val effectiveKcal: Int
            get() = if (isFastingDay) (totalKcal * 0.5).roundToInt() else totalKcal

        val effectiveProteinGrams: Int
            get() = if (isFastingDay) (proteinGrams * 0.5).roundToInt() else proteinGrams

        val effectiveCarbsGrams: Int
            get() = if (isFastingDay) (carbsGrams * 0.5).roundToInt() else carbsGrams

        val effectiveFatGrams: Int
            get() = if (isFastingDay) (fatGrams * 0.5).roundToInt() else fatGrams

        /**
         * Calculate macro caloric percentages based on baseline values.
         */
        val calculatedTotalKcalFromMacros: Int
            get() = (proteinGrams * 4) + (carbsGrams * 4) + (fatGrams * 9)

        val proteinKcalPercentage: Int
            get() = if (totalKcal > 0) ((proteinGrams * 4 * 100.0) / totalKcal).roundToInt() else 0

        val carbsKcalPercentage: Int
            get() = if (totalKcal > 0) ((carbsGrams * 4 * 100.0) / totalKcal).roundToInt() else 0

        val fatKcalPercentage: Int
            get() = if (totalKcal > 0) ((fatGrams * 9 * 100.0) / totalKcal).roundToInt() else 0

        /**
         * Constraint handling:
         * If Total Kcal is locked AND 2 macros are locked, the 3rd macro MUST be locked/disabled,
         * because its value is mathematically fixed.
         */
        val isProteinAutoLocked: Boolean
            get() = isProteinLocked || (isKcalLocked && isCarbsLocked && isFatLocked)

        val isCarbsAutoLocked: Boolean
            get() = isCarbsLocked || (isKcalLocked && isProteinLocked && isFatLocked)

        val isFatAutoLocked: Boolean
            get() = isFatLocked || (isKcalLocked && isProteinLocked && isCarbsLocked)
    }

    /**
     * Adjust Total Calories (kcal).
     * Proportionally scales all unlocked macronutrients based on their current ratio
     * so their calorie sum matches the new Total kcal. Locked macros remain static.
     */
    fun updateTotalKcal(state: NutritionalGoalState, newKcal: Int): NutritionalGoalState {
        val targetKcal = newKcal.coerceAtLeast(0)
        if (state.isKcalLocked) return state

        // Locked macros keep their current calories
        val lockedProteinKcal = if (state.isProteinLocked) state.proteinGrams * 4 else 0
        val lockedCarbsKcal = if (state.isCarbsLocked) state.carbsGrams * 4 else 0
        val lockedFatKcal = if (state.isFatLocked) state.fatGrams * 9 else 0
        val totalLockedKcal = lockedProteinKcal + lockedCarbsKcal + lockedFatKcal

        val availableKcalForUnlocked = (targetKcal - totalLockedKcal).coerceAtLeast(0)

        val currentUnlockedProteinKcal = if (!state.isProteinLocked) state.proteinGrams * 4 else 0
        val currentUnlockedCarbsKcal = if (!state.isCarbsLocked) state.carbsGrams * 4 else 0
        val currentUnlockedFatKcal = if (!state.isFatLocked) state.fatGrams * 9 else 0
        val totalUnlockedKcalSum = currentUnlockedProteinKcal + currentUnlockedCarbsKcal + currentUnlockedFatKcal

        val newProteinGrams: Int
        val newCarbsGrams: Int
        val newFatGrams: Int

        if (totalUnlockedKcalSum > 0) {
            // Proportionally redistribute available calories according to current ratios
            val pRatio = currentUnlockedProteinKcal.toDouble() / totalUnlockedKcalSum
            val cRatio = currentUnlockedCarbsKcal.toDouble() / totalUnlockedKcalSum
            val fRatio = currentUnlockedFatKcal.toDouble() / totalUnlockedKcalSum

            newProteinGrams = if (!state.isProteinLocked) {
                ((availableKcalForUnlocked * pRatio) / PROTEIN_KCAL_PER_G).roundToInt().coerceAtLeast(0)
            } else state.proteinGrams

            newCarbsGrams = if (!state.isCarbsLocked) {
                ((availableKcalForUnlocked * cRatio) / CARBS_KCAL_PER_G).roundToInt().coerceAtLeast(0)
            } else state.carbsGrams

            newFatGrams = if (!state.isFatLocked) {
                ((availableKcalForUnlocked * fRatio) / FAT_KCAL_PER_G).roundToInt().coerceAtLeast(0)
            } else state.fatGrams
        } else {
            // Default split if all unlocked macros were 0 (40% P, 40% C, 20% F)
            val unlockedCount = listOf(!state.isProteinLocked, !state.isCarbsLocked, !state.isFatLocked).count { it }
            if (unlockedCount == 0) {
                return state.copy(totalKcal = targetKcal)
            }

            val pWeight = if (!state.isProteinLocked) 0.4 else 0.0
            val cWeight = if (!state.isCarbsLocked) 0.4 else 0.0
            val fWeight = if (!state.isFatLocked) 0.2 else 0.0
            val weightSum = pWeight + cWeight + fWeight

            newProteinGrams = if (!state.isProteinLocked) {
                (((availableKcalForUnlocked * (pWeight / weightSum))) / PROTEIN_KCAL_PER_G).roundToInt().coerceAtLeast(0)
            } else state.proteinGrams

            newCarbsGrams = if (!state.isCarbsLocked) {
                (((availableKcalForUnlocked * (cWeight / weightSum))) / CARBS_KCAL_PER_G).roundToInt().coerceAtLeast(0)
            } else state.carbsGrams

            newFatGrams = if (!state.isFatLocked) {
                (((availableKcalForUnlocked * (fWeight / weightSum))) / FAT_KCAL_PER_G).roundToInt().coerceAtLeast(0)
            } else state.fatGrams
        }

        return state.copy(
            totalKcal = targetKcal,
            proteinGrams = newProteinGrams,
            carbsGrams = newCarbsGrams,
            fatGrams = newFatGrams
        )
    }

    /**
     * Adjust Protein (g).
     * - If Total Kcal is UNLOCKED: keep other macros static and recalculate Total Kcal.
     * - If Total Kcal is LOCKED: distribute calorie delta proportionally among remaining unlocked macros.
     */
    fun updateProtein(state: NutritionalGoalState, requestedGrams: Int): NutritionalGoalState {
        if (state.isProteinAutoLocked) return state
        val targetProteinGrams = requestedGrams.coerceAtLeast(0)

        if (!state.isKcalLocked) {
            val newTotalKcal = (targetProteinGrams * 4) + (state.carbsGrams * 4) + (state.fatGrams * 9)
            return state.copy(
                proteinGrams = targetProteinGrams,
                totalKcal = newTotalKcal
            )
        } else {
            // Total Kcal is LOCKED -> Distribute delta among unlocked remaining macros (Carbs & Fat)
            val oldProteinKcal = state.proteinGrams * 4
            val requestedProteinKcal = targetProteinGrams * 4
            val deltaKcal = requestedProteinKcal - oldProteinKcal

            val (adjustedProteinGrams, newCarbs, newFat) = redistributeDeltaForMacroChange(
                changingMacroType = MacroType.PROTEIN,
                requestedMacroGrams = targetProteinGrams,
                deltaKcal = deltaKcal,
                state = state
            )

            return state.copy(
                proteinGrams = adjustedProteinGrams,
                carbsGrams = newCarbs,
                fatGrams = newFat
            )
        }
    }

    /**
     * Adjust Carbohydrates (g).
     */
    fun updateCarbs(state: NutritionalGoalState, requestedGrams: Int): NutritionalGoalState {
        if (state.isCarbsAutoLocked) return state
        val targetCarbsGrams = requestedGrams.coerceAtLeast(0)

        if (!state.isKcalLocked) {
            val newTotalKcal = (state.proteinGrams * 4) + (targetCarbsGrams * 4) + (state.fatGrams * 9)
            return state.copy(
                carbsGrams = targetCarbsGrams,
                totalKcal = newTotalKcal
            )
        } else {
            val oldCarbsKcal = state.carbsGrams * 4
            val requestedCarbsKcal = targetCarbsGrams * 4
            val deltaKcal = requestedCarbsKcal - oldCarbsKcal

            val (newProtein, adjustedCarbsGrams, newFat) = redistributeDeltaForMacroChange(
                changingMacroType = MacroType.CARBS,
                requestedMacroGrams = targetCarbsGrams,
                deltaKcal = deltaKcal,
                state = state
            )

            return state.copy(
                proteinGrams = newProtein,
                carbsGrams = adjustedCarbsGrams,
                fatGrams = newFat
            )
        }
    }

    /**
     * Adjust Fat (g).
     */
    fun updateFat(state: NutritionalGoalState, requestedGrams: Int): NutritionalGoalState {
        if (state.isFatAutoLocked) return state
        val targetFatGrams = requestedGrams.coerceAtLeast(0)

        if (!state.isKcalLocked) {
            val newTotalKcal = (state.proteinGrams * 4) + (state.carbsGrams * 4) + (targetFatGrams * 9)
            return state.copy(
                fatGrams = targetFatGrams,
                totalKcal = newTotalKcal
            )
        } else {
            val oldFatKcal = state.fatGrams * 9
            val requestedFatKcal = targetFatGrams * 9
            val deltaKcal = requestedFatKcal - oldFatKcal

            val (newProtein, newCarbs, adjustedFatGrams) = redistributeDeltaForMacroChange(
                changingMacroType = MacroType.FAT,
                requestedMacroGrams = targetFatGrams,
                deltaKcal = deltaKcal,
                state = state
            )

            return state.copy(
                proteinGrams = newProtein,
                carbsGrams = newCarbs,
                fatGrams = adjustedFatGrams
            )
        }
    }

    /**
     * Helper to redistribute calorie delta across remaining unlocked macros when Total Kcal is locked.
     */
    private enum class MacroType { PROTEIN, CARBS, FAT }

    private fun redistributeDeltaForMacroChange(
        changingMacroType: MacroType,
        requestedMacroGrams: Int,
        deltaKcal: Int,
        state: NutritionalGoalState
    ): Triple<Int, Int, Int> {
        val proteinUnlocked = changingMacroType != MacroType.PROTEIN && !state.isProteinLocked
        val carbsUnlocked = changingMacroType != MacroType.CARBS && !state.isCarbsLocked
        val fatUnlocked = changingMacroType != MacroType.FAT && !state.isFatLocked

        val unlockedProteinKcal = if (proteinUnlocked) state.proteinGrams * 4 else 0
        val unlockedCarbsKcal = if (carbsUnlocked) state.carbsGrams * 4 else 0
        val unlockedFatKcal = if (fatUnlocked) state.fatGrams * 9 else 0
        val totalOtherUnlockedKcal = unlockedProteinKcal + unlockedCarbsKcal + unlockedFatKcal

        if (totalOtherUnlockedKcal <= 0 && deltaKcal > 0) {
            // Cannot increase changing macro because no other macro can absorb the delta
            return Triple(state.proteinGrams, state.carbsGrams, state.fatGrams)
        }

        // Clamp deltaKcal so that other unlocked macros don't drop below 0
        val clampedDeltaKcal = deltaKcal.coerceAtMost(totalOtherUnlockedKcal)

        // Calculate actual achievable grams for changing macro
        val effectiveChangingGrams = when (changingMacroType) {
            MacroType.PROTEIN -> (state.proteinGrams + clampedDeltaKcal / 4.0).roundToInt().coerceAtLeast(0)
            MacroType.CARBS -> (state.carbsGrams + clampedDeltaKcal / 4.0).roundToInt().coerceAtLeast(0)
            MacroType.FAT -> (state.fatGrams + clampedDeltaKcal / 9.0).roundToInt().coerceAtLeast(0)
        }

        val remainingKcalToAbsorb = totalOtherUnlockedKcal - clampedDeltaKcal

        val newProteinGrams: Int
        val newCarbsGrams: Int
        val newFatGrams: Int

        if (totalOtherUnlockedKcal > 0) {
            val pRatio = unlockedProteinKcal.toDouble() / totalOtherUnlockedKcal
            val cRatio = unlockedCarbsKcal.toDouble() / totalOtherUnlockedKcal
            val fRatio = unlockedFatKcal.toDouble() / totalOtherUnlockedKcal

            newProteinGrams = if (changingMacroType == MacroType.PROTEIN) effectiveChangingGrams
            else if (proteinUnlocked) ((remainingKcalToAbsorb * pRatio) / PROTEIN_KCAL_PER_G).roundToInt().coerceAtLeast(0)
            else state.proteinGrams

            newCarbsGrams = if (changingMacroType == MacroType.CARBS) effectiveChangingGrams
            else if (carbsUnlocked) ((remainingKcalToAbsorb * cRatio) / CARBS_KCAL_PER_G).roundToInt().coerceAtLeast(0)
            else state.carbsGrams

            newFatGrams = if (changingMacroType == MacroType.FAT) effectiveChangingGrams
            else if (fatUnlocked) ((remainingKcalToAbsorb * fRatio) / FAT_KCAL_PER_G).roundToInt().coerceAtLeast(0)
            else state.fatGrams
        } else {
            newProteinGrams = if (changingMacroType == MacroType.PROTEIN) effectiveChangingGrams else state.proteinGrams
            newCarbsGrams = if (changingMacroType == MacroType.CARBS) effectiveChangingGrams else state.carbsGrams
            newFatGrams = if (changingMacroType == MacroType.FAT) effectiveChangingGrams else state.fatGrams
        }

        return Triple(newProteinGrams, newCarbsGrams, newFatGrams)
    }

    /**
     * Toggle "Heute ist Fastentag" (Fasting Day).
     * Temporarily halves target values when active, and restores exact baseline values when inactive without rounding drift.
     */
    fun toggleFastingDay(state: NutritionalGoalState): NutritionalGoalState {
        return state.copy(isFastingDay = !state.isFastingDay)
    }

    /**
     * Lock/Unlock toggles.
     */
    fun toggleLockKcal(state: NutritionalGoalState): NutritionalGoalState =
        state.copy(isKcalLocked = !state.isKcalLocked)

    fun toggleLockProtein(state: NutritionalGoalState): NutritionalGoalState =
        state.copy(isProteinLocked = !state.isProteinLocked)

    fun toggleLockCarbs(state: NutritionalGoalState): NutritionalGoalState =
        state.copy(isCarbsLocked = !state.isCarbsLocked)

    fun toggleLockFat(state: NutritionalGoalState): NutritionalGoalState =
        state.copy(isFatLocked = !state.isFatLocked)
}
