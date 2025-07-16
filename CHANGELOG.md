# Hierophantics

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