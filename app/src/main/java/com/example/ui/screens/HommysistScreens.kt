package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.TextStyle
import java.util.Locale
import com.example.data.model.Category
import com.example.data.model.ServiceRequest
import com.example.data.model.Subcategory
import com.example.data.model.User
import com.example.ui.theme.*
import com.example.ui.viewmodel.HommysistViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path

@Composable
fun HommysistAppContent(viewModel: HommysistViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                "landing" -> LandingScreen(viewModel)
                "login" -> LoginScreen(viewModel)
                "register_client" -> RegisterClientScreen(viewModel)
                "register_provider" -> RegisterProviderScreen(viewModel)
                "main" -> MainScreenController(viewModel)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

// ----------------------------------------------------
// 1. LANDING SCREEN
// ----------------------------------------------------
@Composable
fun HommysistLogo(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 90.dp,
    shape: androidx.compose.foundation.shape.CornerSize = androidx.compose.foundation.shape.CornerSize(24.dp),
    serviceType: String? = null, // null = Main (Azul Océano), "Limpieza" = Steel Blue, "Reparaciones" = Soft sky blue, "Especiales" = Celeste pastel
    showText: Boolean = false,
    isHeader: Boolean = false
) {
    val roundedShape = RoundedCornerShape(shape)
    // Custom colors matching the image exactly
    val backgroundColor = when (serviceType) {
        "Limpieza", "Limpieza General", "Limpieza Profunda" -> Color(0xFF3D85C6) // Steel Blue (Azul acero: transmite orden, frescura, limpieza)
        "Reparaciones", "Reparaciones y Mantenimiento", "Mantenimiento" -> Color(0xFF7CA4D6) // Soft sky blue (Azul cielo suave: equilibrio, trabajo, arreglos)
        "Especiales", "Servicios Especiales", "Especial" -> Color(0xFFC9DAF2) // Celeste pastel (Celeste pastel: suavidad, confianza, tranquilidad)
        else -> Color(0xFF0F4C81) // Azul Océano (Main primary classic ocean blue)
    }

    val contentColor = Color.White

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = if (isHeader) Modifier.fillMaxWidth() else modifier
    ) {
        Box(
            modifier = if (isHeader) {
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(backgroundColor)
            } else {
                Modifier
                    .size(size)
                    .clip(roundedShape)
                    .background(backgroundColor)
            },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(if (isHeader) 100.dp else size * 0.6f)) {
                val w = this.size.width
                val h = this.size.height

                // 1. Pointed Solid thick Roof ("Techo")
                val roofPath = Path().apply {
                    moveTo(w * 0.50f, h * 0.14f) // peak top outer
                    lineTo(w * 0.90f, h * 0.46f) // right eave top-outer
                    lineTo(w * 0.81f, h * 0.46f) // right eave bottom-inner (horizontal flat eave cut)
                    lineTo(w * 0.50f, h * 0.23f) // inner peak
                    lineTo(w * 0.19f, h * 0.46f) // left eave bottom-inner
                    lineTo(w * 0.10f, h * 0.46f) // left eave top-outer
                    close()
                }
                drawPath(path = roofPath, color = contentColor)

                // 2. House walls/body ("Paredes") with triangular top parallel to roof to leave a beautiful slit of background
                val wallPath = Path().apply {
                    moveTo(w * 0.21f, h * 0.53f) // top-left shoulder of the wall
                    lineTo(w * 0.50f, h * 0.33f) // top-center peak of the wall
                    lineTo(w * 0.79f, h * 0.53f) // top-right shoulder of the wall
                    lineTo(w * 0.79f, h * 0.86f) // bottom-right corner of the wall (straight right corner)
                    lineTo(w * 0.21f, h * 0.86f) // bottom-left corner of the wall (straight left corner)
                    close()
                }
                drawPath(path = wallPath, color = contentColor)

                // 3. Door Cutout (Negative space in blue/background color on the left/center)
                val doorLeft = w * 0.31f
                val doorWidth = w * 0.21f
                val doorTop = h * 0.50f
                val doorHeight = (h * 0.86f) - doorTop
                drawRect(
                    color = backgroundColor,
                    topLeft = Offset(doorLeft, doorTop),
                    size = Size(doorWidth, doorHeight)
                )

                // 4. White door frame outline (very thin white border)
                drawRect(
                    color = contentColor,
                    topLeft = Offset(doorLeft, doorTop),
                    size = Size(doorWidth, doorHeight),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.015f)
                )

                // 5. Door handle (Pomo/picaporte: Diana/Ring)
                val knobX = doorLeft + doorWidth - (w * 0.045f)
                val knobY = doorTop + (doorHeight * 0.50f)
                val knobRadius = w * 0.014f
                drawCircle(color = contentColor, radius = knobRadius, center = Offset(knobX, knobY))
                drawCircle(color = backgroundColor, radius = knobRadius * 0.40f, center = Offset(knobX, knobY))

                // 6. Broom head (Bristles / Escoba) in lower right corner of wall (drawn as blue cutout)
                val mopaTop = h * 0.72f
                val mopaBottom = h * 0.86f
                val mopaPath = Path().apply {
                    moveTo(w * 0.58f, mopaTop) // top-left
                    lineTo(w * 0.72f, mopaTop) // top-right
                    lineTo(w * 0.75f, mopaBottom) // bottom-right
                    lineTo(w * 0.55f, mopaBottom) // bottom-left
                    close()
                }
                drawPath(path = mopaPath, color = backgroundColor)

                // Draw a small white horizontal collar or line inside the broom cutout to separate the brush body
                drawRect(
                    color = contentColor,
                    topLeft = Offset(w * 0.59f, mopaTop + h * 0.02f),
                    size = Size(w * 0.11f, h * 0.015f)
                )

                // Draw three vertical white bristles lines at the bottom of the broom cutout
                val bristleHeight = h * 0.07f
                val bristleY = mopaBottom - bristleHeight
                val bristleWidth = w * 0.012f
                drawRect(color = contentColor, topLeft = Offset(w * 0.60f, bristleY), size = Size(bristleWidth, bristleHeight))
                drawRect(color = contentColor, topLeft = Offset(w * 0.644f, bristleY), size = Size(bristleWidth, bristleHeight))
                drawRect(color = contentColor, topLeft = Offset(w * 0.688f, bristleY), size = Size(bristleWidth, bristleHeight))

                // 7. Thin vertical broom handle (entirely inside the wall cutout)
                val broomCenter = w * 0.65f
                val handleWidth = w * 0.024f
                val wallSlantY = h * 0.433f
                val handleTop = h * 0.48f

                // Blue broom handle cutout in the white wall segment
                drawRect(
                    color = backgroundColor,
                    topLeft = Offset(broomCenter - (handleWidth / 2f), handleTop),
                    size = Size(handleWidth, mopaTop - handleTop)
                )
            }
        }

        if (showText) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Hommysist.",
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.18f).sp,
                color = if (serviceType == null) Color(0xFF0F4C81) else backgroundColor,
                textAlign = TextAlign.Center
            )
            if (serviceType != null) {
                val subText = when (serviceType) {
                    "Limpieza", "Limpieza General", "Limpieza Profunda" -> "Limpieza"
                    "Reparaciones", "Reparaciones y Mantenimiento", "Mantenimiento" -> "Reparaciones"
                    else -> "Especiales"
                }
                Text(
                    text = subText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = (size.value * 0.14f).sp,
                    color = if (serviceType.contains("Especial")) Color.Gray else backgroundColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun LandingScreen(viewModel: HommysistViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // Full width header/logo rectangle touching left and right extremes
            HommysistLogo(isHeader = true)
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Hommysist",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "\"Problema en casa... solución sin pausa\"",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryGreen,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tu marketplace de confianza para servicios domésticos bajo demanda en Managua, Nicaragua.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Main CTAs
                Button(
                    onClick = { viewModel.setScreen("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("landing_login_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Iniciar Sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Button(
                    onClick = { viewModel.setScreen("register_client") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("landing_register_client_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrarse como Cliente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.setScreen("register_provider") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("landing_register_provider_button"),
                    border = BorderStroke(1.5.dp, PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Postular como Proveedor", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Highlights from business reports
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "¿Por qué usar Hommysist?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                FeatureHighlightCard(
                    icon = Icons.Default.CheckCircle,
                    title = "100% Verificados",
                    desc = "Cada trabajador doméstico e instalador técnico pasa por filtrado riguroso de antecedentes penales en Managua."
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                FeatureHighlightCard(
                    icon = Icons.Default.Star,
                    title = "Calificaciones Transparentes",
                    desc = "Controla la calidad mediante el sistema de satisfacción con rankings de 1 a 5 estrellas posterior a cada visita."
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                FeatureHighlightCard(
                    icon = Icons.Default.Info,
                    title = "Servicio Ágil",
                    desc = "Encuentra ayuda en minutos para Limpieza General, Electricidad, Plomería, Mascotas o Mandados en tu área local."
                )
            }
        }
    }
}

@Composable
fun FeatureHighlightCard(icon: ImageVector, title: String, desc: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LightBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = DarkText, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = desc, color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}

// ----------------------------------------------------
// 2. LOGIN SCREEN
// ----------------------------------------------------
@Composable
fun LoginScreen(viewModel: HommysistViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.setScreen("landing") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Sesión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ingresa con tus credenciales registradas para gestionar tus solicitudes en Managua.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Button(
                onClick = {
                    loading = true
                    viewModel.login(email, password) { success ->
                        loading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Ingresar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        // Shortcuts block specifically for fast evaluators and test runs
        item {
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Accesos Rápidos (Evaluación MVP):",
                fontWeight = FontWeight.Bold,
                color = SubtitleText,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            QuickLoginChip(
                role = "Cliente",
                email = "cliente@hommysist.com",
                desc = "Ver flujo de contratación del hogar.",
                onClick = {
                    email = "cliente@hommysist.com"
                    password = "123456"
                }
            )
        }

        item {
            QuickLoginChip(
                role = "Proveedor (Verificado)",
                email = "proveedor@hommysist.com",
                desc = "Aceptar solicitudes de limpieza activas.",
                onClick = {
                    email = "proveedor@hommysist.com"
                    password = "123456"
                }
            )
        }

        item {
            QuickLoginChip(
                role = "Proveedor (No Verificado)",
                email = "tecnico@hommysist.com",
                desc = "Ver el estado de aprobación bloqueado.",
                onClick = {
                    email = "tecnico@hommysist.com"
                    password = "123456"
                }
            )
        }

        item {
            QuickLoginChip(
                role = "Administrador",
                email = "admin@hommysist.com",
                desc = "Aprobar perfiles y auditar transacciones.",
                onClick = {
                    email = "admin@hommysist.com"
                    password = "123456"
                }
            )
        }
    }
}

@Composable
fun QuickLoginChip(role: String, email: String, desc: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = role, fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 13.sp)
                Text(text = "Correo: $email", fontSize = 12.sp, color = DarkText)
                Text(text = desc, fontSize = 11.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
        }
    }
}

// ----------------------------------------------------
// 3. REGISTER CLIENT SCREEN
// ----------------------------------------------------
@Composable
fun RegisterClientScreen(viewModel: HommysistViewModel) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.setScreen("landing") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registro de Cliente", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono (+505)") },
                placeholder = { Text("+505 8888-8888") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (Min 6 caracteres)") },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.register(nombre, telefono, email, "Cliente", password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Crear Cuenta de Cliente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        item {
            TextButton(onClick = { viewModel.setScreen("login") }) {
                Text("¿Ya tienes una cuenta? Inicia Sesión de inmediato", color = PrimaryBlue)
            }
        }
    }
}

// ----------------------------------------------------
// 4. REGISTER PROVIDER SCREEN
// ----------------------------------------------------
@Composable
fun RegisterProviderScreen(viewModel: HommysistViewModel) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var chosenCategory by remember { mutableStateOf("Limpieza") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.setScreen("landing") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ofrecer Servicios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Llena tus datos para registrarte como colaborador. Tu perfil será evaluado administrativamente antes de activarse.",
                color = Color.Gray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        item {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono de Contacto") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Text(
                text = "Categoría Principal de Trabajo:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                color = DarkText
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Limpieza", "Reparaciones", "Especiales").forEach { cat ->
                    val selected = chosenCategory == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) PrimaryBlue else Color(0xFFF1F5F9))
                            .border(
                                width = 1.dp,
                                color = if (selected) PrimaryBlue else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { chosenCategory = cat }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            color = if (selected) Color.White else DarkText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightBlueBg),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, PrimaryBlue)
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue)
                    Text(
                        text = "Recuerda: Los administradores en Managua validarán tus referencias laborales antes de desbloquear tu cuenta para aceptar solicitudes de clientes.",
                        fontSize = 11.sp,
                        color = PrimaryBlue,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.register(nombre, telefono, email, "Proveedor", password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Postular Como Colaborador", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        item {
            TextButton(onClick = { viewModel.setScreen("login") }) {
                Text("¿Ya estás registrado? Inicia Sesión", color = PrimaryBlue)
            }
        }
    }
}

