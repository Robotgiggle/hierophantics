# Hierophantics

## 1.2.1
- Added `/hierophantics addMind <player>` command to directly add an embedded mind to the target player
- Added `/hierophantics disable <player>` command to disable the target player's minds
- Added config option for the maximum number of embedded minds per player (default is 64)
- Dying will no longer disable your embedded minds if you don't have any minds
- 'When teleporting' trigger will no longer fire when entering or leaving dimensions with coordinate scaling
- 'When velocity exceeds X' trigger will no longer fire when teleporting or when respawning

## 1.2.0
- Villagers (except for nitwits) can now use the Imbuement Bed! Embedding a mind into a villager will:
  - Increase the target's level by one, without needing any trading
  - Transfer all of the available trades from the victim to the target
  - Change the target's profession to Quiltmind, if the two professions don't match
- Added the Edified Workstation, which acts as a job site for Quiltmind villagers
- Creating an embedded mind from a named villager will now use that name instead of generating a random one
- 'When damaged' and 'When damaged by' triggers can no longer detect generic_kill damage (ie /kill)
- 'When damaged' and 'When damaged by' triggers now fire after the damage is applied rather than before, though they're still able to fire as you die
- Imbuement Bed is no longer bouncy, and no longer reduces fall damage
- Hierophantics book entry now links to the Hierophantic Patterns and Hierophantic Triggers entries

## 1.1.0
- Some trigger types now start their cast with a relevant iota on the stack
- Added advancements for embedding a mind, wasting a mind, and having one of each trigger type
- Casting Trigger Reflection: Damage Type as a non-player now produces a 'When damaged' trigger rather than Null
- 'When health drops below X' trigger no longer tries to fire if the health decrease killed you
- Mishap text for trying to examine a disabled mind is now slightly different if the mind isn't yours
- Added zh_cn translations (thanks ChuijkYahus!)
- Assorted internal cleanup

## 1.0.2
- 'When teleporting' trigger no longer tries to fire when you respawn
- Fixed a couple typos in the guidebook

## 1.0.1
- Embedded mind casts now play the mishap sound if the spell mishaps
- Payload Purification and Trigger Purification now work properly on other players' minds
- Embedded minds will now throw a mishap if they get triggered while disabled after dying
- Iota tooltips now use translations instead of being hardcoded

## 1.0.0
- Initial release