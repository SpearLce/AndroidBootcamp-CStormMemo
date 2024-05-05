package com.illidancstormrage.cstormmemo.ui.editor.dialog.show

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.illidancstormrage.cstormmemo.R
import com.illidancstormrage.cstormmemo.databinding.ActivityShowBinding

class ShowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.showCSTextEditor.fromHtml(intent.getStringExtra("html") ?: "")
    }
}