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
import com.fs.starfarer.api.loading.ProjectileSpawnType;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import static data.scripts.util.AdvForce.applyMomentum;
import data.scripts.util.Neutrino_CollisionUtilsEX;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;


public class NeutGravitonEffect implements OnHitEffectPlugin {

    //credits to Cycerin and Co.
    public Vector2f particleVelocity1;
    public Vector2f particleVelocity2;
    public float damageAmount = 1000f; //set this to whatever the damage should be
    public float empAmount = 0f; //set this to whatever the EMP damage should be
    public Color particleColor = new Color(54, 179, 200, 200);
    public float particleSize = 7f;
    public float particleBrightness = 0.98f;
    public float particleDuration = 3.75f;
    public float explosionSize = 575f;
    public float explosionSize2 = 315f;
    public float explosionDuration = 0.22f;
    public float explosionDuration2 = 1.0f;
    public float pitch = 1f; //sound pitch. Default seems to be 1
    public float volume = 0.7f; //volume, scale from 0-1
    public String soundName = "neutrino_gravitonexplosion"; //assign the sound we want to play
    public boolean dealsSoftFlux = false;

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean b, ApplyDamageResultAPI applyDamageResultAPI, CombatEngineAPI engine) {
        particleVelocity1 = projectile.getVelocity();
        particleVelocity2 = projectile.getVelocity();
        particleVelocity1.scale(0.02f);
        particleVelocity2.scale(0.06f);
//            damageAmount += (500f * Math.random());
//            if (shieldHitCheck) {
//                engine.applyDamage(target, point, damageAmount, DamageType.KINETIC, empAmount, false, dealsSoftFlux, engine);
//            }
        engine.addHitParticle(point, particleVelocity1, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity2, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity1, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity2, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity1, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity2, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity1, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity2, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity2, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity1, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity2, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity1, particleSize, particleBrightness, particleDuration, particleColor);
        engine.addHitParticle(point, particleVelocity2, particleSize, particleBrightness, particleDuration, particleColor);
        engine.spawnExplosion(point, particleVelocity1, particleColor, explosionSize, explosionDuration);
        engine.spawnExplosion(point, particleVelocity2, particleColor, explosionSize2, explosionDuration2);
        if (Global.getSettings().getModManager().isModEnabled("shaderLib")) {
            RippleDistortion Rip = new RippleDistortion();
            Rip.setLocation(point);
            Rip.setIntensity(200f);
            Rip.setLifetime(1f);
            Rip.setFrameRate(60);
            Rip.setCurrentFrame(0);
            Rip.setSize(350f);
            Rip.fadeInSize(0.25f);
            Rip.fadeOutIntensity(1f);
            Rip.flip(true);
            DistortionShader.addDistortion(Rip);
        }

        //SFX
        Global.getSoundPlayer().playSound(soundName, 1f, 1f, target.getLocation(), target.getVelocity());
        if (target instanceof ShipAPI) {
            ((ShipAPI) target).getFluxTracker().beginOverloadWithTotalBaseDuration(MathUtils.getRandomNumberInRange(4, 6));
        }
        List<CombatEntityAPI> entitis = CombatUtils.getEntitiesWithinRange(point, 475);
        for (CombatEntityAPI e : entitis) {
            Vector2f hitPoint;
            if (e instanceof DamagingProjectileAPI p) {
                if ("neutrino_graviton".equals(p.getProjectileSpecId()) || !ProjectileSpawnType.MISSILE.equals(p.getSpawnType())) {
                    continue;
                }
            }
            if (e instanceof ShipAPI hitShip) {

                if (hitShip.isFighter() || hitShip.isPhased()) {
                    continue;
                }
                hitPoint = Neutrino_CollisionUtilsEX.getShipCollisionPoint(point, e.getLocation(), hitShip);
                for (int i = 0; i < 5; i++) {
                    engine.spawnEmpArcPierceShields(
                            projectile.getSource(),
                            projectile.getLocation(),
                            hitShip,
                            hitShip,
                            DamageType.KINETIC,
                            damageAmount,
                            empAmount,
                            empAmount,
                            null,
                            10,
                            particleColor,
                            particleColor);
                }
            } else {
                hitPoint = e.getLocation();
                engine.spawnEmpArcPierceShields(
                        projectile.getSource(),
                        point,
                        null,
                        e,
                        DamageType.KINETIC,
                        damageAmount,
                        empAmount,
                        empAmount,
                        null,
                        10,
                        particleColor,
                        particleColor);
            }
            applyMomentum(e, hitPoint, projectile.getVelocity(), projectile.getDamageAmount() / 10f, true);
        }
    }
}
