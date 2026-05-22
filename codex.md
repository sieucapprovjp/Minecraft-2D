# codex.md

## Project Goal
- A 2D Minecraft-style game built with Java + LibGDX.
- Vertical slice priority: `world + player + collision + 1 mob`.

## Stack And Structure
- Engine: LibGDX.
- Build: Gradle (`gradlew.bat`).
- Modules:
  - `core`: game logic, screens, world, entities, physics, navigation.
  - `lwjgl3`: desktop launcher.
  - `assets`: textures, sprites, fonts.

## Core File Map
- `core/src/main/java/com/main/game/MainGame.java`
  - Game entry point (`Game`).
  - Creates shared resources: `SpriteBatch`, `AssetManager`, `ScreenRouter`, `GameState`.
  - `render()` calls `screenRouter.flush()` before `super.render()`.
  - Responsible for disposing shared resources when the game exits.
- `core/src/main/java/com/main/game/GameState.java`
  - Stores global state used by multiple screens, for example `brightness`.

- `core/src/main/java/com/main/game/navigation/ScreenId.java`
  - Enum identifiers for screens (`LOADING`, `MENU`, `GAME`, `PAUSE`, `GAME_OVER`, ...).
- `core/src/main/java/com/main/game/navigation/ScreenRouter.java`
  - Safe screen transition mechanism through `request()` + `flush()`.
  - Enforces lifecycle order: `onExit -> dispose -> create new screen -> onEnter`.
  - Avoids switching to the same current screen.

- `core/src/main/java/com/main/game/screens/BaseScreen.java`
  - Base class for screens. Holds references to `MainGame`, `batch`, `camera`, `viewport`.
  - Defines `onEnter()` / `onExit()` hooks and `update()` / `draw()`.
- `core/src/main/java/com/main/game/screens/GameScreen.java`
  - Main gameplay integration point: `World`, `PhysicsEngine`, `Player`, `EntityManager`.
  - Handles test transition input (`ESC/P`, `M`, `K`, ...), camera follow, HUD, overlay.
  - Render order: world -> entities -> HUD -> overlay.
- `core/src/main/java/com/main/game/screens/StateScreen.java`
  - State screen (`PAUSE`, `GAME_OVER`) with navigation back to game/menu.
- `core/src/main/java/com/main/game/screens/MenuScreen.java`
  - Main menu.
- `core/src/main/java/com/main/game/screens/LoadingScreen.java`
  - Loading screen and transition.
- `core/src/main/java/com/main/game/screens/ModeSelectScreen.java`
  - Game mode selection.
- `core/src/main/java/com/main/game/screens/SettingsScreen.java`
  - Settings screen, affects `GameState`.

- `core/src/main/java/com/main/game/world/World.java`
  - Manages the world grid, terrain generation, spawn point, and camera-based rendering.
  - Priority optimization target when moving to chunk streaming.
- `core/src/main/java/com/main/game/world/BlockPalette.java`
  - Loads and stores shared block textures.
  - Has fallback textures and centralized disposal.
- `core/src/main/java/com/main/game/world/DemoBlockViewer.java`
  - Debug tool for spawning and quickly viewing blocks in the world.
- `core/src/main/java/com/main/game/worldgen/*.java`
  - New world generation module: biome, surface rules, decoration, house structure, spawn safety, biome-based mob spawning.

- `core/src/main/java/com/main/game/blocks/AbstractBlock.java`
  - Block contract with physical properties: `solid`, `breakable`, `hardness`, `bounds`.
- `core/src/main/java/com/main/game/blocks/SimpleBlock.java`
  - Basic implementation for blocks.
- `core/src/main/java/com/main/game/blocks/types/*.java`
  - Block groups by domain (`Nature`, `Ore`, `Stone`, `Wood`, `Utility`).

- `core/src/main/java/com/main/game/entities/Entity.java`
  - Base entity: position, velocity, bounds, alive/dead state.
- `core/src/main/java/com/main/game/entities/player/Player.java`
  - Player input + state machine + health/damage/respawn.
- `core/src/main/java/com/main/game/entities/player/PlayerRenderer.java`
  - Renders the player rig by body part, including arm swing while mining.
- `core/src/main/java/com/main/game/entities/mob/Mob.java`
  - Mob orchestration: state, health, physics, AI/render calls.
- `core/src/main/java/com/main/game/entities/mob/MobBrain.java`
  - Mob AI (`PATROL`, `CHASE`, `ATTACK`).
- `core/src/main/java/com/main/game/entities/mob/MobProfile.java`
  - Per-mob-type parameters: hostile/passive, aggro, speed, damage, HP.
- `core/src/main/java/com/main/game/entities/mob/MobRenderer.java`
  - Chooses animation frames and renders mobs.
- `core/src/main/java/com/main/game/entities/mob/MobAssetPack.java`
  - Loads assets by mob type, with fallback when frames are missing.
- `core/src/main/java/com/main/game/entities/mob/MobMovementHelper.java`
  - Helper for simple obstacle jumping.
