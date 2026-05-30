# codex.md

## File Purpose
- Record a concise summary of completed project features.
- Provide a quick progress snapshot before starting new work.
- Canonical path for future sessions: `D:\Project OOP\doc\codex.md`.
- Project root for all paths below: `D:\Project OOP`.
- Future Codex sessions should read this file first to understand the current system state before planning or implementing new work.
- This file does not replace `README.md`, is not a detailed design document, and should not store unfinished plans.

## Completed Features

### Screens And Navigation
- Separate screens exist for loading, menu, mode select, game, pause, game over, and settings.
- `ScreenRouter` handles safe screen transitions through request/flush.
- `MainGame` owns shared resources such as `SpriteBatch` and `AssetManager`.
- Related files:
  - `core/src/main/java/com/main/game/MainGame.java`
  - `core/src/main/java/com/main/game/GameState.java`
  - `core/src/main/java/com/main/game/navigation/ScreenRouter.java`
  - `core/src/main/java/com/main/game/navigation/ScreenId.java`
  - `core/src/main/java/com/main/game/screens/BaseScreen.java`
  - `core/src/main/java/com/main/game/screens/LoadingScreen.java`
  - `core/src/main/java/com/main/game/screens/MenuScreen.java`
  - `core/src/main/java/com/main/game/screens/ModeSelectScreen.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`
  - `core/src/main/java/com/main/game/screens/StateScreen.java`
  - `core/src/main/java/com/main/game/screens/SettingsScreen.java`

### Finite World And Camera
- The world is finite with `WORLD_WIDTH = 400` and `WORLD_HEIGHT = 128`.
- Chunks are used as internal storage/rendering structures, without assuming an infinite world.
- The camera follows the player and is clamped inside world bounds.
- Camera zoom is closer to the player at `0.5f`.
- Related files:
  - `core/src/main/java/com/main/game/utils/Constants.java`
  - `core/src/main/java/com/main/game/world/World.java`
  - `core/src/main/java/com/main/game/world/Chunk.java`
  - `core/src/main/java/com/main/game/ui/GameCameraController.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`

### World Generation
- World generation logic has been moved into the `worldgen` package.
- Basic biomes are implemented: forest, desert, and snow.
- Biomes support surface, filler, and deep layers.
- Basic trees, cactus, ice/snow patches, and simple house/village structures are implemented.
- Spawn safety helpers exist for players and mobs.
- Related files:
  - `core/src/main/java/com/main/game/worldgen/WorldGenerator.java`
  - `core/src/main/java/com/main/game/worldgen/WorldNoise.java`
  - `core/src/main/java/com/main/game/worldgen/WorldBlockFactory.java`
  - `core/src/main/java/com/main/game/worldgen/BiomeType.java`
  - `core/src/main/java/com/main/game/worldgen/BiomeProfile.java`
  - `core/src/main/java/com/main/game/worldgen/StructurePlacer.java`
  - `core/src/main/java/com/main/game/worldgen/SpawnSafety.java`
  - `core/src/main/java/com/main/game/worldgen/BiomeMobSpawner.java`
  - `core/src/main/java/com/main/game/world/World.java`

### Cave System V1
- Cave V1 is implemented in the `worldgen/cave` package.
- The cave module includes `CaveGenerator`, `CaveCarver`, and `OreVeinPlacer`.
- Caves are generated only inside finite world bounds.
- Cave carving avoids the surface, spawn area, bedrock/deep boundary, and non-stone blocks.
- Ore veins are placed after carving and only replace remaining `stone` or `deepslate`.
- Generation remains deterministic from the world seed.
- Related files:
  - `core/src/main/java/com/main/game/worldgen/cave/CaveGenerator.java`
  - `core/src/main/java/com/main/game/worldgen/cave/CaveCarver.java`
  - `core/src/main/java/com/main/game/worldgen/cave/OreVeinPlacer.java`
  - `core/src/main/java/com/main/game/world/World.java`
  - `core/src/main/java/com/main/game/worldgen/WorldBlockFactory.java`
  - `doc/CAVE_IMPLEMENT_PLAN.md`

### Deepslate And Ores
- A `deepslate` layer is implemented for deeper underground areas.
- Bedrock remains at the bottom of the world instead of filling the whole deep area.
- Normal ores are implemented: coal, iron, gold, diamond, copper, lapis, redstone, and emerald.
- Deepslate ore variants are implemented for the cave/ore generator.
- Ore and deepslate texture lookup uses real assets when available and falls back safely when assets are missing.
- Related files:
  - `core/src/main/java/com/main/game/worldgen/WorldBlockFactory.java`
  - `core/src/main/java/com/main/game/worldgen/cave/OreVeinPlacer.java`
  - `core/src/main/java/com/main/game/world/BlockPalette.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/blocks/types/OreBlocks.java`
  - `core/src/main/java/com/main/game/blocks/types/StoneBlocks.java`
  - `assets/Ores/`

