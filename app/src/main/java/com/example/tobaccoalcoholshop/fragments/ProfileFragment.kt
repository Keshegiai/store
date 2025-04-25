package com.example.tobaccoalcoholshop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tobaccoalcoholshop.MainActivity
import com.example.tobaccoalcoholshop.R

class ProfileFragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var logoutButton: Button

    companion object {
        private const val ARG_USERNAME = "username"

        fun newInstance(username: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameTextView = view.findViewById(R.id.usernameTextView)
        logoutButton = view.findViewById(R.id.logoutButton)

        val username = arguments?.getString(ARG_USERNAME) ?: "Ошибка"
        usernameTextView.text = username

        logoutButton.setOnClickListener {
            (activity as? MainActivity)?.logoutUser()
        }
    }
}