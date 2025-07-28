package com.example.aegisai.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aegisai.model.*
import com.example.aegisai.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ProfileViewModel : ViewModel() {

    private val _contacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val contacts: StateFlow<List<EmergencyContact>> = _contacts

    private val api = RetrofitClient.instance

    fun loadInitialData(context: Context) {
        val sharedPrefs = context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        val contactsJson = sharedPrefs.getString("EMERGENCY_CONTACTS_JSON", null)
        if (contactsJson != null) {
            val type = object : TypeToken<List<EmergencyContact>>() {}.type
            _contacts.value = Gson().fromJson(contactsJson, type)
        }
    }

    private fun getUserId(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("USER_ID", null)
    }

    fun addContact(context: Context, name: String, phone: String) {
        viewModelScope.launch {
            val userId = getUserId(context) ?: return@launch
            val request = AddContactRequest(userId = userId, name = name, phone = phone)
            try {
                api.addContact(request) // Assuming success, update the list
                val updatedList = _contacts.value.toMutableList().apply { add(EmergencyContact(name, phone)) }
                updateContactsInPrefs(context, updatedList)
            } catch (e: Exception) { /* Handle error */ }
        }
    }

    fun deleteContact(context: Context, phone: String) {
        viewModelScope.launch {
            val userId = getUserId(context) ?: return@launch
            val request = DeleteContactRequest(userId = userId, phone = phone)
            try {
                api.deleteContact(request)
                val updatedList = _contacts.value.filterNot { it.phone == phone }
                updateContactsInPrefs(context, updatedList)
            } catch (e: Exception) { /* Handle error */ }
        }
    }

    fun updateContact(context: Context, oldPhone: String, newName: String, newPhone: String) {
        viewModelScope.launch {
            val userId = getUserId(context) ?: return@launch
            val request = UpdateContactRequest(userId = userId, oldPhone = oldPhone, name = newName, phone = newPhone)
            try {
                api.updateContact(request)
                val updatedList = _contacts.value.map {
                    if (it.phone == oldPhone) EmergencyContact(newName, newPhone) else it
                }
                updateContactsInPrefs(context, updatedList)
            } catch (e: Exception) { /* Handle error */ }
        }
    }

    private fun updateContactsInPrefs(context: Context, contacts: List<EmergencyContact>) {
        _contacts.value = contacts
        val sharedPrefs = context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("EMERGENCY_CONTACTS_JSON", Gson().toJson(contacts)).apply()
    }

    fun getUserName(context: Context): String {
        val sharedPrefs = context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        val userName = sharedPrefs.getString("USER_NAME", "User")
        return userName?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: "User"
    }

    fun getUserPhone(context: Context): String {
        val sharedPrefs = context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("USER_PHONE", "N/A") ?: "N/A"
    }
}