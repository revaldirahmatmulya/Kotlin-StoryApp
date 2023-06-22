package com.revaldi.storyapp.UI


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.revaldi.storyapp.Api.ApiConfig.instanceRetrofit
import com.revaldi.storyapp.Helper.PreferenceManager
import com.revaldi.storyapp.R
import com.revaldi.storyapp.databinding.ActivityDetailStoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityDetailStoryBinding
    companion object {
        const val EXTRA_STORY_ID = "storyId"
    }

    private lateinit var storyId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getStringExtra(EXTRA_STORY_ID)

        CoroutineScope(Dispatchers.Main).launch {
            showLoading(true)

            loadDetailStory(id.toString())

            showLoading(false)
        }

    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = android.view.View.VISIBLE
        } else {
            binding.progressBar.visibility = android.view.View.GONE
        }
    }


    private fun loadDetailStory(id:String) {
        val sharedPreferencesManager = PreferenceManager(this@DetailStoryActivity)
        val token =   "Bearer ${sharedPreferencesManager.getToken()}"
        println(token)
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = instanceRetrofit.getDetailStories(token, id)
                if (!response.error) {
                    val story = response.story
                    val location = "Location of this story : ${story.lat}, ${story.lon}"
                    binding.tvUsername.text = story.name
                    binding.tvCreatedAt.text = story.createdAt
                    binding.tvDescription.text = story.description
                    binding.tvLocation.text = location
                    //binding image with glde
                    Glide.with(this@DetailStoryActivity)
                        .load(story.photoUrl)
                        .error(R.drawable.ic_error) // Gambar yang ditampilkan saat error
                        .centerCrop()
                        .into(binding.ivStory)
                    Log.d("API Success", response.story.toString())
                } else {
                    Log.e("API Error", response.story.toString())
                }
            } catch (e: Exception) {
                Log.e("API Error", e.message ?: "Unknown error occurred")
            }
        }
    }
}