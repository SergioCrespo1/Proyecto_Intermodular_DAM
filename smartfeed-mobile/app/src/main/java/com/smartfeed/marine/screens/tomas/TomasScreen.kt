package com.smartfeed.marine.screens.tomas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartfeed.marine.data.local.entity.ProgramacionTomaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TomasScreen(viewModel: TomasViewModel = viewModel()) {
    val tomas by viewModel.tomasDelDia.collectAsState()
    var tomaSeleccionada by remember { mutableStateOf<ProgramacionTomaEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartFeed Marine - Cubierta", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D47A1),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Órdenes de alimentación de hoy (Modo Offline)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(tomas) { toma ->
                    TarjetaOrdenToma(
                        toma = toma,
                        onPulsarEjecutar = { tomaSeleccionada = toma }
                    )
                }
            }
        }

        tomaSeleccionada?.let { orden ->
            DialogoEjecutarToma(
                orden = orden,
                horaInicioInicial = viewModel.horaActualIso(),
                onDismiss = { tomaSeleccionada = null },
                onConfirmar = { horaInicio, apetito, sacos ->
                    viewModel.guardarTomaOffline(
                        idProgramacion = orden.idProgramacion,
                        horaInicio = horaInicio,
                        apetito = apetito,
                        sacosGastados = sacos
                    )
                    tomaSeleccionada = null
                }
            )
        }
    }
}

@Composable
fun TarjetaOrdenToma(
    toma: ProgramacionTomaEntity,
    onPulsarEjecutar: () -> Unit
) {
    val esPendiente = toma.estadoOrden == "PENDIENTE"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esPendiente) Color(0xFFF5F9FF) else Color(0xFFE8F5E9)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jaula ${toma.codigoJaula} (${toma.codigoLote})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Badge(
                    containerColor = if (esPendiente) Color(0xFFFF9800) else Color(0xFF2E7D32)
                ) {
                    Text(
                        text = toma.estadoOrden,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Toma ${toma.numeroToma} de ${toma.totalTomasDia} • Programado: ${toma.sacosProgramados} sacos")

            if (esPendiente) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onPulsarEjecutar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("INICIAR TOMA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DialogoEjecutarToma(
    orden: ProgramacionTomaEntity,
    horaInicioInicial: String,
    onDismiss: () -> Unit,
    onConfirmar: (String, Int, Double) -> Unit
) {
    var apetitoSeleccionado by remember { mutableIntStateOf(3) }
    var sacosGastados by remember { mutableDoubleStateOf(orden.sacosProgramados) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Toma en Jaula ${orden.codigoJaula}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Inicio registrado: ${horaInicioInicial.substringAfter("T").take(8)}",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold
                )

                Text("Sacos reales gastados (25 kg):", fontWeight = FontWeight.Bold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { if (sacosGastados > 0.5) sacosGastados -= 0.5 }) {
                        Text("- 0.5", fontSize = 18.sp)
                    }
                    Text(
                        text = "$sacosGastados",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = { sacosGastados += 0.5 }) {
                        Text("+ 0.5", fontSize = 18.sp)
                    }
                }

                Text("Apetito de los peces (1 = Bajo, 4 = Voraz):", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..4).forEach { nivel ->
                        val seleccionado = apetitoSeleccionado == nivel
                        Button(
                            onClick = { apetitoSeleccionado = nivel },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (seleccionado) Color(0xFF0D47A1) else Color.LightGray,
                                contentColor = if (seleccionado) Color.White else Color.Black
                            ),
                            modifier = Modifier.size(56.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("$nivel", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(horaInicioInicial, apetitoSeleccionado, sacosGastados) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("FINALIZAR Y GUARDAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}