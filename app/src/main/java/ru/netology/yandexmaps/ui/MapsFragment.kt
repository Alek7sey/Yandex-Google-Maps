package ru.netology.yandexmaps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.BuildConfig
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.databinding.FragmentMapsBinding
import ru.netology.yandexmaps.databinding.PlacemarkItemBinding
import ru.netology.yandexmaps.viewmodel.PlaceMarkViewModel

class MapsFragment : Fragment() {
    private var mapView: MapView? = null
    private lateinit var userLocation: UserLocationLayer
   // private var map: Map? = null

    private val listener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit

        override fun onMapLongTap(map: Map, point: Point) {
            findNavController().navigate(
                R.id.action_mapsFragment_to_addPlaceMarkFragment, bundleOf(
                    LATITUDE to point.latitude,
                    LONGITUDE to point.longitude
                )
            )
        }
    }

    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(view: UserLocationView) = Unit

        override fun onObjectRemoved(view: UserLocationView) = Unit

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocation.cameraPosition()?.target?.let {
                mapView?.map?.move(START_POSITION)
            }
            userLocation.setObjectListener(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey(savedInstanceState) // Проверяем: был ли уже ранее установлен API-ключ в приложении. Если нет - устанавливаем его.
        MapKitFactory.initialize(requireContext())
    }

    private fun setApiKey(savedInstanceState: Bundle?) {
        val haveApiKey = savedInstanceState?.getBoolean(haveApiKey) ?: false // При первом запуске приложения всегда false
        if (!haveApiKey) {
            MapKitFactory.setApiKey(BuildConfig.MAPS_API_KEY) // API-ключ должен быть задан единожды перед инициализацией MapKitFactory
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(haveApiKey, true)
    }

    private val viewModel by viewModels<PlaceMarkViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapsBinding.inflate(layoutInflater, container, false)

        val placemarkTapListener = MapObjectTapListener { _, point ->
            Toast.makeText(requireContext(), "${point.longitude}, ${point.latitude})", Toast.LENGTH_SHORT).show()
            true
        }

        val mapWindow = binding.map.mapWindow
        val map = mapWindow.map

        map.move(START_POSITION, START_ANIMATION, null)

        fun changeZoomByStep(value: Float) {
            with(map.cameraPosition) {
                map.move(
                    CameraPosition(this.target, zoom + value, azimuth, tilt),
                    SMOOTH_ANIMATION,
                    null,
                )
            }
        }

        binding.apply {
            btnPlus.setOnClickListener {
                changeZoomByStep(ZOOM_STEP)
            }
            btnMinus.setOnClickListener {
                changeZoomByStep(-ZOOM_STEP)
            }

//            btnFocusGeometry.setOnClickListener {
//                val geometry = Geometry.fromPolyline(polyline)
//                val position = map.cameraPosition(geometry)
//                map.move(position, SMOOTH_ANIMATION, null)
//            }
        }

        mapView = binding.map.apply {
            userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            userLocation.isVisible = true
            userLocation.isHeadingEnabled = false

            map.addInputListener(listener)

            val collection = map.mapObjects.addCollection()

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.placemarks.collectLatest {
                        collection.clear()
                        it.forEach { point ->
                            val placemarkBinding = PlacemarkItemBinding.inflate(layoutInflater)
                            placemarkBinding.title.text = point.title
                            placemarkBinding.type.setImageResource(R.drawable.ic_placemark)
                            collection.addPlacemark(
                                Point(point.latitude, point.longitude),
                                ViewProvider(placemarkBinding.root)
                            ).apply {
                                userData = point.id
                            }
                        }
                    }
                }
            }
            collection.addTapListener(placemarkTapListener)
        }

        val arguments = arguments
        if (arguments != null &&
            arguments.containsKey(LATITUDE) &&
            arguments.containsKey(LONGITUDE)
        ) {
            val cameraPosition = map.cameraPosition
            map.move(
                CameraPosition(
                    Point(arguments.getDouble(LATITUDE), arguments.getDouble(LONGITUDE)),
                    10F,
                    cameraPosition.azimuth,
                    cameraPosition.tilt,
                )
            )
            arguments.remove(LATITUDE)
            arguments.remove(LONGITUDE)
        } else {
            // При входе в приложение показываем текущее местоположение
            userLocation.setObjectListener(locationObjectListener)
        }
        binding.placemarksList.setOnClickListener {
            findNavController().navigate(
                R.id.action_mapsFragment_to_placeMarksFragment
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        mapView?.onStop()
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView = null
    }



//    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//        when {
//            granted -> {
//                MapKitFactory.getInstance().resetLocationManagerToDefault()
//                userLocation.cameraPosition()?.target?.also {
//                    val map = mapView?.map ?: return@registerForActivityResult
//                    val cameraPosition = map.cameraPosition
//                    map.move(
//                        CameraPosition(
//                            it,
//                            cameraPosition.zoom,
//                            cameraPosition.azimuth,
//                            cameraPosition.tilt,
//                        )
//                    )
//                }
//            }
//
//            else -> {
//                Toast.makeText(
//                    requireContext(),
//                    getString(R.string.location_permission),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }

    companion object {
        private const val ZOOM_STEP = 1f
        private val START_ANIMATION = Animation(Animation.Type.LINEAR, 3f)
        private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
        private val START_POSITION = CameraPosition(Point(54.707590, 20.508898), 15f, 0f, 0f)
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        private val haveApiKey = "haveApiKey"
    }
}
