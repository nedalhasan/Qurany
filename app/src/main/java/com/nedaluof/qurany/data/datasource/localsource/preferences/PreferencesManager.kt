package com.nedaluof.qurany.data.datasource.localsource.preferences

import android.content.Context
import com.nedaluof.mihawk.MiHawk
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by nedaluof on 6/23/2020. {Java}
 * Created by nedaluof on 12/17/2020. {Kotlin}
 * Updated by nedaluof on 9/17/2021. {From [PreferenceManager] to [preferencesDataStore]}
 * Updated by nedaluof on 12/9/2022. {From [PreferenceManager] to [MiHawk]}
 */

@Singleton
class PreferencesManager @Inject constructor(
  @ApplicationContext private val context: Context
) {

  init {
    MiHawk.Builder(context)
      .withLoggingEnabled(true)
      .withPreferenceName("QURANY_PREFERENCES")
      .build()
  }

  inline fun <reified T> addToPreferences(key: String, value: T) {
    MiHawk.put(key, value)
  }

  inline fun <reified T> getFromPreferences(key: String): T? {
    return MiHawk.get(key)
  }

  fun removeFromPreferences(key: String) {
    MiHawk.remove(key) {}
  }

  fun hasKeyInPreferences(key: String, result: (Boolean) -> Unit) {
    MiHawk.contains(key, result)
  }

  fun hasKeyInPreferences(key: String): Boolean {
    var hasKey = false
    runBlocking {
      MiHawk.contains(key) {
        hasKey = it
      }
    }
    return hasKey
  }
}
