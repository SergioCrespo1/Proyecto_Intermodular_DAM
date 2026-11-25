package com.smartfeed.marine.screens.tomas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartfeed.marine.data.local.database.AppDatabase
import com.smartfeed.marine.data.local.entity.LineaConsumoPiensoEntity
import com.smartfeed.marine.data.local.entity.ProgramacionTomaEntity
import com.smartfeed.marine.data.local.entity.RegistroConsumoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TomasViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).tomasDao()

    val tomasDelDia: StateFlow<List<ProgramacionTomaEntity>> = dao.observarTomasDelDia()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Si la BD local está vacía, metemos 2 órdenes de prueba para trabajar en modo avión
        viewModelScope.launch {
            if (dao.observarTomasDelDia().first().isEmpty()) {
                dao.insertarOrdenes(
                    listOf(
                        ProgramacionTomaEntity(
                            idProgramacion = 101,
                            idLotePeces = 1,
                            codigoJaula = "J-01",
                            codigoLote = "DOR-2026-A",
                            fechaProgramada = "2026-11-25",
                            numeroToma = 1,
                            totalTomasDia = 2,
                            sacosProgramados = 2.5,
                            estadoOrden = "PENDIENTE"
                        ),
                        ProgramacionTomaEntity(
                            idProgramacion = 102,
                            idLotePeces = 2,
                            codigoJaula = "J-02",
                            codigoLote = "LUB-2026-B",
                            fechaProgramada = "2026-11-25",
                            numeroToma = 1,
                            totalTomasDia = 2,
                            sacosProgramados = 3.0,
                            estadoOrden = "PENDIENTE"
                        )
                    )
                )
            }
        }
    }

    fun horaActualIso(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    fun guardarTomaOffline(
        idProgramacion: Int,
        horaInicio: String,
        apetito: Int,
        sacosGastados: Double
    ) {
        viewModelScope.launch {
            val horaFin = horaActualIso()

            // 1. Guarda el parte en Room como PENDIENTE de subir
            val consumo = RegistroConsumoEntity(
                idProgramacion = idProgramacion,
                horaInicio = horaInicio,
                horaFin = horaFin,
                respuestaAlimentaria = apetito,
                idUsuario = 1,
                estadoSync = "PENDIENTE"
            )
            dao.insertarConsumo(consumo)

            // 2. Guarda los sacos gastados del lote
            val lineaPienso = LineaConsumoPiensoEntity(
                idProgramacion = idProgramacion,
                idLotePienso = 1,
                codigoLoteFabrica = "LOT-BIOMAR-01",
                sacosGastados = sacosGastados
            )
            dao.insertarLineasPienso(listOf(lineaPienso))

            // 3. Marca la orden como REALIZADA
            dao.marcarOrdenComoRealizada(idProgramacion)
        }
    }
}