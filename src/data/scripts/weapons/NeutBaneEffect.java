package data.scripts.weapons;

import java.awt.Color;

import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import data.scripts.util.Neutrino_ParticlesEffectLib;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;

public class NeutBaneEffect implements OnHitEffectPlugin {


    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f vector2f, boolean shieldHit, ApplyDamageResultAPI applyDamageResultAPI, CombatEngineAPI engine) {
        if ((float) Math.random() > 0.975f && target instanceof ShipAPI) {
            float emp = projectile.getDamageAmount();
            float dam = 4 * emp;
            float facing = projectile.getFacing();
            if (shieldHit) {
                SimpleEntity dir = new SimpleEntity(MathUtils.getPointOnCircumference(vector2f, -10f, facing));
                engine.spawnEmpArc(projectile.getSource(), vector2f, dir, dir,
                        DamageType.KINETIC,
                        dam,
                        emp, // emp
                        25f, // max range
                        "tachyon_lance_emp_impact",
                        0f, // thickness
                        new Color(217, 204, 0, 255),
                        new Color(234, 80, 64, 255));
                engine.applyDamage(target, vector2f, dam, DamageType.ENERGY, emp, false, true, projectile.getSource());
            } else {
                engine.spawnEmpArc(projectile.getSource(), vector2f, target, target,
                        DamageType.ENERGY,
                        dam,
                        emp, // emp
                        130f, // max range
                        "tachyon_lance_emp_impact",
                        2F, // thickness
                        new Color(217, 204, 0, 255),
                        new Color(234, 80, 64, 255));
            }

            Neutrino_ParticlesEffectLib.AddParticles(
                    15, 1, false,
                    vector2f, 5f,
                    facing, 30f,
                    -100, -25,
                    5, 10,
                    0.7f, 1f,
                    0.5f, 2f,
                    new Color(234, 80, 64, 255));
        }
    }
}
