package com.calmare.app.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.calmare.app.R
import com.calmare.app.managers.AdManager
import com.calmare.app.managers.BillingManager
import com.calmare.app.ui.fragments.FavoritesFragment
import com.calmare.app.ui.fragments.HomeFragment
import com.calmare.app.ui.fragments.SettingsFragment
import com.calmare.app.ui.fragments.SoundsFragment
import com.calmare.app.utils.ReminderManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var adManager: AdManager
    private lateinit var billingManager: BillingManager
    private lateinit var reminderManager: ReminderManager

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa managers
        adManager = AdManager(this).apply {
            initialize()
            preloadInterstitialAd()
            preloadRewardedAd()
        }

        billingManager = BillingManager(this, lifecycleScope).apply {
            initialize()
        }

        reminderManager = ReminderManager(this)

        setupNavigation()
        setupPremiumButton()

        // Carrega o fragment inicial (Home)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Observa status premium para controlar anúncios
        billingManager.isPremium.observe(this) { _ ->
            // Atualiza UI baseado em status premium
        }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_sounds -> {
                    loadFragment(SoundsFragment())
                    true
                }
                R.id.nav_favorites -> {
                    loadFragment(FavoritesFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    private fun setupPremiumButton() {
        findViewById<android.view.View>(R.id.btn_premium_badge)?.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }

        // Configura o botão do sino (notificações/lembretes)
        findViewById<android.view.View>(R.id.btn_notifications)?.setOnClickListener {
            showNotificationReminderDialog()
        }
    }

    private fun showNotificationReminderDialog() {
        // Verifica permissão de notificações (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Pede permissão
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
                return
            }
        }

        // Mostra opções de lembretes
        showReminderOptionsDialog()
    }

    private fun showReminderOptionsDialog() {
        val options = arrayOf(
            "⏰ Ativar Lembretes Padrão (8h, 12h, 20h)",
            "🕐 Escolher Horário Personalizado",
            "❌ Desativar Todos os Lembretes"
        )

        AlertDialog.Builder(this)
            .setTitle("🔔 Lembretes de Meditação")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> activateDefaultReminders()
                    1 -> showCustomTimePickerDialog()
                    2 -> deactivateAllReminders()
                }
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }

    private fun activateDefaultReminders() {
        reminderManager.scheduleDefaultReminders()
        Toast.makeText(
            this,
            "✅ Lembretes ativados!\n🌅 8h  |  ☀️ 12h  |  🌙 20h",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showCustomTimePickerDialog() {
        val calendar = java.util.Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                reminderManager.scheduleReminder(
                    hourOfDay,
                    minute,
                    "🧘 Hora de Meditar",
                    "Reserve alguns minutos para sua paz interior"
                )
                Toast.makeText(
                    this,
                    "✅ Lembrete agendado para ${String.format("%02d:%02d", hourOfDay, minute)}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE),
            true
        ).show()
    }

    private fun deactivateAllReminders() {
        // Cancela horários padrão
        reminderManager.cancelReminder(8, 0)
        reminderManager.cancelReminder(12, 0)
        reminderManager.cancelReminder(20, 0)

        Toast.makeText(
            this,
            "🔕 Todos os lembretes foram desativados",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showReminderOptionsDialog()
            } else {
                Toast.makeText(
                    this,
                    "⚠️ Permissão de notificações necessária para lembretes",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
    }
}
