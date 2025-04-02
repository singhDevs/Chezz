<img src="https://github.com/user-attachments/assets/6641ba14-3075-47b7-863e-1789771b637b" width="100" height="100" style="border-radius: 50%;">

# Chezz
A real-time multiplayer Chess game android app built using web-sockets.

## Features
- **Real-Time Multiplayer Game**: Play live chess games with opponents around the world. Matches are timed and conclude on checkmate, draw, or timer expiry.
**Secure & Seamless**: Integrated Google sign-in ensures secure access. All game data is stored in PostgreSQL, allowing you to access games across devices.
**Flexible Game Modes**: Choose between Rated and Casual matches. In-game options include resigning and offering draws.
**Dynamic Skill Tracking**: Rated matches use a Glicko-2 rating system to accurately reflect your skill level, powered by the glicko2-ts library.
**Game Sharing**: Export your games in PGN format for detailed analysis or to share with friends.

## Time Control Modes
Chezz offers several time control modes to cater to different chess play styles:
- Bullet: 1 minute per game
- Blitz: Options for 3 or 5 minutes per game
- Rapid: Options for 10 or 30 minutes per game

These modes allow players to choose the pace that best fits their skill level and preference, whether you prefer fast, intense matches or a more measured, strategic game.

## Gameplay
1. **Sign In**: Log in using your Google account to securely access your game data.
2. **Start a Match**: Choose between Rated or Casual matches. For rated games, your skill level will be tracked using the Glicko-2 system. Choose the game duration you want to play with.
3. **Play & Enjoy**: Engage in fast-paced, timed chess matches. Utilize in-game options such as resigning or offering a draw.
4. **Review & Share**:  Export your game records in PGN format for further analysis or sharing with the chess community.

## About the Backend
Chezz communicates with a dedicated backend that manages:
- Real-time game updates via web-sockets.
- Secure storage and retrieval of game data.
- Authentication and user management.

For more details, please visit the [Chezz-Backend](https://github.com/singhDevs/Chezz-Backend) repository.

## About the Glicko-2 Rating System
Chezz integrates the glicko2-ts library to manage player ratings in Rated matches. I developed the glicko2-ts library to implement the advanced Glicko-2 system, which provides:
- **Accurate Skill Tracking**:  Adjusts player ratings based on match outcomes with considerations for uncertainty (Rating Deviation) and performance consistency (Volatility).
- **Adaptive Adjustments**:  When a player’s performance deviates from expectations, volatility increases, leading to faster rating changes. Conversely, consistent performance lowers volatility, stabilizing ratings.
- **Seamless Integration**: The glicko2-ts library is fully integrated into Chezz, enabling smooth and real-time updates of player ratings after every rated match.
- **Open Source Availability**: The glicko2-ts library is available for anyone to use, making it easy to integrate advanced dynamic rating systems into your own projects.

For more details, you can explore the [glicko2-ts](https://github.com/singhDevs/glicko2-ts) repository.
