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

        // Mostra badge premium se necessário
        holder.chipPremium.visibility = if (sound.isPremium) View.VISIBLE else View.GONE

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

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        return "$minutes min"
    }
}