// ----------------------------------------------------
// 5. MAIN NAVIGATION CONTROLLER (Home Layout)
// ----------------------------------------------------
@Composable
fun MainScreenController(viewModel: HommysistViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                if (currentUser?.rol == "Cliente") {
                    NavigationBarItem(
                        selected = selectedTab == "home_client",
                        onClick = { viewModel.setSelectedTab("home_client") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "my_services",
                        onClick = { viewModel.setSelectedTab("my_services") },
                        icon = { Icon(Icons.Default.List, contentDescription = "Mis Servicios") },
                        label = { Text("Servicios", fontSize = 11.sp) }
                    )
                }

                if (currentUser?.rol == "Proveedor") {
                    NavigationBarItem(
                        selected = selectedTab == "dashboard_provider",
                        onClick = { viewModel.setSelectedTab("dashboard_provider") },
                        icon = { Icon(Icons.Default.Build, contentDescription = "Mis Trabajos") },
                        label = { Text("Trabajar", fontSize = 11.sp) }
                    )
                }

                if (currentUser?.rol == "Administrador") {
                    NavigationBarItem(
                        selected = selectedTab == "dashboard_admin",
                        onClick = { viewModel.setSelectedTab("dashboard_admin") },
                        icon = { Icon(Icons.Default.AccountBox, contentDescription = "Admin") },
                        label = { Text("Panel Admin", fontSize = 11.sp) }
                    )
                }

                NavigationBarItem(
                    selected = selectedTab == "profile",
                    onClick = { viewModel.setSelectedTab("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Mi Perfil") },
                    label = { Text("Mi Perfil", fontSize = 11.sp) }
                )
            }
        }
    ) { innerPadding ->
        val activeChatRequestId by viewModel.activeChatRequestId.collectAsStateWithLifecycle()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeChatRequestId != null) {
                ChatScreen(viewModel, activeChatRequestId!!)
            } else {
                when (selectedTab) {
                    "home_client" -> ClientDashboard(viewModel)
                    "my_services" -> ClientMyServicesScreen(viewModel)
                    "create_request" -> CreateRequestScreen(viewModel)
                    "dashboard_provider" -> ProviderDashboardScreen(viewModel)
                    "dashboard_admin" -> AdminDashboardScreen(viewModel)
                    "profile" -> ProfileScreen(viewModel)
                }
            }
        }
    }
}

