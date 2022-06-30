package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PowerUp {
    // power up characteristics
    float movementSpeed; // in world units per second
    Vector2 directionVector;
    private String name;

    // position and dimension
    Rectangle boundingBox;

    // graphics
    TextureRegion powerUpTextureRegion;

    // constructor
    public PowerUp(float xCenter, float yCenter,
                    float width, float height,
                    float movementSpeed, String name,
                    TextureRegion powerUpTextureRegion) {

        this.movementSpeed = movementSpeed;

        this.boundingBox = new Rectangle(xCenter - width / 2, yCenter - height / 2, width, height);
        this.powerUpTextureRegion = powerUpTextureRegion;
        this.name = name;

        directionVector = new Vector2(0, -1);
    }
    public String getName() {
        return name;
    }

    public boolean intersects(Rectangle otherRectangle) {
        return boundingBox.overlaps(otherRectangle);
    }

    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, boundingBox.y + yChange);
    }

    private void randomizedDirectionVector() {
        //moves strait down
        double bearing = 3.141592; // 0 to 3PI/2
        directionVector.x = (float) Math.sin(bearing);
        directionVector.y = (float) Math.cos(bearing);
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
        batch.draw(powerUpTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
