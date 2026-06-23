package com.example.data.repository

import com.example.data.dao.UserDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.SubcategoryDao
import com.example.data.dao.ServiceRequestDao
import com.example.data.dao.ChatMessageDao
import com.example.data.model.User
import com.example.data.model.Category
import com.example.data.model.Subcategory
import com.example.data.model.ServiceRequest
import com.example.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(
    val userDao: UserDao,
    val categoryDao: CategoryDao,
    val subcategoryDao: SubcategoryDao,
    val serviceRequestDao: ServiceRequestDao,
    val chatMessageDao: ChatMessageDao
) {
    val allUsers: Flow<List<User>> = userDao.getAllUsersFlow()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategoriesFlow()
    val allSubcategories: Flow<List<Subcategory>> = subcategoryDao.getAllSubcategoriesFlow()
    val allServiceRequests: Flow<List<ServiceRequest>> = serviceRequestDao.getAllServicesFlow()
    val availableServiceRequests: Flow<List<ServiceRequest>> = serviceRequestDao.getAvailableServicesFlow()

    fun getServicesByClient(clientId: String): Flow<List<ServiceRequest>> =
        serviceRequestDao.getServicesByClientFlow(clientId)

    fun getServicesByProvider(providerId: String): Flow<List<ServiceRequest>> =
        serviceRequestDao.getServicesByProviderFlow(providerId)

    fun getMessagesForService(serviceId: Int): Flow<List<ChatMessage>> =
        chatMessageDao.getMessagesForServiceFlow(serviceId)

    suspend fun insertChatMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun getUserById(id: String): User? = userDao.getUserById(id)
    fun getUserFlow(id: String): Flow<User?> = userDao.getUserFlow(id)

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun loginUser(email: String, passwordHash: String): User? {
        return userDao.getUserByEmailAndPassword(email, passwordHash)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun createServiceRequest(request: ServiceRequest): Long {
        return serviceRequestDao.insertService(request)
    }

    suspend fun updateServiceRequest(request: ServiceRequest) {
        serviceRequestDao.updateService(request)
    }

    suspend fun getServiceById(id: Int): ServiceRequest? {
        return serviceRequestDao.getServiceById(id)
    }

    suspend fun seedDatabase() {
        val currentCategories = allCategories.first()
        if (currentCategories.isEmpty()) {
            val categories = listOf(
                Category(1, "Limpieza"),
                Category(2, "Reparaciones y Mantenimiento"),
                Category(3, "Servicios Especiales")
            )
            categoryDao.insertCategories(categories)

            val subcategories = listOf(
                Subcategory(1, "Limpieza General", 1),
                Subcategory(2, "Limpieza Profunda", 1),
                Subcategory(3, "Electricidad", 2),
                Subcategory(4, "Plomería", 2),
                Subcategory(5, "Carpintería", 2),
                Subcategory(6, "Pintura", 2),
                Subcategory(7, "Jardinería", 3),
                Subcategory(8, "Mascotas", 3),
                Subcategory(9, "Adultos Mayores", 3),
                Subcategory(10, "Mandados", 3)
            )
            subcategoryDao.insertSubcategories(subcategories)

            // Seed mock accounts
            // 1. Cliente
            userDao.insertUser(
                User(
                    id = "cliente@hommysist.com",
                    nombre = "Yader Jarquín",
                    telefono = "+505 8424-2234",
                    email = "cliente@hommysist.com",
                    rol = "Cliente",
                    passwordHash = "123456",
                    verificado = true
                )
            )
            // 2. Proveedor verificado
            userDao.insertUser(
                User(
                    id = "proveedor@hommysist.com",
                    nombre = "María Auxiliadora López",
                    telefono = "+505 7654-3210",
                    email = "proveedor@hommysist.com",
                    rol = "Proveedor",
                    passwordHash = "123456",
                    verificado = true,
                    disponible = true,
                    cedula = "001-150289-1001A",
                    fotoRostro = "[FACIAL_PHOTO_MARIA_CONFIRMED]"
                )
            )
            // 3. Proveedor pendiente de verificación
            userDao.insertUser(
                User(
                    id = "tecnico@hommysist.com",
                    nombre = "Franklin Pérez (Fontanero)",
                    telefono = "+505 8242-8115",
                    email = "tecnico@hommysist.com",
                    rol = "Proveedor",
                    passwordHash = "123456",
                    verificado = false, // Pending admin approval
                    disponible = true,
                    cedula = "441-291195-1002K",
                    fotoRostro = "[FACIAL_PHOTO_FRANKLIN_SELFIE]"
                )
            )
            // 4. Administrador
            userDao.insertUser(
                User(
                    id = "admin@hommysist.com",
                    nombre = "Admin Hommysist UAM",
                    telefono = "+505 2278-3829",
                    email = "admin@hommysist.com",
                    rol = "Administrador",
                    passwordHash = "123456",
                    verificado = true
                )
            )

            // Seed some default Mock Service Requests
            serviceRequestDao.insertService(
                ServiceRequest(
                    cliente_id = "cliente@hommysist.com",
                    proveedor_id = null,
                    categoria_id = 1,
                    subcategoria_id = 1,
                    descripcion = "Necesito limpieza general en mi casa de dos pisos cerca de Metrocentro, Managua. De ser posible traer desinfectante.",
                    direccion = "Metrocentro 2 cuadras al sur, Managua",
                    fecha_servicio = "Mañana 9:00 AM",
                    estado = "Pendiente"
                )
            )
            serviceRequestDao.insertService(
                ServiceRequest(
                    cliente_id = "cliente@hommysist.com",
                    proveedor_id = "proveedor@hommysist.com",
                    categoria_id = 2,
                    subcategoria_id = 3,
                    descripcion = "Instalación de abanico de techo y cambio de tomacorriente defectuoso en sala principal.",
                    direccion = "Colonia Centroamérica, Sector J, Managua",
                    fecha_servicio = "Próximo Sábado 2:00 PM",
                    estado = "Aceptado"
                )
            )
            serviceRequestDao.insertService(
                ServiceRequest(
                    cliente_id = "cliente@hommysist.com",
                    proveedor_id = "proveedor@hommysist.com",
                    categoria_id = 3,
                    subcategoria_id = 8,
                    descripcion = "Cuidado y paseo de mascota (perro pastor alemán) durante 3 días mientras viajo.",
                    direccion = "Residencial Las Colinas, Managua",
                    fecha_servicio = "Hace 3 días",
                    estado = "Finalizado"
                )
            )
        }
    }
}
