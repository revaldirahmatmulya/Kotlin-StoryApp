package com.revaldi.storyapp.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.revaldi.storyapp.Adapter.StoryAdapter
import com.revaldi.storyapp.Api.ApiConfig.instanceRetrofit
import com.revaldi.storyapp.Helper.PreferenceManager
import com.revaldi.storyapp.databinding.ActivityHomeBinding
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.revaldi.storyapp.Maps.StoryMapsActivity
import com.revaldi.storyapp.Models.StoriesData
import com.revaldi.storyapp.R
import com.revaldi.storyapp.ViewModel.MainViewModel
import kotlinx.coroutines.*
import androidx.activity.viewModels
import com.revaldi.storyapp.Adapter.LoadingStateAdapter
import com.revaldi.storyapp.ViewModel.ViewModelFactory
import okhttp3.Dispatcher


class HomeActivity : AppCompatActivity() {

    lateinit var binding : ActivityHomeBinding
    lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferencesManager: PreferenceManager
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }
    companion object {
        fun getLaunchService(from: Context) = Intent(from, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedPreferencesManager = PreferenceManager(this)
        progressBar = binding.progressBar
        binding.rvStory.layoutManager = LinearLayoutManager(this@HomeActivity)

        loadStories()

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, StoryAddActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogout.setOnClickListener(){
            sharedPreferencesManager.clearLoginData()
            startActivity(Intent(LoginActivity.getLaunchService(this@HomeActivity)))
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
    fun loadStories() {
        CoroutineScope(Dispatchers.Main).launch {
            showLoading(true)
            try {
                delay(5000)
                val adapter = StoryAdapter()
                binding.rvStory.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )
                mainViewModel.story.observe(this@HomeActivity, {
                    adapter.submitData(lifecycle, it)
                })
                adapter.setOnClickCallback(object : StoryAdapter.OnItemClickCallBack {
                    override fun onItemClicked(data: StoriesData) {
                        val intent =
                            Intent(this@HomeActivity, DetailStoryActivity::class.java)
                        intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, data.id)
                        startActivity(intent)
                    }
                })
            } catch (e: Exception){
                Log.e("API Error", e.message ?: "Unknown error occurred")

            } finally {
                showLoading(false)
            }

        }

    }

 /*  private fun loadStories() {
        CoroutineScope(Dispatchers.Main).launch {
            showLoading(true)
            try {
                delay(5000)
                val token = "Bearer ${sharedPreferencesManager.getToken()}"
                Log.e("Token Home", token)
                val response = instanceRetrofit.getStories(token,null,null,null)
                if (!response.error) {
                    val stories = response.listStory
                    storyAdapter = StoryAdapter(stories)
                    binding.rvStory.layoutManager = LinearLayoutManager(this@HomeActivity)
                    storyAdapter.setOnClickCallback(object : StoryAdapter.OnItemClickCallBack {
                        override fun onItemClicked(data: StoriesData) {
                            val intent =
                                Intent(this@HomeActivity, DetailStoryActivity::class.java)
                            intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, data.id)
                            startActivity(intent)
                        }
                    })
                    binding.rvStory.adapter = storyAdapter
                    Log.d("API Success", response.listStory.toString())
                } else {
                    Log.e("API Error", response.listStory.toString())
                }
            } catch (e: Exception) {
                Log.e("API Error", e.message ?: "Unknown error occurred")
            } finally {
                showLoading(false)
            }
        }
    }
    */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.maps_btn -> {
                val intent = Intent(this, StoryMapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}