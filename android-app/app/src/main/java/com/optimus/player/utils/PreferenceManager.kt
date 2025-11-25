package com.optimus.player.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Gerenciador de preferências do app
 * Armazena licença, configurações, etc.
 */
class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "optimus_player_prefs"

        // Keys
        private const val KEY_IS_ACTIVATED = "is_activated"
        private const val KEY_ACTIVATION_CODE = "activation_code"
        private const val KEY_LICENSE_EXPIRES_AT = "license_expires_at"
        private const val KEY_MAC_ADDRESS = "mac_address"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_USER_TYPE = "user_type" // direct | reseller
        private const val KEY_PLAYLIST_URL = "playlist_url"
        private const val KEY_LAST_SYNC = "last_sync"
    }

    // Activation
    fun isActivated(): Boolean = prefs.getBoolean(KEY_IS_ACTIVATED, false)

    fun setActivated(activated: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ACTIVATED, activated).apply()
    }

    fun getActivationCode(): String? = prefs.getString(KEY_ACTIVATION_CODE, null)

    fun setActivationCode(code: String) {
        prefs.edit().putString(KEY_ACTIVATION_CODE, code).apply()
    }

    // License
    fun getLicenseExpiresAt(): Long = prefs.getLong(KEY_LICENSE_EXPIRES_AT, 0)

    fun setLicenseExpiresAt(timestamp: Long) {
        prefs.edit().putLong(KEY_LICENSE_EXPIRES_AT, timestamp).apply()
    }

    fun isLicenseValid(): Boolean {
        val expiresAt = getLicenseExpiresAt()
        return expiresAt > System.currentTimeMillis()
    }

    // Device Info
    fun getMacAddress(): String? = prefs.getString(KEY_MAC_ADDRESS, null)

    fun setMacAddress(mac: String) {
        prefs.edit().putString(KEY_MAC_ADDRESS, mac).apply()
    }

    fun getDeviceId(): String? = prefs.getString(KEY_DEVICE_ID, null)

    fun setDeviceId(id: String) {
        prefs.edit().putString(KEY_DEVICE_ID, id).apply()
    }

    // User Type
    fun getUserType(): String = prefs.getString(KEY_USER_TYPE, "direct") ?: "direct"

    fun setUserType(type: String) {
        prefs.edit().putString(KEY_USER_TYPE, type).apply()
    }

    // Playlist
    fun getPlaylistUrl(): String? = prefs.getString(KEY_PLAYLIST_URL, null)

    fun setPlaylistUrl(url: String) {
        prefs.edit().putString(KEY_PLAYLIST_URL, url).apply()
    }

    // Sync
    fun getLastSync(): Long = prefs.getLong(KEY_LAST_SYNC, 0)

    fun setLastSync(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_SYNC, timestamp).apply()
    }

    // Clear all data (logout)
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
