package com.lenincompany.mychat.ui.main.contacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.FragmentContactsBinding
import com.lenincompany.mychat.models.Contact
import com.lenincompany.mychat.ui.main.chats.ChatsRecyclerAdapter
import com.lenincompany.mychat.ui.main.settings.SettingsFragment
import dagger.android.support.AndroidSupportInjection
import moxy.MvpAppCompatFragment
import moxy.presenter.ProvidePresenter
import javax.inject.Inject


class ContactsFragment: MvpAppCompatFragment(), ContactsView {
    @Inject
    lateinit var dataRepository: DataRepository

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    lateinit var rvAdapter: ContactsRecyclerAdapter
    val contactsList = mutableListOf<Contact>()
    private lateinit var binding: FragmentContactsBinding

    @ProvidePresenter
    fun providePresenter() = SettingsPresenter(dataRepository)

    companion object {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvAdapter = ContactsRecyclerAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = rvAdapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            getContacts()
        }
        askContactPermission()
    }


    private val requestPermissionLauncher = this.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getContacts()
        } else {
            Toast.makeText(requireContext(), "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
        }
    }

    // Запрос разрешения
    private fun askContactPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getContacts()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
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
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getContacts() {
        val contentResolver = requireContext().contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            while (cursor.moveToNext()) {
                val name = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                )
                val phone = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
                contactsList.add(Contact(name,phone))
            }
        }
        rvAdapter.setData(contactsList)
    }


}