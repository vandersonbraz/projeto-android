package com.calmare.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calmare.app.R
import com.calmare.app.data.Sound
import com.google.android.material.chip.Chip

class SoundsAdapter(
    private var sounds: List<Sound>,
    private var favoriteSoundIds: Set<Int> = emptySet(),
    private val onSoundClick: (Sound) -> Unit,
    private val onFavoriteClick: (Sound) -> Unit
) : RecyclerView.Adapter<SoundsAdapter.SoundViewHolder>() {

    class SoundViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_sound_title)
        val tvDescription: TextView = view.findViewById(R.id.tv_sound_description)
        val tvDuration: TextView = view.findViewById(R.id.tv_duration)
        val tvCategory: TextView = view.findViewById(R.id.tv_category)
        val chipPremium: Chip = view.findViewById(R.id.chip_premium)
        val btnFavorite: ImageButton = view.findViewById(R.id.btn_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sound, parent, false)
        return SoundViewHolder(view)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        val sound = sounds[position]

        holder.tvTitle.text = sound.title
        holder.tvDescription.text = sound.description
        holder.tvDuration.text = formatDuration(sound.duration)
        holder.tvCategory.text = sound.category

        // Premium removido - todos os sons são gratuitos agora
        holder.chipPremium.visibility = View.GONE

        // Atualiza cor do coração (vermelho se favoritado, cinza se não)
        val isFavorite = favoriteSoundIds.contains(sound.id)
        if (isFavorite) {
            holder.btnFavorite.setColorFilter(holder.itemView.context.getColor(R.color.error))
            holder.btnFavorite.alpha = 1.0f
        } else {
            holder.btnFavorite.setColorFilter(holder.itemView.context.getColor(R.color.text_secondary))
            holder.btnFavorite.alpha = 0.5f
        }

        // Click listeners
        holder.itemView.setOnClickListener {
            onSoundClick(sound)
        }

        holder.btnFavorite.setOnClickListener {
            onFavoriteClick(sound)
        }
    }

    override fun getItemCount() = sounds.size

    fun updateSounds(newSounds: List<Sound>) {
        sounds = newSounds
        notifyDataSetChanged()
    }

    fun updateFavorites(newFavorites: Set<Int>) {
        favoriteSoundIds = newFavorites
        notifyDataSetChanged()
    }

    fun getCurrentSounds(): List<Sound> {
        return sounds
    }

    private fun formatDuration(seconds: Int): String {
        return if (seconds < 60) {
            "${seconds}s"
        } else {
            val minutes = seconds / 60
            "$minutes min"
        }
    }
}
