package com.lenincompany.mychat.ui.chat.editChat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.lenincompany.mychat.R
import com.lenincompany.mychat.databinding.ActivityChatBinding
import com.lenincompany.mychat.databinding.ActivityChatEditBinding
import com.lenincompany.mychat.models.chat.Message
import dagger.android.AndroidInjection
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import java.time.LocalDate

class EditChatActivity : MvpAppCompatActivity() {

   // @InjectPresenter
  //  private lateinit var presenter: EditChatPresenter
    private lateinit var binding: ActivityChatEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityChatEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}