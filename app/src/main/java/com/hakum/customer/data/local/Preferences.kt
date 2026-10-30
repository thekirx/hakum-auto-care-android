package com.hakum.customer.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.customerPreferences by preferencesDataStore("hakum_preferences")
data class Preferences(val signedIn:Boolean=false,val dark:Boolean=true,val notifications:Boolean=true)
class PreferenceStore(context:Context) {
    private val store=context.customerPreferences
    private val session=booleanPreferencesKey("session")
    private val theme=booleanPreferencesKey("dark")
    private val notifications=booleanPreferencesKey("notifications")
    val state=store.data.map{Preferences(it[session]?:false,it[theme]?:true,it[notifications]?:true)}
    suspend fun session(value:Boolean){store.edit{it[session]=value}}
    suspend fun dark(value:Boolean){store.edit{it[theme]=value}}
    suspend fun notifications(value:Boolean){store.edit{it[notifications]=value}}
}
