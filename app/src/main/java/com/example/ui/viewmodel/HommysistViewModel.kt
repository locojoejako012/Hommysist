package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Category
import com.example.data.model.ServiceRequest
import com.example.data.model.Subcategory
import com.example.data.model.User
import com.example.data.model.ChatMessage
import com.example.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HommysistViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(
        db.userDao(),
        db.categoryDao(),
        db.subcategoryDao(),
        db.serviceRequestDao(),
        db.chatMessageDao()
    )

    // Current logged-in user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Navigation and UX state
    private val _currentScreen = MutableStateFlow("landing") // "landing", "login", "register_client", "register_provider", "main"
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Selected tab in main screen: "home_client", "my_services", "dashboard_provider", "dashboard_admin", "profile"
    private val _selectedTab = MutableStateFlow("home_client")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    // Database Flows
    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subcategories: StateFlow<List<Subcategory>> = repository.allSubcategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServiceRequests: StateFlow<List<ServiceRequest>> = repository.allServiceRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableRequests: StateFlow<List<ServiceRequest>> = repository.availableServiceRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI feedback messages
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // Service request setup state
    val selectedCategoryForRequest = MutableStateFlow<Category?>(null)
    val selectedSubcategoryForRequest = MutableStateFlow<Subcategory?>(null)

    init {
        // Auto-seed database with initial classifications and test credentials
        viewModelScope.launch {
            try {
                repository.seedDatabase()
            } catch (e: Exception) {
                _uiMessage.value = "Error al inicializar datos: ${e.localizedMessage}"
            }
        }
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun showUiMessage(msg: String) {
        _uiMessage.value = msg
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }

    // Authentication Actions
    fun login(email: String, passwordHash: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.loginUser(email.trim(), passwordHash)
            if (user != null) {
                _currentUser.value = user
                _currentScreen.value = "main"
                // Auto-route based on role
                when (user.rol) {
                    "Cliente" -> _selectedTab.value = "home_client"
                    "Proveedor" -> _selectedTab.value = "dashboard_provider"
                    "Administrador" -> _selectedTab.value = "dashboard_admin"
                    else -> _selectedTab.value = "profile"
                }
                _uiMessage.value = "¡Bienvenido de vuelta, ${user.nombre}!"
                onResult(true)
            } else {
                _uiMessage.value = "Credenciales incorrectas o usuario no registrado."
                onResult(false)
            }
        }
    }

    fun register(
        nombre: String,
        telefono: String,
        email: String,
        rol: String,
        passwordHash: String
    ) {
        viewModelScope.launch {
            if (nombre.isBlank() || telefono.trim().isEmpty() || email.trim().isEmpty() || passwordHash.isEmpty()) {
                _uiMessage.value = "Por favor completa todos los campos requeridos."
                return@launch
            }
            val existing = repository.getUserById(email.trim())
            if (existing != null) {
                _uiMessage.value = "Este correo electrónico ya está registrado."
                return@launch
            }

            // Providers start unverified until admin approves, clients are auto-approved for frictionless MVP use
            val isVerified = (rol == "Cliente")
            val newUser = User(
                id = email.trim(),
                nombre = nombre.trim(),
                telefono = telefono.trim(),
                email = email.trim(),
                rol = rol,
                passwordHash = passwordHash,
                verificado = isVerified,
                disponible = true
            )

            repository.registerUser(newUser)
            _currentUser.value = newUser
            _currentScreen.value = "main"
            when (rol) {
                "Cliente" -> _selectedTab.value = "home_client"
                "Proveedor" -> _selectedTab.value = "dashboard_provider"
                else -> _selectedTab.value = "profile"
            }
            _uiMessage.value = if (rol == "Proveedor") {
                "Registro exitoso. Tu perfil pasará al panel de aprobación administrativa."
            } else {
                "¡Cuenta creada con éxito!"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = "landing"
        _selectedTab.value = "home_client"
        selectedCategoryForRequest.value = null
        selectedSubcategoryForRequest.value = null
    }

    // Client Actions: Create Solicitud
    fun createRequest(descripcion: String, direccion: String, fecha: String, onComplete: () -> Unit) {
        val user = _currentUser.value ?: return
        val cat = selectedCategoryForRequest.value ?: return
        val sub = selectedSubcategoryForRequest.value ?: return

        if (descripcion.isBlank() || direccion.isBlank() || fecha.isBlank()) {
            _uiMessage.value = "Por favor, completa todos los detalles de la solicitud."
            return
        }

        viewModelScope.launch {
            val req = ServiceRequest(
                cliente_id = user.id,
                categoria_id = cat.id,
                subcategoria_id = sub.id,
                descripcion = descripcion.trim(),
                direccion = direccion.trim(),
                fecha_servicio = fecha.trim(),
                estado = "Pendiente"
            )
            repository.createServiceRequest(req)
            _uiMessage.value = "Solicitud de servicio publicada en Managua con éxito."
            onComplete()
            _selectedTab.value = "my_services"
        }
    }

    // Provider Actions: Access/Respond to request
    fun acceptRequest(requestId: Int) {
        val provider = _currentUser.value ?: return
        if (provider.rol != "Proveedor") return
        if (!provider.verificado) {
            _uiMessage.value = "Tu cuenta aún no está verificada por los administradores."
            return
        }

        viewModelScope.launch {
            val request = repository.getServiceById(requestId)
            if (request != null && request.estado == "Pendiente") {
                val updated = request.copy(
                    proveedor_id = provider.id,
                    estado = "Aceptado"
                )
                repository.updateServiceRequest(updated)
                _uiMessage.value = "¡Has aceptado el trabajo con éxito!"
            } else {
                _uiMessage.value = "Esta solicitud ya no está disponible."
            }
        }
    }

    fun startService(requestId: Int) {
        viewModelScope.launch {
            val request = repository.getServiceById(requestId)
            if (request != null && request.estado == "Aceptado") {
                repository.updateServiceRequest(request.copy(estado = "En Progreso"))
                _uiMessage.value = "El servicio está ahora en progreso."
            }
        }
    }

    fun completeRequest(requestId: Int) {
        viewModelScope.launch {
            val request = repository.getServiceById(requestId)
            if (request != null && (request.estado == "Aceptado" || request.estado == "En Progreso")) {
                repository.updateServiceRequest(request.copy(estado = "Finalizado"))
                _uiMessage.value = "¡Excelente! Has marcado la solicitud como finalizada."
            }
        }
    }

    fun cancelRequest(requestId: Int) {
        viewModelScope.launch {
            val request = repository.getServiceById(requestId)
            if (request != null && request.estado == "Pendiente") {
                repository.updateServiceRequest(request.copy(estado = "Cancelado"))
                _uiMessage.value = "Solicitud cancelada correctamente."
            }
        }
    }

    // Admin Actions
    fun approveProvider(providerId: String) {
        viewModelScope.launch {
            val user = repository.getUserById(providerId)
            if (user != null && user.rol == "Proveedor") {
                repository.updateUser(user.copy(verificado = true, rechazado = false, motivoRechazo = null))
                _uiMessage.value = "Proveedor aprobado: ${user.nombre}"
            }
        }
    }

    fun rejectProvider(providerId: String, reason: String) {
        viewModelScope.launch {
            val user = repository.getUserById(providerId)
            if (user != null && user.rol == "Proveedor") {
                repository.updateUser(user.copy(verificado = false, rechazado = true, motivoRechazo = reason))
                _uiMessage.value = "Proveedor rechazado: ${user.nombre}. Motivo: $reason"
            }
        }
    }

    fun toggleUserActiveStatus(userId: String) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            if (user != null) {
                val updated = user.copy(disponible = !user.disponible)
                repository.updateUser(updated)
                _currentUser.value?.let { current ->
                    if (current.id == userId) {
                        _currentUser.value = updated
                    }
                }
                val msg = if (updated.disponible) "Cuenta reactivada y visible" else "Cuenta inhabilitada (Acceso y privacidad protegidos)"
                _uiMessage.value = "$msg para: ${user.nombre}"
            }
        }
    }

    // Provider Verification Details Submission
    fun submitVerificationDetails(cedula: String, fotoRostro: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(
                cedula = cedula,
                fotoRostro = fotoRostro,
                rechazado = false,
                motivoRechazo = null,
                verificado = false // marked for admin review
            )
            repository.updateUser(updated)
            _currentUser.value = updated
            _uiMessage.value = "Documentación enviada con éxito para revisión."
        }
    }

    // Helper functions for displaying text mappings
    fun getCategoryName(id: Int): String {
        return when (id) {
            1 -> "Limpieza"
            2 -> "Reparaciones y Mantenimiento"
            3 -> "Servicios Especiales"
            else -> "Otro"
        }
    }

    fun getSubcategoryName(id: Int): String {
        return when (id) {
            1 -> "Limpieza General"
            2 -> "Limpieza Profunda"
            3 -> "Electricidad"
            4 -> "Plomería"
            5 -> "Carpintería"
            6 -> "Pintura"
            7 -> "Jardinería"
            8 -> "Mascotas"
            9 -> "Adultos Mayores"
            10 -> "Mandados"
            else -> "Estándar"
        }
    }

    // Chat state
    private val _activeChatRequestId = MutableStateFlow<Int?>(null)
    val activeChatRequestId: StateFlow<Int?> = _activeChatRequestId.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private var chatJob: Job? = null

    fun openChat(requestId: Int) {
        _activeChatRequestId.value = requestId
        chatJob?.cancel()
        chatJob = viewModelScope.launch {
            repository.getMessagesForService(requestId).collect { list ->
                _chatMessages.value = list
            }
        }
    }

    fun closeChat() {
        _activeChatRequestId.value = null
        chatJob?.cancel()
        _chatMessages.value = emptyList()
    }

    fun sendChatMessage(messageText: String) {
        val serviceId = _activeChatRequestId.value ?: return
        val sender = _currentUser.value ?: return
        if (messageText.isBlank()) return

        viewModelScope.launch {
            val msg = ChatMessage(
                servicio_id = serviceId,
                sender_id = sender.id,
                message = messageText,
                timestamp = System.currentTimeMillis()
            )
            repository.insertChatMessage(msg)
        }
    }

    // Rating & Review Actions
    fun submitRatingAndReview(requestId: Int, rating: Int, reviewText: String) {
        viewModelScope.launch {
            val request = repository.getServiceById(requestId)
            if (request != null) {
                val updated = request.copy(
                    rating = rating,
                    comentario = reviewText
                )
                repository.updateServiceRequest(updated)
                _uiMessage.value = "¡Gracias por calificar el servicio con $rating estrellas!"
            }
        }
    }
}
