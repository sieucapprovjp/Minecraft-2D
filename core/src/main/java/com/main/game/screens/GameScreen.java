package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.main.game.MainGame;
import com.main.game.audio.AudioId;
import com.main.game.audio.MobAmbientAudioController;
import com.main.game.blocks.AbstractBlock;
import com.main.game.combat.PlayerAttackController;
import com.main.game.crafting.CraftingController;
import com.main.game.entities.EntityManager;
import com.main.game.entities.player.Player;
import com.main.game.evoker.EvokerSpellManager;
import com.main.game.utilityblock.chest.ChestInteractionController;
import com.main.game.utilityblock.chest.ChestInteractionHandler;
import com.main.game.utilityblock.chest.ChestManager;
import com.main.game.utilityblock.chest.ChestRenderer;
import com.main.game.utilityblock.chest.ChestState;
import com.main.game.utilityblock.furnace.FurnaceInteractionController;
import com.main.game.utilityblock.furnace.FurnaceInteractionHandler;
import com.main.game.utilityblock.furnace.FurnaceManager;
import com.main.game.utilityblock.furnace.FurnaceRenderer;
import com.main.game.utilityblock.furnace.FurnaceState;
import com.main.game.interaction.BlockBreakOverlay;
import com.main.game.interaction.BlockBreaker;
import com.main.game.interaction.BlockPlacementController;
import com.main.game.utilityblock.craftingtable.CraftingTableInteractionController;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryController;
import com.main.game.inventory.InventoryInteractionHandler;
import com.main.game.inventory.InventoryRenderer;
import com.main.game.inventory.ItemStack;
import com.main.game.inventory.StarterInventoryKit;
import com.main.game.inventory.ToolRegistry;
import com.main.game.items.BlockDropFactory;
import com.main.game.items.DroppedItemManager;
import com.main.game.items.HarvestEntry;
import com.main.game.items.MobDropFactory;
import com.main.game.navigation.ScreenId;
import com.main.game.physics.PhysicsEngine;
import com.main.game.projectile.ProjectileManager;
import com.main.game.projectile.ProjectileType;
import com.main.game.raid.RaidController;
import com.main.game.raid.RaidState;
import com.main.game.time.DayNightCycle;
import com.main.game.trading.TradingController;
import com.main.game.trading.TradingInteractionHandler;
import com.main.game.trading.TradingRenderer;
import com.main.game.trading.VillagerInteractionController;
import com.main.game.ui.GameCameraController;
import com.main.game.ui.GameHudRenderer;
import com.main.game.ui.GameOverlayRenderer;
import com.main.game.utils.TextureManager;
import com.main.game.world.BlockPalette;
import com.main.game.world.DemoBlockViewer;
import com.main.game.world.SpawnSafetyController;
import com.main.game.world.World;
import com.main.game.entities.mob.Mob;
import com.main.game.worldgen.BiomeMobSpawner;
import com.main.game.worldgen.village.VillageVillagerSpawner;
import java.util.Locale;
import java.util.Random;

public class GameScreen extends BaseScreen {

    private static final String PERF_LOG_TAG = "GameScreenPerf";
    private static final float CAMERA_ZOOM = 0.5f;

