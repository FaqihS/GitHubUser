package com.fshou.githubuser.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fshou.githubuser.data.remote.response.User
import com.fshou.githubuser.databinding.FragmentFollowBinding
import com.fshou.githubuser.data.Result
import com.fshou.githubuser.ui.view_model.FollowViewModel
import com.fshou.githubuser.ui.adapter.UserListAdapter
import com.fshou.githubuser.ui.view_model.ViewModelFactory

class FollowFragment : Fragment() {

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<FollowViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = arguments?.getInt(ARG_SECTION_NUMBER)
        if (index == 1) {
            viewModel.followerList.observe(viewLifecycleOwner) { resultHandler(it) }
        } else {
            viewModel.followingList.observe(viewLifecycleOwner) { resultHandler(it) }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun resultHandler(result: Result<List<User>>) {
        when (result) {
            is Result.Loading -> binding?.progressBar?.visibility = View.VISIBLE
            is Result.Error -> {
                binding?.progressBar?.visibility = View.GONE
                val message = result.error.getContentIfNotHandled()
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
            }

            is Result.Success -> {
                binding?.progressBar?.visibility = View.GONE
                setUserList(result.data)
            }
        }
    }

    private fun setUserList(users: List<User>) {
        val userListAdapter = UserListAdapter(users)
        val rvFollowerLayoutManager = LinearLayoutManager(requireActivity())

        userListAdapter.apply {
            showArrow = false
            addIntent = false
        }
        binding?.rvFollower?.apply {
            setHasFixedSize(true)
            layoutManager = rvFollowerLayoutManager
            adapter = userListAdapter
        }
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"

    }

}