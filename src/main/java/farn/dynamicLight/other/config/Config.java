package farn.dynamicLight.other.config;

import farn.dynamicLight.DynamicLight;
import farn.dynamicLight.other.cache.ItemLightData;
import farn.dynamicLight.other.mod.DynamicLightEntryPoint;
import farn.dynamicLight.other.world.Dispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.item.Item;

import java.io.*;
import java.util.Map;

public class Config {

    public static File settingsFile;
    public static boolean resetLight = false;

    private static boolean init = false;

    public static void initializeSettingsFile(boolean doResetLight)
    {
        if(!init) {
            settingsFile = new File(FabricLoader.getInstance().getConfigDir() + "/dynamicLight_item.setting");
            init = true;
        }

        if(!settingsFile.exists()) {
            putDefaultItemLightDataMap();
            writeDefaultSettingFile(true);
        }

        readSettingFile(doResetLight);
    }
    private static void readSettingFile(boolean doResetLight) {
        try {
            Dispatcher.lightdataMap.clear();
            if(doResetLight) {
                Dispatcher.clearCacheAndResetPool();
                Dispatcher.itemArray.clear();
                Dispatcher.entityArray.clear();
                resetLight = true;
            }

            BufferedReader in = new BufferedReader(new FileReader(settingsFile));
            String sCurrentLine;
            int[][] newLightData = new int[64][5];
            int i = 0;

            while ((sCurrentLine = in.readLine()) != null) {
                if (sCurrentLine.startsWith("#")) continue;

                if(sCurrentLine.startsWith("enabled")) {

                }

                String[] curLine = sCurrentLine.split(":");

                int id;
                int brightness;
                int range;
                int deathAge = -1;
                boolean underwater = true;

                if (curLine.length > 2) {
                    id = Integer.parseInt(curLine[0]); // Item ID
                    brightness = Integer.parseInt(curLine[1]); // Max Brightness
                    range = Integer.parseInt(curLine[2]); // Range
                } else {
                    continue;
                }

                if (curLine.length > 3)
                    deathAge = Integer.parseInt(curLine[3]); // Death Age

                if (curLine.length > 4)
                    underwater = curLine[4].equals("true"); // Work UnderWater

                Dispatcher.lightdataMap.put(id,new ItemLightData(id, brightness, range, deathAge, underwater));
                DynamicLight.LOGGER.info(id);
            }
            in.close();
            resetLight = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeDefaultSettingFile(boolean reset) {
        if(reset) putDefaultItemLightDataMap();
        try {
            PrintWriter configWriter = new PrintWriter(new FileWriter(settingsFile));
            configWriter.println("#Format (note: ItemLightTimeLimit and WorksUnderwater are optional)");
            configWriter.println("#ItemID:MaximumBrightness:LightRange:ItemLightTimeLimit:WorksUnderwater(true:false)");
            configWriter.println(" ");
            configWriter.println(" ");
            for(Map.Entry<Integer, ItemLightData> dataEntry: Dispatcher.lightdataMap.entrySet()) {
                configWriter.println("#" + Item.ITEMS[dataEntry.getKey()].getTranslatedName());
                ItemLightData data = dataEntry.getValue();
                configWriter.println(formatSetting(dataEntry.getKey(), data.brightness, data.range, data.deathAge, data.underwater));
            }
            configWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void putDefaultItemLightDataMap() {
        Dispatcher.lightdataMap.clear();
        for (EntrypointContainer<DynamicLightEntryPoint> entrypoint : FabricLoader.getInstance().getEntrypointContainers("dynamiclight_reset", DynamicLightEntryPoint.class)) {
            entrypoint.getEntrypoint().onDefaultConfig(Dispatcher.lightdataMap);
        }
    }

    public static String formatSetting(int id, int brightness, int range, int deathage, boolean underWater) {
        return String.format("%d:%d:%d:%d:%b", id, brightness, range, deathage, underWater);
    }

}
