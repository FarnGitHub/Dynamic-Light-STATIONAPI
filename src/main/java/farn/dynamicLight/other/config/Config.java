package farn.dynamicLight.other.config;

import farn.dynamicLight.DynamicLight;
import farn.dynamicLight.other.cache.ItemLightData;
import farn.dynamicLight.other.world.Dispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.io.*;

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
            writeDefaultSettingFile();
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

                Dispatcher.lightdataMap.put(id,new ItemLightData(brightness, range, deathAge, underwater));
                DynamicLight.LOGGER.info(id);
            }
            in.close();
            resetLight = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeDefaultSettingFile() {
        try {
            PrintWriter configWriter = new PrintWriter(new FileWriter(settingsFile));
            configWriter.println("#Format (note: ItemLightTimeLimit and WorksUnderwater are optional)");
            configWriter.println("#ItemID:MaximumBrightness:LightRange:ItemLightTimeLimit:WorksUnderwater(true:false)");
            configWriter.println(" ");
            configWriter.println(" ");
            configWriter.println("#Torch");
            configWriter.println(Block.TORCH.id + ":15:31:-1:false");
            configWriter.println("#Glowstone dust");
            configWriter.println(Item.GLOWSTONE_DUST.id + ":10:21");
            configWriter.println("#Glowstone");
            configWriter.println(Block.GLOWSTONE.id + ":12:25");
            configWriter.println("#Jack o Lantern");
            configWriter.println(Block.JACK_O_LANTERN.id + ":15:31");
            configWriter.println("#Bucket of Lava");
            configWriter.println(Item.LAVA_BUCKET.id + ":15:31");
            configWriter.println("#Redstone Torch");
            configWriter.println(Block.REDSTONE_TORCH.id + ":10:21");
            configWriter.println("#Redstone Ore (Stone)");
            configWriter.println(Block.REDSTONE_ORE.id + ":10:21");
            configWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
