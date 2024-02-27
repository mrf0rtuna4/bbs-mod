package mchorse.bbs_mod;

import mchorse.bbs_mod.data.storage.DataStorage;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.entity.ActorEntity;
import mchorse.bbs_mod.film.replays.Replay;
import mchorse.bbs_mod.l10n.L10n;
import mchorse.bbs_mod.resources.AssetProvider;
import mchorse.bbs_mod.resources.packs.ExternalAssetsSourcePack;
import mchorse.bbs_mod.resources.packs.InternalAssetsSourcePack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BBSMod implements ModInitializer
{
    public static final String MOD_ID = "bbs";

    /* Important folders */
    private static File gameFolder;
    private static File assetsFolder;
    private static File settingsFolder;
    private static File dataFolder;

    /* Core services */
    private static AssetProvider provider;

    /* Foundation services */
    private static L10n l10n;

    private static List<Runnable> scheduledRunnables = new ArrayList<>();

    public static final Identifier PLAY_PACKET_ID = new Identifier(MOD_ID, "play");
    public static final Identifier RECORD_PACKET_ID = new Identifier(MOD_ID, "record");

    public static final EntityType<ActorEntity> ACTOR_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MOD_ID, "actor"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ActorEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.8F)).build()
    );

    /**
     * Main folder, where all the other folders are located.
     */
    public static File getGameFolder()
    {
        return gameFolder;
    }

    public static File getGamePath(String path)
    {
        return new File(gameFolder, path);
    }

    /**
     * Assets folder within game's folder. It's used to store any assets that can
     * be loaded by {@link #provider}.
     */
    public static File getAssetsFolder()
    {
        return assetsFolder;
    }

    public static File getAssetsPath(String path)
    {
        return new File(assetsFolder, path);
    }

    /**
     * Config folder within game's folder. It's used to store any configuration
     * files.
     */
    public static File getSettingsFolder()
    {
        return settingsFolder;
    }

    public static File getSettingsPath(String path)
    {
        return new File(settingsFolder, path);
    }

    /**
     * Data folder within game's folder. It's used to store game data like
     * quests, dialogues, states, player data, etc. anything related to game
     * basically.
     */
    public static File getDataFolder()
    {
        return dataFolder;
    }

    public static File getDataPath(String path)
    {
        return new File(dataFolder, path);
    }

    public static File getExportFolder()
    {
        return getGamePath("export");
    }

    public static AssetProvider getProvider()
    {
        return provider;
    }

    public static L10n getL10n()
    {
        return l10n;
    }

    public static void schedule(Runnable runnable)
    {
        scheduledRunnables.add(runnable);
    }

    @Override
    public void onInitialize()
    {
        /* Core */
        gameFolder = FabricLoader.getInstance().getGameDir().toFile();
        assetsFolder = new File(gameFolder, "config/bbs/assets");
        settingsFolder = new File(gameFolder, "config/bbs/settings");
        dataFolder = new File(gameFolder, "config/bbs/data");

        assetsFolder.mkdirs();

        provider = new AssetProvider();
        provider.register(new ExternalAssetsSourcePack("assets", assetsFolder).providesFiles());
        provider.register(new InternalAssetsSourcePack());

        l10n = new L10n();

        /* Entities */
        FabricDefaultAttributeRegistry.register(ACTOR_ENTITY, ActorEntity.createActorAttributes());
    }
}