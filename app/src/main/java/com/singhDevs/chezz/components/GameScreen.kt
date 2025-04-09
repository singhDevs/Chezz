package com.singhDevs.chezz.components

//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Refresh
//import androidx.compose.material.icons.rounded.MoreVert
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.singhDevs.chezz.R
//
//@Composable
//fun GameActivity(modifier: Modifier = Modifier) {
//    // App color scheme based on logo colors
//    val darkBackground = Color(0xFF1A1A1A)
//    val darkerBackground = Color(0xFF121212)
//    val primaryAmber = Color(0xFFBF8F3F)       // Gold/amber color from knight's accents
//    val textColor = Color(0xFFE0E0E0)          // Light gray for text
//    val fieldBackground = Color(0xFF2A2A2A)    // Slightly lighter than background
//    val accentBrown = Color(0xFF8B5A2B)        // Darker accent for buttons
//
//    val gradientColors = listOf(primaryAmber, accentBrown)
//
//    var showMenu by remember { mutableStateOf(false) }
//    var moveHistoryExpanded by remember { mutableStateOf(false) }
//
//    val player1 = Player("hey.idkrandom6", R.drawable.pfp_unavailable, "04:12")
//    val player2 = Player("guranshsingh100", R.drawable.anime_pfp, "05:23")
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(darkerBackground)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            // Top App Bar
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(
//                    onClick = { /* Back action */ },
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(CircleShape)
//                        .background(fieldBackground)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,
//                        contentDescription = "Back",
//                        tint = textColor
//                    )
//                }
//
//                Text(
//                    text = "CHEZZ",
//                    style = MaterialTheme.typography.titleLarge.copy(
//                        fontWeight = FontWeight.Bold,
//                        letterSpacing = 2.sp
//                    ),
//                    color = primaryAmber
//                )
//
//                IconButton(
//                    onClick = { showMenu = !showMenu },
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(CircleShape)
//                        .background(fieldBackground)
//                ) {
//                    Icon(
//                        imageVector = Icons.Rounded.MoreVert,
//                        contentDescription = "Menu",
//                        tint = textColor
//                    )
//                }
//
//                DropdownMenu(
//                    expanded = showMenu,
//                    onDismissRequest = { showMenu = false },
//                    modifier = Modifier.background(darkBackground)
//                ) {
//                    DropdownMenuItem(
//                        text = { Text("Settings", color = textColor) },
//                        onClick = { /* Handle settings */ },
//                        leadingIcon = {
//                            Icon(
//                                imageVector = Icons.Default.Refresh,
//                                contentDescription = null,
//                                tint = primaryAmber
//                            )
//                        }
//                    )
//                    DropdownMenuItem(
//                        text = { Text("New Game", color = textColor) },
//                        onClick = { /* Handle new game */ },
//                        leadingIcon = {
//                            Icon(
//                                imageVector = Icons.Default.Refresh,
//                                contentDescription = null,
//                                tint = primaryAmber
//                            )
//                        }
//                    )
//                }
//            }
//
//            // Player 2 (Top player)
//            PlayerInfoCard(
//                player = player2,
//                isTopPlayer = true,
//                surfaceColor = darkBackground,
//                accentColor = primaryAmber,
//                textColor = textColor,
//                fieldBackground = fieldBackground
//            )
//
//            // Chess Board (Placeholder)
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(1f)
//                    .padding(vertical = 16.dp)
//            ) {
//                // ChessBoard Composable will be placed here
//                // Don't modify this as per your instructions
//            }
//
//            // Player 1 (Bottom player)
//            PlayerInfoCard(
//                player = player1,
//                isTopPlayer = false,
//                surfaceColor = darkBackground,
//                accentColor = primaryAmber,
//                textColor = textColor,
//                fieldBackground = fieldBackground
//            )
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            // Game actions and move history
//            Column(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                // Move History Section
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp),
//                    colors = CardDefaults.cardColors(containerColor = darkBackground),
//                    shape = RoundedCornerShape(12.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(12.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = "Move History",
//                                style = MaterialTheme.typography.titleMedium,
//                                color = primaryAmber
//                            )
//
//                            IconButton(onClick = { moveHistoryExpanded = !moveHistoryExpanded }) {
//                                Icon(
//                                    imageVector = if (moveHistoryExpanded) Icons.Default.ArrowBack else Icons.Default.ArrowBack,
//                                    contentDescription = "Expand",
//                                    modifier = Modifier.padding(4.dp),
//                                    tint = primaryAmber
//                                )
//                            }
//                        }
//
//                        if (moveHistoryExpanded) {
//                            // Show more moves when expanded
//                            MoveHistoryItem(
//                                moveNumber = 1,
//                                moveNotation = "f2f3",
//                                accentColor = primaryAmber,
//                                backgroundColor = fieldBackground,
//                                textColor = textColor
//                            )
//                            // Add more move history items here as needed
//                        } else {
//                            // Show only the latest move when collapsed
//                            MoveHistoryItem(
//                                moveNumber = 1,
//                                moveNotation = "f2f3",
//                                accentColor = primaryAmber,
//                                backgroundColor = fieldBackground,
//                                textColor = textColor
//                            )
//                        }
//                    }
//                }
//
//                // Action Buttons
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    // Resign Button
//                    Button(
//                        onClick = { /* Resign action */ },
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(50.dp)
//                            .padding(end = 8.dp),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = accentBrown
//                        )
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_resign),
//                            contentDescription = "Resign",
//                            tint = textColor,
//                            modifier = Modifier.padding(end = 8.dp)
//                        )
//                        Text(
//                            text = "RESIGN",
//                            color = textColor,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//
//                    // Draw Button
//                    Button(
//                        onClick = { /* Draw action */ },
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(50.dp)
//                            .padding(start = 8.dp),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = accentBrown
//                        )
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_draw),
//                            contentDescription = "Draw",
//                            tint = textColor,
//                            modifier = Modifier.padding(end = 8.dp)
//                        )
//                        Text(
//                            text = "DRAW",
//                            color = textColor,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun PlayerInfoCard(
//    player: Player,
//    isTopPlayer: Boolean,
//    surfaceColor: Color,
//    accentColor: Color,
//    textColor: Color,
//    fieldBackground: Color
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = surfaceColor),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Player avatar
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape)
//                    .background(accentColor),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = player.username.first().toString(),
//                    color = Color.Black,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp
//                )
//                // In actual implementation, use an Image composable with the avatar resource
//            }
//
//            // Player info
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(horizontal = 12.dp)
//            ) {
//                Text(
//                    text = player.username,
//                    style = MaterialTheme.typography.titleMedium,
//                    color = textColor
//                )
//
//                Text(
//                    text = if (isTopPlayer) "Black pieces" else "White pieces",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.Gray
//                )
//            }
//
//            // Timer
//            Box(
//                modifier = Modifier
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(
//                        if (isTopPlayer) fieldBackground else accentColor
//                    )
//                    .padding(horizontal = 12.dp, vertical = 8.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.ic_rapid),
//                        contentDescription = "Timer",
//                        tint = if (isTopPlayer) textColor else Color.Black,
//                        modifier = Modifier.size(16.dp)
//                    )
//
//                    Spacer(modifier = Modifier.width(4.dp))
//
//                    Text(
//                        text = player.timeRemaining,
//                        color = if (isTopPlayer) textColor else Color.Black,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 14.sp
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun MoveHistoryItem(
//    moveNumber: Int,
//    moveNotation: String,
//    accentColor: Color,
//    backgroundColor: Color,
//    textColor: Color
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        // Move number
//        Box(
//            modifier = Modifier
//                .size(24.dp)
//                .clip(CircleShape)
//                .background(accentColor),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = moveNumber.toString(),
//                color = Color.Black,
//                fontSize = 12.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        // Move notation
//        Text(
//            text = moveNotation,
//            modifier = Modifier
//                .padding(start = 12.dp)
//                .weight(1f),
//            color = textColor
//        )
//    }
//}
//
//data class Player(
//    val username: String,
//    val avatarResId: Int,
//    val timeRemaining: String
//)
//
//@Preview
//@Composable
//private fun GameActivityScreen() {
//    GameActivity()
//}