    private World world;
    private PhysicsEngine physics;
    private Player player;
    private EntityManager entityManager;
    private BlockBreaker blockBreaker;
    private BlockPlacementController blockPlacementController;
    private CraftingTableInteractionController craftingTableInteractionController;
    private ChestInteractionController chestInteractionController;
    private FurnaceInteractionController furnaceInteractionController;
    private BlockBreakOverlay blockBreakOverlay;
    private PlayerAttackController playerAttackController;
    private DroppedItemManager droppedItemManager;
    private Inventory inventory;
    private InventoryController inventoryController;
    private InventoryRenderer inventoryRenderer;
    private InventoryInteractionHandler inventoryInteractionHandler;
    private ChestRenderer chestRenderer;
    private ChestInteractionHandler chestInteractionHandler;
    private ChestManager chestManager;
    private ChestState openChestState;
    private FurnaceRenderer furnaceRenderer;
    private FurnaceInteractionHandler furnaceInteractionHandler;
    private FurnaceManager furnaceManager;
    private FurnaceState openFurnaceState;
    private VillagerInteractionController villagerInteractionController;
    private TradingController tradingController;
    private TradingRenderer tradingRenderer;
    private TradingInteractionHandler tradingInteractionHandler;
    private CraftingController craftingController;
    private GameCameraController cameraController;
    private GameHudRenderer hudRenderer;
    private GameOverlayRenderer overlayRenderer;
    private SpawnSafetyController spawnSafetyController;
    private BiomeMobSpawner mobSpawner;
    private DayNightCycle dayNightCycle;
    private RaidController raidController;
    private ProjectileManager projectileManager;
    private EvokerSpellManager evokerSpellManager;
    private MobAmbientAudioController mobAmbientAudioController;
    private VillageVillagerSpawner villageVillagerSpawner;
    private Random mobDropRandom;
    private boolean paused;
    private boolean dead;
    private boolean debugMode;
    private int lastPlayerHealthForAudio;
    private RaidState lastRaidAudioState;

    private float deathBtnX, deathBtnY, deathBtnW, deathBtnH;

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        long setupStartNanos = System.nanoTime();
        game.getAudioManager().stopMusic();

        // Tích hợp Seed Random
        long currentSeed = System.currentTimeMillis();
        world = new World(currentSeed);
        physics = new PhysicsEngine();

        // Sinh toàn bộ finite world trước khi tìm spawn để cave/ore không bị lỗi seam.
        long worldGenerateStartNanos = System.nanoTime();
        world.generate();
        long worldGenerateNanos = System.nanoTime() - worldGenerateStartNanos;
        camera.position.set(world.width / 2f, world.height / 2f, 0f);
        camera.update();

        Vector2 spawn = world.getInitialSpawnPoint();
        float spawnX = spawn.x;
        float spawnY = spawn.y;

        player = new Player(spawnX, spawnY, physics, world);
        spawnSafetyController = new SpawnSafetyController();
        spawnSafetyController.beginInitialSpawn(world, player);

        camera.position.set(player.getX(), player.getY(), 0f);
        camera.update();

        // ── Khởi tạo EntityManager & Tools ─────────────
        entityManager = new EntityManager();
        entityManager.setPlayer(player);
        projectileManager = new ProjectileManager(new Random(currentSeed + 8819L));
        projectileManager.setHitListener(this::handleProjectileHitPlayer);
        evokerSpellManager = new EvokerSpellManager();
        evokerSpellManager.setFangHitListener(() -> game.getAudioManager().playMobAttack(Mob.MobType.EVOKER));
        entityManager.setMobRangedAttackListener(projectileManager::spawnFromMobAttack);
        entityManager.setMobCastSpellListener((mob, target, damage) -> {
            EvokerSpellManager.CastResult castResult = evokerSpellManager.cast(mob, target, damage, world, projectileManager);
            if (castResult != EvokerSpellManager.CastResult.NONE) {
                game.getAudioManager().play(AudioId.EVOKER_CAST);
            }
            if (castResult == EvokerSpellManager.CastResult.FANGS) {
                game.getAudioManager().play(AudioId.EVOKER_FANGS);
            }
        });
        entityManager.setMobMeleeAttackListener(this::handleMobDamagedPlayer);
        mobAmbientAudioController = new MobAmbientAudioController(new Random(currentSeed + 9929L));
        raidController = new RaidController();
        lastRaidAudioState = raidController.getState();
        villageVillagerSpawner = new VillageVillagerSpawner();
        blockBreaker = new BlockBreaker();
        blockPlacementController = new BlockPlacementController();
        blockPlacementController.setBlockPlacementListener(this::handleBlockPlaced);
        craftingTableInteractionController = new CraftingTableInteractionController();
        chestInteractionController = new ChestInteractionController();
        furnaceInteractionController = new FurnaceInteractionController();
        blockBreakOverlay = new BlockBreakOverlay();
        playerAttackController = new PlayerAttackController();
        playerAttackController.setMobDeathListener(this::handleMobKilled);
        playerAttackController.setMobHitListener(this::handleMobHit);
        droppedItemManager = new DroppedItemManager();
        droppedItemManager.setPickupListener(() -> game.getAudioManager().play(AudioId.ITEM_PICKUP));
        mobDropRandom = new Random(currentSeed + 7717L);
        inventory = new Inventory();
        StarterInventoryKit.grant(inventory);
        player.setArmorLoadout(inventory.getArmorLoadout());
        inventoryController = new InventoryController();
        inventoryRenderer = new InventoryRenderer();
        inventoryInteractionHandler = new InventoryInteractionHandler();
        chestInteractionHandler = new ChestInteractionHandler();
        chestManager = new ChestManager();
        furnaceInteractionHandler = new FurnaceInteractionHandler();
        furnaceManager = new FurnaceManager();
        villagerInteractionController = new VillagerInteractionController();
        tradingController = new TradingController();
        tradingInteractionHandler = new TradingInteractionHandler();
        craftingController = new CraftingController();
        cameraController = new GameCameraController();
        syncHeldItem();
        blockBreaker.setBlockBreakListener(this::handleBlockBroken);

