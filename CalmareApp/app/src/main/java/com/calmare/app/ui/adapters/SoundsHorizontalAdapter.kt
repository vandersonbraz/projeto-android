package com.calmare.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calmare.app.R
import com.calmare.app.data.Sound
import com.google.android.material.chip.Chip

class SoundsHorizontalAdapter(
    private val sounds: List<Sound>,
    private val onSoundClick: (Sound) -> Unit
) : RecyclerView.Adapter<SoundsHorizontalAdapter.SoundViewHolder>() {

    class SoundViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tv_sound_emoji)
        val tvTitle: TextView = view.findViewById(R.id.tv_sound_title_horizontal)
        val tvDuration: TextView = view.findViewById(R.id.tv_duration_horizontal)
        val chipPremium: Chip = view.findViewById(R.id.chip_premium_horizontal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sound_horizontal, parent, false)
        return SoundViewHolder(view)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        val sound = sounds[position]

        // Set emoji based on title
        holder.tvEmoji.text = getSoundEmoji(sound.title)
        holder.tvTitle.text = sound.title
        holder.tvDuration.text = formatDuration(sound.duration)

        // Show premium badge if needed
        holder.chipPremium.visibility = if (sound.isPremium) View.VISIBLE else View.GONE

        // Click listener
        holder.itemView.setOnClickListener {
            onSoundClick(sound)
        }
    }

    override fun getItemCount() = sounds.size

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        return "$minutes min"
    }

    private fun getSoundEmoji(title: String): String {
        return when {
            title.contains("Chuva", ignoreCase = true) -> "🌧️"
            title.contains("Mar", ignoreCase = true) || title.contains("Onda", ignoreCase = true) -> "🌊"
            title.contains("Floresta", ignoreCase = true) -> "🌳"
            title.contains("Meditação", ignoreCase = true) -> "🧘"
            title.contains("Respiração", ignoreCase = true) -> "🫁"
            title.contains("Fogo", ignoreCase = true) || title.contains("Lareira", ignoreCase = true) -> "🔥"
            title.contains("Piano", ignoreCase = true) -> "🎹"
            title.contains("Ruído", ignoreCase = true) || title.contains("Sono", ignoreCase = true) -> "😴"
            else -> "🎵"
        }
    }
}
