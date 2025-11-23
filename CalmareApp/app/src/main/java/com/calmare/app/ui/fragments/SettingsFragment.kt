package com.calmare.app.ui.fragments

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.calmare.app.R
import com.calmare.app.data.PreferencesManager
import com.calmare.app.managers.AdManager
import com.calmare.app.managers.BillingManager
import com.calmare.app.ui.PremiumActivity
import com.calmare.app.utils.ReminderManager
import com.calmare.app.utils.ReminderStorage
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import java.util.Calendar

class SettingsFragment : Fragment() {

    private lateinit var adManager: AdManager
    private lateinit var billingManager: BillingManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var reminderManager: ReminderManager
    private lateinit var reminderStorage: ReminderStorage

    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var switchAutoPlay: SwitchMaterial
    private lateinit var switchDownloadWifi: SwitchMaterial
    private lateinit var btnPremium: Button
    private lateinit var tvPremiumStatus: TextView
    private lateinit var cardPremium: MaterialCardView
    private lateinit var btnConfigureReminders: View

    private var isTogglingProgrammatically = false
    private var isRequestingPermissionForReminders = false

    // Permission launcher para Android 13+
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (isRequestingPermissionForReminders) {
                // Permissão concedida para configurar lembretes
                showManageRemindersDialog()
            } else {
                // Permissão concedida apenas para ativar notificações
                preferencesManager.setNotificationsEnabled(true)
                Toast.makeText(
                    requireContext(),
                    "✅ Notificações ativadas!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Permissão negada
            isTogglingProgrammatically = true
            switchNotifications.isChecked = false
            isTogglingProgrammatically = false
            Toast.makeText(
                requireContext(),
                "Permissão de notificação necessária",
                Toast.LENGTH_SHORT
            ).show()
        }
        isRequestingPermissionForReminders = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adManager = AdManager(requireContext())
        billingManager = BillingManager(requireContext(), lifecycleScope)
        preferencesManager = PreferencesManager(requireContext())
        reminderManager = ReminderManager(requireContext())
        reminderStorage = ReminderStorage(requireContext())

        // Carrega banner de anúncio
        val adContainer = view.findViewById<FrameLayout>(R.id.ad_container)
        adManager.loadBannerAd(adContainer)

        // Initialize views
        switchNotifications = view.findViewById(R.id.switch_notifications)
        switchAutoPlay = view.findViewById(R.id.switch_autoplay)
        switchDownloadWifi = view.findViewById(R.id.switch_download_wifi)
        btnPremium = view.findViewById(R.id.btn_premium)
        tvPremiumStatus = view.findViewById(R.id.tv_premium_status)
        cardPremium = view.findViewById(R.id.card_premium)
        btnConfigureReminders = view.findViewById(R.id.btn_configure_reminders)

        setupListeners()
        loadSettings()
        updateNotificationSwitch()
    }

    private fun setupListeners() {
        // Notification toggle - apenas liga/desliga notificações e pede permissões
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isTogglingProgrammatically) return@setOnCheckedChangeListener

