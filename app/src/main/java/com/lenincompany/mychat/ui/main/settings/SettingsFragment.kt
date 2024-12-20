package com.lenincompany.mychat.ui.main.settings

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lenincompany.mychat.databinding.FragmentSettingsBinding
import com.lenincompany.mychat.models.user.UserInfoResponse
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var binding: FragmentSettingsBinding

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 1001
        internal const val ARG_TYPE = "arg_type"

        fun newInstance(type: String): SettingsFragment {
            val args = Bundle()
            args.putString(ARG_TYPE, type)
            return SettingsFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        //settingsViewModel.downloadUserPhoto()
        //if (settingsViewModel.isUserInfoAvailable()) {
        //    populateUserInfo()
        //} else {
        //
        //}
        settingsViewModel.fetchUserInfo()
        binding.updateImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE)
        }
    }

    private fun setupObservers() {
        settingsViewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
            setInfoOnActivity(userInfo)
        }

        settingsViewModel.userPhoto.observe(viewLifecycleOwner) { userPhoto ->
            Picasso.get().load(userPhoto).into(binding.imageView2)
        }

        settingsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("SettingsFragment", "Error: $errorMessage")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                // Получаем MIME-тип файла
                val mimeType = requireContext().contentResolver.getType(selectedImageUri)
                // Пытаемся получить файл
                val file = getDriveFilePath(selectedImageUri)
                if (file != null) {
                    // Если MIME-тип известен, передаем его при загрузке
                    if (mimeType != null) {
                        settingsViewModel.uploadUserPhoto(file, mimeType)
                    } else {
                        // Если MIME-тип не определился, можно попробовать по умолчанию
                        settingsViewModel.uploadUserPhoto(file, "image/*")
                    }
                }
            }
        }
    }

    private fun getDriveFilePath(uri: Uri): File? {
        val file = File(requireContext().cacheDir, uri.lastPathSegment!!)
        try {
            val instream: InputStream = requireContext().contentResolver.openInputStream(uri)!!
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var size: Int
            while (instream.read(buffer).also { size = it } != -1) {
                output.write(buffer, 0, size)
            }
            instream.close()
            output.close()
            return file
        } catch (e: IOException) {
            Log.d("SettingsFragment", "Error creating file: ${e}")
            return null
        }
    }

    private fun populateUserInfo() {
        binding.nameTv.text = settingsViewModel.getUserName()
        binding.emailTv.text = settingsViewModel.getUserEmail()
        binding.useridTv.text = settingsViewModel.getUserId().toString()
    }

    private fun setInfoOnActivity(response: UserInfoResponse) {
        binding.nameTv.text = response.Name
        binding.emailTv.text = response.Email
        binding.useridTv.text = response.UserId.toString()
        Picasso.get().load(response.Photo).into(binding.imageView2);
    }
}
