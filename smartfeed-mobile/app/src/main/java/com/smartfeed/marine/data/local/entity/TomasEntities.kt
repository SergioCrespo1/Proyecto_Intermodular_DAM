package com.smartfeed.marine.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "programacion_toma")
data class ProgramacionTomaEntity(
    @PrimaryKey val idProgramacion: Int,
    val idLotePeces: Int,
    val codigoJaula: String,      // EJEMPLO : "X-02"
    val codigoLote: String,       // EJEMPLO: "ARM-2026-A"
    val fechaProgramada: String,  // EJEMPLO: "2026-11-10"
    val numeroToma: Int,
    val totalTomasDia: Int,
    val sacosProgramados: Double, // EJEMPLO: 2.8
    val estadoOrden: String       // "PENDIENTE" o "REALIZADA"
)

@Entity(tableName = "registro_consumo")
data class RegistroConsumoEntity(
    @PrimaryKey val idProgramacion: Int, // MISMA PK: impide duplicar el consumo de una toma
    val horaInicio: String,
    val horaFin: String,
    val respuestaAlimentaria: Int,       // ESCALA 1 a 4
    val idUsuario: Int,
    val estadoSync: String = "PENDIENTE" // "PENDIENTE" o "SINCRONIZADO"
)

@Entity(tableName = "linea_consumo_pienso")
data class LineaConsumoPiensoEntity(
    @PrimaryKey(autoGenerate = true) val idLineaLocal: Int = 0,
    val idProgramacion: Int,
    val idLotePienso: Int,
    val codigoLoteFabrica: String,
    val sacosGastados: Double
)