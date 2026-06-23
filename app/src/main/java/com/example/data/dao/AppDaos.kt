package com.example.data.dao

import androidx.room.*
import com.example.data.model.User
import com.example.data.model.Category
import com.example.data.model.Subcategory
import com.example.data.model.ServiceRequest
import com.example.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserFlow(id: String): Flow<User?>

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, passwordHash: String): User?

    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun getAllUsersFlow(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categorias ORDER BY id ASC")
    fun getAllCategoriesFlow(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)
}

@Dao
interface SubcategoryDao {
    @Query("SELECT * FROM subcategorias ORDER BY id ASC")
    fun getAllSubcategoriesFlow(): Flow<List<Subcategory>>

    @Query("SELECT * FROM subcategorias WHERE categoria_id = :categoryId ORDER BY id ASC")
    fun getSubcategoriesByCategoryId(categoryId: Int): Flow<List<Subcategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubcategories(subcategories: List<Subcategory>)
}

@Dao
interface ServiceRequestDao {
    @Query("SELECT * FROM servicios ORDER BY created_at DESC")
    fun getAllServicesFlow(): Flow<List<ServiceRequest>>

    @Query("SELECT * FROM servicios WHERE cliente_id = :clientId ORDER BY created_at DESC")
    fun getServicesByClientFlow(clientId: String): Flow<List<ServiceRequest>>

    @Query("SELECT * FROM servicios WHERE proveedor_id = :providerId ORDER BY created_at DESC")
    fun getServicesByProviderFlow(providerId: String): Flow<List<ServiceRequest>>

    @Query("SELECT * FROM servicios WHERE estado = 'Pendiente' ORDER BY created_at DESC")
    fun getAvailableServicesFlow(): Flow<List<ServiceRequest>>

    @Query("SELECT * FROM servicios WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: Int): ServiceRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(request: ServiceRequest): Long

    @Update
    suspend fun updateService(request: ServiceRequest)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE servicio_id = :serviceId ORDER BY timestamp ASC")
    fun getMessagesForServiceFlow(serviceId: Int): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)
}
