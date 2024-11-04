# Minecraft code breaking changes
## 1.20.1 (backport)
### No Workaround:
- `tryDrainFluid` and `tryFillWithFluid` did not take a player as parameters. (Barrier blocks could not be waterlogged.)

## 1.21.1
Initial Release

## 1.21.2
### No Workaround:
- `FlowableFluid.receivesFlow` has become static.
- `onScheduledTick` and `tryFlow` now take a `ServerWorld` and `BlockState` as parameters.
