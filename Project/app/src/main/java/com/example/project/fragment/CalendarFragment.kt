package com.example.project.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.project.R
import com.example.project.viewmodel.SharedViewModel

// Fragment クラスを継承
class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private lateinit var model: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }
}