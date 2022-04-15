package com.nulp.moonice.ui.books

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nulp.moonice.PlayerActivity
import com.nulp.moonice.databinding.FragmentBooksBinding

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(BooksViewModel::class.java)

        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textBooks
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

//        binding.testActivity.setOnClickListener {
//            val intent = Intent(this, PlayerActivity::class.java)
//            // start your next activity
//            startActivity(intent)
//        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}