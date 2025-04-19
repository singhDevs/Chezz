<img src="https://github.com/user-attachments/assets/6641ba14-3075-47b7-863e-1789771b637b" width="100" height="100" style="border-radius: 50%;">

# Chezz
A real-time multiplayer Chess game android app built using web-sockets.<br>
[Jump to Phone screenshots](https://github.com/singhDevs/Chezz?tab=readme-ov-file#screenshots)

## Releases
Chezz v1.2.4 has been released! Downalod the APK from the release. Check it out [here](https://github.com/singhDevs/Chezz/releases/tag/v1.2.4).

## Features
### 🎮 Play the Way You Like

-  **Rated or Casual** — Choose your mode instantly. Red for Rated, Green for Casual.
-  **Game Types** — Play Bullet (1 min), Blitz (3–5 min), or Rapid (10 min).
-  **Smart Play Button** — One tap to start the right match.

### ⏱️ In-Game, In Control
- Clean UI. Intuitive design. View players' ratings (in rated games) & time left with each player.
- Scrollable move history during gameplay.

### 🏁 Instant Game Summary
- See game results and rating changes right after you finish.
- Options to:
  - Start a new game
  - Export game in PGN format
  - View opponent's profile

### ♻️ Replay & Improve
- Watch every past move with a visual board.
- Analyze turn-by-turn and learn from mistakes.
- Relive your best matches anytime.

### 📊 Performance at a Glance
- Interactive graph showing rating trends across Bullet, Blitz, and Rapid.
- Drag to view how your rating evolved over time.
- Recent games feed for quick reviews.

### 👤 Your Chess Identity
- View your profile image, join date, and all ratings.
- Full game history: played, won, lost, drawn.
- Win probability stats to track your growth.
- Share your profile via link with friends or online.

## Time Control Modes
Chezz offers several time control modes to cater to different chess play styles:
- Bullet: 1 minute per game
- Blitz: Options for 3 or 5 minutes per game
- Rapid: Options for 10 or 30 minutes per game

These modes allow players to choose the pace that best fits their skill level and preference, whether you prefer fast, intense matches or a more measured, strategic game.

## 📲 How to Install
1. **Visit the latest release**: [Chezz v1.2.4](https://github.com/singhDevs/Chezz/releases/tag/v1.2.4)
2. **Download the APK**  
   - Scroll down to the **Assets** section below.  
   - Click to download the file: `Chezz-v1.2.4.apk`.

3. **Enable installation from unknown sources**  
   - On your Android device, go to:  
     `Settings → Apps & notifications → Special app access → Install unknown apps`  
     (or `Settings → Security`, depending on your Android version).  
   - Select the app you used to download the APK (e.g., Chrome or Files) and enable **"Allow from this source."**

4. **Install the APK**  
   - Open your Downloads folder and tap the `Chezz-v1.2.4.apk` file.  
   - Confirm any prompts to complete the installation.

5. **Launch Chezz**  
   - Open the app from your home screen or app drawer and start playing! ♟️

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

## Why Chezz?
No clutter. No distractions. Just you, your opponent, and the board. Chezz is built to be elegant, fast, and focused — perfect for improving your game and enjoying every move.

## T&C and Privacy policy
You can view Chezz's T&C and privacy polivy [here](https://singhdevs.github.io/chezz/T&C/).

## Screenshots
<img src="https://github.com/user-attachments/assets/49a2065b-18b5-4a0e-8a54-e067a1f7dfe0" width="350" height="600" style="border-radius: 12px; margin-right: 16px;" /> 
<img src="https://github.com/user-attachments/assets/b6e74f3f-5084-4f6a-97e2-0d7032db2a07" width="350" height="600" style="border-radius: 12px;" />
<img src="https://github.com/user-attachments/assets/997894dd-67f4-4ab0-998e-249ca1a1b796" width="350" height="600"/>
<img src="https://github.com/user-attachments/assets/1b732b2e-58d2-42e2-bdb3-ce297ab07403" width="350" height="600"/>
<img src="https://github.com/user-attachments/assets/a88d0f2f-b7fb-4d98-9958-b0a13e69237c" width="350" height="600"/>
<img src="https://github.com/user-attachments/assets/b22764fc-fb48-4e50-9ed6-2126151dabaf" width="350" height="600"/>
