package com.lenincompany.mychat.ui.chat.fullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lenincompany.mychat.databinding.FragmentFullscreenImageBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullscreenImageFragment : Fragment() {
    private lateinit var binding: FragmentFullscreenImageBinding
    companion object {
        fun newInstance(imageUrl: String): FullscreenImageFragment {
            val fragment = FullscreenImageFragment()
            val args = Bundle()
            args.putString("image_url", imageUrl)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullscreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageUrl = arguments?.getString("image_url")
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(binding.fullscreenImageView)
        } else {
            Toast.makeText(context, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        // Закрыть Fragment при нажатии на кнопку
        binding.closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
