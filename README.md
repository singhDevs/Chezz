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

## Screenshots
### Home
![home](https://github.com/user-attachments/assets/62f1973d-a708-4d12-8fe9-35c4d8e3dcf5)

<br/><br/>
### Rated or Casual options
![rated or casual](https://github.com/user-attachments/assets/346b7147-5615-447e-a45c-4601c175f66b)

<br/><br/>
### Time Control options
![time modes](https://github.com/user-attachments/assets/feec2830-5480-4d50-8897-4bffa447271c)

<br/><br/>
### Rating History Chart
![Ratings Chart](https://github.com/user-attachments/assets/f07f16db-24f4-4f9c-94f6-0c8909b3fcfd)

<br/><br/>
### Matchmaking screen
![waiting](https://github.com/user-attachments/assets/b038f355-9ce1-4fee-bdcd-6dba7709182c)

<br/><br/>
### Game begins
![game start](https://github.com/user-attachments/assets/7b14dcf1-0b80-42b7-bccc-ffbfad97032b)

<br/><br/>
### Resignation dialog
![resign](https://github.com/user-attachments/assets/5bc82c09-127e-440f-9bee-720007bb6feb)

<br/><br/>
### Draw option
![draw](https://github.com/user-attachments/assets/4d84dfb0-921e-45d7-a667-dabe70512601)

<br/><br/>
### Game Over
![Game Over](https://github.com/user-attachments/assets/7212fd39-6b77-4707-bd45-d52b239dd27b)

<br/><br/>
### Exporting PGN
![export pgn](https://github.com/user-attachments/assets/3554d12f-06cb-421f-8b92-e0b35af3a20b)
