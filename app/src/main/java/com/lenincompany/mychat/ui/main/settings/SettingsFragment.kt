package com.lenincompany.mychat.ui.main.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.FragmentSettingsBinding
import com.lenincompany.mychat.models.user.UserInfoResponse
import dagger.android.support.AndroidSupportInjection
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


class SettingsFragment: MvpAppCompatFragment(), SettingsView {
    @Inject
    lateinit var dataRepository: DataRepository

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @InjectPresenter
    lateinit var presenter: SettingsPresenter
    private lateinit var binding: FragmentSettingsBinding

    @ProvidePresenter
    fun providePresenter() = SettingsPresenter(dataRepository)

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 1001
        internal const val ARG_TYPE = "arg_type"
        /**
         * Returns created [WorksFragment] with given filter fragmentType
         */
        fun newInstance(type: String): SettingsFragment {
            val args = Bundle()
            args.putString(ARG_TYPE, type)
            return SettingsFragment().apply {
                arguments = args
            }
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
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
        presenter.downloadUserPhoto(sharedPrefs.getUserId())
        if(sharedPrefs.getEmail() == null || sharedPrefs.getUserName() == null)
            presenter.getInfoForUser(sharedPrefs.getUserId())
        else
        {
            binding.nameTv.text = sharedPrefs.getUserName()
            binding.emailTv.text = sharedPrefs.getEmail()
            binding.useridTv.text = sharedPrefs.getUserId().toString()
        }

        binding.updateImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE) // Укажите REQUEST_CODE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                presenter.uploadUserPhoto(requireContext(),sharedPrefs.getUserId(),getDriveFilePath(selectedImageUri))
            }
        }
    }

    fun getDriveFilePath(uri: Uri): File?{
        val file = File(requireContext().getCacheDir(), uri.lastPathSegment!!)
        try {
            val instream: InputStream = requireContext().getContentResolver().openInputStream(uri)!!
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
            Log.d("TAG1", "e: ${e}")
            return null
        }
    }

    override fun setInfoOnActivity(response: UserInfoResponse)
    {
        binding.nameTv.text = response.Name
        binding.emailTv.text = response.Email
        binding.useridTv.text = response.UserId.toString()
    }

    override fun setPhoto(inputStream: InputStream) {
        try {
            val byteArray = inputStream.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            inputStream.close()
            binding.imageView2.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("ApiError", "Error reading photo data: ${e.message}")
        }
    }

}