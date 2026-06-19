package com.mobile.agregarural.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.agregarural.data.model.Banner
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.ItemBannerBinding

class BannerAdapter(
    private val listaBanners: List<Banner>
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(
        val binding: ItemBannerBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = listaBanners[position]

        Glide.with(holder.itemView.context)
            .load(banner.url)
            .placeholder(R.drawable.anuncio1)
            .error(R.drawable.anuncio1)
            .centerCrop()
            .into(holder.binding.imgBanner)
    }

    override fun getItemCount(): Int {
        return listaBanners.size
    }
}