- `core/src/main/java/com/main/game/entities/mob/MobSightHelper.java`
  - Line-of-sight checks to prevent attacks through blocks.
- `core/src/main/java/com/main/game/entities/EntityManager.java`
  - Centralized update/render/dispose for player + mob list.
- `core/src/main/java/com/main/game/entities/EntityState.java`
  - Entity state enum (`IDLE`, `RUN`, `JUMP`, `FALL`, ...).

- `core/src/main/java/com/main/game/interaction/BlockBreaker.java`
  - Handles block hover/breaking, covered-block checks, and the unbreakable block list.
- `core/src/main/java/com/main/game/interaction/BlockBreakOverlay.java`
  - Renders cursor and crack texture while breaking blocks.

- `core/src/main/java/com/main/game/items/DroppedItemManager.java`
  - Manages dropped item entities on the map.
- `core/src/main/java/com/main/game/items/DroppedItem.java`
  - Dropped item physics, pickup delay, suction toward player, and inventory insertion.
- `core/src/main/java/com/main/game/items/BlockDropFactory.java`
  - Creates drops from broken blocks.

- `core/src/main/java/com/main/game/inventory/Inventory.java`
  - Inventory model with 36 pickup slots: hotbar + main inventory.
- `core/src/main/java/com/main/game/inventory/InventoryController.java`
  - Inventory toggle and selected hotbar slot.
- `core/src/main/java/com/main/game/inventory/InventoryInteractionHandler.java`
  - Left/right click behavior for hold, place, swap, stack, and split stack.
- `core/src/main/java/com/main/game/inventory/InventoryRenderer.java`
  - Renders hotbar/inventory/held item and stack numbers.
- `core/src/main/java/com/main/game/inventory/InventoryLayout.java`
  - Shared slot coordinates for rendering and hit testing.
