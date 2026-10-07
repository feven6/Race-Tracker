/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.racetracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.racetracker.R
import com.example.racetracker.ui.theme.RaceTrackerTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun RaceTrackerApp() {

    val playerOne = remember {
        RaceParticipant(name = "Player 1", progressIncrement = 1)
    }

    val playerTwo = remember {
        RaceParticipant(name = "Player 2", progressIncrement = 2)
    }

    var raceInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(raceInProgress) {
        if (raceInProgress) {
            coroutineScope {
                launch { playerOne.run() }
                launch { playerTwo.run() }
            }
            raceInProgress = false
        }
    }

    RaceTrackerScreen(
        playerOne = playerOne,
        playerTwo = playerTwo,
        isRunning = raceInProgress,
        onRunStateChange = { raceInProgress = it },
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    )
}

@Composable
fun RaceTrackerScreen(
    playerOne: RaceParticipant,
    playerTwo: RaceParticipant,
    isRunning: Boolean,
    onRunStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Race Tracker",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_walk),
            contentDescription = "Walking icon",
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )

        StatusIndicator(
            participantName = playerOne.name,
            currentProgress = playerOne.currentProgress,
            maxProgress = "Max: ${playerOne.maxProgress}",
            progressFactor = playerOne.progressFactor,
            modifier = Modifier.fillMaxWidth()
        )

        if (playerOne.currentProgress >= playerOne.maxProgress / 2) {
            Text(
                text = "Player 1 At Mid Race!!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        StatusIndicator(
            participantName = playerTwo.name,
            currentProgress = playerTwo.currentProgress,
            maxProgress = "Max: ${playerTwo.maxProgress}",
            progressFactor = playerTwo.progressFactor,
            modifier = Modifier.fillMaxWidth()
        )

        if (playerTwo.currentProgress >= playerTwo.maxProgress / 2) {
            Text(
                text = "Player 2 At Mid Race!!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        RaceControls(
            isRunning = isRunning,
            onRunStateChange = onRunStateChange,
            onReset = {
                playerOne.reset()
                playerTwo.reset()
                onRunStateChange(false)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StatusIndicator(
    participantName: String,
    currentProgress: Int,
    maxProgress: String,
    progressFactor: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_walk),
                contentDescription = "$participantName walking icon",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = participantName,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        LinearProgressIndicator(
            progress = { progressFactor },
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Progress: $currentProgress")
            Text(text = maxProgress)
        }
    }
}

@Composable
fun RaceControls(
    isRunning: Boolean,
    onRunStateChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { onRunStateChange(!isRunning) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRunning) "Pause" else "Start")
        }

        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaceTrackerAppPreview() {
    RaceTrackerTheme {
        RaceTrackerApp()
    }
}