// ----------------------------------------------------
// 6. CLIENT DASHBOARD (Category Directory)
// ----------------------------------------------------
@Composable
fun ClientDashboard(viewModel: HommysistViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val subcategories by viewModel.subcategories.collectAsStateWithLifecycle()
    val allRequests by viewModel.allServiceRequests.collectAsStateWithLifecycle()

    var expandedCategoryId by remember { mutableStateOf<Int?>(null) }

    // Find if user has any active/running service requests
    val activeRequest = allRequests.firstOrNull {
        it.cliente_id == currentUser?.id && (it.estado == "Aceptado" || it.estado == "En Progreso")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP APP BAR (Professional Polish style)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // White logo on Ocean Blue background for the Home Screen / "Pantalla de inicio"
                    HommysistLogo(size = 42.dp, shape = androidx.compose.foundation.shape.CornerSize(12.dp))
                    Column {
                        Text(
                            text = "HOMMYSIST",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F4C81), // Azul Océano match
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Hola, ${currentUser?.nombre ?: "Cliente"} 👋",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ubicación",
                                tint = Color(0xFF64748B), // slate-500
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Altos de Santo Domingo, Managua",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B) // slate-500
                            )
                        }
                    }
                }

                // Profile Bubble Avatar with border and subtle shadow
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = currentUser?.nombre?.firstOrNull()?.toString()?.uppercase() ?: "C"
                    Text(
                        text = initial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        if (currentUser?.disponible == false) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = ErrorRed)
                        Column {
                            Text("Privacidad Resguardada", fontWeight = FontWeight.Bold, color = ErrorRed, fontSize = 14.sp)
                            Text(
                                "Tu cuenta está configurada en modo privado o restringido por la administración. No podrás coordinar solicitudes públicas hasta que se complete la verificación final.",
                                color = Color(0xFF7F1D1D),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. ACTIVE REQUEST CARD (Duplicating Design HTML logic using real state)
        if (activeRequest != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (activeRequest.estado == "En Progreso") "Servicio en Progreso" else "Servicio Aceptado",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeRequest.estado == "En Progreso") SecondaryGreen else PrimaryBlue,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${viewModel.getSubcategoryName(activeRequest.subcategoria_id)} - ${activeRequest.descripcion}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText,
                                    modifier = Modifier.padding(top = 2.dp),
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(100.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ID: ${activeRequest.id}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Proveedor",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeRequest.proveedor_id ?: "Asignando Colaborador...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Text(
                                    text = if (activeRequest.estado == "En Progreso") "Asistiendo en domicilio" else "Llega en 15 min aprox.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            IconButton(
                                onClick = { viewModel.showUiMessage("Llamando a colaborador: ${activeRequest.proveedor_id ?: "Asignando..."}") },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF8FAFC), CircleShape)
                                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Llamar",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. CATEGORIES HEADER
        item {
            Text(
                text = "¿Qué necesitas hoy?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155), // slate-700
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 5. DETAILED CATEGORY SELECTIONS
        if (categories.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
        } else {
            items(categories) { category ->
                val isExpanded = expandedCategoryId == category.id
                val categoryLogoName = when (category.id) {
                    1 -> "Limpieza"
                    2 -> "Reparaciones"
                    3 -> "Especiales"
                    else -> "Limpieza"
                }
                val color = when (category.id) {
                    1 -> Color(0xFF3D85C6) // Steel Blue
                    2 -> Color(0xFF7CA4D6) // Soft Sky Blue
                    3 -> Color(0xFFC9DAF2) // Celeste Pastel
                    else -> Color(0xFF3D85C6)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, if (isExpanded) color.copy(alpha = 0.5f) else Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedCategoryId = if (isExpanded) null else category.id
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            HommysistLogo(
                                size = 68.dp,
                                shape = androidx.compose.foundation.shape.CornerSize(14.dp),
                                serviceType = categoryLogoName
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = category.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = DarkText
                                )
                                Text(
                                    text = when (category.id) {
                                        1 -> "Limpieza General, Profunda..."
                                        2 -> "Electricidad, Plomería, Pintura..."
                                        3 -> "Mascotas, Adultos Mayores, Mandados..."
                                        else -> "Servicios rápidos de confianza"
                                    },
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expandir",
                                tint = Color.Gray
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val subs = subcategories.filter { it.categoria_id == category.id }
                                subs.forEach { sub ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.selectedCategoryForRequest.value = category
                                                viewModel.selectedSubcategoryForRequest.value = sub
                                                viewModel.setSelectedTab("create_request")
                                            }
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                            )
                                            Text(
                                                text = sub.nombre,
                                                fontWeight = FontWeight.SemiBold,
                                                color = DarkText,
                                                fontSize = 14.sp
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.AddCircle,
                                            contentDescription = "Crear Solicitud",
                                            tint = color,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. RECENT HISTORY / SUGGESTIONS SECTION
        item {
            Text(
                text = "Historial Reciente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155), // slate-700
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val completedRequests = allRequests.filter {
            it.cliente_id == currentUser?.id && (it.estado == "Finalizado" || it.estado == "Cancelado")
        }

        if (completedRequests.isEmpty()) {
            // Replicate the perfect polished visual layout as per the Design HTML
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF8FAFC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8), // slate-400
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Jardinería General",
                                fontWeight = FontWeight.Bold,
                                color = DarkText,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "12 Feb 2026 • Finalizado",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "C$ 450",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        } else {
            items(completedRequests) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF8FAFC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (request.categoria_id == 1) Icons.Default.Star else Icons.Default.Build,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = viewModel.getSubcategoryName(request.subcategoria_id),
                                fontWeight = FontWeight.Bold,
                                color = DarkText,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${request.fecha_servicio} • ${request.estado}",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "C$ 380",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 7. CREATE REQUEST FORM
// ----------------------------------------------------
@Composable
fun CreateRequestScreen(viewModel: HommysistViewModel) {
    val chosenCategory by viewModel.selectedCategoryForRequest.collectAsStateWithLifecycle()
    val chosenSubcategory by viewModel.selectedSubcategoryForRequest.collectAsStateWithLifecycle()

    var descripcion by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.setSelectedTab("home_client") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                }
                Text("Crear Solicitud", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Selection Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LightBlueBg),
                border = BorderStroke(1.dp, PrimaryBlue)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryBlue)
                        Column {
                            Text(
                                text = "Servicio Requerido:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                            Text(
                                text = "${chosenCategory?.nombre ?: "Categoría"} > ${chosenSubcategory?.nombre ?: "Subcategoría"}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción Detallada del Trabajo") },
                placeholder = { Text("Ej: Necesito limpieza de casa, lavado de ropa, pintura de sala, etc...") },
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("request_desc_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección de Domicilio o Colonia en Managua") },
                placeholder = { Text("Ej: Altamira, del pali 2 cuadras arriba.") },
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("request_address_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha y Hora Deseada") },
                placeholder = { Text("Ej: Mañana a las 10:00 AM") },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("request_date_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.createRequest(descripcion, direccion, fecha) {
                        descripcion = ""
                        direccion = ""
                        fecha = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("request_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar Solicitud", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ----------------------------------------------------
// 8. MIS SERVICIOS (Client solicitudes historical)
// ----------------------------------------------------
@Composable
fun ClientMyServicesScreen(viewModel: HommysistViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allRequests by viewModel.allServiceRequests.collectAsStateWithLifecycle()
    val myRequests = allRequests.filter { it.cliente_id == currentUser?.id }

    var selectedFilter by remember { mutableStateOf("Todos") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Historial de Solicitudes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // Filter tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos", "Pendientes", "Activos", "Finalizados").forEach { filter ->
                val selected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) PrimaryBlue else Color(0xFFF1F5F9))
                        .clickable { selectedFilter = filter }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        color = if (selected) Color.White else DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        val filteredList = when (selectedFilter) {
            "Pendientes" -> myRequests.filter { it.estado == "Pendiente" }
            "Activos" -> myRequests.filter { it.estado == "Aceptado" || it.estado == "En Progreso" }
            "Finalizados" -> myRequests.filter { it.estado == "Finalizado" || it.estado == "Cancelado" }
            else -> myRequests
        }

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(50.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No se encontraron solicitudes.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { request ->
                    ClientServiceCard(request, viewModel)
                }
            }
        }
    }
}

@Composable
fun ClientServiceCard(request: ServiceRequest, viewModel: HommysistViewModel) {
    val categoryName = viewModel.getCategoryName(request.categoria_id)
    val subcategoryName = viewModel.getSubcategoryName(request.subcategoria_id)

    var showRatingForm by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val serviceLogoType = when (request.categoria_id) {
                        1 -> "Limpieza"
                        2 -> "Reparaciones"
                        3 -> "Especiales"
                        else -> null
                    }
                    HommysistLogo(
                        size = 32.dp,
                        shape = androidx.compose.foundation.shape.CornerSize(8.dp),
                        serviceType = serviceLogoType
                    )
                    Text(
                        text = subcategoryName,
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                StatusBadge(status = request.estado)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = request.descripcion, color = DarkText, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Text(text = "Ubicación: ${request.direccion}", fontSize = 12.sp, color = SubtitleText)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Text(text = "Fecha: ${request.fecha_servicio}", fontSize = 12.sp, color = SubtitleText)
            }

            if (request.proveedor_id != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightBlueBg)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = PrimaryBlue)
                    Column {
                        Text("Proveedor asignado:", fontSize = 11.sp, color = Color.Gray)
                        Text(request.proveedor_id, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    }
                }
            }

            // CHAT ENABLER: Active direct messaging.
            if (request.estado == "Aceptado" || request.estado == "En Progreso") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.openChat(request.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chatear con Colaborador", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            if (request.estado == "Pendiente") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { viewModel.cancelRequest(request.id) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = BorderStroke(1.dp, ErrorRed),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Text("Cancelar Solicitud", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // RATING AND REVIEWS FOR CLIENT
            if (request.estado == "Finalizado") {
                if (request.rating == null) {
                    if (!showRatingForm) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showRatingForm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningGold),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Calificar este Servicio", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // subtle amber highlight
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Ayúdanos a evaluar a tu colaborador:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Stars selection row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 1..5) {
                                        val isSelected = i <= selectedRating
                                        IconButton(
                                            onClick = { selectedRating = i },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "$i estrellas",
                                                tint = if (isSelected) WarningGold else Color.LightGray,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = reviewComment,
                                    onValueChange = { reviewComment = it },
                                    placeholder = { Text("Deja tu reseña o comentario...", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextStyle(fontSize = 13.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = Color(0xFFCBD5E1)
                                    ),
                                    maxLines = 3
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { showRatingForm = false }) {
                                        Text("Cancelar", color = Color.Gray, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.submitRatingAndReview(request.id, selectedRating, reviewComment)
                                            showRatingForm = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Enviar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Display submitted rating/review read-only
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tu Evaluación:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                // Row of display stars
                                Row {
                                    for (i in 1..5) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (i <= request.rating) WarningGold else Color.LightGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            if (!request.comentario.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "\"${request.comentario}\"",
                                    fontSize = 13.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = DarkText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 9. PROVIDER DASHBOARD
// ----------------------------------------------------
@Composable
fun ProviderDashboardScreen(viewModel: HommysistViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allRequests by viewModel.allServiceRequests.collectAsStateWithLifecycle()
    val availableRequests = allRequests.filter { it.estado == "Pendiente" }
    val assignedRequests = allRequests.filter { it.proveedor_id == currentUser?.id }

    var selectedSection by remember { mutableStateOf("Disponibles") }

    if (currentUser?.verificado == false) {
        val needsDocumentation = currentUser?.cedula.isNullOrBlank() || currentUser?.fotoRostro.isNullOrBlank() || currentUser?.rechazado == true
        
        if (needsDocumentation) {
            var cedulaNumber by remember { mutableStateOf(currentUser?.cedula ?: "") }
            var isCedulaPhotoTaken by remember { mutableStateOf(currentUser?.cedula != null) }
            var isFacePhotoTaken by remember { mutableStateOf(currentUser?.fotoRostro != null) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Verificación de Identidad Requerida",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Para garantizar la seguridad y privacidad de todos los hogares en Nicaragua, debes subir una foto de tu cédula de identidad y una foto de tu rostro.",
                        fontSize = 13.sp,
                        color = SubtitleText,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }

                if (currentUser?.rechazado == true) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                    Text("Evaluación Rechazada", fontWeight = FontWeight.Bold, color = ErrorRed, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Motivo: ${currentUser?.motivoRechazo ?: "Información ilegible o incorrecta"}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF7F1D1D)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Por favor, vuelve a ingresar tu número de cédula y captura nuevas fotos.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF991B1B)
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = cedulaNumber,
                        onValueChange = { cedulaNumber = it },
                        label = { Text("Número de Cédula (Ej: 001-120593-1002A)") },
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )
                }

                // Cédula Document Upload Button / Simulation
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isCedulaPhotoTaken) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, if (isCedulaPhotoTaken) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCedulaPhotoTaken = true }
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isCedulaPhotoTaken) Icons.Default.CheckCircle else Icons.Default.Add,
                                contentDescription = null,
                                tint = if (isCedulaPhotoTaken) SecondaryGreen else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = if (isCedulaPhotoTaken) "Foto de Cédula Registrada ✓" else "Subir/Tomar Foto de Cédula",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isCedulaPhotoTaken) SecondaryGreen else DarkText
                            )
                            if (!isCedulaPhotoTaken) {
                                Text(
                                    text = "Asegúrate de que tus nombres y la foto sean perfectamente legibles.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SecondaryGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Simulación: Cedula_Frente_Preview.png", color = SecondaryGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                // Face Photo Upload Button / Simulation
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isFacePhotoTaken) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, if (isFacePhotoTaken) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isFacePhotoTaken = true }
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isFacePhotoTaken) Icons.Default.CheckCircle else Icons.Default.AccountBox,
                                contentDescription = null,
                                tint = if (isFacePhotoTaken) SecondaryGreen else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = if (isFacePhotoTaken) "Selfie Facial Registrada ✓" else "Tomar Foto del Rostro (Selfie)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isFacePhotoTaken) SecondaryGreen else DarkText
                            )
                            if (!isFacePhotoTaken) {
                                Text(
                                    text = "Mira de frente a la cámara en un espacio con buena iluminación.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SecondaryGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Simulación: Rostro_Selfie_Live.png", color = SecondaryGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            viewModel.submitVerificationDetails(cedulaNumber.trim(), "[ROSTRO_SELFIE_LIVE_OK]")
                        },
                        enabled = cedulaNumber.isNotBlank() && isCedulaPhotoTaken && isFacePhotoTaken,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Enviar Documentación para Registro", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                item {
                    TextButton(onClick = { viewModel.logout() }) {
                        Text("Cerrar Sesión", color = Color.Gray)
                    }
                }
            }
        } else {
            // Unverified but documents are already submitted and pending
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(WarningGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh, 
                        contentDescription = null, 
                        tint = WarningGold, 
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Documentación en Evaluación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hola, ${currentUser?.nombre}.\n\nHemos recibido correctamente tu Cédula (${currentUser?.cedula}) y tu foto de rostro. Actualmente, nuestro equipo de soporte está verificando la autenticidad e integridad de tu documentación.\n\nSe te notificará en cuanto un administrador confirme tus datos. ¡Gracias por confiar en Hommysist!",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = SubtitleText,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
                OutlinedButton(onClick = { viewModel.logout() }) {
                    Text("Cerrar Sesión")
                }
            }
        }
    } else {
        // Verified provider dashboard
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Panel del Proveedor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Tabs
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Disponibles", "Mis Trabajos").forEach { section ->
                    val selected = selectedSection == section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) PrimaryBlue else Color(0xFFF1F5F9))
                            .clickable { selectedSection = section }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (section == "Disponibles") "Solicitudes Libres (${availableRequests.size})" else "Mis Trabajos (${assignedRequests.size})",
                            color = if (selected) Color.White else DarkText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            val currentList = if (selectedSection == "Disponibles") availableRequests else assignedRequests

            if (currentList.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron solicitudes en esta categoría.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentList) { request ->
                        ProviderServiceCard(request, selectedSection == "Disponibles", viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderServiceCard(request: ServiceRequest, isAvailable: Boolean, viewModel: HommysistViewModel) {
    val subcategoryName = viewModel.getSubcategoryName(request.subcategoria_id)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val serviceLogoType = when (request.categoria_id) {
                        1 -> "Limpieza"
                        2 -> "Reparaciones"
                        3 -> "Especiales"
                        else -> null
                    }
                    HommysistLogo(
                        size = 32.dp,
                        shape = androidx.compose.foundation.shape.CornerSize(8.dp),
                        serviceType = serviceLogoType
                    )
                    Text(
                        text = subcategoryName,
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                StatusBadge(status = request.estado)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = request.descripcion, color = DarkText, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Text(text = "Zona: ${request.direccion}", fontSize = 12.sp, color = SubtitleText)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Text(text = "Fecha/Hora: ${request.fecha_servicio}", fontSize = 12.sp, color = SubtitleText)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isAvailable) {
                Button(
                    onClick = { viewModel.acceptRequest(request.id) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aceptar Solicitud", fontWeight = FontWeight.Bold)
                }
            } else {
                when (request.estado) {
                    "Aceptado" -> {
                        Button(
                            onClick = { viewModel.startService(request.id) },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Iniciar Trabajo", fontWeight = FontWeight.Bold)
                        }
                    }
                    "En Progreso" -> {
                        Button(
                            onClick = { viewModel.completeRequest(request.id) },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
                        ) {
                            Text("Marcar como Completado", fontWeight = FontWeight.Bold)
                        }
                    }
                    "Finalizado" -> {
                        if (request.rating != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Evaluación del Cliente:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B)
                                        )
                                        Row {
                                            for (i in 1..5) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (i <= request.rating) WarningGold else Color.LightGray,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                    if (!request.comentario.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "\"${request.comentario}\"",
                                            fontSize = 12.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = DarkText
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Sin evaluación de cliente todavía",
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.Gray
                            )
                        }
                    }
                    else -> {}
                }

                // Chat button for provider on active request
                if (request.estado == "Aceptado" || request.estado == "En Progreso") {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { viewModel.openChat(request.id) },
                        border = BorderStroke(1.dp, PrimaryBlue),
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chatear con Cliente", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 10. ADMIN DASHBOARD SCREEN
// ----------------------------------------------------
@Composable
fun AdminDashboardScreen(viewModel: HommysistViewModel) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allRequests by viewModel.allServiceRequests.collectAsStateWithLifecycle()

    val clients = allUsers.filter { it.rol == "Cliente" }
    val providers = allUsers.filter { it.rol == "Proveedor" }

    var activeAdminTab by remember { mutableStateOf("Proveedores") }
    var rejectionReasonInput by remember { mutableStateOf("") }
    var selectedProviderForRejection by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Panel Administrativo y de Privacidad", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // Statistics Summary
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Clientes", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("${clients.size} usuarios", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                }
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFE2E8F0)))
                Column {
                    Text("Proveedores", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("${providers.size} registrados", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                }
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFE2E8F0)))
                Column {
                    Text("Aprobados", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("${providers.count { it.verificado }} verificados", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                }
            }
        }

        // Triple tab layout
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Proveedores", "Clientes", "Servicios (${allRequests.size})").forEach { item ->
                val cleanedName = item.substringBefore(" ")
                val selected = activeAdminTab.startsWith(cleanedName)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) PrimaryBlue else Color(0xFFF1F5F9))
                        .clickable { activeAdminTab = cleanedName }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = if (selected) Color.White else DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        when (activeAdminTab) {
            "Proveedores" -> {
                if (providers.isEmpty()) {
                    Text("No hay proveedores.", color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(providers) { provider ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, CardBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = provider.nombre, fontWeight = FontWeight.Bold, color = DarkText)
                                            Text(text = "Email: ${provider.email}", fontSize = 12.sp, color = SubtitleText)
                                            Text(text = "Tel: ${provider.telefono}", fontSize = 12.sp, color = SubtitleText)
                                        }

                                        // Status representation toggling active privacy
                                        Column(horizontalAlignment = Alignment.End) {
                                            if (provider.disponible) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(100.dp))
                                                        .background(SecondaryGreen.copy(alpha = 0.12f))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Habilitado / Activo", color = SecondaryGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(100.dp))
                                                        .background(ErrorRed.copy(alpha = 0.12f))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Inhabilitado / Restringido", color = ErrorRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            TextButton(
                                                onClick = { viewModel.toggleUserActiveStatus(provider.email) },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(
                                                    text = if (provider.disponible) "Inhabilitar (Privacidad)" else "Habilitar Acceso",
                                                    fontSize = 11.sp,
                                                    color = PrimaryBlue,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (provider.verificado) SecondaryGreen
                                                    else if (provider.rechazado) ErrorRed
                                                    else WarningGold
                                                )
                                        )
                                        Text(
                                            text = if (provider.verificado) "Identidad Verificada" 
                                                   else if (provider.rechazado) "Identidad Rechazada" 
                                                   else "Esperando Validación de Identidad",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (provider.verificado) SecondaryGreen 
                                                    else if (provider.rechazado) ErrorRed 
                                                    else WarningGold
                                        )
                                    }

                                    // Display submitted identity documentation check block
                                    if (!provider.cedula.isNullOrBlank() || !provider.fotoRostro.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    "📂 Documentación Presentada:",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF475569)
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    "• Cédula de Identidad: ${provider.cedula ?: "No entregada"}",
                                                    fontSize = 12.sp,
                                                    color = DarkText,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text("• Foto de Rostro:", fontSize = 12.sp, color = DarkText)
                                                    if (provider.fotoRostro != null) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(PrimaryBlue.copy(alpha = 0.15f))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = "Selfie Cargada ✓", 
                                                                fontSize = 10.sp, 
                                                                color = PrimaryBlue, 
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    } else {
                                                        Text("No entregada", fontSize = 12.sp, color = ErrorRed)
                                                    }
                                                }
                                                
                                                // Preview simulations for facial comparison verification
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(60.dp)
                                                        .background(Color.White, RoundedCornerShape(6.dp))
                                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                                        .padding(6.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(48.dp)
                                                                .background(Color(0xFFE2E8F0), CircleShape),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.Gray)
                                                        }
                                                        Column {
                                                            Text("Verificación Audiovisual", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                                            Text(
                                                                text = "Coincidencia facial biométrica simulada: 98.4% de fiabilidad.",
                                                                fontSize = 10.sp,
                                                                color = SecondaryGreen,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (provider.rechazado && !provider.motivoRechazo.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Motivo de rechazo: ${provider.motivoRechazo}",
                                            color = ErrorRed,
                                            fontSize = 11.sp,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (selectedProviderForRejection == provider.email) {
                                            // Show rejection details input inline
                                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                OutlinedTextField(
                                                    value = rejectionReasonInput,
                                                    onValueChange = { rejectionReasonInput = it },
                                                    label = { Text("Motivo de rechazo", fontSize = 11.sp) },
                                                    maxLines = 1,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textStyle = TextStyle(fontSize = 12.sp)
                                                )
                                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                                    TextButton(onClick = { selectedProviderForRejection = null }) {
                                                        Text("Cancelar", fontSize = 11.sp)
                                                    }
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Button(
                                                        onClick = {
                                                            if (rejectionReasonInput.isNotBlank()) {
                                                                viewModel.rejectProvider(provider.email, rejectionReasonInput)
                                                                rejectionReasonInput = ""
                                                                selectedProviderForRejection = null
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                                        modifier = Modifier.height(32.dp)
                                                    ) {
                                                        Text("Confirmar Rechazo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        } else {
                                            // Non-editing button row
                                            if (!provider.verificado) {
                                                Button(
                                                    onClick = { viewModel.approveProvider(provider.email) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                                                    modifier = Modifier.weight(1f).height(38.dp),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Aprobar Propuesta", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                
                                                if (!provider.rechazado) {
                                                    OutlinedButton(
                                                        onClick = { selectedProviderForRejection = provider.email },
                                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                                        modifier = Modifier.weight(1f).height(38.dp),
                                                        border = BorderStroke(1.dp, ErrorRed),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Rechazar Propuesta", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            } else {
                                                // Revoke / Block verified identity
                                                OutlinedButton(
                                                    onClick = { 
                                                        selectedProviderForRejection = provider.email 
                                                        rejectionReasonInput = "Revocación administrativa por reporte o problema de privacidad."
                                                    },
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                                    modifier = Modifier.fillMaxWidth().height(38.dp),
                                                    border = BorderStroke(1.dp, ErrorRed),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Revocar / Rechazar Verificación", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "Clientes" -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(clients) { client ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = client.nombre, fontWeight = FontWeight.Bold, color = DarkText)
                                    Text(text = "Email: ${client.email}", fontSize = 12.sp, color = SubtitleText)
                                    Text(text = "Tel: ${client.telefono}", fontSize = 12.sp, color = SubtitleText)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Miembro desde: 2026", 
                                        fontSize = 11.sp, 
                                        color = Color.LightGray
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    if (client.disponible) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(100.dp))
                                                .background(SecondaryGreen.copy(alpha = 0.12f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Activo y Libre", color = SecondaryGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(100.dp))
                                                .background(ErrorRed.copy(alpha = 0.12f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Restringido / Privado", color = ErrorRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = { viewModel.toggleUserActiveStatus(client.email) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp),
                                        border = BorderStroke(1.dp, if (client.disponible) ErrorRed else PrimaryBlue)
                                    ) {
                                        Text(
                                            text = if (client.disponible) "Proteger Privacidad" else "Habilitar Cliente", 
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (client.disponible) ErrorRed else PrimaryBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "Servicios" -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allRequests) { req ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = viewModel.getSubcategoryName(req.subcategoria_id),
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                    StatusBadge(status = req.estado)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = req.descripcion, fontSize = 13.sp, color = DarkText)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Cliente: ${req.cliente_id}", fontSize = 11.sp, color = Color.Gray)
                                if (req.proveedor_id != null) {
                                    Text(text = "Proveedor: ${req.proveedor_id}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 11. PROFILE SCREEN
// ----------------------------------------------------
@Composable
fun ProfileScreen(viewModel: HommysistViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allRequests by viewModel.allServiceRequests.collectAsStateWithLifecycle()

    val myCompletedServices = allRequests.filter { it.proveedor_id == currentUser?.id && it.estado == "Finalizado" }
    val myRatedServices = myCompletedServices.filter { it.rating != null }
    val averageRating = if (myRatedServices.isNotEmpty()) myRatedServices.map { it.rating!! }.average() else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top initial badge
        item {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (currentUser?.nombre?.firstOrNull() ?: 'U').toString().uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }
        }

        item {
            Text(
                text = currentUser?.nombre ?: "Usuario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (currentUser?.verificado == true) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (currentUser?.verificado == true) SecondaryGreen else WarningGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (currentUser?.verificado == true) "Cuenta Verificada" else "Pendiente de Verificación",
                    fontWeight = FontWeight.Bold,
                    color = if (currentUser?.verificado == true) SecondaryGreen else WarningGold,
                    fontSize = 12.sp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (currentUser?.rol == "Proveedor") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), // light green focus card
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Mi Reputación y Desempeño",
                            fontWeight = FontWeight.Bold,
                            color = SecondaryGreen,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f", averageRating),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkText
                            )
                            Column {
                                Row {
                                    for (i in 1..5) {
                                        val filled = i <= Math.round(averageRating)
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (filled) WarningGold else Color.LightGray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${myRatedServices.size} evaluaciones recibidas",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Personal Information block
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Información de la Cuenta", fontWeight = FontWeight.Bold, color = DarkText, fontSize = 14.sp)
                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    ProfileInfoRow(label = "Rol:", value = currentUser?.rol ?: "No Definido")
                    ProfileInfoRow(label = "Correo:", value = currentUser?.email ?: "")
                    ProfileInfoRow(label = "Teléfono:", value = currentUser?.telefono ?: "")
                }
            }
        }

        // Business/Socioeconomic contact information from PDF
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Desarrollo de Negocio Sostenible", fontWeight = FontWeight.Bold, color = DarkText, fontSize = 14.sp)
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Text(
                        text = "Hommysist es un proyecto piloto fundado con el apoyo de la Universidad Americana (UAM) de Nicaragua.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = "Dirección: De la Rotonda Centroamérica, 2 C. al Norte, 1 C. al Este. Managua, Nicaragua.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("logout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = SubtitleText, fontSize = 13.sp)
        Text(text = value, fontWeight = FontWeight.Bold, color = DarkText, fontSize = 13.sp)
    }
}

// ----------------------------------------------------
// auxiliary components
// ----------------------------------------------------
@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "Pendiente" -> Pair(Color(0xFF64748B), "Pendiente")
        "Aceptado" -> Pair(PrimaryBlue, "Asignado")
        "En Progreso" -> Pair(WarningGold, "En Progreso")
        "Finalizado" -> Pair(SecondaryGreen, "Completado")
        "Cancelado" -> Pair(ErrorRed, "Cancelado")
        else -> Pair(Color.Gray, status)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.5.dp, color, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ChatScreen(viewModel: HommysistViewModel, requestId: Int) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allRequests by viewModel.allServiceRequests.collectAsStateWithLifecycle()
    
    val request = allRequests.firstOrNull { it.id == requestId }
    val detailName = request?.let { viewModel.getSubcategoryName(it.subcategoria_id) } ?: "Detalles de Servicio"
    
    var messageText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    // Scroll to bottom when new messages arrive or screen active
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // slate-50 background 
    ) {
        // Chat Header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.closeChat() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Cerrar Chat",
                        tint = DarkText
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = detailName,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (currentUser?.rol == "Cliente") {
                            "Proveedor: ${request?.proveedor_id ?: "Asignando"}"
                        } else {
                            "Cliente: ${request?.cliente_id ?: "Usuario"}"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFE2E8F0))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Chat de Coordinación y Detalles",
                            color = Color(0xFF475569),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No hay mensajes todavía.",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                            Text(
                                "Envía un mensaje para iniciar la conversación.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(messages) { message ->
                    val isMyMessage = message.sender_id == currentUser?.id
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isMyMessage) 16.dp else 4.dp,
                                        bottomEnd = if (isMyMessage) 4.dp else 16.dp
                                    )
                                )
                                .background(if (isMyMessage) PrimaryBlue else Color.White)
                                .border(
                                    1.dp, 
                                    if (isMyMessage) Color.Transparent else Color(0xFFF1F5F9), 
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isMyMessage) 16.dp else 4.dp,
                                        bottomEnd = if (isMyMessage) 4.dp else 16.dp
                                    )
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column {
                                if (!isMyMessage) {
                                    Text(
                                        text = if (message.sender_id.contains("@")) message.sender_id.substringBefore("@") else message.sender_id,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = PrimaryBlue,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                                Text(
                                    text = message.message,
                                    color = if (isMyMessage) Color.White else DarkText,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Message Input Bottom Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Escribe un mensaje...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendChatMessage(messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .background(PrimaryBlue, CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
