# SharieBot

### 0.1.0 - Completed 9.29.23
Implement authentication for the bot through Twitch using the implicit grant flow.</br>
Basic informational commands (plain text) such as !school, using an event handler to gather chat messages.</br>
### 0.2.0 - In Progress
Begin to build more user interactive commands, such as !shiny.</br>
Add dependencies for database integration (SQLite) and create schemas.</br>
Working connection between commands and data storage, including writing and reading.</br>
### 0.3.0
Add further event handling for follows, subscriptions, and raids.</br>
Auto-shoutout a streamer upon receiving a raid.</br>
Cascade follow and subscription events to OBS to update visual overlay.</br>
Implement broadcaster only commands, such as !addquote and !brb.</br>
### 0.4.0
Wrangle mp3 handling for the music portion of the chatbot.</br>
Possible libraries: ffsampled.</br>
Implement shuffle behavior.</br>
Integrate automatic polls for selecting upcoming songs.</br>
Store data for the current session to use in commands like !playhistory.</br>
### 1.0.0
Final polish, including any last minute commands.</br>
Add commands for editing other commands that are likely to change in order to reduce the frequency that the code base needs to be “touched” and redeployed.</br>
Deploy in order to keep the bot running 24/7, and not on a personal machine.</br>
