package com.byagowi.persiancalendar.ui.preferences.interfacecalendar

import android.content.SharedPreferences
import androidx.core.content.edit
import com.byagowi.persiancalendar.DEFAULT_WEEK_ENDS
import com.byagowi.persiancalendar.DEFAULT_WEEK_START
import com.byagowi.persiancalendar.PREF_APP_LANGUAGE
import com.byagowi.persiancalendar.PREF_HOLIDAY_TYPES
import com.byagowi.persiancalendar.PREF_MAIN_CALENDAR_KEY
import com.byagowi.persiancalendar.PREF_OTHER_CALENDARS_KEY
import com.byagowi.persiancalendar.PREF_PERSIAN_DIGITS
import com.byagowi.persiancalendar.PREF_WEEK_ENDS
import com.byagowi.persiancalendar.PREF_WEEK_START
import com.byagowi.persiancalendar.entities.Language
import com.byagowi.persiancalendar.utils.EnabledHolidays

// Preferences changes be applied automatically when user requests a language change
fun changeLanguage(prefs: SharedPreferences, language: Language) = prefs.edit {
    putString(PREF_APP_LANGUAGE, language.code)
    putBoolean(PREF_PERSIAN_DIGITS, language.prefersLocalDigits)

    when {
        language.isAfghanistanExclusive -> {
            val enabledHolidays = EnabledHolidays(prefs, emptySet())
            if (enabledHolidays.isEmpty || enabledHolidays.onlyIranHolidaysIsEnabled)
                putStringSet(PREF_HOLIDAY_TYPES, EnabledHolidays.afghanistanDefault)
        }
        language.isIranExclusive -> {
            val enabledHolidays = EnabledHolidays(prefs, emptySet())
            if (enabledHolidays.isEmpty || enabledHolidays.onlyAfghanistanHolidaysIsEnabled)
                putStringSet(PREF_HOLIDAY_TYPES, EnabledHolidays.iranDefault)
        }
    }

    when {
        language.prefersGregorianCalendar -> {
            putString(PREF_MAIN_CALENDAR_KEY, "GREGORIAN")
            putString(PREF_OTHER_CALENDARS_KEY, "ISLAMIC,SHAMSI")
            putString(PREF_WEEK_START, "1")
            putStringSet(PREF_WEEK_ENDS, setOf("1"))
        }
        language.prefersIslamicCalendar -> {
            putString(PREF_MAIN_CALENDAR_KEY, "ISLAMIC")
            putString(PREF_OTHER_CALENDARS_KEY, "GREGORIAN,SHAMSI")
            putString(PREF_WEEK_START, DEFAULT_WEEK_START)
            putStringSet(PREF_WEEK_ENDS, DEFAULT_WEEK_ENDS)
        }
        language.prefersPersianCalendar -> {
            putString(PREF_MAIN_CALENDAR_KEY, "SHAMSI")
            putString(PREF_OTHER_CALENDARS_KEY, "GREGORIAN,ISLAMIC")
            putString(PREF_WEEK_START, DEFAULT_WEEK_START)
            putStringSet(PREF_WEEK_ENDS, DEFAULT_WEEK_ENDS)
        }
    }
}