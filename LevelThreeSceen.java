package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

//black
public class LevelThreeScreen implements Screen {
    private MySpaceFarceGame game;
    OrthographicCamera camera;

    //screen
    private int enemiesKilled = 0;
    private Viewport viewport;

    //graphics
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private Texture explosionTexture;

    private TextureRegion[] backgrounds;
    private float backgroundHeight; //height of background in World units

    private TextureRegion playerShip1TextureRegion, playerShip2TextureRegion,
            playerShip3TextureRegion, playerShieldTextureRegion,
            enemySmallShipTextureRegion, enemyMediumShipTextureRegion,
            enemyBossShipTextureRegion, enemyShieldTextureRegion,
            bossEnemyLaserTextureRegion, playerLaserTextureRegion,
            smallEnemyLaserTextureRegion, mediumEnemyLaserTextureRegion,
            mediumAsteroidTextureRegion, bigAsteroidTextureRegion,
            greenShieldTextureRegion, fire5TextureRegion,
            greenPillTextureRegion, playerShipTextureRegion;

    //timing
    private float[] backgroundOffsets = {0, 0, 0, 0};
    private float backgroundMaxScrollingSpeed;
    private float enemySpawnTimer = 0;
    private float astroidSpawnTimer = 0;
    private float greenShieldSpawnTimer = 0;
    private float greenPillSpawnTimer = 0;
    private float fire5SpawnTimer = 0;

    //world parameters
    private final float WORLD_WIDTH = 72;
    private final float WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f;

    //game objects
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipList;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> explosionList;
    private LinkedList<Asteroid> astroidList;
    private LinkedList<PowerUp> powerUpList;

    // says how many ships on the screen at a time;
    private int bossShipCount = 0;
    private int mediumShipCount = 0;
    private int smallShipCount = 0;
    private int mediumAsteroidCount = 0;
    private int bigAsteroidCount = 0;

    // power up items
    private int greenShieldCount = 0;
    private int fire5Count = 0;
    private int greenPillCount = 0;
    private float timeBetweenGreenShieldSpawns;
    private float timeBetweenFire5Spawns;
    private float timeBetweenGreenPillSpawns;

    private int score;
    private int lives;
    private int lifeFactor;

    //Heads-Up Display
    BitmapFont font;
    float hudVerticalMargin, hudLeftX, hudRightX, hudCenterX, hudRow1Y, hudRow2Y,
            hudSectionWidth;

    LevelThreeScreen(MySpaceFarceGame game, int score, int lives, int lifeFactor, TextureRegion playerShipTextureRegion) {
        this.game = game;
        this.score = score;
        this.lives = lives;
        this.lifeFactor = lifeFactor;
        this.playerShip1TextureRegion = playerShipTextureRegion;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 320, 640);
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //set up the texture atlas
        textureAtlas = new TextureAtlas("images.atlas");

        //setting up the background
        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("purple1");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");

        backgroundHeight = WORLD_HEIGHT * 2;
        backgroundMaxScrollingSpeed = (float) (WORLD_HEIGHT) / 4;

        // initialize player texture regions
        playerShip1TextureRegion = textureAtlas.findRegion("playerShip1_green");
        playerShip2TextureRegion = textureAtlas.findRegion("playerShip2_green");
        playerShip3TextureRegion = textureAtlas.findRegion("playerShip3_green");
        playerShieldTextureRegion = textureAtlas.findRegion("shield3");
        playerLaserTextureRegion = textureAtlas.findRegion("laserGreen13");

        // initialize enemy texture regions
        enemySmallShipTextureRegion =  textureAtlas.findRegion("enemyRed2");
        enemyMediumShipTextureRegion = textureAtlas.findRegion("ufoYellow");
        enemyBossShipTextureRegion = textureAtlas.findRegion("enemyBlue1");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
        enemyShieldTextureRegion.flip(false, true);

        // enemy lasers
        smallEnemyLaserTextureRegion = textureAtlas.findRegion("laserRed14");
        mediumEnemyLaserTextureRegion = textureAtlas.findRegion("laserRed10");
        bossEnemyLaserTextureRegion = textureAtlas.findRegion("laserBlue03");