### Spawn Safety
- Fixed player spawning inside caves, dirt, stone, or bedrock.
- The first spawn uses a temporary initial spawn platform.
- The platform is removed afterward, and future respawns return to normal surface spawning.
- Player spawn space is cleared so the player does not get stuck in blocks after spawn/respawn.
- Related files:
  - `core/src/main/java/com/main/game/world/World.java`
  - `core/src/main/java/com/main/game/world/SpawnSafetyController.java`
  - `core/src/main/java/com/main/game/worldgen/SpawnSafety.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`
  - `core/src/main/java/com/main/game/entities/player/Player.java`

### Player And Physics
- Player movement supports left/right movement, jump, fall, hurt, death, and respawn.
- Physics supports gravity, ground detection, and X/Y axis collision resolution.
- Player rendering is separated into its own renderer and includes basic animation.
- Related files:
  - `core/src/main/java/com/main/game/entities/player/Player.java`
  - `core/src/main/java/com/main/game/entities/player/PlayerRenderer.java`
  - `core/src/main/java/com/main/game/entities/Entity.java`
  - `core/src/main/java/com/main/game/entities/EntityState.java`
  - `core/src/main/java/com/main/game/physics/PhysicsEngine.java`

### Mobs And Combat
- Mobs are grouped as tamed, passive, and hostile.
- Multiple passive and hostile mob types are implemented with their own size, health, speed, and damage profiles.
- Mobs support patrol, chase, attack, panic, line-of-sight checks, and knockback when hit.
- The player can perform left-click melee attacks.
- Combat includes cooldown, reach checks, falling critical hits, and tool-based damage.
- Related files:
  - `core/src/main/java/com/main/game/combat/PlayerAttackController.java`
  - `core/src/main/java/com/main/game/entities/EntityManager.java`
  - `core/src/main/java/com/main/game/entities/mob/Mob.java`
  - `core/src/main/java/com/main/game/entities/mob/MobAllegiance.java`
  - `core/src/main/java/com/main/game/entities/mob/MobBrain.java`
  - `core/src/main/java/com/main/game/entities/mob/MobProfile.java`
  - `core/src/main/java/com/main/game/entities/mob/MobRenderer.java`
  - `core/src/main/java/com/main/game/entities/mob/MobAssetPack.java`
  - `core/src/main/java/com/main/game/entities/mob/MobMovementHelper.java`
  - `core/src/main/java/com/main/game/entities/mob/MobSightHelper.java`
  - `core/src/main/java/com/main/game/worldgen/BiomeMobSpawner.java`

### Block Breaking And Dropped Items
- The player can mine blocks with left-click.
- Block hover cursor and crack overlay are implemented while mining.
- Fully covered blocks cannot be mined directly.
- Bedrock is unbreakable.
- Broken blocks can spawn dropped items.
- Dropped items have physics, pickup delay, player suction, and inventory pickup.
- Related files:
  - `core/src/main/java/com/main/game/interaction/BlockBreaker.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreakOverlay.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreakRules.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreakListener.java`
  - `core/src/main/java/com/main/game/items/BlockDropFactory.java`
  - `core/src/main/java/com/main/game/items/DroppedItem.java`
  - `core/src/main/java/com/main/game/items/DroppedItemManager.java`
  - `core/src/main/java/com/main/game/items/HarvestEntry.java`

### Inventory And Hotbar
- A 9-slot hotbar and main inventory are implemented.
- Inventory opens with `E`.
- Left/right click supports pick, place, swap, stack, and split-stack behavior.
- Item textures and stack counts render in the hotbar/inventory.
- Tool stack size is `1`; block stack size defaults to `64`.
- Related files:
  - `core/src/main/java/com/main/game/inventory/Inventory.java`
  - `core/src/main/java/com/main/game/inventory/InventoryController.java`
  - `core/src/main/java/com/main/game/inventory/InventoryInteractionHandler.java`
  - `core/src/main/java/com/main/game/inventory/InventoryLayout.java`
  - `core/src/main/java/com/main/game/inventory/InventoryRenderer.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/inventory/ItemStack.java`
  - `assets/images/gui_invrow/inventory.png`

