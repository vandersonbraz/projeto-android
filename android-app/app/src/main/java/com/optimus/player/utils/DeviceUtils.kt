package com.optimus.player.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import java.net.NetworkInterface
import java.util.Collections
import java.util.UUID

/**
 * Utilitários para obter informações do dispositivo
 * MAC Address, Device ID, etc.
 */
object DeviceUtils {

    /**
     * Obtém o MAC Address do dispositivo
     * Nota: No Android 6+, o MAC pode ser "02:00:00:00:00:00" por segurança
     * Usamos métodos alternativos quando possível
     */
    @SuppressLint("HardwareIds")
    fun getMacAddress(context: Context): String {
        try {
            // Método 1: WiFi Manager (funciona em versões antigas)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                wifiManager?.connectionInfo?.macAddress?.let {
                    if (it != "02:00:00:00:00:00") return formatMacAddress(it)
                }
            }

            // Método 2: Network Interfaces (funciona em algumas versões)
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (intf.name.equals("wlan0", ignoreCase = true)) {
                    val mac = intf.hardwareAddress ?: continue
                    val macAddress = mac.joinToString(":") { String.format("%02X", it) }
                    if (macAddress != "02:00:00:00:00:00") {
                        return macAddress
                    }
                }
            }

            // Método 3: Fallback - gera MAC baseado em Device ID (consistente)
            val deviceId = getDeviceId(context)
            return generateMacFromDeviceId(deviceId)

        } catch (e: Exception) {
            // Se tudo falhar, gera MAC fake mas consistente
            val deviceId = getDeviceId(context)
            return generateMacFromDeviceId(deviceId)
        }
    }

    /**
     * Gera um Device ID único e persistente
     */
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return try {
            // Android ID (mais confiável)
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )

            if (androidId != null && androidId != "9774d56d682e549c") { // Emulador ID
                "OPTIMUS-${androidId.uppercase()}"
            } else {
                // Fallback: UUID salvo em SharedPreferences
                val prefs = context.getSharedPreferences("device_info", Context.MODE_PRIVATE)
                var uuid = prefs.getString("device_uuid", null)
                if (uuid == null) {
                    uuid = UUID.randomUUID().toString()
                    prefs.edit().putString("device_uuid", uuid).apply()
                }
                "OPTIMUS-${uuid.replace("-", "").uppercase()}"
            }
        } catch (e: Exception) {
            "OPTIMUS-${UUID.randomUUID().toString().replace("-", "").uppercase()}"
        }
    }

    /**
     * Formata MAC Address para padrão XX:XX:XX:XX:XX:XX
     */
    private fun formatMacAddress(mac: String): String {
        return mac.replace(":", "")
            .chunked(2)
            .joinToString(":")
            .uppercase()
    }

    /**
     * Gera MAC Address consistente baseado no Device ID
     */
    private fun generateMacFromDeviceId(deviceId: String): String {
        val hash = deviceId.hashCode().toString().padStart(12, '0')
        return hash.chunked(2)
            .take(6)
            .joinToString(":")
            .uppercase()
    }

    /**
     * Obtém modelo do dispositivo
     */
    fun getDeviceModel(): String {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else {
            "$manufacturer $model"
        }
    }

    /**
     * Obtém versão do Android
     */
    fun getAndroidVersion(): String {
        return "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }

    /**
     * Verifica se é Android TV / Fire Stick
     */
    fun isAndroidTV(context: Context): Boolean {
        return context.packageManager.hasSystemFeature("android.software.leanback")
    }
}
