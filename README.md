# Space-Farce-Game
Made a game for my senior project using LibGDX and Android Studio

Hello, I followed a template from a Youtuber name Bradon Grasley and it help me learn how to create a
space shooter type game. The name of their tutorial is Android- Space Shooter game. This application 
use LibGDX setup and ran on desktop but also able to be ran on an Adroid Application. This is also 
my first game I constructed by myself for my senior project

The gradle script I showed in build.gradle(Project: Space_Farce) was changed from what was imported 
when running the setup with LibGdx launcher. The changes to android, core, and desktop allow for 
the text editor that I used in my project. 

In the DesktopLauncher.java class the foreground frames per secound to 60. The title was set to the 
name of my game. And the windowed mode was set to 360 in width and 640 in the height so that the 
background is not stretched too much. 

The MySpaceFarceGame.java file is where the game is initialized when the game is started. The 
MainMenuScreen class gets passed a refrence to the game and passed the initial score of zero. 
The MainMenuScreen class initilizes the players lives to five. This screen sends some text to 
the screen and takes a click from the mouse, touch of the screen if on the phone or pressing 
enter to transition to the next screen. The MainMenuScreen class passes a refrence to the game,
the initial score, and the initial player lives to the LevelOneScreen class. 
