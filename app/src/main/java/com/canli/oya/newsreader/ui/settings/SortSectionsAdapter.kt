package com.canli.oya.newsreader.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.newsreader.databinding.ItemDragsortListBinding
import java.util.*


class SortSectionsAdapter(val sectionList : ArrayList<String>) : RecyclerView.Adapter<SortSectionsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSection = sectionList[position]
        holder.bind(currentSection)
    }

    override fun getItemCount(): Int = sectionList.size

    fun swapItems(from : Int, to : Int){
        Collections.swap(sectionList, from, to)
    }

    class ViewHolder private constructor(val binding: ItemDragsortListBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentSection : String){
            binding.itemSection.text = currentSection
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDragsortListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}