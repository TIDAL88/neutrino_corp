package data.missions.neutrino_randomsim;

import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import data.missions.BaseRandomNeutrinoMissionDefinition;
import org.lwjgl.input.Keyboard;

public class MissionDefinition extends BaseRandomNeutrinoMissionDefinition {

    private static int stat = 1;
    private static int iterator = 0;
    private static int iterator2 = 0;

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        if (Keyboard.isCreated()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
                if (stat == 1) iterator++; else iterator = 0;
                stat = 1;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
                if (stat == 2) iterator++; else iterator = 0;
                stat = 2;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
                if (stat == 3) iterator++; else iterator = 0;
                stat = 3;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
                if (stat == 4) iterator++; else iterator = 0;
                stat = 4;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_5) || Keyboard.isKeyDown(Keyboard.KEY_6)) {
                if (stat == 5) iterator2++; else iterator2 = 0;
                stat = 5;
            }
        }

        switch (stat) {
            case 1:
                if (chooseFactions("neutrinocorp", null, false, iterator)) {
                    iterator = 0;
                }
                break;
            case 2:
                if (chooseFactions(null, "neutrinocorp", false, iterator)) {
                    iterator = 0;
                }
                break;
            case 3:
                if (chooseFactions("neutrinocorp", null, true, iterator)) {
                    iterator = 0;
                }
                break;
            case 4:
                if (chooseFactions(null, "neutrinocorp", true, iterator)) {
                    iterator = 0;
                }
                break;
            case 5:
            default:
                if (chooseFactions("neutrinocorp", null, true, iterator)) iterator = 0;
                if (chooseFactions(null, "neutrinocorp", true, iterator2)) iterator2 = 0;
                break;
        }

        super.defineMission(api);
    }
}