package com.singhDevs.chezz.models

enum class MessageTypes(val value: String) {
    INFO("info"),
    INIT_GAME("init_game"),
    START_GAME("start_game"),
    MOVE("move"),
    RESIGN("resign"),
    DRAW("draw"),
    DRAW_REQUESTED("draw_requested"),
    GAME_OVER("game_over")
}