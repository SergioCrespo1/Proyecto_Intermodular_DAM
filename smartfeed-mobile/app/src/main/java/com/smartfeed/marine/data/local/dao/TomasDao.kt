package com.smartfeed.marine.data.local.dao

import androidx.room.*
import com.smartfeed.marine.data.local.entity.LineaConsumoPiensoEntity
import com.smartfeed.marine.data.local.entity.ProgramacionTomaEntity
import com.smartfeed.marine.data.local.entity.RegistroConsumoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TomasDao {

    @Query("SELECT * FROM programacion_toma ORDER BY numeroToma ASC")
    fun observarTomasDelDia(): Flow<List<ProgramacionTomaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOrdenes(ordenes: List<ProgramacionTomaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarConsumo(consumo: RegistroConsumoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLineasPienso(lineas: List<LineaConsumoPiensoEntity>)

    @Query("UPDATE programacion_toma SET estadoOrden = 'REALIZADA' WHERE idProgramacion = :idProg")
    suspend fun marcarOrdenComoRealizada(idProg: Int)

    @Query("SELECT * FROM registro_consumo WHERE estadoSync = 'PENDIENTE'")
    suspend fun obtenerConsumosPendientes(): List<RegistroConsumoEntity>

    @Query("SELECT * FROM linea_consumo_pienso WHERE idProgramacion = :idProg")
    suspend fun obtenerLineasPorProgramacion(idProg: Int): List<LineaConsumoPiensoEntity>

    @Query("UPDATE registro_consumo SET estadoSync = 'SINCRONIZADO' WHERE idProgramacion = :idProg")
    suspend fun marcarComoSincronizado(idProg: Int)
}