package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import java.awt.Color;

import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import java.lang.Math;

public class NeutPulseEffect implements OnHitEffectPlugin {

    //credits to Cycerin and Co.
    public Vector2f particleVelocity1;
    public Vector2f particleVelocity2;
//    public float damageAmount = 200f; //set this to whatever the damage should be
    public float empAmount = 0f; //set this to whatever the EMP damage should be
    public Color particleColor = new Color(255,255, 255, 250);
    public float particleSize = 150f;
    public float particleBrightness = 0.98f;
    public float particleDuration = 1.75f;
    public float explosionSize = 200f;
    public float explosionSize2 = 65f;
    public float explosionDuration = 0.22f;
    public float explosionDuration2 = 1.0f;
    public float pitch = 1f; //sound pitch. Default seems to be 1
    public float volume = 0.7f; //volume, scale from 0-1
    public String soundName = "neutrino_gravitonexplosion2"; //assign the sound we want to play
    public boolean dealsSoftFlux = false;

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI applyDamageResultAPI, CombatEngineAPI engine) {
        if ((float) Math.random() > 0.00f && !shieldHit && target instanceof ShipAPI) {

            particleVelocity1 = projectile.getVelocity();
            particleVelocity2 = projectile.getVelocity();
            particleVelocity1.scale(0.1f);
            particleVelocity2.scale(0.1f);
            float damageAmount = projectile.getDamageAmount() * 0.5f;

            ((ShipAPI) target).getMutableStats().getEffectiveArmorBonus().modifyMult("NeutPulseEffect", 0);
            engine.applyDamage(target, point, damageAmount, DamageType.ENERGY, empAmount, false, dealsSoftFlux, engine);
            ((ShipAPI) target).getMutableStats().getEffectiveArmorBonus().unmodifyMult("NeutPulseEffect");
            for (int i = (int) particleSize; i > 4; i -= 3) {
                engine.addSmokeParticle(point, particleVelocity1, i, particleBrightness, particleDuration * (1 - (i / particleSize * 2)), particleColor);
            }

            Global.getSoundPlayer().playSound(soundName, 1f, 1f, target.getLocation(), target.getVelocity());

        }
    }
}
