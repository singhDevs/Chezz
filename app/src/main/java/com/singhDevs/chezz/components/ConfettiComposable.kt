package com.singhDevs.chezz.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.singhDevs.chezz.viewmodels.ChessBoardViewModel
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem

@Composable
fun ConfettiComposable(viewModel: ChessBoardViewModel) {
    val state: ChessBoardViewModel.State by viewModel.state.observeAsState(ChessBoardViewModel.State.Idle)
    when (val newState = state) {
        is ChessBoardViewModel.State.Started ->
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = newState.party,
                updateListener =
                    object : OnParticleSystemUpdateListener {
                        override fun onParticleSystemEnded(
                            system: PartySystem,
                            activeSystems: Int,
                        ) {
                            if (activeSystems == 0) viewModel.ended()
                        }
                    },
            )

        ChessBoardViewModel.State.Idle -> {}
    }
}