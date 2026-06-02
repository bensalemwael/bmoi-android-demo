package com.bmoi.sifipdemo.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmoi.sifipdemo.R
import com.bmoi.sifipdemo.data.mock.MockScenario
import com.bmoi.sifipdemo.ui.components.BmoiPrimaryButton
import com.bmoi.sifipdemo.ui.components.BmoiSecondaryButton
import com.bmoi.sifipdemo.ui.components.BmoiTopBar
import com.bmoi.sifipdemo.ui.components.CheckStepRow
import com.bmoi.sifipdemo.ui.theme.BmoiBorder
import com.bmoi.sifipdemo.ui.theme.BmoiMuted
import com.bmoi.sifipdemo.ui.theme.BmoiPurple
import com.bmoi.sifipdemo.ui.theme.BmoiPurpleDeep
import com.bmoi.sifipdemo.ui.theme.StatusError
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onAuthenticated: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        // Bandeau supérieur "Identification" avec scénario picker en surcouche
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BmoiPurple)
                .height(48.dp),
        ) {
            Text(
                text = "Identification",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                ScenarioPicker(current = state.scenario, onSelected = viewModel::setScenario)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bienvenue sur BMOI Mobile Banking",
            color = BmoiPurpleDeep,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Carte "Identifiants" plate avec coins légèrement arrondis
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BmoiBorder),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FieldLabel("Login")
                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = viewModel::onPhoneChanged,
                    placeholder = { Text("Numéro de téléphone") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.PersonOutline,
                            contentDescription = null,
                            tint = BmoiPurple,
                        )
                    },
                    enabled = state.phase != LoginPhase.Running,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiPurple),
                )

                FieldLabel("Contrôles SIFIP")
                HorizontalDivider(color = BmoiBorder, thickness = 1.dp)
                CheckStepRow(check = state.numberVerify)
                CheckStepRow(check = state.simSwap)
                CheckStepRow(check = state.deviceSwap)
                CheckStepRow(check = state.authorization)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton Valider centré
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp),
        ) {
            when (state.phase) {
                LoginPhase.Failure -> {
                    Text(
                        text = "Connexion bloquée par le contrôle SIFIP.",
                        color = StatusError,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                    )
                    BmoiSecondaryButton(
                        text = "Réessayer",
                        onClick = viewModel::reset,
                    )
                }
                else -> {
                    BmoiPrimaryButton(
                        text = "Valider",
                        onClick = { viewModel.login(onAuthenticated) },
                        loading = state.phase == LoginPhase.Running,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = BmoiMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun ScenarioPicker(
    current: MockScenario,
    onSelected: (MockScenario) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = "Scénario : ${current.label}",
                tint = Color.White,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MockScenario.values().forEach { scenario ->
                DropdownMenuItem(
                    text = { Text(scenario.label) },
                    onClick = {
                        onSelected(scenario)
                        expanded = false
                    },
                )
            }
        }
    }
}
