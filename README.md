# FNP Patcher

This is an extension mod for Krypton Fabric, ported from KryptonFNP.

## Feature

- More basic optimizations
- Support RecastLib (Velocity Native rewritten in Rust, compatible with Windows x64/arm64)

## Install as a Mod

Just install it normally.

In addition, KryptonFNP Patcher also includes a repair patch for Krypton Fabric, which will be automatically applied
when Krypton Fabric is detected to be installed. (You can turn it off manually)

## Installed as a patch for Krypton Fabric

KryptonFNP PatcherIn order to implement Krypton Patch, the patch must be executed in the following way:

1. Install Krypton Fabric and KyrptonFNP Patcher as a Mod
2. Enter the Mod installation directory
3. Open Terminal
4. Use command: `java -jar kryptonfnp_patcher.jar` (Please use actual file names!)
5. Done

This will replace the Velocity Native included in Krypton Fabric with a native library that mixes RecastLib with
Velocity Native.

## Warning

When performing the patch operation, you need to close the running game and make sure that no other processes are
occupying the Mods directory.

You should not include the modified Krypton Fabric in your modpack!

Instead install both Krypton Fabric and KryptonFNP Patcher and give instructions on how to use them.

## Config

Add the following parameters to the Java startup parameters to control the mixin enablement:

| Parameter                     | Description                                  | Default value |
|-------------------------------|----------------------------------------------|---------------|
| krypton.loginVT               | Enable Login VirtualThread optimization      | true          |
| krypton.textFilterVT          | Enable TextFilter VirtualThread optimization | true          |
| krypton.utilVT                | Enable Util VirtualThread optimization       | true          |
| krypton.bestVarLong           | Enable VarLong optimization                  | true          |
| velocity.natives-disable      | Disable Native                               | false         |
| velocity.linux-recast-enabled | Enable RecastLib for Linux                   | false         |

example:

```shell
java -Dkrypton.loginVT=false -jar neoforge_launcher.jar
```

### Use env instead of jvm args

Some configuration items support using environment variables instead of jvm args.

| JVM ARGS                      | Environment Variable |
|-------------------------------|----------------------|
| velocity.linux-recast-enabled | ENABLE_LINUX_RECAST  |

## License

This work has a restrictive license in addition to the original license to prevent some unexpected behavior,
see [404Setup Works Redistribution License](https://github.com/404Setup/404Setup/blob/main/LICENSE.md)