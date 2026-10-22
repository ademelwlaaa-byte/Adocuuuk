@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.util.AppFeatureFlags

/**
 * Özellik toggle paneli — GlobalSettingsModal içinden veya tek başına açılabilir.
 * AppFeatureFlags.allToggles() listesini okur, kararlı + deneysel olarak gruplar.
 */
@Composable
fun FeatureFlagsPanel(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EmochiCard)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Başlık
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = null,
                    tint = EmochiPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Özellikler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmochiTextPrimary
                )
            }

            Spacer(Modifier.height(4.dp))
            Text(
                "Değişiklikler anında geçerli olur ve uygulama yeniden başlatılsa da korunur.",
                fontSize = 12.sp,
                color = EmochiTextMuted,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(16.dp))

            val allToggles = remember { AppFeatureFlags.allToggles() }
            val stable = allToggles.filter { !it.isExperimental }
            val experimental = allToggles.filter { it.isExperimental }

            // ─── Kararlı özellikler ────────────────────────────────────────
            if (stable.isNotEmpty()) {
                Text(
                    "Kararlı",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmochiTextMuted,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                stable.forEach { spec ->
                    FeatureToggleRow(spec)
                    Spacer(Modifier.height(4.dp))
                }
            }

            if (experimental.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Divider(color = EmochiBorder, thickness = 0.5.dp)
                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Science,
                        contentDescription = null,
                        tint = EmochiTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Deneysel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmochiTextMuted
                    )
                }
                Spacer(Modifier.height(6.dp))
                experimental.forEach { spec ->
                    FeatureToggleRow(spec, isExperimental = true)
                    Spacer(Modifier.height(4.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Kapat", color = EmochiPrimary)
            }
        }
    }
}

@Composable
private fun FeatureToggleRow(
    spec: AppFeatureFlags.ToggleSpec,
    isExperimental: Boolean = false
) {
    var checked by remember { mutableStateOf(spec.getter()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(EmochiSurface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                spec.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = EmochiTextPrimary
            )
            Text(
                spec.description,
                fontSize = 11.sp,
                color = EmochiTextMuted,
                lineHeight = 15.sp
            )
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = { v ->
                checked = v
                spec.setter(v)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = EmochiPrimary,
                checkedTrackColor = EmochiPrimary.copy(alpha = 0.4f),
                uncheckedThumbColor = EmochiTextMuted,
                uncheckedTrackColor = EmochiBorder
            )
        )
    }
}