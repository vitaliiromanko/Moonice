package com.nulp.moonice.ui.fragment.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.databinding.FragmentBooksBinding
import com.nulp.moonice.utils.FIREBASE_URL

class SearchFragment: Fragment() {
    private var _binding: FragmentBooksBinding? = null
    private lateinit var popularBooksRecyclerView: RecyclerView

    private lateinit var ref: DatabaseReference

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference


        return binding.root
    }

}