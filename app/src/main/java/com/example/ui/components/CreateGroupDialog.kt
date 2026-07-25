package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ContactEntity
import com.example.ui.theme.*

@Composable
fun CreateGroupDialog(
    contacts: List<ContactEntity>,
    onDismiss: () -> Unit,
    onCreateGroup: (String, List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    val selectedContactIds = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Group, contentDescription = null, tint = TacticalEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Encrypted Group", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name (e.g. Tactical Squad)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TacticalEmerald,
                        unfocusedBorderColor = BorderSlate
                    ),
                    modifier = Modifier.testTag("input_group_name")
                )

                Text("Select Nodes to Include:", fontSize = 12.sp, color = TextMuted)

                LazyColumn(
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(contacts, key = { it.id }) { contact ->
                        val isSelected = selectedContactIds.contains(contact.id)
                        Surface(
                            color = if (isSelected) TacticalEmerald.copy(alpha = 0.2f) else CardSlate,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) selectedContactIds.remove(contact.id)
                                    else selectedContactIds.add(contact.id)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = contact.name, color = TextPrimary, fontSize = 14.sp)
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = TacticalEmerald)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (groupName.isNotBlank() && selectedContactIds.isNotEmpty()) {
                        onCreateGroup(groupName, selectedContactIds.toList())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_confirm_create_group")
            ) {
                Text("Create Multi-Key Group", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSlate
    )
}
