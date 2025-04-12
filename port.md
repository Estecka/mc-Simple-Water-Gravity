# Minecraft code breaking changes
## 1.21.1
Initial Release

## 1.21.2
### No Workaround:
- `FlowableFluid.receivesFlow` has become static.
- `onScheduledTick` and `tryFlow` now take a `ServerWorld` and `BlockState` as parameters.

## 1.21.5
### No Workaround:
- `FluidDrainable::tryDrainFluid` now takes a `linvingEntity` instead of a `PlayerEntity`. No code change required, but needs recompilation.
