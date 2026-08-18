package data.scripts.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@code CombatEntityAPI} for moving dummy entities.
 *
 * @author Deathfly
 */
public class MovingEntity implements CombatEntityAPI {

    protected Vector2f velocity = new Vector2f();
    protected Vector2f location = new Vector2f();
    protected float timeStamp = 0f;
    protected Map<String, Object> customData = new HashMap<>();

    public MovingEntity(Vector2f location, Vector2f velocity) {
        this.location = location != null ? new Vector2f(location) : new Vector2f();
        this.velocity = velocity != null ? new Vector2f(velocity) : new Vector2f();
        this.timeStamp = getTET();
    }

    private float getTET() {
        return Global.getCombatEngine() != null ? Global.getCombatEngine().getTotalElapsedTime(false) : 0f;
    }

    public void setLocation(Vector2f location) {
        this.location = location != null ? new Vector2f(location) : new Vector2f();
        this.timeStamp = getTET();
    }

    @Override
    public Vector2f getLocation() {
        float currentTET = getTET();
        if (velocity != null && (velocity.x != 0f || velocity.y != 0f) && currentTET != timeStamp) {
            Vector2f variation = new Vector2f(velocity);
            variation.scale(currentTET - timeStamp);
            Vector2f.add(location, variation, location);
            timeStamp = currentTET;
        }
        return location;
    }

    public void setVelocity(Vector2f velocity) {
        float currentTET = getTET();
        if (timeStamp != 0f && currentTET != timeStamp) {
            getLocation();
        }
        this.velocity = velocity != null ? new Vector2f(velocity) : new Vector2f();
    }

    @Override
    public Vector2f getVelocity() {
        return velocity;
    }

    @Override
    public float getFacing() {
        return 0f;
    }

    @Override
    public void setFacing(float facing) {
    }

    @Override
    public float getAngularVelocity() {
        return 0f;
    }

    @Override
    public void setAngularVelocity(float angVel) {
    }

    @Override
    public int getOwner() {
        return 100;
    }

    @Override
    public void setOwner(int owner) {
    }

    @Override
    public float getCollisionRadius() {
        return 0f;
    }

    @Override
    public void setCollisionRadius(float radius) {
    }

    @Override
    public CollisionClass getCollisionClass() {
        return CollisionClass.NONE;
    }

    @Override
    public void setCollisionClass(CollisionClass collisionClass) {
    }

    @Override
    public float getMass() {
        return 0f;
    }

    @Override
    public void setMass(float mass) {
    }

    @Override
    public BoundsAPI getExactBounds() {
        return null;
    }

    @Override
    public ShieldAPI getShield() {
        return null;
    }

    @Override
    public float getHullLevel() {
        return 1f;
    }

    @Override
    public float getHitpoints() {
        return 1f;
    }

    @Override
    public float getMaxHitpoints() {
        return 1f;
    }

    @Override
    public void setHitpoints(float v) {
    }

    @Override
    public Object getAI() {
        return null;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void setCustomData(String key, Object value) {
        customData.put(key, value);
    }

    @Override
    public void removeCustomData(String key) {
        customData.remove(key);
    }

    @Override
    public Map<String, Object> getCustomData() {
        return customData;
    }

    @Override
    public boolean isPointInBounds(Vector2f vector2f) {
        return false;
    }

    @Override
    public boolean wasRemoved() {
        return false;
    }
}