// By Deathfly
package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class NeutrinoAdvancedDiveBombAIStats extends BaseShipSystemScript {

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        stats.getMaxSpeed().modifyPercent(id, 20f * effectLevel);
        stats.getAcceleration().modifyPercent(id, 20f * effectLevel);
        stats.getDeceleration().modifyPercent(id, 20f * effectLevel);
        stats.getTurnAcceleration().modifyPercent(id, 20f * effectLevel);
        stats.getMaxTurnRate().modifyPercent(id, 20f * effectLevel);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("advanced dive bomb AI engaged", false);
        }
        return null;
    }


    @Override
    public String getDisplayNameOverride(State state, float v) {
        return "";
    }
}