            if (isChecked) {
                // Usuário quer ativar notificações - pede permissão
                checkNotificationPermission()
            } else {
                // Usuário quer desativar notificações
                lifecycleScope.launch {
                    preferencesManager.setNotificationsEnabled(false)
                    Toast.makeText(
                        requireContext(),
                        "Notificações desativadas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Botão Configure Reminders - abre gerenciamento de lembretes
        btnConfigureReminders.setOnClickListener {
            openConfigureReminders()
        }

        // Auto-play toggle
        switchAutoPlay.setOnCheckedChangeListener { _, isChecked ->
            saveAutoPlaySetting(isChecked)
        }

        // Download only on WiFi toggle
        switchDownloadWifi.setOnCheckedChangeListener { _, isChecked ->
            saveDownloadWifiSetting(isChecked)
        }

        // Premium button
        btnPremium.setOnClickListener {
            openPremiumActivity()
        }

        // Check premium status
        billingManager.initialize()
        billingManager.isPremium.observe(viewLifecycleOwner) { isPremium ->
            updatePremiumUI(isPremium)
        }
    }

    private fun checkNotificationPermission() {
        // Apenas pede permissão, não abre time picker
        isRequestingPermissionForReminders = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permissão já concedida
                    lifecycleScope.launch {
                        preferencesManager.setNotificationsEnabled(true)
                        Toast.makeText(
                            requireContext(),
                            "✅ Notificações ativadas!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> {
                    // Pede permissão
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android < 13 não precisa de permissão runtime
            lifecycleScope.launch {
                preferencesManager.setNotificationsEnabled(true)
                Toast.makeText(
                    requireContext(),
                    "✅ Notificações ativadas!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openConfigureReminders() {
        // Primeiro verifica se tem permissão
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permissão já concedida, abre gerenciamento
                    showManageRemindersDialog()
                }
                else -> {
                    // Pede permissão para poder configurar lembretes
                    isRequestingPermissionForReminders = true
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android < 13 não precisa de permissão runtime
            showManageRemindersDialog()
        }
    }

    private fun updateNotificationSwitch() {
        // Atualiza o switch baseado se notificações estão ativadas
        lifecycleScope.launch {
            preferencesManager.notificationsEnabled.collect { enabled ->
                isTogglingProgrammatically = true
                switchNotifications.isChecked = enabled
                isTogglingProgrammatically = false
            }
        }
    }

    private fun showAddReminderDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                // Adiciona o novo lembrete
                addReminder(selectedHour, selectedMinute)
            },
            hour,
            minute,
            true // Formato 24 horas
        ).show()
    }

    private fun addReminder(hour: Int, minute: Int) {
        // Verifica se já existe
        val existingReminders = reminderStorage.getReminders()
        if (existingReminders.any { it.hour == hour && it.minute == minute }) {
            Toast.makeText(
                requireContext(),
                "Lembrete já existe para este horário",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Adiciona ao storage
        reminderStorage.addReminder(hour, minute)

        // Agenda o alarme
        reminderManager.scheduleReminder(
            hour, minute,
            "🧘 Hora de Meditar",
            "Reserve alguns minutos para sua paz interior"
        )

        // Atualiza o switch
        updateNotificationSwitch()

        // Mostra confirmação
        Toast.makeText(
            requireContext(),
            String.format(java.util.Locale.getDefault(), "✅ Lembrete agendado para %02d:%02d", hour, minute),
            Toast.LENGTH_SHORT
        ).show()

        // Pergunta se quer adicionar mais
        AlertDialog.Builder(requireContext())
            .setTitle("Lembrete Adicionado")
            .setMessage("Deseja adicionar outro horário?")
            .setPositiveButton("Sim") { _, _ ->
                showAddReminderDialog()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun showManageRemindersDialog() {
        val reminders = reminderStorage.getReminders()
        if (reminders.isEmpty()) {
            showAddReminderDialog()
            return
        }

        val items = reminders.map {
            String.format(java.util.Locale.getDefault(), "⏰ %02d:%02d", it.hour, it.minute)
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Lembretes Configurados (${reminders.size})")
            .setItems(items) { _, which ->
                // Ao clicar em um lembrete, pergunta se quer deletar
                val reminder = reminders[which]
                showDeleteReminderDialog(reminder.hour, reminder.minute)
            }
            .setPositiveButton("Adicionar Novo") { _, _ ->
                showAddReminderDialog()
            }
            .setNegativeButton("Fechar", null)
            .show()
    }

    private fun showDeleteReminderDialog(hour: Int, minute: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remover Lembrete")
            .setMessage(String.format(
                java.util.Locale.getDefault(),
                "Deseja remover o lembrete de %02d:%02d?",
                hour, minute
            ))
            .setPositiveButton("Remover") { _, _ ->
                deleteReminder(hour, minute)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteReminder(hour: Int, minute: Int) {
        // Remove do storage
        reminderStorage.removeReminder(hour, minute)

        // Cancela o alarme
        reminderManager.cancelReminder(hour, minute)

        // Atualiza o switch
        updateNotificationSwitch()

        Toast.makeText(
            requireContext(),
            "Lembrete removido",
            Toast.LENGTH_SHORT
        ).show()

        // Se ainda tem lembretes, mostra a lista novamente
        val remainingReminders = reminderStorage.getReminders()
        if (remainingReminders.isNotEmpty()) {
            showManageRemindersDialog()
        }
    }

    private fun showDisableRemindersDialog() {
        val reminders = reminderStorage.getReminders()
        if (reminders.isEmpty()) {
            // Não tem lembretes, só desliga o switch
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Desativar Lembretes")
            .setMessage("Deseja remover todos os ${reminders.size} lembretes configurados?")
            .setPositiveButton("Sim") { _, _ ->
                disableAllReminders()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                // Usuário cancelou, volta o switch para ligado
                isTogglingProgrammatically = true
                switchNotifications.isChecked = true
                isTogglingProgrammatically = false
            }
            .setOnCancelListener {
                // Usuário cancelou, volta o switch para ligado
                isTogglingProgrammatically = true
                switchNotifications.isChecked = true
                isTogglingProgrammatically = false
            }
            .show()
    }

    private fun disableAllReminders() {
        val reminders = reminderStorage.getReminders()

        // Cancela todos os alarmes
        reminders.forEach { reminder ->
            reminderManager.cancelReminder(reminder.hour, reminder.minute)
        }

        // Limpa o storage
        reminderStorage.clearAllReminders()

        // Atualiza o switch
        updateNotificationSwitch()

        Toast.makeText(
            requireContext(),
            "Todos os lembretes foram removidos",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadSettings() {
        // Carrega configurações do DataStore
        lifecycleScope.launch {
            preferencesManager.autoPlayEnabled.collect { enabled ->
                switchAutoPlay.isChecked = enabled
            }
        }
        lifecycleScope.launch {
            preferencesManager.downloadWifiOnly.collect { enabled ->
                switchDownloadWifi.isChecked = enabled
            }
        }
    }

    private fun saveAutoPlaySetting(enabled: Boolean) {
        lifecycleScope.launch {
            preferencesManager.setAutoPlayEnabled(enabled)
        }
    }

    private fun saveDownloadWifiSetting(enabled: Boolean) {
        lifecycleScope.launch {
            preferencesManager.setDownloadWifiOnly(enabled)
        }
    }

    private fun updatePremiumUI(isPremium: Boolean) {
        if (isPremium) {
            tvPremiumStatus.text = "✨ Você é Premium!"
            tvPremiumStatus.setTextColor(resources.getColor(R.color.accent, null))
            btnPremium.text = "Gerenciar Assinatura"
            cardPremium.setCardBackgroundColor(resources.getColor(R.color.primary_light, null))
        } else {
            tvPremiumStatus.text = "Desbloqueie recursos premium"
            tvPremiumStatus.setTextColor(resources.getColor(R.color.text_secondary, null))
            btnPremium.text = "Assinar Premium"
            cardPremium.setCardBackgroundColor(resources.getColor(R.color.card_background, null))
        }
    }

    private fun openPremiumActivity() {
        val intent = Intent(requireContext(), PremiumActivity::class.java)
        startActivity(intent)
    }
}
