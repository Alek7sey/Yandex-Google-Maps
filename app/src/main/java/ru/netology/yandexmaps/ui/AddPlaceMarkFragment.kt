package ru.netology.yandexmaps.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.yandexmaps.databinding.FragmentAddBinding
import ru.netology.yandexmaps.dto.PlaceMark
import ru.netology.yandexmaps.viewmodel.PlaceMarkViewModel

class AddPlaceMarkFragment : Fragment() {

    private val viewModel by viewModels<PlaceMarkViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentAddBinding.inflate(layoutInflater, container, false)

        binding.apply {
            titleField.text = Editable.Factory.getInstance().newEditable(
                requireArguments().getString(TITLE) ?: ""
            )

            save.setOnClickListener {
                val title = titleField.text.toString().trim()
                if (title.isBlank()) {
                    Toast.makeText(requireContext(), "Title is blank", Toast.LENGTH_LONG).show()
                } else {
                    viewModel.addPlaceMark(
                        PlaceMark(
                            id = requireArguments().getLong(ID),
                            title = title,
                            longitude = requireArguments().getDouble(LONGITUDE),
                            latitude = requireArguments().getDouble(LATITUDE),
                        )
                    )
                }
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    companion object {
        const val ID = "ID"
        const val TITLE = "TITLE"
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
    }
}