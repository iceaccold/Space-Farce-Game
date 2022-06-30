package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Asteroid {
    // asteroid characteristics
    float movementSpeed; // in world units per second
    int health;
    int score;
    Vector2 directionVector;

    // position and dimension
    Rectangle boundingBox;

    // graphics
    TextureRegion asteroidTextureRegion;

    // constructor
    public Asteroid(float xCenter, float yCenter,
                    float width, float height,
                    float movementSpeed, int health, int score,
                    TextureRegion asteroidTextureRegion) {

        this.movementSpeed = movementSpeed;
        this.health = health;
        this.score = score;

        this.boundingBox = new Rectangle(xCenter - width / 2, yCenter- height / 2, width, height);
        this.asteroidTextureRegion = asteroidTextureRegion;

        directionVector = new Vector2(0, -1);
    }
    public int getScore() {
        return score;
    }
    public boolean intersects(Rectangle otherRectangle) {
        return boundingBox.overlaps(otherRectangle);
    }
    public boolean hitAndCheckDestroyed(Laser laser, int damage) {
        if(health > 0) {
            health -= damage;
            return false;
        }
        return true;
    }
    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, boundingBox.y + yChange);
    }
    private void randomizedDirectionVector() {
        //moves strait down
        double bearing = 3.141592; // 0 to 3PI/2
        directionVector.x = (float)Math.sin(bearing);
        directionVector.y = (float)Math.cos(bearing);
        // 4.712388 3pi/2
        //3.141592
    }
    public Vector2 getDirectionVector() {
        return directionVector;
    }
    public void update(float deltaTime) {
        randomizedDirectionVector();
    }
    public void draw(Batch batch) {
        batch.draw(asteroidTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        if(health > 0) {
            batch.draw(asteroidTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        }
    }
}
