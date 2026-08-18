// by Deathfly
// Credit to MesoTroniK, mod from his code.
package data.missions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class BaseRandomNeutrinoMissionDefinition implements MissionDefinitionPlugin {

    private static final String[] OBJECTIVE_TYPES = {
            "sensor_array", "nav_buoy", "comm_relay"
    };

    private static final float TARGET_DP = 240f;

    private FactionAPI enemy;
    private FactionAPI player;
    private final Random rand = new Random();
    private boolean fullRandom = false;

    private static final Map<String, Float> QUALITY_FACTORS = new HashMap<>(10);

    static {
        QUALITY_FACTORS.put("neutrinocorp", 0.85f);
        QUALITY_FACTORS.put(Factions.DIKTAT, 0.5f);
        QUALITY_FACTORS.put(Factions.HEGEMONY, 0.5f);
        QUALITY_FACTORS.put(Factions.INDEPENDENT, 0.5f);
        QUALITY_FACTORS.put(Factions.LUDDIC_CHURCH, 0.25f);
        QUALITY_FACTORS.put(Factions.LUDDIC_PATH, 0f);
        QUALITY_FACTORS.put(Factions.PIRATES, 0f);
        QUALITY_FACTORS.put(Factions.TRITACHYON, 0.85f);
        QUALITY_FACTORS.put(Factions.PERSEAN, 0.5f);
    }

    private boolean isMilitaryCombatShip(ShipVariantAPI variant) {
        if (variant == null || variant.isFighter() || variant.isStation()) {
            return false;
        }

        String variantId = variant.getHullVariantId();
        if (variantId != null) {
            String vLower = variantId.toLowerCase();
            if (vLower.contains("unsung") || vLower.contains("neutrino_rm-r") || vLower.contains("neutrino_hammer")) {
                return false;
            }
        }

        ShipHullSpecAPI hullSpec = variant.getHullSpec();
        if (hullSpec == null) {
            return false;
        }


        if (hullSpec.getHints().contains(ShipHullSpecAPI.ShipTypeHints.CIVILIAN)) {
            return false;
        }
        if (hullSpec.getHints() != null && hullSpec.getHints().contains(ShipHullSpecAPI.ShipTypeHints.CIVILIAN)) {
            return false;
        }

        String hullId = hullSpec.getHullId();
        if (hullId != null) {
            String hLower = hullId.toLowerCase();
            return !hLower.contains("unsung") && !hLower.contains("neutrino_rm-r") && !hLower.contains("neutrino_hammer");
        }

        return true;
    }

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        if (player == null || enemy == null) {
            chooseFactions(null, null, fullRandom, 0);
        }

        api.initFleet(FleetSide.PLAYER, "", FleetGoal.ATTACK, false, 5);
        api.initFleet(FleetSide.ENEMY, "", FleetGoal.ATTACK, true, 5);

        api.setFleetTagline(FleetSide.PLAYER, player.getDisplayNameLong() + " forces");
        api.setFleetTagline(FleetSide.ENEMY, enemy.getDisplayNameLong() + " forces");

        int size = 3;

        int playerPoints = generateFleetForSide(api, FleetSide.PLAYER, player, size);
        int enemyPoints = generateFleetForSide(api, FleetSide.ENEMY, enemy, size);

        float width = 18000f;
        float height = 18000f;
        api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);

        float minX = -width / 2;
        float minY = -height / 2;

        int objectiveCount = 6;
        while (objectiveCount > 0) {
            String type = OBJECTIVE_TYPES[rand.nextInt(3)];
            float theta = (float) (Math.random() * Math.PI);
            double radius = width;
            radius = radius * 0.15 + radius * 0.3 * Math.random();
            int x = (int) (Math.cos(theta) * radius);
            int y = (int) (Math.sin(theta) * radius);
            api.addObjective(x, -y, type);
            api.addObjective(-x, y, type);
            objectiveCount -= 2;
        }

        api.addBriefingItem(player.getDisplayName() + "  (" + playerPoints + " DP)   vs.  " + enemy.getDisplayName() + "  (" + enemyPoints + " DP)");

        float nebulaChance = MathUtils.getRandomNumberInRange(0, 100);
        if (nebulaChance < 50) {
            float nebulaCount = 15 + (float) Math.random() * 25;
            float nebulaSize = (float) Math.random();

            for (int i = 0; i < nebulaCount; i++) {
                float x = (float) Math.random() * width - width / 2;
                float y = (float) Math.random() * height - height / 2;
                float nebulaRadius = (400f + (float) Math.random() * 1600f) * nebulaSize;
                api.addNebula(x, y, nebulaRadius);
            }
            api.addBriefingItem("Nebulosity:  " + (int) (((nebulaCount * nebulaSize) / 40f) * 100) + "%");
        } else {
            api.addBriefingItem("Nebulosity: N/A");
        }

        float asteroidChance = MathUtils.getRandomNumberInRange(0, 100);
        if (asteroidChance < 50) {
            int minAsteroidSpeed = MathUtils.getRandomNumberInRange(15, 50);
            int asteroidCount = 120 + (int) (100 * Math.pow(Math.random(), 2));

            api.addAsteroidField(
                    minX + width * 0.5f,
                    minY + height * 0.5f,
                    rand.nextInt(90) - 45 + (rand.nextInt() % 2) * 180,
                    100 + (int) (Math.random() * height / 2),
                    minAsteroidSpeed,
                    minAsteroidSpeed * 1.1f,
                    asteroidCount);

            api.addBriefingItem("Asteroid Density:  " + (int) ((asteroidCount / 1000f) * 100) + "%");
            api.addBriefingItem("Asteroid Speed:  " + minAsteroidSpeed);
        } else {
            api.addBriefingItem("Asteroid Density: N/A");
            api.addBriefingItem("Asteroid Speed: N/A");
        }
    }

    protected boolean chooseFactions(String playerFactionId, String enemyFactionId, boolean fullRandom, int i) {
        return chooseFactions(playerFactionId, enemyFactionId, fullRandom, i, i);
    }

    protected boolean chooseFactions(String playerFactionId, String enemyFactionId, boolean fullRandom, int i, int j) {
        this.fullRandom = fullRandom;
        boolean outOfBorder = false;
        List<FactionAPI> acceptableFactions = new ArrayList<>(40);

        if (playerFactionId != null && Global.getSector() != null) {
            player = Global.getSector().getFaction(playerFactionId);
        }
        if (enemyFactionId != null && Global.getSector() != null) {
            enemy = Global.getSector().getFaction(enemyFactionId);
        }

        if (!fullRandom) {
            if (Global.getSector() != null) {
                acceptableFactions.add(Global.getSector().getFaction(Factions.DIKTAT));
                acceptableFactions.add(Global.getSector().getFaction(Factions.HEGEMONY));
                acceptableFactions.add(Global.getSector().getFaction(Factions.INDEPENDENT));
                acceptableFactions.add(Global.getSector().getFaction(Factions.LUDDIC_CHURCH));
                acceptableFactions.add(Global.getSector().getFaction(Factions.LUDDIC_PATH));
                acceptableFactions.add(Global.getSector().getFaction(Factions.PIRATES));
                acceptableFactions.add(Global.getSector().getFaction(Factions.TRITACHYON));
                acceptableFactions.add(Global.getSector().getFaction(Factions.PERSEAN));
            }
        } else {
            if (Global.getSector() != null) {
                acceptableFactions.addAll(Global.getSector().getAllFactions());
            }
            List<FactionAPI> toRemove = new ArrayList<>();
            for (FactionAPI faction : acceptableFactions) {
                if (faction == null || !faction.isShowInIntelTab() || faction.isPlayerFaction()) {
                    toRemove.add(faction);
                }
            }
            acceptableFactions.removeAll(toRemove);
        }

        if (acceptableFactions.isEmpty()) {
            if (player == null) player = Global.getSector().getFaction("neutrinocorp");
            if (enemy == null) enemy = Global.getSector().getFaction(Factions.HEGEMONY);
            return true;
        }

        if (i >= acceptableFactions.size()) {
            i = i % acceptableFactions.size();
            outOfBorder = true;
        }
        if (j >= acceptableFactions.size()) {
            j = j % acceptableFactions.size();
            outOfBorder = true;
        }

        player = player != null ? player : acceptableFactions.get(Math.abs(i) % acceptableFactions.size());
        acceptableFactions.remove(player);

        if (!acceptableFactions.isEmpty()) {
            enemy = enemy != null ? enemy : acceptableFactions.get(Math.abs(j) % acceptableFactions.size());
        } else {
            enemy = Global.getSector().getFaction(Factions.HEGEMONY);
        }

        return outOfBorder;
    }

    private int generateFleetForSide(MissionDefinitionAPI api, FleetSide side, FactionAPI faction, int size) {
        if (faction != null && "neutrinocorp".equals(faction.getId())) {
            return generateNeutrinoFleetDynamic(api, side);
        } else {
            float qf = QUALITY_FACTORS.get(faction.getId()) == null ? rand.nextFloat() : QUALITY_FACTORS.get(faction.getId());
            return generateStandardFactionFleetV3(faction, api, side, qf);
        }
    }
    private int generateNeutrinoFleetDynamic(MissionDefinitionAPI api, FleetSide side) {
        float currentDP = 0f;
        boolean flagshipSet = false;

        Map<HullSize, List<String>> sizeMap = new HashMap<>();
        sizeMap.put(HullSize.CAPITAL_SHIP, new ArrayList<String>());
        sizeMap.put(HullSize.CRUISER, new ArrayList<String>());
        sizeMap.put(HullSize.DESTROYER, new ArrayList<String>());
        sizeMap.put(HullSize.FRIGATE, new ArrayList<String>());

        Collection<String> registeredVariants = Global.getSettings().getAllVariantIds();

        for (String vId : registeredVariants) {
            if (vId == null || !vId.toLowerCase().startsWith("neutrino_")) {
                continue;
            }

            ShipVariantAPI variant = Global.getSettings().getVariant(vId);
            if (!isMilitaryCombatShip(variant)) {
                continue;
            }

            HullSize size = variant.getHullSize();
            if (sizeMap.containsKey(size)) {
                sizeMap.get(size).add(vId);
            }
        }

        List<String> capitals = sizeMap.get(HullSize.CAPITAL_SHIP);
        if (capitals != null && !capitals.isEmpty()) {
            String capId = capitals.get(rand.nextInt(capitals.size()));
            ShipVariantAPI v = Global.getSettings().getVariant(capId);
            if (v != null) {
                api.addToFleet(side, capId, FleetMemberType.SHIP, true);
                flagshipSet = true;
                currentDP += getVariantDP(v);
            }
        }


        List<String> cruisers = sizeMap.get(HullSize.CRUISER);
        if (cruisers != null && !cruisers.isEmpty()) {
            int cruiserCount = 3 + rand.nextInt(2);
            for (int i = 0; i < cruiserCount; i++) {
                if (currentDP >= TARGET_DP) break;

                String cruiserId = cruisers.get(rand.nextInt(cruisers.size()));
                ShipVariantAPI v = Global.getSettings().getVariant(cruiserId);
                if (v != null) {
                    boolean isFlagship = !flagshipSet;
                    api.addToFleet(side, cruiserId, FleetMemberType.SHIP, isFlagship);
                    if (isFlagship) flagshipSet = true;
                    currentDP += getVariantDP(v);
                }
            }
        }


        List<String> escorts = new ArrayList<>();
        if (sizeMap.get(HullSize.DESTROYER) != null) escorts.addAll(sizeMap.get(HullSize.DESTROYER));
        if (sizeMap.get(HullSize.FRIGATE) != null) escorts.addAll(sizeMap.get(HullSize.FRIGATE));

        if (!escorts.isEmpty()) {
            int safetyLimit = 30;
            while (currentDP < TARGET_DP && safetyLimit > 0) {
                safetyLimit--;
                String escortId = escorts.get(rand.nextInt(escorts.size()));
                ShipVariantAPI v = Global.getSettings().getVariant(escortId);
                if (v == null) continue;

                float dp = getVariantDP(v);
                if (currentDP + (dp * 0.5f) > TARGET_DP && currentDP > 0) {
                    break;
                }

                boolean isFlagship = !flagshipSet;
                api.addToFleet(side, escortId, FleetMemberType.SHIP, isFlagship);
                if (isFlagship) flagshipSet = true;
                currentDP += dp;
            }
        }

        return Math.round(currentDP);
    }

    /**
     * Standard faction fleet composition rules:
     * Generates a large fleet pool via FleetFactoryV3, then picks:
     * - 1 Capital
     * - 3 to 4 Cruisers
     * - Rest Destroyers and Frigates up to 240 DP
     */
    private int generateStandardFactionFleetV3(FactionAPI faction, MissionDefinitionAPI api, FleetSide side, float qf) {
        FleetParamsV3 fleetParam = new FleetParamsV3(
                null,
                new Vector2f(),
                faction.getId(),
                0f,
                FleetTypes.PATROL_LARGE,
                350f,
                0f,
                0f,
                0f,
                0f,
                0f,
                qf
        );

        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(fleetParam);
        if (fleet == null || fleet.getMembersWithFightersCopy().isEmpty()) return 0;

        Map<HullSize, List<FleetMemberAPI>> sizeMap = new HashMap<>();
        sizeMap.put(HullSize.CAPITAL_SHIP, new ArrayList<FleetMemberAPI>());
        sizeMap.put(HullSize.CRUISER, new ArrayList<FleetMemberAPI>());
        sizeMap.put(HullSize.DESTROYER, new ArrayList<FleetMemberAPI>());
        sizeMap.put(HullSize.FRIGATE, new ArrayList<FleetMemberAPI>());

        for (FleetMemberAPI m : fleet.getMembersWithFightersCopy()) {
            if (m.isFighterWing() || m.isStation()) continue;

            ShipVariantAPI variant = m.getVariant();
            if (!isMilitaryCombatShip(variant)) continue;

            HullSize size = m.getHullSpec().getHullSize();
            if (sizeMap.containsKey(size)) {
                sizeMap.get(size).add(m);
            }
        }

        float currentDP = 0f;
        boolean flagshipSet = false;


        List<FleetMemberAPI> capitals = sizeMap.get(HullSize.CAPITAL_SHIP);
        if (capitals != null && !capitals.isEmpty()) {
            FleetMemberAPI cap = capitals.get(rand.nextInt(capitals.size()));
            float dp = getFleetMemberDP(cap);
            api.addToFleet(side, cap.getVariant().getHullVariantId(), FleetMemberType.SHIP, cap.getShipName(), true);
            flagshipSet = true;
            currentDP += dp;
        }


        List<FleetMemberAPI> cruisers = sizeMap.get(HullSize.CRUISER);
        if (cruisers != null && !cruisers.isEmpty()) {
            int cruiserCount = 3 + rand.nextInt(2);
            for (int i = 0; i < cruiserCount; i++) {
                if (currentDP >= TARGET_DP || cruisers.isEmpty()) break;

                FleetMemberAPI cruiser = cruisers.remove(rand.nextInt(cruisers.size()));
                float dp = getFleetMemberDP(cruiser);

                boolean isFlagship = !flagshipSet;
                api.addToFleet(side, cruiser.getVariant().getHullVariantId(), FleetMemberType.SHIP, cruiser.getShipName(), isFlagship);
                if (isFlagship) flagshipSet = true;
                currentDP += dp;
            }
        }


        List<FleetMemberAPI> escorts = new ArrayList<>();
        if (sizeMap.get(HullSize.DESTROYER) != null) escorts.addAll(sizeMap.get(HullSize.DESTROYER));
        if (sizeMap.get(HullSize.FRIGATE) != null) escorts.addAll(sizeMap.get(HullSize.FRIGATE));

        if (!escorts.isEmpty()) {
            int safetyLimit = 40;
            while (currentDP < TARGET_DP && safetyLimit > 0 && !escorts.isEmpty()) {
                safetyLimit--;
                FleetMemberAPI escort = escorts.remove(rand.nextInt(escorts.size()));
                float dp = getFleetMemberDP(escort);

                if (currentDP + (dp * 0.5f) > TARGET_DP && currentDP > 0) {
                    break;
                }

                boolean isFlagship = !flagshipSet;
                api.addToFleet(side, escort.getVariant().getHullVariantId(), FleetMemberType.SHIP, escort.getShipName(), isFlagship);
                if (isFlagship) flagshipSet = true;
                currentDP += dp;
            }
        }

        return Math.round(currentDP);
    }

    private float getVariantDP(ShipVariantAPI variant) {
        if (variant == null || variant.getHullSpec() == null) return 10f;
        float dp = variant.getHullSpec().getSuppliesToRecover();
        return dp > 0 ? dp : 10f;
    }

    private float getFleetMemberDP(FleetMemberAPI member) {
        if (member == null) return 10f;
        float dp = member.getUnmodifiedDeploymentPointsCost();
        if (dp <= 0) dp = member.getStats().getSuppliesToRecover().getModifiedValue();
        return dp > 0 ? dp : 10f;
    }
}