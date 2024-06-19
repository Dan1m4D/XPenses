package com.d479.xpenses.viewModels

import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.User
import com.d479.xpenses.repositories.UserRepository
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch

class MapViewModel() : ViewModel() {
    private val userRepository = UserRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val invoices = MutableStateFlow(emptyList<Invoice>())
    val userInvoices: StateFlow<List<Invoice>> = invoices

    private var _location = MutableStateFlow<Location>(
        Location("DEFAULT_LOCATION").apply {
            latitude = 40.638076
            longitude = -8.653603
        }
    )
    val location: StateFlow<Location> = _location

    val uiSettings = mutableStateOf(MapUiSettings())
    val properties =
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))


    init {
        viewModelScope.launch {
            val fetchedUser = userRepository.getUser()
            _user.emit(fetchedUser)

            // Set the user's location as the initial camera position
            val userLocation = getCoordinates()
            _location.emit(userLocation)

            val fetchedInvoices = userRepository.getUserInvoices()
            invoices.emitAll(fetchedInvoices)
        }
    }

    fun updateLocation(location: Location) {
        this._location = MutableStateFlow(location)
    }

    fun getCoordinates(): Location {
        return _location.value
    }

    // get invoices locations
    fun getInvoicesLocations(): List<Location> {
        return invoices.value.map { invoice ->
            Location(invoice.title).apply {
                latitude = invoice.latitude
                longitude = invoice.longitude
            }
        }
    }


}