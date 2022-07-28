package com.dicoding.picodiploma.storyapp.ui.liststory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.preference.SessionPreferences

class ListStoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_story)
        findNavController(R.id.nav_host_story).setGraph(
            R.navigation.story_navigation,
            intent.extras
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.story_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_story -> {
                Navigation.findNavController(this, R.id.nav_host_story)
                    .navigate(R.id.action_listStoryFragment_to_addStoryActivity)
                true
            }

            R.id.logout -> {
                SessionPreferences(this).clearSession()
                Navigation.findNavController(this, R.id.nav_host_story)
                    .navigate(R.id.action_listStoryFragment_to_mainActivity)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}