package com.lenincompany.mychat.ui.main

import android.os.Bundle
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.ActivityMainBinding
import com.lenincompany.mychat.ui.main.chats.ChatsFragment
import com.lenincompany.mychat.ui.main.contacts.ContactsFragment
import com.lenincompany.mychat.ui.main.settings.SettingsFragment
import dagger.android.AndroidInjection
import moxy.MvpAppCompatActivity
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf.Visibility

class MainActivity : MvpAppCompatActivity(), MainView {

    @Inject
    lateinit var dataRepository: DataRepository

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ChatsFragment())
                .commit()
        }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chats -> {
                    binding.toolbar.isGone = false
                    binding.titleTv.setText(R.string.nav_bottom_chats)
                    openFragment(ChatsFragment())
                    true
                }
                R.id.nav_contacts -> {
                    binding.toolbar.isGone = false
                    binding.titleTv.setText(R.string.nav_bottom_contacts)
                    openFragment(ContactsFragment())
                    true
                }
                R.id.nav_settings -> {
                    binding.toolbar.isGone = true
                    openFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}