package com.nulp.moonice.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nulp.moonice.databinding.ItemLikeBinding
import com.nulp.moonice.model.LikeItem
import com.squareup.picasso.Picasso


class LikesAdapter(private val likeList: ArrayList<LikeItem>
) : RecyclerView.Adapter<LikesAdapter.LikesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLikeBinding.inflate(inflater, parent, false)

        return LikesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LikesViewHolder, position: Int) {
        val likeItem = likeList[position]
        with(holder.binding) {
            Log.d("onBindViewHolder LIKE", "${likeItem.username} and ${likeItem.profileImage}")
            holder.itemView.tag = likeItem
            usernameLike.text = likeItem.username
            if (likeItem.profileImage != null && likeItem.profileImage != "") {
                Picasso.get().load(likeItem.profileImage).into(avatarLike)
            }
        }
    }

    override fun getItemCount(): Int = likeList.size


    class LikesViewHolder(
        val binding: ItemLikeBinding
    ) : RecyclerView.ViewHolder(binding.root)
}