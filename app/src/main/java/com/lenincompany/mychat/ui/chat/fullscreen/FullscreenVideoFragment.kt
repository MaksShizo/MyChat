package com.lenincompany.mychat.ui.chat.fullscreen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lenincompany.mychat.databinding.FragmentFullscreenVideoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullscreenVideoFragment : Fragment() {
    private lateinit var binding: FragmentFullscreenVideoBinding
    companion object {
        fun newInstance(videoUri: Uri): FullscreenVideoFragment {
            val fragment = FullscreenVideoFragment()
            val args = Bundle()
            args.putParcelable("video_uri", videoUri)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullscreenVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val videoUri = arguments?.getParcelable<Uri>("video_uri")
        if (videoUri != null) {
            binding.fullscreenVideoView.setVideoURI(videoUri)
            binding.fullscreenVideoView.start()
        } else {
            Toast.makeText(context, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        binding.closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
