package com.optimus.player.ui.activation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.optimus.player.R
import com.optimus.player.databinding.ActivityActivationBinding
import com.optimus.player.ui.MainActivity
import com.optimus.player.utils.DeviceUtils
import com.optimus.player.utils.PreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Tela de Ativação de Licença
 * Mostra MAC Address, Device ID e permite inserir código de ativação
 */
class ActivationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActivationBinding
    private lateinit var prefManager: PreferenceManager
    private var macAddress: String = ""
    private var deviceId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferenceManager(this)

        setupDeviceInfo()
        setupListeners()
        setupCodeInput()
    }

    private fun setupDeviceInfo() {
        // Get MAC Address
        macAddress = DeviceUtils.getMacAddress(this)
        binding.macValue.text = macAddress
        prefManager.setMacAddress(macAddress)

        // Get Device ID
        deviceId = DeviceUtils.getDeviceId(this)
        binding.deviceIdValue.text = deviceId
        prefManager.setDeviceId(deviceId)
    }

    private fun setupListeners() {
        // Copy MAC Address
        binding.copyMacBtn.setOnClickListener {
            copyToClipboard(macAddress, "MAC Address")
        }

        // Copy Device ID
        binding.copyDeviceBtn.setOnClickListener {
            copyToClipboard(deviceId, "Device ID")
        }

        // Activate Button
        binding.activateBtn.setOnClickListener {
            val code = binding.codeInput.text?.toString()?.trim()
            if (code.isNullOrEmpty()) {
                binding.codeInputLayout.error = "Digite o código de ativação"
                return@setOnClickListener
            }

            if (code.length < 14) {
                binding.codeInputLayout.error = "Código inválido"
                return@setOnClickListener
            }

            activateLicense(code)
        }
    }

    private fun setupCodeInput() {
        // Auto-format code as XXXX-XXXX-XXXX
        binding.codeInput.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private var deletingHyphen = false
            private var hyphenStart = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (isFormatting) return

                // Detect if user is deleting a hyphen
                if (count > 0 && after == 0) {
                    val char = s?.getOrNull(start)
                    if (char == '-') {
                        deletingHyphen = true
                        hyphenStart = start
                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true

                // Remove all hyphens
                val cleanString = s.toString().replace("-", "").uppercase()

                // If deleting hyphen, also delete preceding character
                if (deletingHyphen && hyphenStart > 0) {
                    val newString = cleanString.removeRange(hyphenStart - 1, hyphenStart)
                    s?.clear()
                    s?.append(formatCode(newString))
                    deletingHyphen = false
                    isFormatting = false
                    return
                }

                // Format as XXXX-XXXX-XXXX
                val formatted = formatCode(cleanString)

                if (formatted != s.toString()) {
                    s?.replace(0, s.length, formatted)
                }

                isFormatting = false
            }

            private fun formatCode(code: String): String {
                val cleaned = code.take(12) // Max 12 chars
                return cleaned.chunked(4).joinToString("-")
            }
        })

        // Clear error on text change
        binding.codeInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.codeInputLayout.error = null
            }
        }
    }

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, getString(R.string.copied_toast), Toast.LENGTH_SHORT).show()
    }

    private fun activateLicense(code: String) {
        // Show loading
        binding.activateBtn.isEnabled = false
        binding.activateBtn.text = getString(R.string.loading)

        lifecycleScope.launch {
            try {
                // TODO: Call API to validate code
                // For now, simulate API call
                delay(1500)

                // Simulate success (any code with correct format is valid for now)
                if (code.matches(Regex("^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$"))) {
                    onActivationSuccess(code)
                } else {
                    onActivationError("Código inválido")
                }

            } catch (e: Exception) {
                onActivationError(e.message ?: "Erro desconhecido")
            }
        }
    }

    private fun onActivationSuccess(code: String) {
        // Save activation
        prefManager.setActivated(true)
        prefManager.setActivationCode(code)

        // Set license expiration (1 year from now)
        val oneYearFromNow = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)
        prefManager.setLicenseExpiresAt(oneYearFromNow)

        // Show success message
        Toast.makeText(
            this,
            getString(R.string.activation_success),
            Toast.LENGTH_LONG
        ).show()

        // Navigate to main app
        lifecycleScope.launch {
            delay(500)
            val intent = Intent(this@ActivationActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun onActivationError(message: String) {
        binding.activateBtn.isEnabled = true
        binding.activateBtn.text = getString(R.string.activate_button)
        binding.codeInputLayout.error = message
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
