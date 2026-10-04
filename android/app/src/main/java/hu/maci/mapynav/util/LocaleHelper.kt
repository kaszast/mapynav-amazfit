package hu.maci.mapynav.util

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {
  private const val PREFS_NAME = "mapynav_prefs"
  private const val KEY_LANGUAGE = "selected_language"

  const val LANG_SYSTEM = "system"
  const val LANG_EN = "en"
  const val LANG_HU = "hu"

  fun getSavedLanguage(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_LANGUAGE, LANG_SYSTEM) ?: LANG_SYSTEM
  }

  fun setLanguage(context: Context, langCode: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_LANGUAGE, langCode).apply()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val localeManager = context.getSystemService(LocaleManager::class.java)
      if (langCode == LANG_SYSTEM) {
        localeManager?.applicationLocales = LocaleList.getEmptyLocaleList()
      } else {
        localeManager?.applicationLocales = LocaleList.forLanguageTags(langCode)
      }
    }
  }

  fun applyLocale(context: Context): Context {
    val langCode = getSavedLanguage(context)
    if (langCode == LANG_SYSTEM) {
      return context
    }

    val locale = Locale.forLanguageTag(langCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    return context.createConfigurationContext(config)
  }
}
