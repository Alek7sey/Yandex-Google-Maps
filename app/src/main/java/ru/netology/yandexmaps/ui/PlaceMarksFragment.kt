package ru.netology.yandexmaps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.adapter.OnInteractionListener
import ru.netology.yandexmaps.adapter.PlaceMarkAdapter
import ru.netology.yandexmaps.databinding.FragmentPlacemarksBinding
import ru.netology.yandexmaps.dto.PlaceMark
import ru.netology.yandexmaps.ui.MapsFragment.Companion.LATITUDE
import ru.netology.yandexmaps.ui.MapsFragment.Companion.LONGITUDE
import ru.netology.yandexmaps.viewmodel.PlaceMarkViewModel

class PlaceMarksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlacemarksBinding.inflate(layoutInflater, container, false)
        val viewModel by viewModels<PlaceMarkViewModel>()

        val adapter = PlaceMarkAdapter(object : OnInteractionListener {

            override fun onClick(placemark: PlaceMark) {
                findNavController().navigate(
                    R.id.action_placeMarksFragment_to_mapsFragment, bundleOf(
                        LATITUDE to placemark.latitude,
                        LONGITUDE to placemark.longitude
                    )
                )
            }

            override fun onDelete(placemark: PlaceMark) {
                viewModel.removePlaceMark(placemark.id)
            }

            override fun onEdit(placemark: PlaceMark) {
                findNavController().navigate(
                    R.id.action_placeMarksFragment_to_addPlaceMarkFragment, bundleOf(
                        AddPlaceMarkFragment.ID to placemark.id,
                        AddPlaceMarkFragment.TITLE to placemark.title,
                        AddPlaceMarkFragment.LATITUDE to placemark.latitude,
                        AddPlaceMarkFragment.LATITUDE to placemark.latitude,
                        )
                )
            }
        })

        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.placemarks.collectLatest {
                    adapter.submitList(it)
                }
            }
        }

        return binding.root
    }
}