### Tool System V1
- `ToolRegistry` is implemented as the central tool metadata API.
- Tool types are registered: pickaxe, axe, shovel, sword, and hoe.
- Tool materials are registered: wood, stone, copper, iron, gold, diamond, and netherite.
- Starter inventory gives all tools for testing.
- Tool textures load through metadata before falling back to block textures.
- Axes support two texture variants:
  - `*_axe_v1` for hotbar/inventory rendering.
  - `*_axe_v2` for held-in-hand rendering.
- Related files:
  - `core/src/main/java/com/main/game/inventory/ToolRegistry.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/inventory/StarterInventoryFactory.java`
  - `core/src/main/java/com/main/game/inventory/ItemStack.java`
  - `core/src/main/java/com/main/game/entities/player/PlayerRenderer.java`
  - `assets/tools/wood/`
  - `assets/tools/stone/`
  - `assets/tools/copper/`
  - `assets/tools/iron/`
  - `assets/tools/gold/`
  - `assets/tools/diamond/`
  - `assets/tools/netherite/`

### Tool Usage And Durability
- Pickaxes mine stone, deepslate, sandstone, ores, and deepslate ores faster.
- Axes chop wood, planks, and leaves faster.
- Shovels dig dirt, grass, sand, and snow faster.
- Swords are for combat and do not speed up mining.
- Hoes are registered but do not have farming behavior yet.
- Tools have durability and show a durability bar under their icon.
- Tools lose durability when used and disappear when durability reaches zero.
- Related files:
  - `core/src/main/java/com/main/game/inventory/ToolRegistry.java`
  - `core/src/main/java/com/main/game/inventory/ItemStack.java`
  - `core/src/main/java/com/main/game/inventory/InventoryRenderer.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreaker.java`
  - `core/src/main/java/com/main/game/combat/PlayerAttackController.java`

### Held Item Rendering
- The player can hold tools and blocks in hand.
- Held items follow the player's facing direction.
- Held items are aligned so tools are gripped by the handle instead of the sprite center.
- Held items work correctly with jump/fall animation.
- Mining/placement swing is reused to make item usage visible.
- Related files:
  - `core/src/main/java/com/main/game/entities/player/Player.java`
  - `core/src/main/java/com/main/game/entities/player/PlayerRenderer.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/inventory/ToolRegistry.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`

### Block Placement V1
- `BlockPlacementController` is implemented.
- Placement uses the cursor tile as the source of truth.
- Placement overlay appears only when the cursor is over a valid empty tile.
- Right-click places the selected block at the overlay tile.
- Only normal 1x1 placeable blocks are supported.
- Tools, bedrock, out-of-reach blocks, player-overlapping blocks, occupied tiles, and floating unsupported blocks cannot be placed.
- Successful placement creates the block through `WorldBlockFactory`, writes it into the world, decrements the stack, and clears the slot when the stack reaches zero.
- Related files:
  - `core/src/main/java/com/main/game/interaction/BlockPlacementController.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreakOverlay.java`
  - `core/src/main/java/com/main/game/worldgen/WorldBlockFactory.java`
  - `core/src/main/java/com/main/game/world/World.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/inventory/Inventory.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`

### GameScreen Refactor
- Some responsibilities have been moved out of `GameScreen`.
- `GameHudRenderer` handles HUD rendering.
- `GameCameraController` handles camera follow/clamping.
- `SpawnSafetyController` handles initial spawn and respawn safety.
- Related files:
  - `core/src/main/java/com/main/game/screens/GameScreen.java`
  - `core/src/main/java/com/main/game/ui/GameHudRenderer.java`
  - `core/src/main/java/com/main/game/ui/GameCameraController.java`
  - `core/src/main/java/com/main/game/ui/GameOverlayRenderer.java`
  - `core/src/main/java/com/main/game/world/SpawnSafetyController.java`

### Git And Docs Cleanup
- A temporary test branch was created to validate the merge before updating `main`.
- Validated work was merged into `main`.
- `main` pull/conflict issues were resolved as requested.
- Unneeded local-only commits were removed as requested.
- A `doc/` folder was created and documentation/spec files were moved there, except `README.md`.
- Related files:
  - `README.md`
  - `doc/codex.md`
  - `doc/CAVE_IMPLEMENT_PLAN.md`
  - `doc/World_Generation_&_Cave_System.md`
  - `doc/TODO_TEAM.md`
  - `doc/PLAN_THIS_WEEK.md`
  - `doc/OUTLINE_CHUC_NANG.md`
  - `doc/newgame_logic.md`

## Latest Verification
- `.\gradlew.bat classes` passed.
- `.\gradlew.bat test` passed.
