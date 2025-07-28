package com.example.aegisai.ui.profile

import android.content.Context
import android.util.Log
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

    private val _operationStatus = MutableStateFlow<String?>(null)
    val operationStatus: StateFlow<String?> = _operationStatus

    private val api = RetrofitClient.instance

    fun deleteContact(context: Context, contactToDelete: EmergencyContact) {
        viewModelScope.launch {
            val userId = getUserId(context)
            if (userId == null) {
                _operationStatus.value = "Error: User not found."
                return@launch
            }

            val request = DeleteContactRequest(userId = userId, phone = contactToDelete.phone)
            try {
                val response = api.deleteContact(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    // On success, update the local list which will trigger the UI to refresh
                    val updatedList = _contacts.value.filterNot { it.phone == contactToDelete.phone }
                    updateContactsInPrefs(context, updatedList)
                    _operationStatus.value = "${contactToDelete.name} deleted successfully."
                } else {
                    val errorMsg = response.body()?.message ?: "Unknown error"
                    _operationStatus.value = "Failed to delete contact: $errorMsg"
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Delete contact failed", e)
                _operationStatus.value = "An error occurred: ${e.message}"
            }
        }
    }

    fun clearOperationStatus() {
        _operationStatus.value = null
    }

    fun loadInitialData(context: Context) {
        val sharedPrefs = context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        val contactsJson = sharedPrefs.getString("EMERGENCY_CONTACTS_JSON", null)
        if (contactsJson != null) {
            try {
                val type = object : TypeToken<List<EmergencyContact>>() {}.type
                _contacts.value = Gson().fromJson(contactsJson, type)
            } catch (e: Exception) {
                _contacts.value = emptyList()
            }
        }
    }

    // --- Other Functions (Add, Update, Getters) ---

    private fun getUserId(context: Context): String? {
        return context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
            .getString("USER_ID", null)
    }

    private fun updateContactsInPrefs(context: Context, contacts: List<EmergencyContact>) {
        _contacts.value = contacts
        val sharedPrefs = context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("EMERGENCY_CONTACTS_JSON", Gson().toJson(contacts)).apply()
    }

    fun addContact(context: Context, name: String, phone: String) {
        viewModelScope.launch {
            val userId = getUserId(context) ?: return@launch
            val request = AddContactRequest(userId, name, phone)
            try {
                val response = api.addContact(request)
                if(response.isSuccessful && response.body()?.success == true) {
                    val updatedList = _contacts.value.toMutableList().apply { add(EmergencyContact(name, phone)) }
                    updateContactsInPrefs(context, updatedList)
                    _operationStatus.value = "$name added successfully."
                } else {
                    _operationStatus.value = "Failed to add contact: ${response.body()?.message}"
                }
            } catch (e: Exception) { _operationStatus.value = "Error: ${e.message}" }
        }
    }

    fun updateContact(context: Context, oldPhone: String, newName: String, newPhone: String) {
        viewModelScope.launch {
            val userId = getUserId(context) ?: return@launch
            val request = UpdateContactRequest(userId, oldPhone, newName, newPhone)
            try {
                val response = api.updateContact(request)
                if(response.isSuccessful && response.body()?.success == true) {
                    val updatedList = _contacts.value.map {
                        if (it.phone == oldPhone) EmergencyContact(newName, newPhone) else it
                    }
                    updateContactsInPrefs(context, updatedList)
                    _operationStatus.value = "Contact updated successfully."
                } else {
                    _operationStatus.value = "Failed to update contact: ${response.body()?.message}"
                }
            } catch (e: Exception) { _operationStatus.value = "Error: ${e.message}" }
        }
    }

    fun getUserName(context: Context): String {
        val sharedPrefs = context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        val userName = sharedPrefs.getString("USER_NAME", "User")
        return userName?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: "User"
    }

    fun getUserPhone(context: Context): String {
        return context.getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE).getString("USER_PHONE", "N/A") ?: "N/A"
    }
}