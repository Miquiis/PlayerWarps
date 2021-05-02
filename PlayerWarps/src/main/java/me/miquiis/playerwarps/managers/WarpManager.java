package me.miquiis.playerwarps.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import me.miquiis.playerwarps.PlayerWarps;
import me.miquiis.playerwarps.data.WarpData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WarpManager {

    private final File warpFolder;
    private final Set<WarpData> warps;
    private final ConfigManager configManager;
    private final Gson gson;

    public WarpManager(PlayerWarps plugin)
    {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.warpFolder = new File(plugin.getDataFolder() + "/warps");
        this.configManager = plugin.getConfigManager();
        this.warps = new HashSet<>();

        loadWarps();
    }

    public Set<WarpData> getPlayerWarps(UUID ownerUUID)
    {
        return warps.stream().filter(w -> w.getOwnerUUID().equals(ownerUUID)).collect(Collectors.toSet());
    }

    public Set<WarpData> getHasAccessWarps(Player member)
    {
        return warps.stream().filter(w -> w.hasAccess(member)).collect(Collectors.toSet());
    }

    public Optional<WarpData> getWarp(String warpName)
    {
        return warps.stream().filter(w -> w.getWarpName().equals(warpName)).findFirst();
    }

    public boolean removePlayerWarp(String warpName)
    {
        Optional<WarpData> warpData = warps.stream().filter(w -> w.getWarpName().equals(warpName)).findFirst();
        if (!warpData.isPresent()) return false;

        warps.remove(warpData.get());
        deleteWarp(warpData.get());

        return true;
    }

    public boolean createPlayerWarp(String warpName, UUID ownerUUID, Location warpLocation)
    {
        Optional<WarpData> duplicateWarp = warps.stream().filter(w -> w.getWarpName().equals(warpName) && w.getOwnerUUID().equals(ownerUUID)).findFirst();
        if (duplicateWarp.isPresent()) return false;

        WarpData warpData = new WarpData(UUID.randomUUID(), warpName, warpLocation, ownerUUID, new HashSet<>(), new HashMap<>(), true);

        warps.add(warpData);
        saveWarp(warpData);

        return true;
    }

    public Set<WarpData> getWarps() {
        return warps;
    }

    public void close()
    {
        saveWarps();
    }

    private void saveWarp(WarpData warpData)
    {
        if (!warpFolder.exists()) {
            warpFolder.mkdir();
        }
        final String json = gson.toJson(warpData);

        File warpFile = new File(warpFolder + "/" + warpData.getWarpUUID().toString() + ".json");
        if (warpFile.exists()) warpFile.delete();

        try {
            Files.write(warpFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveWarps()
    {
        if (!warpFolder.exists()) {
            warpFolder.mkdir();
        }

        for (WarpData warpData : warps)
        {
            final String json = gson.toJson(warpData);

            File warpFile = new File(warpFolder + "/" + warpData.getWarpUUID().toString() + ".json");
            if (warpFile.exists()) warpFile.delete();

            try {
                Files.write(warpFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteWarp(WarpData warpData)
    {
        if (!warpFolder.exists()) {
            warpFolder.mkdir();
        }

        File warpFile = new File(warpFolder + "/" + warpData.getWarpUUID().toString() + ".json");
        if (warpFile.exists()) warpFile.delete();
    }

    private void loadWarps()
    {
        if (!warpFolder.exists()) {
            warpFolder.mkdir();
            return;
        }

        File[] warpsFiles = warpFolder.listFiles();

        for (File warpFile : warpsFiles)
        {
            if (!warpFile.exists()) continue;

            try {
                JsonReader jsonReader = new JsonReader(new FileReader(warpFile));
                WarpData warpData = gson.fromJson(jsonReader, WarpData.class);
                warpData.checkNulls();
                warps.add(warpData);
                jsonReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
