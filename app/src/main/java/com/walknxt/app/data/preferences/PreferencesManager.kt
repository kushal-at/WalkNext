package com.walknxt.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("walknxt_settings", Context.MODE_PRIVATE)

    private val _useImperial = MutableStateFlow(prefs.getBoolean(KEY_USE_IMPERIAL, false))
    val useImperial: StateFlow<Boolean> = _useImperial.asStateFlow()

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM)
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dailyStepGoal = MutableStateFlow(prefs.getInt(KEY_DAILY_STEP_GOAL, 10000))
    val dailyStepGoal: StateFlow<Int> = _dailyStepGoal.asStateFlow()

    fun setUseImperial(useImperial: Boolean) {
        prefs.edit().putBoolean(KEY_USE_IMPERIAL, useImperial).apply()
        _useImperial.value = useImperial
    }

    fun setThemeMode(theme: String) {
        prefs.edit().putString(KEY_THEME_MODE, theme).apply()
        _themeMode.value = theme
    }

    fun setDailyStepGoal(goal: Int) {
        prefs.edit().putInt(KEY_DAILY_STEP_GOAL, goal).apply()
        _dailyStepGoal.value = goal
    }

    companion object {
        const val KEY_USE_IMPERIAL = "use_imperial"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_DAILY_STEP_GOAL = "daily_step_goal"

        const val THEME_SYSTEM = "SYSTEM"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"
    }
}
