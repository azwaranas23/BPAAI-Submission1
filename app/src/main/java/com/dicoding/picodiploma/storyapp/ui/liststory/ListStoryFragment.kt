package com.dicoding.picodiploma.storyapp.ui.liststory

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.preference.SessionPreferences
import com.dicoding.picodiploma.storyapp.network.StoryItem
import com.dicoding.picodiploma.storyapp.databinding.FragmentListStoryBinding
import com.dicoding.picodiploma.storyapp.databinding.ItemRowStoryBinding

class ListStoryFragment : Fragment() {

    private lateinit var binding: FragmentListStoryBinding
    private lateinit var viewModel: ListStoryViewModel
    private lateinit var session: SessionPreferences
    private var state: Parcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListStoryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onPause() {
        super.onPause()
        binding.rvStories.layoutManager?.onSaveInstanceState().let {
            state = it
        }
    }

    override fun onResume() {
        super.onResume()
        state?.let {
            binding.rvStories.layoutManager?.onRestoreInstanceState(it)
        }
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.app_name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionPreferences(view.context)
        val token = session.getAuthToken() ?: ""

        viewModel = ViewModelProvider(
            this,
            ListStoryViewmodelFactory(token)
        )[ListStoryViewModel::class.java]
        viewModel.listStory.observe(viewLifecycleOwner) { stories ->
            showRecyclerList(stories)
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            when {
                error.isError && error.type == ListStoryViewModel.ErrorType.NO_DATA ->
                    showError(true, getString(R.string.no_data_to_display))
                error.isError ->
                    error.errorMsg?.let { showError(true, it) }
                else -> showError(false)
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        binding.btnLoad.setOnClickListener {
            loadStory(token)
        }

        binding.btnRetry.setOnClickListener {
            loadStory(token)
        }

        binding.srlStories.setOnRefreshListener {
            loadStory(token)
        }

        ListStoryActivityArgs.fromBundle(arguments as Bundle).toastText?.let {
            showToast(it)
            loadStory(token)
            arguments?.clear()
        }

    }

    private fun loadStory(token: String) {
        if (binding.edtStoryQty.text.toString().isNotEmpty()) {
            val qty = binding.edtStoryQty.text.toString().toInt()
            viewModel.getStory(token, size = qty)
        } else binding.edtStoryQty.error = getString(R.string.cant_be_empty)
    }

    private fun showRecyclerList(stories: List<StoryItem>) {

        binding.rvStories.layoutManager = LinearLayoutManager(view?.context)

        val listStoryAdapter = ListStoryAdapter(stories)
        binding.rvStories.adapter = listStoryAdapter

        listStoryAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(story: StoryItem, binding: ItemRowStoryBinding) {
                binding.pbItemStory.visibility = View.VISIBLE
                showStory(story, binding)
            }
        })
    }

    private fun showStory(story: StoryItem, binding: ItemRowStoryBinding) {
        val extras = FragmentNavigatorExtras(
            binding.imgItemStory to story.photoUrl
        )
        val toDetailStoryFragment =
            ListStoryFragmentDirections.actionListStoryFragmentToDetailStoryFragment(
                story
            )
        view?.findNavController()?.navigate(toDetailStoryFragment, extras)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.tvPlcStoryperpage.isEnabled = false
            binding.edtStoryQty.isEnabled = false
            binding.btnLoad.isEnabled = false
            binding.srlStories.isRefreshing = true
            binding.rvStories.visibility = View.GONE
        } else {
            binding.tvPlcStoryperpage.isEnabled = true
            binding.edtStoryQty.isEnabled = true
            binding.btnLoad.isEnabled = true
            binding.srlStories.isRefreshing = false
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(view?.context, text, Toast.LENGTH_SHORT).show()
    }

    private fun showError(isError: Boolean, msg: String = "") {
        if (isError) {
            binding.tvErrorMsg.text = msg
            binding.rvStories.visibility = View.GONE
            binding.tvErrorMsg.visibility = View.VISIBLE
            binding.btnRetry.visibility = View.VISIBLE
        } else {
            binding.rvStories.visibility = View.VISIBLE
            binding.tvErrorMsg.visibility = View.GONE
            binding.btnRetry.visibility = View.GONE
        }
    }
}