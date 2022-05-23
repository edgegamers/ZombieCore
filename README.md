# ZombieCore

ZombieCore (or ZC) modifies multiple core mechanics of Mineraft to encourage a more difficult and "apocalyptic" style of
gameplay. ZombieCore is intended to be used in parallel with ZombieApocalypse
(found [here](https://www.spigotmc.org/resources/zombieapocalypse-8-zombie-types.82106/)). _Note that version 1.3.2 of
this plugin overrides this plugin, if you are experiencing issues spawning custom mobs in with this plugin, make sure
you are using either an updated version or the custom jar provided_.

### Permissions

There are minimal permissions for ZC.

| Permission                 | Description                               |
|----------------------------|-------------------------------------------|
| **COMMANDS**               | Command related permissions               |
| zombiecore.command.spawn   | Grants access to spawn custom mobs        |
| zombiecore.command.delete  | Grants access to delete saved mobs        |
| zombiecore.command.reload  | Grants access to reload config/lang files |
| zombiecore.command.reset   | Grants access to reset config/lang files  |
| zombiecore.command.config  | Grants access to set config values        |
| **BYPASSES**               | Feature/Bypass related permissions        |
| zombiecore.bypass.breeding | Grants access to bypass breeding limits   |
| zombiecore.bypass.fishing  | Grants access to bypass fishing limits    |

## Features

### Custom Boss Spawning

Players with the permission `zombiecore.command.spawn` are able to spawn and customize mobs.

1. Type `/zc spawn [Entity]`
2. Type `/zc [Attribute] [Value]`
    1. **Examples**:
    2. `/zc health 5`
    3. `/zc speed 0.5`
    4. `/zc name &6Golem &d&lDAD`
    5. `/zc head diamondhelmet name:Dad Bod protection:5 unbreakable:true`
    6. _(Use tab-complete for assistance)_
3. Type `/zc spawn` to spawn the entity.
4. (Optional) Type `/zc save [Name]` to save the entity.
5. Type `/zc new` to clear out the current entity.

### Disabled Animal Breeding

ZC by default disables animal breeding. This reduces the amount of food possible to players. <br>

```yml
Breeding:
  # All entities that you want to restrict breedability should be listed here.
  # Note that if ALL is present in this list (and all future lists), all possible values are automatically filled in.
  Entities:
    - 'ALL'
    - 'COW'
  # BlockClicks will prevent players from even interacting with breedable animals.
  # This means they will not lose the food, and the animals will not go into "love mode".
  BlockClicks: false
  # BlockBreeding in theory should never be needed assuming BlockClicks and BLockLove are set to true.
  # In any case, BlockBreeding will cancel any attempt for animals to breed, regardless of the source.
  BlockBreeding: true
  # BlockLove will prevent animals from entering love mode. For example if you want to allow players to feed sheep (ie lose wheat)
  # but prevent the sheep from attempting to mate, setting this to true would achieve this.
  BlockLove: true
  # ResetBreeding should be set to true if you want animals to stop trying to breed once prevented
  ResetBreeding: true
  EggThreshold: 64 # (-1 to disable)
```

### Allows Daylight Monster Spawning

ZC allows Zombies (and other mobs) to spawn during the Daytime. Note that this doesn't modify the mobs aside from
spawns (ie Zombies/Skeletons will burn in daylight)

```yml
DaySpawns:
  CorruptionChances: # 10% Chance that a monster will spawn when a regular mob spawns
     0: 0.50 # Start of day, 6 AM
     1000: 0.30
     [...]
  LightLevels:
     SkyMin: 0
     SkyMax: 15
     BlockMin: 0
     BlockMax: 7
  RangeOffset: # The mobs can spawn up to 10 blocks away from the original mob spawn
     Min: -4
     Max: 8
  MinimumPlayerRangeSquared: 1024 # Mobs won't spawn within sqrt(1024) = 32 blocks of the player
  MobAmount: # These are WEIGHTs, currently configured to also be percentages
     1: 0.70    # 70% chance for 1 mob to spawn
     2: 0.25    # 25% for 2, etc.
     3: 0.03
     4: 0.015
     5: 0.005
     #6: 0.0001  # (Examples)
     #10: 0.00001
  MaxChunkMobs:
     # Day Cycle Image: https://static.wikia.nocookie.net/minecraft_gamepedia/images/b/bc/Day_Night_Clock_24h.png/revision/latest?cb=20130103232456
     # 1 -> 8AM-4PM (8 hours / 6.67 minutes)
     # 2 -> 4PM-10PM (6 hours / 5 minutes)
     # 3 -> 10PM-2AM (4 hours / 3.33 minutes)
     # 2 -> 2AM-8AM (6 hours / 5 minutes)
     0: 1
     2000: 1 # 8AM
     10000: 2 # 4PM
     16000: 3 # 10PM
     20000: 2 # 2AM
```

### Nerfs Fishing

ZC slows fishing by cancelling / restricting how fast players can fish.

```yml
Fish:
  MinTime: 20000 # All hook events will be cancelled if they occur before this many milliseconds (-1 to disable)
  MaxTime: -1 # No hook events will be cancelled if they occur beyond this many milliseconds (-1 to disable)
  CancelChance: #  a(x+b)^c
     Constant: 80 # a
     Offset: 80 # b
     Exponent: -1 # c
  BlockEnchants: true
  AllowItems: # Material
     - COD
    [...]
```

### Disables Naturally Spawned Enchanted Items

ZC disables mobs naturally spawning with enchanted items.

```yml
NoEnchants: # Restricts what items mobs can spawn with
  BlockEntities: # Entities
     - 'ALL'
  BlockSlots: # EquipmentSlot
     - 'ALL'
  BlockItems: # Material
     - 'ALL'
  BlockEnchants: # Enchantments
     - 'ALL'
```

### Disables spawning of specific entities

```yml
  BlockSpawns:
     Method: CANCEL # BlockMethods
     AllowNamed: true # If true, mobs that are name tagged will remain
     BlockEntities: # Entities
        - "SHEEP"
        - "COW"
        [...]
     BlockReasons: # SpawnReason
        - "NATURAL"
        - "EGG"
        - "DISPENSE_EGG"
```

### Disables crafting of specific items

```yml
Crafting: # Blocks specific item crafting
  Block: # Items to block
    - 'ENCHANTING_TABLE'
  # Item to replace with result slot
  RestrictItem: 'graystainedglasspane name:&c&lDisabled! lore:&7This item is disabled!'
```