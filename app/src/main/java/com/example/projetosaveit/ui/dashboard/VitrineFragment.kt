package com.example.projetosaveit.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projetosaveit.databinding.FragmentVitrineBinding

class VitrineFragment : Fragment() {
    private var binding: FragmentVitrineBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val dashboardViewModel =
            ViewModelProvider(this).get(VitrineViewModel::class.java)

        binding = FragmentVitrineBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val textView = binding!!.textDashboard
        dashboardViewModel.text.observe(
            viewLifecycleOwner
        ) { text: String? ->
            textView.text =
                text
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}