        // extra regions
        explosionTexture = new Texture("Explosion.png");
        mediumAsteroidTextureRegion = textureAtlas.findRegion("meteorGrey_med2");
        bigAsteroidTextureRegion = textureAtlas.findRegion("meteorGrey_big1");

        // power up
        greenShieldTextureRegion = textureAtlas.findRegion("powerupGreen_shield"); // shield
        fire5TextureRegion = textureAtlas.findRegion("fire05"); // upgrade laser
        greenPillTextureRegion = textureAtlas.findRegion("pill_green"); // life up

        //set up game objects
        playerShip = new PlayerShip(WORLD_WIDTH / 2, WORLD_HEIGHT / 4,
                10, 10, 49, 3, 0.4f, 4,
                45, 0.5f, 3,
                playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);

        enemyShipList = new LinkedList<>();
        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();
        explosionList = new LinkedList<>();
        astroidList = new LinkedList<>();
        powerUpList = new LinkedList<>();

        batch = new SpriteBatch();

        prepareHUD();
    }
    private void prepareHUD() {
        //Create a BitmapFont from our font file
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1, 1, 1, 0.3f);
        fontParameter.borderColor = new Color(0, 0, 0, 0.3f);

        font = fontGenerator.generateFont(fontParameter);

        //scale the font to fit world
        font.getData().setScale(0.08f);

        //calculate hud margins, etc.
        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudCenterX = WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;
    }
    @Override
    public void render(float deltaTime) {
        batch.begin();

        // scrolling background
        renderBackground(deltaTime);

        detectInput(deltaTime);
        playerShip.update(deltaTime);

        // spawn enemy ships
        spawnEnemyShips(deltaTime, 16, 3f,
                10,7f,
                7, 12f);
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while(enemyShipListIterator.hasNext()) {
            EnemyShip smallEnemyShip = enemyShipListIterator.next();
            moveEnemy(smallEnemyShip, deltaTime);
            smallEnemyShip.update(deltaTime);
            // enemy ships
            smallEnemyShip.draw(batch);
        }

        // spawn astroids
        spawnAsteroids(deltaTime, 24, 2.9f,
                14, 4.9f);
        ListIterator<Asteroid> asteroidListIterator = astroidList.listIterator();
        while(asteroidListIterator.hasNext()) {
            Asteroid asteroid = asteroidListIterator.next();
            moveAsteroid(asteroid, deltaTime);
            asteroid.update(deltaTime);
            // draw asteroid
            asteroid.draw(batch);

            //hope this takes it off the screen
            if(asteroid.boundingBox.y + asteroid.boundingBox.height < 0) {
                asteroidListIterator.remove();
            }
        }

        // spawn power ups
        spawnPowerUps(deltaTime, 4, 7.9f, 5,
                12.9f, 9, 8.9f);
        ListIterator<PowerUp> powerUpListIterator = powerUpList.listIterator();
        while(powerUpListIterator.hasNext()) {
            PowerUp powerUp = powerUpListIterator.next();
            movePowerUp(powerUp, deltaTime);
            powerUp.update(deltaTime);

            // draw powerup
            powerUp.draw(batch);

            //hope this takes it off the screen
            if(powerUp.boundingBox.y + powerUp.boundingBox.height < 0) {
                powerUpListIterator.remove();
            }
        }

        // player ship
        playerShip.draw(batch);

        // lasers
        renderLasers(deltaTime);

        // detect collisions between lasers and ships
        detectLaserShipCollisions();

        // detect collisions between lasers and asteroids
        detectLaserAsteroidCollisions();

        // detect collisions with the power up images
        detectPowerUpCollisions(deltaTime);

        // explosions
        updateAndRenderExplosions(deltaTime);

        // hud rendering
        updateAndRenderHUD();

        batch.end();

        if(enemiesKilled == 33) {
            // press enter to change screens
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.setScreen(new EndGameScreen(game, score));
                dispose();
            }
        }
    }
    private void updateAndRenderHUD() {
        // render top row labels
        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        font.draw(batch, "Shield", hudCenterX, hudRow1Y, hudSectionWidth, Align.center, false);
        font.draw(batch, "Lives", hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);

        // render second row values
        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudLeftX, hudRow2Y, hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.shield), hudCenterX, hudRow2Y, hudSectionWidth, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", lives), hudRightX, hudRow2Y, hudSectionWidth, Align.right, false);

        // add a life when score reaches 1000
        if(score >= 500 * lifeFactor * 1.6) {
            lives++;
            lifeFactor++;
        }
        if(lives >= 999) { // this does not stop lives from going above 999
            lives = 999;
        }
        if(lives == 0 && playerShip.shield == 0) {
            game.setScreen(new DiedScreen(game, score));
        }
    }
    private void spawnPowerUps(float deltaTime, int boldSilverFinalCount, float timeBetweenBoldSilverSpawns,
                               int fire5FinalCount, float timeBetweenFire5Spawns,
                               int greenPillFinalCount, float timeBetweenGreenPillSpawns) {
        spawnGreenShield(deltaTime, boldSilverFinalCount, timeBetweenBoldSilverSpawns);
        spawnFire5(deltaTime, fire5FinalCount, timeBetweenFire5Spawns);
        spawnGreenPill(deltaTime, greenPillFinalCount, timeBetweenGreenPillSpawns);
    }
    private void spawnGreenShield(float deltaTime, int boldSilverFinalCount, float timeBetweenGreenShieldSpawns) {// adds to shield number
        greenShieldSpawnTimer += deltaTime;

        if(greenShieldSpawnTimer > timeBetweenGreenShieldSpawns) {
            // spawn bold silver powre up
            if(greenShieldCount  < boldSilverFinalCount) {
                powerUpList.add(new PowerUp(MySpaceFarceGame.random.nextFloat() * (WORLD_WIDTH - 10) + 3,
                        WORLD_HEIGHT - 3, 3, 4, 38, "bold",
                        greenShieldTextureRegion));
                greenShieldSpawnTimer -= timeBetweenGreenShieldSpawns;
                greenShieldCount++;
            }
        }
    }
    private void spawnGreenPill(float deltaTime, int greenPillFinalCount, float timeBetweenGreenPillSpawns) {// adds to life number
        greenPillSpawnTimer += deltaTime;

        if(greenPillSpawnTimer > timeBetweenGreenPillSpawns) {
            // spawn green pills
            if(greenPillCount  < greenPillFinalCount) {
                powerUpList.add(new PowerUp(MySpaceFarceGame.random.nextFloat() * (WORLD_WIDTH - 10) + 3,
                        WORLD_HEIGHT - 3, 3, 4, 48, "pill", greenPillTextureRegion));
                greenPillSpawnTimer -= timeBetweenGreenPillSpawns;
                greenPillCount++;
            }
        }
    }
    private void spawnFire5(float deltaTime, int fire5FinalCount, float timeBetweenFire5Spawns) { // should change player ship lasers
        fire5SpawnTimer += deltaTime;

        if(fire5SpawnTimer > timeBetweenFire5Spawns) {
            if(fire5Count < fire5FinalCount) {
                powerUpList.add(new PowerUp(MySpaceFarceGame.random.nextFloat() * (WORLD_WIDTH - 10) + 3,
                        WORLD_HEIGHT - 3, 3, 4, 60, "five",
                        fire5TextureRegion));
                fire5SpawnTimer -= timeBetweenFire5Spawns;
                fire5Count++;
            }
        }
    }
    private void spawnAsteroids(float deltaTime, int mediumAsteroidFinalCount, float timeBetweenMAstroidSpawns,
                                int bigAsteroidFinalCount, float timeBetweenBAstroidSpawns) {
        spawnMediumAsteroids(deltaTime, mediumAsteroidFinalCount, timeBetweenMAstroidSpawns);
        spawnBigAsteroids(deltaTime, bigAsteroidFinalCount, timeBetweenBAstroidSpawns);
    }
    private void spawnMediumAsteroids(float deltaTime, int mediumAsteroidFinalCount, float timeBetweenMAsteroidSpawns) {
        astroidSpawnTimer += deltaTime;

        if(astroidSpawnTimer > timeBetweenMAsteroidSpawns) {
            // spawn medium sized astroids
            if(mediumAsteroidCount  < mediumAsteroidFinalCount) {
                astroidList.add(new Asteroid(MySpaceFarceGame.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                        WORLD_HEIGHT - 5, 5, 5, 18, 8, 12,
                        mediumAsteroidTextureRegion));
                astroidSpawnTimer -= timeBetweenMAsteroidSpawns;
                mediumAsteroidCount++;
            }
        }
    }
    private void spawnBigAsteroids(float deltaTime, int bigAsteroidFinalCount, float timeBetweenBAstroidSpawns) {
        astroidSpawnTimer += deltaTime;

        if(astroidSpawnTimer > timeBetweenBAstroidSpawns) {
            // spawn medium sized astroids
            if(bigAsteroidCount < bigAsteroidFinalCount) {
                astroidList.add(new Asteroid(MySpaceFarceGame.random.nextFloat() * (WORLD_WIDTH - 15) + 15,
                        WORLD_HEIGHT - 10, 15, 15, 15, 10, 24,
                        bigAsteroidTextureRegion));
                astroidSpawnTimer -= timeBetweenBAstroidSpawns;
                bigAsteroidCount++;
            }
        }
    }
    private void spawnEnemyShips(float deltaTime, int smallShipFinalCount, float timeBetweenSmallEnemySpawns,
                                 int mediumShipFinalCount, float timeBetweenMediumEnemySpawns,
                                 int bossShipFinalCount, float timeBetweenBossSpawns) {
        spawnSmallEnemyShips(deltaTime, smallShipFinalCount, timeBetweenSmallEnemySpawns);
        spawnMediumEnemyShips(deltaTime, mediumShipFinalCount, timeBetweenMediumEnemySpawns);
        spawnBossShip(deltaTime, bossShipFinalCount, timeBetweenBossSpawns);
    }
    private void spawnSmallEnemyShips(float deltaTime, int smallShipFinalCount, float timeBetweenSmallEnemySpawns) {
        enemySpawnTimer += deltaTime;

        if(enemySpawnTimer > timeBetweenSmallEnemySpawns) {
            // spawns little yellow fast ships
            if(smallShipCount < smallShipFinalCount) {
                enemyShipList.add(new EnemyShip(MySpaceFarceGame.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                        WORLD_HEIGHT - 5,
                        5, 5, 44, 4, 0.5f, 1,
                        25, 2f, 132, "small", 1,
                        enemySmallShipTextureRegion, enemyShieldTextureRegion, smallEnemyLaserTextureRegion));
                enemySpawnTimer -= timeBetweenSmallEnemySpawns;
                smallShipCount++;
            }
        }
    }
    private void spawnMediumEnemyShips(float deltaTime, int mediumShipFinalCount, float timeBetweenMediumEnemySpawns) {
        enemySpawnTimer += deltaTime;

        if(enemySpawnTimer > timeBetweenMediumEnemySpawns) {
            // spawns medium blue ships
            if(mediumShipCount < mediumShipFinalCount) {
                enemyShipList.add(new EnemyShip(MySpaceFarceGame.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                        WORLD_HEIGHT - 5,
                        7, 7, 34, 6, 1f, 2,
                        20, 1f, 242, "medium", 1,
                        enemyMediumShipTextureRegion, enemyShieldTextureRegion, mediumEnemyLaserTextureRegion));
                enemySpawnTimer -= timeBetweenMediumEnemySpawns;
                mediumShipCount++;
            }
        }
    }
    private void spawnBossShip(float deltaTime, int bossShipFinalCount, float timeBetweenBossSpawns) {
        enemySpawnTimer += deltaTime;

        if(enemySpawnTimer > timeBetweenBossSpawns) {
            // spawn big black alien ship
            if(bossShipCount < bossShipFinalCount) {
                enemyShipList.add(new EnemyShip(MySpaceFarceGame.random.nextFloat() * (WORLD_WIDTH - 20) + 5,
                        WORLD_HEIGHT - 5, 20, 20, 35, 13, 1.5f, 3,
                        20, 1f, 847, "boss", 2,
                        enemyBossShipTextureRegion, enemyShieldTextureRegion, bossEnemyLaserTextureRegion));
                enemySpawnTimer -= timeBetweenBossSpawns;
                bossShipCount++;
            }
        }
    }
    private void detectInput(float deltaTime) {
        // detect keyboard, touch, and click for mouse input
        // strategy: determine the max distance the ship can move
        // check each key that matters and move accordingly
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = (float) WORLD_HEIGHT / 2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) && rightLimit > 0) {
            playerShip.translate(Math.min(playerShip.movementSpeed * deltaTime, rightLimit), 0f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) && upLimit > 0) {
            playerShip.translate( 0f, Math.min(playerShip.movementSpeed * deltaTime, upLimit));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) && leftLimit < 0) {
            playerShip.translate(Math.max(-playerShip.movementSpeed * deltaTime, leftLimit), 0f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) && downLimit < 0) {
            playerShip.translate(0f, Math.max(-playerShip.movementSpeed * deltaTime, downLimit));
        }

        //touch input (also mouse)
        if(Gdx.input.isTouched()) {
            //get the screen position of the touch
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            //convert to world position
            Vector2 touchPoint = new Vector2(xTouchPixels, yTouchPixels);
            touchPoint = viewport.unproject(touchPoint);

            //calculate the x and y differences
            Vector2 playerShipCentre = new Vector2(
                    playerShip.boundingBox.x + playerShip.boundingBox.width / 2,
                    playerShip.boundingBox.y + playerShip.boundingBox.height / 2);

            float touchDistance = touchPoint.dst(playerShipCentre);

            if(touchDistance > TOUCH_MOVEMENT_THRESHOLD) {
                float xTouchDifference = touchPoint.x - playerShipCentre.x;
                float yTouchDifference = touchPoint.y - playerShipCentre.y;

                //scale to the maximum speed of the ship
                float xMove = xTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;
                float yMove = yTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;

                if(xMove > 0) xMove = Math.min(xMove, rightLimit);
                else xMove = Math.max(xMove, leftLimit);

                if(yMove > 0) yMove = Math.min(yMove, upLimit);
                else yMove = Math.max(yMove, downLimit);

                playerShip.translate(xMove, yMove);
            }
        }
    }
    private void movePowerUp(PowerUp powerUp, float deltaTime) {
        // strategy: determine the max distance the powerUp texture can spawn
        float leftLimit, rightLimit;
        leftLimit = -powerUp.boundingBox.x;
        rightLimit = WORLD_WIDTH - powerUp.boundingBox.x - powerUp.boundingBox.width;

        float xMove = powerUp.getDirectionVector().x * powerUp.movementSpeed * deltaTime;
        float yMove = powerUp.getDirectionVector().y * powerUp.movementSpeed * deltaTime;

        if(xMove > 0) {
            xMove = Math.min(xMove, rightLimit);
        } else {
            xMove = Math.max(xMove, leftLimit);
        }

        powerUp.translate(xMove, yMove);
    }
    private void moveAsteroid(Asteroid asteroid, float deltaTime) {
        // strategy: determin the max distance the ship can move in each direction
        float leftLimit, rightLimit;
        leftLimit = -asteroid.boundingBox.x;
        rightLimit = WORLD_WIDTH - asteroid.boundingBox.x - asteroid.boundingBox.width;

        float xMove = asteroid.getDirectionVector().x * asteroid.movementSpeed * deltaTime;
        float yMove = asteroid.getDirectionVector().y * asteroid.movementSpeed * deltaTime;

        if(xMove > 0) {
            xMove = Math.min(xMove, rightLimit);
        } else {
            xMove = Math.max(xMove, leftLimit);
        }

        asteroid.translate(xMove, yMove);
    }
    private void moveEnemy(EnemyShip enemyShip, float deltaTime) {
        //strategy: determine the max distance the ship can move
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -enemyShip.boundingBox.x;
        downLimit = (float) WORLD_HEIGHT / 2 - enemyShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        upLimit = WORLD_HEIGHT - enemyShip.boundingBox.y - enemyShip.boundingBox.height;

        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * deltaTime;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * deltaTime;

        if(xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove, leftLimit);

        if(yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove, downLimit);

        enemyShip.translate(xMove, yMove);
    }
    private void detectLaserShipCollisions() {
        //for each player laser, check whether it intersects an enemy ship
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while(enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();

                if(enemyShip.intersects(laser.boundingBox)) {
                    //contact with enemy ship
                    if(enemyShip.hitAndCheckDestroyed(laser)) {
                        enemyShipListIterator.remove();
                        explosionList.add(new Explosion(explosionTexture,
                                new Rectangle(enemyShip.boundingBox),
                                0.7f));
                        score += enemyShip.getScore();
                        enemiesKilled++;
                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }
        //for each enemy laser, check whether it intersects the player ship
        laserListIterator = enemyLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if(playerShip.intersects(laser.boundingBox)) {
                //contact with player ship
                if(playerShip.hitAndCheckDestroyed(laser)) {
                    explosionList.add(new Explosion(explosionTexture,
                            new Rectangle(playerShip.boundingBox),
                            1.6f));
                    if(lives > 0) {
                        playerShip.shield = 10;
                        lives--;
                    } else if(lives == 0) {
                        playerShip.shield = 0;
                    }
                }
                laserListIterator.remove();
            }
        }
    }
    private void detectPowerUpCollisions(float deltaTime) {
        // for each player laser, checks whether it intersects an asteroid
        ListIterator<PowerUp> powerUpListIterator = powerUpList.listIterator();
        while(powerUpListIterator.hasNext()) {
            PowerUp powerUp = powerUpListIterator.next();

            if(playerShip.intersects(powerUp.boundingBox)) {
                // contact with player ship
                if(powerUp.getName() == "pill" && powerUp.intersects(playerShip.boundingBox)) {
                    lives += 1;
                    powerUpList.remove();
                    break;
                } else if(powerUp.getName() == "bold" && powerUp.intersects(playerShip.boundingBox)) {
                    playerShip.shield += 3;
                    powerUpList.remove();
                    break;
                } else if(powerUp.getName() == "five" && powerUp.intersects(playerShip.boundingBox)) {
                    playerShip.setLasers(fire5TextureRegion, 2);
                    playerShip.update(deltaTime);
                    powerUpList.remove();
                    break;
                }
                powerUpListIterator.remove();
            }
        }
    }
    private void detectLaserAsteroidCollisions() {
        // for each player laser, checks whether it intersects an asteroid
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<Asteroid> asteroidListIterator = astroidList.listIterator();
            while(asteroidListIterator.hasNext()) {
                Asteroid asteroid = asteroidListIterator.next();

                if(asteroid.intersects(laser.boundingBox)) {
                    // contact with enemy ship
                    if(asteroid.hitAndCheckDestroyed(laser, playerShip.getPower())) {
                        asteroidListIterator.remove();
                        explosionList.add(new Explosion(explosionTexture,
                                new Rectangle(asteroid.boundingBox), 0.7f));
                        score += asteroid.getScore();
                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }
        // for each enemy laser, checks whether it intersects an asteroid
        laserListIterator = enemyLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<Asteroid> asteroidListIterator = astroidList.listIterator();
            while(asteroidListIterator.hasNext()) {
                Asteroid asteroid = asteroidListIterator.next();

                if(asteroid.intersects(laser.boundingBox)) {
                    // contact with enemy ship
                    if(asteroid.hitAndCheckDestroyed(laser, playerShip.getPower())) {
                        asteroidListIterator.remove();
                        explosionList.add(new Explosion(explosionTexture,
                                new Rectangle(asteroid.boundingBox), 0.7f));
                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }
    }
    private void updateAndRenderExplosions(float deltaTime) {
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while(explosionListIterator.hasNext()) {
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if(explosion.isFinished()) {
                explosionListIterator.remove();
            } else {
                explosion.draw(batch);
            }
        }
    }
    private void renderLasers(float deltaTime) {
        // create new player lasers
        if(playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            playerLaserList.addAll(Arrays.asList(lasers));
        }
        // create new enemy lasers
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while(enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            if(enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                enemyLaserList.addAll(Arrays.asList(lasers));
            }
        }
        // draw lasers and remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while(iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed * deltaTime;
            if(laser.boundingBox.y > WORLD_HEIGHT) {
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while(iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * deltaTime;
            if(laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }
    private void renderBackground(float deltaTime) {
        //update position of background images
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 8;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[3] += deltaTime * backgroundMaxScrollingSpeed;

        //draw each background layer
        for(int layer = 0; layer < backgroundOffsets.length; layer++) {
            if(backgroundOffsets[layer] > WORLD_HEIGHT) {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer],
                    WORLD_WIDTH, backgroundHeight);
        }
    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void show() {}
    @Override
    public void dispose() {}
}
