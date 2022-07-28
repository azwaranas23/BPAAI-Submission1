package com.dicoding.picodiploma.storyapp.ui.detailstory

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.network.StoryItem
import com.dicoding.picodiploma.storyapp.databinding.FragmentDetailStoryBinding
import com.dicoding.picodiploma.storyapp.ui.liststory.ListStoryActivity

class DetailStoryFragment : Fragment() {

    private lateinit var binding: FragmentDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context as Context)
            .inflateTransition(android.R.transition.move)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        DetailStoryFragmentArgs.fromBundle(arguments as Bundle).story.let {
            showStory(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showStory(story: StoryItem) {
        (requireActivity() as ListStoryActivity).supportActionBar?.title =
            getString(R.string.owner_story, story.name)

        binding.ivStory.transitionName = story.photoUrl

        binding.tvDesc.text = story.description

        Glide.with(binding.ivStory.context)
            .load(story.photoUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.ivErrorImg.visibility = View.VISIBLE
                    sharedElementEnterTransition = TransitionInflater.from(context as Context)
                        .inflateTransition(android.R.transition.no_transition)
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

            })
            .into(binding.ivStory)
    }
}