        dayNightCycle = new DayNightCycle();
        mobSpawner = new BiomeMobSpawner(currentSeed);
        long mobSpawnStartNanos = System.nanoTime();
        mobSpawner.spawnInitial(world, player, physics, entityManager, dayNightCycle.isNight());
        long mobSpawnNanos = System.nanoTime() - mobSpawnStartNanos;

        paused = false;
        dead = false;
        camera.zoom = CAMERA_ZOOM;
        hudRenderer = new GameHudRenderer();
        overlayRenderer = new GameOverlayRenderer();
        lastPlayerHealthForAudio = player.getHealth();
        logPerformanceSnapshot(currentSeed, setupStartNanos, worldGenerateNanos, mobSpawnNanos);
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) paused = !paused;
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) game.getScreenRouter().request(ScreenId.MENU);
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) DemoBlockViewer.populateDemo(world, Math.max(2, (int) player.getX()), Math.max(2, (int) player.getY()));
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) player.kill();
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) player.ban();
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) debugMode = !debugMode;

        boolean inventoryKeyPressed = inventoryController.update();
        if (inventoryKeyPressed) handleInventoryKey();
        if (inventoryController.wasJustClosed()) handleInventoryClosed();
        syncHeldItem();

        if (paused) {
            player.setMining(false, player.getX() + player.getWidth() / 2f);
            return;
        }

        if (!dead) {
            if (dayNightCycle != null) {
                dayNightCycle.update(delta);
            }
            furnaceManager.update(delta);
            entityManager.update(delta);
            if (mobAmbientAudioController != null) {
                mobAmbientAudioController.update(delta, entityManager, player, game.getAudioManager());
            }
            if (mobSpawner != null) {
                mobSpawner.update(delta, world, player, physics, entityManager,
                    dayNightCycle == null || dayNightCycle.isNight());
            }
            if (villageVillagerSpawner != null) {
                int spawnedVillagers = villageVillagerSpawner.update(world, player, physics, entityManager);
                if (spawnedVillagers > 0) {
                    Gdx.app.log(PERF_LOG_TAG, "spawnedVillagers=" + spawnedVillagers);
                }
            }
            if (projectileManager != null) {
                projectileManager.update(delta, world, player);
            }
            if (evokerSpellManager != null) {
                evokerSpellManager.update(delta, world, player);
            }
            if (raidController != null) {
                int spawnedRaidMobs = raidController.update(delta, world, player, physics, entityManager);
                handleRaidAudio(spawnedRaidMobs);
                if (spawnedRaidMobs > 0) {
                    Gdx.app.log(PERF_LOG_TAG, "spawnedRaidMobs=" + spawnedRaidMobs
                        + ", raidWave=" + raidController.getCurrentWave()
                        + "/" + raidController.getMaxWaves());
                }
            }
            spawnSafetyController.update(delta, world, player);
            droppedItemManager.update(delta, world, player, inventory);
            if (inventoryController.isInventoryOpen()) {
                if (tradingController != null && tradingController.isOpen()) {
                    tradingInteractionHandler.update(inventory, tradingController, getTradingRenderer());
                } else if (openChestState != null) {
                    chestInteractionHandler.update(inventory, openChestState, getChestRenderer());
                } else if (openFurnaceState != null) {
                    furnaceInteractionHandler.update(inventory, openFurnaceState, getFurnaceRenderer());
                } else {
                    inventoryInteractionHandler.update(inventory, inventoryRenderer, craftingController);
                }
                syncHeldItem();
            }
        }

        if (player.getHealth() <= 0) dead = true;
        updatePlayerDamageAudio();

        if (paused || dead) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (paused && Gdx.input.justTouched()) handlePauseClick(mx, my);
            else if (dead) {
                player.setMining(false, player.getX() + player.getWidth() / 2f);
                updateDeathButtonLayout();
                if (Gdx.input.justTouched() && mx >= deathBtnX && mx <= deathBtnX + deathBtnW && my >= deathBtnY && my <= deathBtnY + deathBtnH) {
                    handleDeathClick();
                }
            }
            return;
        }

        // KIEN: Cập nhật chunk trong phạm vi map khi di chuyển
        world.update(camera);
        cameraController.update(camera, world, player, delta);

        String heldItemId = getHeldItemId();
        player.setHeldItemId(heldItemId);
        boolean consumedFood = tryConsumeHeldFood(heldItemId);
        boolean placedBlock = false;
        if (!consumedFood && blockPlacementController.update(player, world, camera, viewport, heldItemId,
            inventoryController.isInventoryOpen())) {
            player.playPlaceAnimation(blockPlacementController.getHoveredPlaceX() + 0.5f, heldItemId);
            reduceHeldStack();
            blockBreaker.cancel();
            placedBlock = true;
        }
        boolean attacked = playerAttackController.update(delta, player, entityManager,
            camera, viewport, inventoryController.isInventoryOpen(), heldItemId);
        if (shouldPlaySwordSlash(heldItemId)) {
            game.getAudioManager().play(AudioId.SWORD_SWING);
            player.playAttackAnimation(mouseWorldX(), heldItemId);
        }
        boolean brokeBlock = false;
        if (consumedFood || placedBlock || attacked || inventoryController.isInventoryOpen()) {
            blockBreaker.cancel();
        } else {
            brokeBlock = blockBreaker.update(delta, player, world, camera, viewport, heldItemId);
            if (blockBreaker.consumeDigSoundRequest()) {
                playHoveredBlockBreakSound();
            }
        }
        if (attacked || brokeBlock) {
            damageHeldTool();
        }
        float miningTargetX = blockBreaker.hasHoveredBlock()
            ? blockBreaker.getHoveredBlockX() + 0.5f
            : player.getX() + player.getWidth() / 2f;
        player.setMining(blockBreaker.isBreaking(), miningTargetX);
    }

    @Override
    public void draw() {
        // KIEN: Tối màu hang động
        float surfaceY = world.height / 2f;
        float deepCaveY = 20f;
        float lightRatio = Math.max(0f, Math.min(1f, (camera.position.y - deepCaveY) / (surfaceY - deepCaveY)));
        float r = (0.4f * lightRatio) + (0.02f * (1 - lightRatio));
        float g = (0.7f * lightRatio) + (0.02f * (1 - lightRatio));
        float b = (1.0f * lightRatio) + (0.05f * (1 - lightRatio));
        int globalLight = dayNightCycle == null ? 0 : dayNightCycle.getGlobalLight();
        float nightFactor = dayNightCycle == null ? 0f : dayNightCycle.getNightFactor();
        r = lerp(r, 0.015f, nightFactor * 0.9f);
        g = lerp(g, 0.025f, nightFactor * 0.9f);
        b = lerp(b, 0.08f, nightFactor * 0.9f);

        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);
        furnaceManager.render(batch, world, camera);
        droppedItemManager.render(batch);
        entityManager.render(batch);
        if (evokerSpellManager != null) evokerSpellManager.render(batch);
        if (projectileManager != null) projectileManager.render(batch);
        if (debugMode) entityManager.renderMobHitboxes(batch);
        blockBreakOverlay.render(batch, blockBreaker, blockPlacementController);
        batch.end();

        overlayRenderer.renderWorldDarkness(batch, globalLight);

        hudRenderer.render(batch, viewport, inventory, inventoryController, inventoryRenderer,
            inventoryInteractionHandler, craftingController,
            openFurnaceState == null ? null : getFurnaceRenderer(), furnaceInteractionHandler,
            openFurnaceState,
            openChestState == null ? null : getChestRenderer(), chestInteractionHandler, openChestState,
            tradingController != null && tradingController.isOpen() ? getTradingRenderer() : null,
            tradingInteractionHandler, tradingController, player);

        if (paused) overlayRenderer.renderPause(batch);
        else if (dead) overlayRenderer.renderDeath(batch);
        overlayRenderer.renderBrightness(batch, game.getGameState());
    }

    private void handlePauseClick(float mx, float my) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float bw = 300f * (sw / 640f);
        float bh = 50f * (sh / 360f);
        float bx = (sw - bw) / 2f;
        float by1 = sh * 0.45f;
        float by2 = sh * 0.30f;
        if (mx >= bx && mx <= bx + bw && my >= by1 && my <= by1 + bh) {
            game.getAudioManager().play(AudioId.UI_CLICK);
            paused = false;
        } else if (mx >= bx && mx <= bx + bw && my >= by2 && my <= by2 + bh) {
            game.getAudioManager().play(AudioId.UI_CLICK);
            game.getScreenRouter().request(ScreenId.MENU);
        }
    }

    private void handleDeathClick() {
        game.getAudioManager().play(AudioId.UI_CLICK);
        spawnSafetyController.respawn(world, player);
        dead = false;
        lastPlayerHealthForAudio = player.getHealth();
    }

    private void handleInventoryKey() {
        if (inventoryController.isInventoryOpen()) {
            inventoryController.close();
            return;
        }
        if (paused || dead) {
            return;
        }
        if (villagerInteractionController.canOpen(player, entityManager, camera, viewport)
            && tradingController.open(villagerInteractionController.getHoveredVillager())) {
            craftingController.closeCrafting(inventory);
            openChestState = null;
            openFurnaceState = null;
            getTradingRenderer();
            inventoryController.open();
            game.getAudioManager().play(AudioId.UI_CLICK);
            return;
        }
        tradingController.close();
        if (chestInteractionController.canOpen(player, world, camera, viewport)) {
            craftingController.closeCrafting(inventory);
            openFurnaceState = null;
            openChestState = chestManager.getOrCreate(world,
                chestInteractionController.getHoveredTileX(),
                chestInteractionController.getHoveredTileY());
            getChestRenderer();
            inventoryController.open();
            game.getAudioManager().play(AudioId.CHEST_OPEN);
            return;
        }
        openChestState = null;
        if (furnaceInteractionController.canOpen(player, world, camera, viewport)) {
            craftingController.closeCrafting(inventory);
            openChestState = null;
            openFurnaceState = furnaceManager.getOrCreate(world,
                furnaceInteractionController.getHoveredTileX(),
                furnaceInteractionController.getHoveredTileY());
            getFurnaceRenderer();
            inventoryController.open();
            game.getAudioManager().play(AudioId.UI_CLICK);
            return;
        }
        openFurnaceState = null;
        if (craftingTableInteractionController.canOpen(player, world, camera, viewport)) {
            craftingController.openTableCrafting(inventory);
        } else {
            craftingController.openPlayerCrafting(inventory);
        }
        inventoryController.open();
        game.getAudioManager().play(AudioId.UI_CLICK);
    }

    private void handleInventoryClosed() {
        if (tradingController != null && tradingController.isOpen()) {
            game.getAudioManager().play(AudioId.UI_CLICK);
            tradingInteractionHandler.onCloseInventory(inventory);
            tradingController.close();
            return;
        }
        if (openFurnaceState != null) {
            game.getAudioManager().play(AudioId.UI_CLICK);
            furnaceInteractionHandler.onCloseInventory(inventory);
            openFurnaceState = null;
            return;
        }
        if (openChestState != null) {
            game.getAudioManager().play(AudioId.CHEST_CLOSE);
            chestInteractionHandler.onCloseInventory(inventory);
            openChestState = null;
            return;
        }
        game.getAudioManager().play(AudioId.UI_CLICK);
        inventoryInteractionHandler.onCloseInventory(inventory, craftingController);
    }

    private void handleBlockBroken(AbstractBlock block, World worldRef) {
        playBlockBreakSound(block);
        if (block != null && "furnace".equals(block.getBlockId())) {
            furnaceManager.dropContents(block, worldRef, droppedItemManager);
        }
        if (block != null && "chest".equals(block.getBlockId())) {
            chestManager.dropContents(block, worldRef, droppedItemManager);
        }
        droppedItemManager.spawn(BlockDropFactory.createDrop(block, worldRef, getHeldItemId()), worldRef);
    }

    private void handleMobHit(Mob mob) {
        if (mob != null && mob.isAlive()) {
            game.getAudioManager().playMobHurt(mob.getType());
        }
    }

    private void handleMobDamagedPlayer(Mob mob) {
        if (mob != null) {
            game.getAudioManager().playMobAttack(mob.getType());
        }
    }

    private void handleProjectileHitPlayer(ProjectileType projectileType) {
        if (projectileType == ProjectileType.EVOKER_MAGIC) {
            game.getAudioManager().playMobAttack(Mob.MobType.EVOKER);
        } else if (projectileType == ProjectileType.PILLAGER_ARROW) {
            game.getAudioManager().playMobAttack(Mob.MobType.PILLAGER);
        }
    }

    private void handleMobKilled(Mob mob) {
        if (mob == null || world == null || droppedItemManager == null || mobDropRandom == null) {
            return;
        }
        game.getAudioManager().playMobDeath(mob.getType());
        for (HarvestEntry entry : MobDropFactory.createDrops(mob, world, mobDropRandom)) {
            droppedItemManager.spawn(entry, world);
        }
    }

    private void handleRaidAudio(int spawnedRaidMobs) {
        if (raidController == null) {
            return;
        }
        if (spawnedRaidMobs > 0) {
            game.getAudioManager().play(AudioId.RAID_WAVE_HORN);
        }
        RaidState state = raidController.getState();
        if (state != lastRaidAudioState && state == RaidState.FAILED) {
            game.getAudioManager().play(AudioId.RAID_CELEBRATE);
        }
        lastRaidAudioState = state;
    }

    private void handleBlockPlaced(String blockId, int tileX, int tileY) {
        if (raidController != null && raidController.tryStartFromBanner(world, blockId, tileX, tileY)) {
            game.getAudioManager().play(AudioId.UI_CLICK);
        }
    }

    private String getHeldItemId() {
        if (inventory == null || inventoryController == null) {
            return null;
        }
        ItemStack stack = inventory.getSlot(inventoryController.getSelectedHotbarSlot());
        return stack == null || stack.getCount() <= 0 ? null : stack.getItemId();
    }

    private void syncHeldItem() {
        if (player != null) {
            player.setHeldItemId(getHeldItemId());
        }
    }

    private void damageHeldTool() {
        if (inventory == null || inventoryController == null) {
            return;
        }
        int slot = inventoryController.getSelectedHotbarSlot();
        ItemStack stack = inventory.getSlot(slot);
        if (stack == null || !stack.hasDurability()) {
            return;
        }
        if (stack.damage(1)) {
            inventory.setSlot(slot, null);
        }
        syncHeldItem();
    }

    private boolean shouldPlaySwordSlash(String heldItemId) {
        return inventoryController != null
            && !inventoryController.isInventoryOpen()
            && ToolRegistry.isSword(heldItemId)
            && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    private float mouseWorldX() {
        Vector2 mouseWorld = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorld);
        return mouseWorld.x;
    }

    private boolean tryConsumeHeldFood(String heldItemId) {
        if (player == null || inventory == null || inventoryController == null
            || inventoryController.isInventoryOpen()
            || !Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)
            || !player.eat(heldItemId)) {
            return false;
        }
        reduceHeldStack();
        game.getAudioManager().play(AudioId.PLAYER_EAT);
        return true;
    }

    private void updatePlayerDamageAudio() {
        if (player == null) {
            return;
        }
        int health = player.getHealth();
        if (health < lastPlayerHealthForAudio) {
            game.getAudioManager().play(health <= 0 ? AudioId.PLAYER_DEATH : AudioId.PLAYER_HURT);
        }
        lastPlayerHealthForAudio = health;
    }

    private void playHoveredBlockBreakSound() {
        if (world == null || blockBreaker == null || !blockBreaker.hasHoveredBlock()) {
            return;
        }
        AbstractBlock block = world.getBlock(blockBreaker.getHoveredBlockX(), blockBreaker.getHoveredBlockY());
        playBlockBreakSound(block);
    }

    private void playBlockBreakSound(AbstractBlock block) {
        if (block != null) {
            game.getAudioManager().playBlockBreak(block.getBlockId());
        }
    }

    private void reduceHeldStack() {
        if (inventory == null || inventoryController == null) {
            return;
        }
        int slot = inventoryController.getSelectedHotbarSlot();
        ItemStack stack = inventory.getSlot(slot);
        if (stack == null || stack.getCount() <= 0) {
            return;
        }
        stack.subtract(1);
        if (stack.getCount() <= 0) {
            inventory.setSlot(slot, null);
        }
        syncHeldItem();
    }

    private void updateDeathButtonLayout() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        deathBtnW = sw * 0.45f;
        deathBtnH = sh * 0.12f;
        deathBtnX = (sw - deathBtnW) / 2f;
        deathBtnY = sh * 0.38f;
    }

    private float lerp(float from, float to, float progress) {
        float t = Math.max(0f, Math.min(1f, progress));
        return from + (to - from) * t;
    }

    private void logPerformanceSnapshot(long seed, long setupStartNanos, long worldGenerateNanos, long mobSpawnNanos) {
        TextureManager textureManager = TextureManager.getInstance();
        Gdx.app.log(PERF_LOG_TAG,
            "seed=" + seed
                + ", setupMs=" + formatMillis(System.nanoTime() - setupStartNanos)
                + ", worldGenerateMs=" + formatMillis(worldGenerateNanos)
                + ", initialMobSpawnMs=" + formatMillis(mobSpawnNanos)
                + ", textureCache=" + textureManager.getCachedTextureCount()
                + ", ownedTextures=" + textureManager.getOwnedTextureCount()
                + ", generatedFallbacks=" + textureManager.getGeneratedFallbackCount()
                + ", mobAssetTypes=" + Mob.getCachedAssetTypeCount()
                + ", mobAssetTextures=" + Mob.getLoadedAssetTextureCount()
                + ", initialMobs=" + (entityManager == null ? 0 : entityManager.aliveMobCount()));
    }

    private static String formatMillis(long nanos) {
        return String.format(Locale.ROOT, "%.2f", nanos / 1_000_000f);
    }

    private ChestRenderer getChestRenderer() {
        if (chestRenderer == null) {
            chestRenderer = new ChestRenderer();
        }
        return chestRenderer;
    }

    private FurnaceRenderer getFurnaceRenderer() {
        if (furnaceRenderer == null) {
            furnaceRenderer = new FurnaceRenderer();
        }
        return furnaceRenderer;
    }

    private TradingRenderer getTradingRenderer() {
        if (tradingRenderer == null) {
            tradingRenderer = new TradingRenderer();
        }
        return tradingRenderer;
    }

    @Override
    public void dispose() {
        super.dispose();
        BlockPalette.dispose();
        if (hudRenderer != null) hudRenderer.dispose();
        if (overlayRenderer != null) overlayRenderer.dispose();
        if (blockBreakOverlay != null) blockBreakOverlay.dispose();
        if (droppedItemManager != null) droppedItemManager.clear();
        if (inventoryRenderer != null) inventoryRenderer.dispose();
        if (chestRenderer != null) chestRenderer.dispose();
        if (tradingRenderer != null) tradingRenderer.dispose();
        if (chestManager != null) chestManager.clear();
        if (furnaceRenderer != null) furnaceRenderer.dispose();
        if (furnaceManager != null) furnaceManager.clear();
        if (evokerSpellManager != null) evokerSpellManager.dispose();
        if (projectileManager != null) projectileManager.dispose();
        entityManager.dispose();
        Mob.disposeSharedAssets();
    }

    @Override
    public ScreenId getScreenId() { return ScreenId.GAME; }
}
