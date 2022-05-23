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
  # Chance (0-1) that a passive mob spawn will trigger a hostile mobs spawn
  CorruptionChance: .1
  # Weights will be totalled and a random number from 0 to that total will decide the mob type.
  # Note that currently ZombieApocalypse overrides this mob type during an apocalypse.
  MobWeights:
    ZOMBIE: 0.2
    SKELETON: 0.18
  # To reproduce the feeling of vanilla spawns, the hostile mobs that spawn from passive mob spawns
  # are offset by a random amount. These values change the min/max offset possible.
  RangeOffset:
    Min: -10
    Max: 10
  # To reproduce the feeling of vanilla spawns, hostile mobs may be spawned in batches or groups.
  # Each group is relative to the original RangeOffset and is nearby to the origin.
  MobAmount:
    1: 0.70
    2: 0.25
    3: 0.03
    4: 0.015
    5: 0.005
```

### Nerfs Fishing

ZC slows fishing by cancelling / restricting how fast players can fish.

```yml
Fish:
  # If a player was going catch before these milliseconds (1000 ms = 1 second), always cancel it (-1 to disable)
  MinTime: 20000
  # If a player was going catch after these milliseconds (1000 ms = 1 second), NEVER cancel it (-1 to disable)
  MaxTime: -1
  Restrict: # Restrict which items can be fished
    - ENCHANTED_BOOK
    - NAME_TAG
    - SADDLE
    - NAUTILUS_SHELL
    - BOW
    # Function to represent exponential decay chance of cancelling event. 1 = 100% cancel, 0 = 0% cancel
  CancelChance: #  a(x+b)^c
    Constant: 80  # a
    Offset: 80   # b
    Exponent: -1 # c
```

### Disables Naturally Spawned Enchanted Items

ZC disables mobs naturally spawning with enchanted items.

```yml
NoEnchants: # Restricts what items mobs can spawn with
  # All entities that you want to restrict breedability should be listed here.
  # Note that if ALL is present in this list all possible values are automatically filled in.
  Entities:
    - 'ALL'
  # Restricts which slots enchantments cannot spawn in.
  Slots:
    - 'ALL'
    - 'HEAD'
  # Restrict which items enchantments cannot spawn with. 
  Items:
    - 'ALL'
    - 'DIAMOND_SWORD'
  # Restrict which specific enchantments cannot be spawned.
  Enchants:
    - 'ALL'
```

### Disables spawning of specific entities

```yml
# Valid methods:
# REMOVE - Removes the entity
# CANCEL - Prevents the entity from spawning
# TP - Teleports the entity under the world
# HP - Sets the entity's HP to 0
BlockSpawns:
  Method: CANCEL # REMOVE, CANCEL, TP, HP
  Entities: [ BAT, BEE, CAT, CHICKEN, COW, DONKEY, FOX, HORSE, MULE, OCELOT, PARROT, PIG, PIGLIN, POLAR_BEAR, RABBIT, SHEEP, SNOWMAN, SQUID, STRIDER, TURTLE, VILLAGER, WOLF ]
  BlockReasons:
    - 'NATURAL'
```

### Disables crafting of specific items

```yml
Crafting: # Blocks specific item crafting
  Block: # Items to block
    - 'ENCHANTING_TABLE'
  # Item to replace with result slot
  RestrictItem: 'graystainedglasspane name:&c&lDisabled! lore:&7This item is disabled!'
```