- `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - Item/block texture lookup and stack limits.

- `core/src/main/java/com/main/game/physics/PhysicsEngine.java`
  - Gravity + collision/ground detection.
  - Hotspot to improve: full four-sided AABB, resolved by X/Y axes.

- `core/src/main/java/com/main/game/utils/Constants.java`
  - Game constants: viewport, tile size, gravity, speed, etc.
- `core/src/main/java/com/main/game/utils/TextureManager.java`
  - Shared texture manager by key/cache.
- `core/src/main/java/com/main/game/ui/UISkin.java`
  - UI style definitions: font, colors, skin binding if Scene2D UI is used.

## Quick Run
- Run desktop game: `./gradlew.bat lwjgl3:run`
- Build all: `./gradlew.bat build`
- Run tests, if available: `./gradlew.bat test`

## Current Implemented Systems
- Screen lifecycle:
  - `SpriteBatch` and `AssetManager` are created/disposed only in `MainGame`.
  - Screens transition only through `ScreenRouter.request(ScreenId)`.
  - Screen switch order: `onExit() -> dispose() -> create new screen -> onEnter()`.
- World:
  - Random terrain, around 400x128, with multiple geological layers.
  - Includes culling to reduce block rendering outside the camera view.
- Player:
  - Basic input: move left/right, jump.
  - Basic states: `IDLE`, `RUN`, `JUMP`, `FALL`, `HURT`, `DEAD`.
  - Rendering has been split into `entities/player/PlayerRenderer.java`.
  - Has mining arm animation while breaking blocks.
- Physics:
  - Basic gravity + ground detection.
  - Collision is resolved by X/Y axes for entities and solid blocks.
- Blocks/Assets:
  - Has `BlockPalette`, `TextureManager`, and block type classes.
  - Asset paths were updated after removing `*_1.png` files.
  - HUD textures have fallback when individual frames are missing.
- Block breaking:
  - Has block hover cursor and crack animation from `assets/cursor`.
  - Only visible blocks can be broken; covered blocks are blocked.
  - Has an unbreakable block list, for example `bedrock`.
  - Broken blocks spawn dropped items.
- Dropped item:
  - Dropped items have basic physics: gravity, horizontal bounce, ground snap, friction.
  - Items are pulled toward the player after pickup delay and can enter inventory.
- Inventory/hotbar:
  - Hotbar renders items using textures.
  - Inventory opens with `E`.
  - Left/right click manages hold, place, swap, stack, and split stack.
  - Stack numbers use the font asset in `assets/fonts`.
- Mob:
  - Hostile mobs: `ZOMBIE`, `HUSK`, `SKELETON`.
  - Passive mobs: `COW`, `PIG`, `SHEEP`, `CHICKEN`.
  - Hostiles aggro the player within 8 blocks; passives do not attack.
  - Includes patrol/chase/attack, line-of-sight checks, and simple obstacle jumping.
  - Mobs were refactored into `Mob`, `MobBrain`, `MobProfile`, `MobRenderer`, `MobAssetPack`, movement/sight helpers.
- Worldgen MVP:
  - `World.generate(seed)` was split into `WorldGenerator.generate(world, seed)`.
  - Has 3 biomes: `FOREST`, `DESERT`, `SNOW`.
  - Has simple village/house structures on flat ground.
  - Has spawn safety helper and initial biome-based mob spawning.
  - Added biome blocks: `snow`, `ice`, `sandstone`, `cactus`, with generated texture fallback if assets are missing.
  - Added biome mob `STRAY` with its own profile and skeleton asset fallback.

## Missing Systems / Next Priorities
- Run `./gradlew.bat lwjgl3:run` after every asset change to catch runtime missing textures.
- Add real projectiles for `SKELETON` instead of direct damage.
- Add player attacks against mobs and mob item/loot drops.
- Upgrade mob spawning to a time/chunk-based system instead of initial spawn on game entry.
- Handle dropped item overflow when closing a full inventory.
- Standardize asset atlas and naming conventions to avoid crashes after renames/deletions.

## Worldgen MVP Progress 2026-05-18
- Split world generation logic from `World` into the `worldgen` package.
- Implemented biome noise for forest/desert/snow and stores biome by world column.
- Added basic decoration: forest/snow trees, desert cactus, snow ice patches.
- Added village/house structure placer with relatively flat-ground checks.
- Added `SpawnSafety` for entity/structure spawn validation.
- Replaced test mob spawning in `GameScreen` with `BiomeMobSpawner.spawnInitialMobs(...)`.
- Added new blocks `snow`, `ice`, `sandstone`, `cactus` to palette/registry.
- Verify: `./gradlew.bat classes` passes.

## End-Of-Day Progress 2026-05-18
- Menu/New Game:
  - New Game screen now uses a random background from `assets/stage`.
  - Settings/done buttons were realigned.
- Block breaking:
  - Has hover cursor, crack animation, and mining arm animation.
  - Checks visible blocks and blocks breaking covered blocks.
  - Blocks unbreakable blocks such as `bedrock`.
- Dropped item:
  - Has dropped item entities from broken blocks.
  - Has physics + pickup/suction into inventory.
- Inventory:
  - Has hotbar, inventory panel, stack numbers, left/right click item management.
  - Slot layout is aligned to `images/gui_invrow/inventory.png`.
- Player:
  - Split into the `entities/player` package.
  - Fixed asset paths after removing `*_1.png` files.
- Mob:
  - Added multiple passive/hostile mob types.
  - Hostiles aggro within 8 blocks; passives only patrol.
  - Split into `entities/mob` and separated logic/render/profile/assets.
- Asset/runtime:
  - Reviewed Java asset paths.
  - Added fallback for missing HUD texture frames.
- Verify:
  - `./gradlew.bat classes` passes after refactoring the `entities` package.
  - Need to run `./gradlew.bat lwjgl3:run` next to verify game runtime after asset changes.

## Codex Working Rules
- Do not make large architecture changes unless requested.
- When implementing a new large feature, automatically split it into new files/classes and a suitable new directory/package. Avoid stuffing large logic into existing integration files.
- Do not over-engineer anything. Only do exactly what was requested and what is necessary for the feature/fix to work correctly.
- Prioritize project performance. If code can be shorter, clearer, and still maintainable, write it shorter.
- When adding a new feature, prefer creating a dedicated class/file under the appropriate module. Integration files like `GameScreen` should only call short APIs (`update/render/dispose`) instead of owning all new logic.
- Every change must include:
  - Files changed.
  - Systems affected.
  - How to verify in game: input/scene and expected result.
- Prioritize gameplay blockers first: physics, input, state.

## Code Comment Rules
- Only comment `why` or trade-offs. Do not restate obvious code behavior.
- In game loop/physics logic, use short comments to record assumptions.
- TODO comments must include context:
  - Work scope.
  - Completion condition.
  - Owner, if available.
- Examples:
  - `// Why: resolve Y first to avoid corner sticking when falling onto a block`
  - `// TODO(lhung): add left/right AABB collision before merging physics`

## PR/Review Comment Rules
- Use severity levels: `blocker`, `major`, `minor`, `nit`.
- Each comment should include:
  - Observed issue.
  - Gameplay/bug risk.
  - Concrete fix suggestion.
- Prioritize review coverage for: crashes, incorrect collision, incorrect state, resource leaks, screen lifecycle violations.

## Mandatory Rules For Screens And Resources
- Do not call `setScreen()` directly in gameplay modules.
- Do not dispose `batch`/`assetManager` inside a `Screen`.
- If a screen transition is needed, call `game.getScreenRouter().request(...)`.

## Task Checklist
1. Restate the task + affected module (`world`, `player`, `physics`, `screen`, `assets`).
2. Read related files under `core/src/main/java/com/main/game/...`.
3. Make a small, scoped change.
4. Run `lwjgl3:run` or the relevant test.
5. Report results and manual in-game verification steps.

## Team Notes
- Merge into `main` through PR. Avoid direct merge without review.
- Prioritize blockers older than 24h, especially Physics because it blocks other branches.
