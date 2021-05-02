package me.miquiis.playerwarps.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WarpData {

    private UUID warpUUID;

    private String warpName;

    private UUID worldUUID;
    private Vector warpPosition;
    private float[] warpDirection;

    private UUID ownerUUID;

    private Set<UUID> membersUUID;

    private Map<UUID, Instant> temporaryMembers;

    private boolean isLocked;

    public WarpData(UUID warpUUID, String warpName, Location location, UUID ownerUUID, Set<UUID> membersUUID, Map<UUID, Instant> temporaryMembers, boolean isLocked) {
        this.warpUUID = warpUUID;
        this.warpName = warpName;

        this.worldUUID = location.getWorld().getUID();
        this.warpPosition = location.toVector();
        this.warpDirection = new float[]{location.getPitch(), location.getYaw()};

        this.ownerUUID = ownerUUID;
        this.membersUUID = membersUUID;
        this.temporaryMembers = temporaryMembers;
        this.isLocked = isLocked;
    }

    public boolean toggleLock()
    {
        isLocked = !isLocked;
        return isLocked;
    }

    public void setOwner(UUID ownerUUID)
    {
        this.ownerUUID = ownerUUID;
    }

    public void setWarpName(String warpName)
    {
        this.warpName = warpName;
    }

    public boolean addMember(UUID memberUUID)
    {
        return this.membersUUID.add(memberUUID);
    }

    public void addTemporaryMember(UUID memberUUID, Instant time) { this.temporaryMembers.put(memberUUID, time); }

    public boolean removeMember(UUID memberUUID)
    {
        return this.membersUUID.remove(memberUUID) || this.temporaryMembers.remove(memberUUID) != null;
    }

    public void setWarp(Location location)
    {
        this.worldUUID = location.getWorld().getUID();
        this.warpPosition = location.toVector();
        this.warpDirection = new float[]{location.getPitch(), location.getYaw()};
    }

    public String getWarpName() {
        return warpName;
    }

    public UUID getWarpUUID() {
        return warpUUID;
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public float[] getWarpDirection() {
        return warpDirection;
    }

    public Vector getWarpPosition() {
        return warpPosition;
    }

    public Set<UUID> getMembersUUID() {
        return membersUUID;
    }

    public Map<UUID, Instant> getTemporaryMembers() {
        return temporaryMembers;
    }

    public void checkNulls()
    {
        if (temporaryMembers == null) temporaryMembers = new HashMap<>();
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public Location getWarpLocation()
    {
        return new Location(Bukkit.getWorld(getWorldUUID()), getWarpPosition().getX(), getWarpPosition().getY(), getWarpPosition().getZ(), getWarpDirection()[1], getWarpDirection()[0]);
    }

    public boolean canManage(Player player)
    {
        return ownerUUID.equals(player.getUniqueId()) || player.hasPermission("playerwarps.admin");
    }

    public boolean hasAccess(Player player)
    {
        return !isLocked || membersUUID.contains(player.getUniqueId()) || temporaryMembers.containsKey(player.getUniqueId()) ||  ownerUUID.equals(player.getUniqueId()) || player.hasPermission("playerwarps.admin");
    }

    public boolean isLocked() {
        return isLocked;
    }
}
