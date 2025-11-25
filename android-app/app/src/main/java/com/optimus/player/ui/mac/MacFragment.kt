package com.optimus.player.ui.mac

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.optimus.player.R
import com.optimus.player.databinding.FragmentMacBinding
import com.optimus.player.utils.DeviceUtils
import com.optimus.player.utils.PreferenceManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * MacFragment - Informações do Dispositivo e Licença
 * 
 * Exibe:
 * - MAC Address
 * - Device ID
 * - Status da licença (ativo/expirado)
 * - Data de expiração
 * - Detalhes do dispositivo (modelo, Android version)
 */
class MacFragment : Fragment() {

    private var _binding: FragmentMacBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMacBinding.inflate(inflater, container, false)
        prefManager = PreferenceManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loadDeviceInfo()
        loadLicenseInfo()
        setupCopyButtons()
    }

    private fun loadDeviceInfo() {
        // MAC Address
        val macAddress = DeviceUtils.getMacAddress(requireContext())
        binding.macValue.text = macAddress
        
        // Device ID
        val deviceId = DeviceUtils.getDeviceId(requireContext())
        binding.deviceIdValue.text = deviceId
        
        // Device Model
        binding.deviceModelValue.text = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
        
        // Android Version
        binding.androidVersionValue.text = "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})"
        
        // App Version
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            binding.appVersionValue.text = packageInfo.versionName
        } catch (e: Exception) {
            binding.appVersionValue.text = "1.0.0"
        }
    }

    private fun loadLicenseInfo() {
        val isActivated = prefManager.isActivated()
        val isValid = prefManager.isLicenseValid()
        
        if (isActivated && isValid) {
            binding.licenseStatus.text = getString(R.string.license_active)
            binding.licenseStatus.setTextColor(resources.getColor(R.color.gold_bright, null))
            binding.licenseMessage.text = getString(R.string.license_status_ok)
            
            // Expiration date
            val expiresAt = prefManager.getLicenseExpiresAt()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.validUntilValue.text = dateFormat.format(Date(expiresAt))
            
            // Days remaining
            val daysRemaining = ((expiresAt - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
            binding.daysRemainingValue.text = "$daysRemaining dias"
            
        } else {
            binding.licenseStatus.text = getString(R.string.license_expired_title)
            binding.licenseStatus.setTextColor(resources.getColor(R.color.error_red, null))
            binding.licenseMessage.text = getString(R.string.license_expired_message)
            binding.validUntilValue.text = "—"
            binding.daysRemainingValue.text = "—"
        }
        
        // License type
        binding.licenseTypeValue.text = getString(R.string.annual_premium)
        
        // Last sync
        binding.lastSyncValue.text = "Há 5 minutos"
    }

    private fun setupCopyButtons() {
        binding.copyMacBtn.setOnClickListener {
            copyToClipboard(binding.macValue.text.toString(), "MAC Address")
        }
        
        binding.copyDeviceIdBtn.setOnClickListener {
            copyToClipboard(binding.deviceIdValue.text.toString(), "Device ID")
        }
    }

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), getString(R.string.copied_toast), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
