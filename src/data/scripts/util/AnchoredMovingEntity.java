package data.scripts.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@code CombatEntityAPI} that follows and rotates with
 * another anchoring {@code CombatEntityAPI} while moving relatively.
 *
 * @author Deathfly
 */
public class AnchoredMovingEntity implements CombatEntityAPI {

    protected Vector2f relativeVelocity = new Vector2f();
    protected Vector2f relativeLocation = new Vector2f();
    protected CombatEntityAPI anchor;
    protected float timeStamp;
    protected float relativeDistance;
    protected float relativeAngle;
    protected float angle;
    protected Map<String, Object> customData = new HashMap<>();

    public AnchoredMovingEntity(CombatEntityAPI anchor, Vector2f location, Vector2f relativeVelocity) {
        reanchor(anchor, location, relativeVelocity);
    }

    private float getTET() {
        return Global.getCombatEngine() != null ? Global.getCombatEngine().getTotalElapsedTime(false) : 0f;
    }

    public void reanchor(CombatEntityAPI newAnchor, Vector2f newLocation, Vector2f newRelativeVelocity) {
        this.anchor = newAnchor;
        if (newAnchor != null) {
            this.relativeDistance = MathUtils.getDistance(newAnchor.getLocation(), newLocation);
            this.angle = newAnchor.getFacing();
            this.relativeAngle = MathUtils.clampAngle(VectorUtils.getAngle(newAnchor.getLocation(), newLocation) - newAnchor.getFacing());
        }
        this.relativeVelocity = newRelativeVelocity != null ? new Vector2f(newRelativeVelocity) : new Vector2f();
        this.timeStamp = getTET();
    }

    public void rotateRelativeVelocity() {
        if (anchor != null && anchor.getFacing() != angle) {
            VectorUtils.rotate(relativeVelocity, MathUtils.getShortestRotation(angle, anchor.getFacing()), relativeVelocity);
            angle = anchor.getFacing();
        }
    }

    public void setLocation(Vector2f location) {
        reanchor(this.anchor, location, this.relativeVelocity);
    }

    @Override
    public Vector2f getLocation() {
        if (anchor == null) return new Vector2f();

        Vector2f toReturn = new Vector2f(anchor.getLocation());
        float currentTET = getTET();
        if (timeStamp != currentTET) {
            rotateRelativeVelocity();
            Vector2f variation = new Vector2f(relativeVelocity);
            variation.scale(currentTET - timeStamp);
            Vector2f.add(toReturn, variation, toReturn);
        }
        return toReturn;
    }

    public void setVelocity(Vector2f velocity) {
        if (this.anchor != null) {
            reanchor(this.anchor, getLocation(), velocity);
        }
    }

    @Override
    public Vector2f getVelocity() {
        if (anchor == null) return new Vector2f();
        rotateRelativeVelocity();
        return Vector2f.add(anchor.getVelocity(), relativeVelocity, null);
    }

    @Override
    public float getFacing() {
        return anchor != null ? anchor.getFacing() : angle;
    }

    @Override
    public void setFacing(float facing) {
        this.angle = facing;
    }

    @Override
    public float getAngularVelocity() {
        return anchor != null ? anchor.getAngularVelocity() : 0f;
    }

    @Override
    public void setAngularVelocity(float angVel) {
    }

    @Override
    public int getOwner() {
        return anchor != null ? anchor.getOwner() : 100;
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
        return anchor == null || anchor.isExpired();
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
    public boolean isPointInBounds(Vector2f point) {
        return false;
    }

    @Override
    public boolean wasRemoved() {
        return false;
    }
}