package com.lenincompany.mychat.ui.chat.edit

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lenincompany.mychat.databinding.ActivityChatEditBinding
import com.lenincompany.mychat.models.Contact
import com.lenincompany.mychat.models.chat.ChatInfo
import com.lenincompany.mychat.ui.chat.ChatActivity
import com.lenincompany.mychat.ui.main.settings.SettingsFragment.Companion.PICK_IMAGE_REQUEST_CODE
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class EditActivity : AppCompatActivity(){

    private lateinit var binding: ActivityChatEditBinding
    private val editViewModel: EditViewModel by viewModels()
    private lateinit var rvAdapter: EditChatUsersRecyclerAdapter
    private var chatId = 0
    private val contactsList = mutableListOf<Contact>()
    private val requestPermissionLauncher = this.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getContacts()
        } else {
            Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        private const val EXTRA_CHAT_ID = "extra_chat_id"
        /**
         * Creates [Intent] for starting [ChatActivity] with extra parameter
         */
        fun forIntent(
            packageContext: Context,
            chatId: Int,
        ): Intent {
            return Intent(packageContext, EditActivity::class.java).apply {
                putExtra(EXTRA_CHAT_ID, chatId)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatEditBinding.inflate(layoutInflater)
        rvAdapter = EditChatUsersRecyclerAdapter(mutableListOf(), { message -> }, mutableListOf())
        binding.usersRv.layoutManager = LinearLayoutManager(this)
        binding.usersRv.adapter = rvAdapter
        setupObservers()
        setContentView(binding.root)

        if (intent != null) {
            chatId = intent.getIntExtra(EXTRA_CHAT_ID, 0)
        }
        editViewModel.getGroupChatInfo(chatId)

        binding.addUser.setOnClickListener {
            askContactPermission()
        }

        binding.addImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE)
        }
    }

    private fun setupObservers() {
        editViewModel.addUser.observe(this) { flag ->
            if(flag) editViewModel.getGroupChatInfo(chatId)
        }

        editViewModel.usersInfo.observe(this) { users ->
            if(!users.isNullOrEmpty())
            showUserSelectionDialog(users.map { Contact(it.Name, it.Phone, it.UserId) }.toList()) {
                editViewModel.addUser(
                    chatId,
                    it.filter { it.userId != null }.map { it.userId!! }.toList()
                )
            }
        }

        editViewModel.usersPhoto.observe(this) { userPhoto ->
            rvAdapter.addUsersPhoto(userPhoto)
        }

        editViewModel.chatInfo.observe(this) { chatInfo ->
            setChatsInfo(chatInfo)
        }

        editViewModel.chatPhoto.observe(this) { chatPhoto ->
            Picasso.get().load(chatPhoto).into(binding.imageChat)
        }

        editViewModel.errorMessage.observe(this) { errorMessage ->
            Log.e("ChatsFragment", "Error: $errorMessage")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                val mimeType = contentResolver.getType(selectedImageUri)
                val file = getDriveFilePath(selectedImageUri)
                if (file != null) {
                    // Если MIME-тип известен, передаем его при загрузке
                    if (mimeType != null) {
                        editViewModel.uploadChatPhoto(chatId, file, mimeType)
                    } else {
                        // Если MIME-тип не определился, можно попробовать по умолчанию
                        editViewModel.uploadChatPhoto(chatId, file, "image/*")
                    }
                }
            }
        }
    }


    private fun getDriveFilePath(uri: Uri): File? {
        val file = File(this.cacheDir, uri.lastPathSegment!!)
        try {
            val instream: InputStream = this.contentResolver.openInputStream(uri)!!
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

    private fun setChatsInfo(chatInfo: ChatInfo) {
        binding.nameTv.text = chatInfo.name
        if(chatInfo.photo!=null)
            Picasso.get().load(chatInfo.photo).into(binding.imageChat)
        rvAdapter.setData(chatInfo.groupChatUsers)
        editViewModel.getUserPhotos(chatInfo.groupChatUsers)
    }

    fun showUserSelectionDialog(
        userList: List<Contact>, // список пользователей
        onUsersSelected: (selectedUsers: List<Contact>) -> Unit // колбэк для выбранных пользователей
    ) {
        val selectedItems = mutableSetOf<Int>() // Хранит индексы выбранных пользователей
        val userNames = userList.map { it.name }.toTypedArray() // Имена пользователей

        AlertDialog.Builder(this)
            .setTitle("Выберите пользователей для добавления")
            .setMultiChoiceItems(userNames, null) { _, index, isChecked ->
                if (isChecked) {
                    selectedItems.add(index)
                } else {
                    selectedItems.remove(index)
                }
            }
            .setPositiveButton("Добавить") { _, _ ->
                // Передаем список выбранных пользователей через колбэк
                val selectedUsers = selectedItems.map { userList[it] }
                onUsersSelected(selectedUsers)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // Запрос разрешения
    private fun askContactPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getContacts()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun getContacts() {
        val contentResolver = contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
        )

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            while (cursor.moveToNext()) {
                val name = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                )
                val phone = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                )
                contactsList.add(Contact(name,phone))
            }
        }
        if(contactsList.isNotEmpty())
        {
            editViewModel.getUsersForPhone(contactsList)
        }
    }

}