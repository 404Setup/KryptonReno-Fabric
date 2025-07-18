# FNP Patcher

This is an extension mod for Krypton Fabric, ported from KryptonFNP.

$\color{Red}\Huge{\textbf{You should not include }}$
$\color{Red}\Huge{\textbf{the }}$
$\color{Gold}\Huge{\textbf{modified }}$
$\color{Red}\Huge{\textbf{Krypton Fabric/PaperServer }}$
$\color{Red}\Huge{\textbf{in your modpack!}}$

$\color{Red}\Huge{\textbf{Instead install both }}$
$\color{Red}\Huge{\textbf{Krypton Fabric/PaperServer and KryptonFNP }}$
$\color{Red}\Huge{\textbf{Patcher and give instructions}}$
$\color{Red}\Huge{\textbf{ on how to use them.}}$

## Feature

- More basic optimizations
- Support RecastLib (Velocity Native rewritten in Rust, compatible with Windows x64/arm64)

## Warning
- When performing the patch operation, you need to close the running game and make sure that no other processes are occupying the Mods directory.
- For PluginServer Patch, FNP Patcher supports all Papers and Paper Forks.
- Distributing FNP Patcher by any means other than Modpack is prohibited. See LICENSE for details.
- It is forbidden to distribute the products of FNP Patcher in any way, such as Krypton Fabric Patched or PaperLike Patched
- Prohibition of monetization

## Install

### Install as a Mod

Just install it normally.

In addition, KryptonFNP Patcher also includes a repair patch for Krypton Fabric, which will be automatically applied
when Krypton Fabric is detected to be installed. (You can turn it off manually)

### Installed as a patch for Krypton Fabric

KryptonFNP PatcherIn order to implement Krypton Patch, the patch must be executed in the following way:

1. Install Krypton Fabric and KyrptonFNP Patcher as a Mod
2. Enter the Mod installation directory
3. Open Terminal
4. Use command: `java -jar kryptonfnp_patcher.jar` (Please use actual file names!)
5. Done

This will replace the Velocity Native included in Krypton Fabric with a native library that mixes RecastLib with
Velocity Native.

### Installed as a patch for PaperServer

1. Download the latest version of KryptonFNP Patcher from Modrinth or Curseforge and put it in your Paper server root directory (along with the .jar file that starts the server)
2. Open Terminal
3. Use command: `java -jar kryptonfnp_patcher.jar` (Please use actual file names!)
4. Done

## Config

Add the following parameters to the Java startup parameters to control the mixin enablement:

| Parameter                     | Description                                                           | Default value | Configuration in file form |
|-------------------------------|-----------------------------------------------------------------------|---------------|----------------------------|
| velocity.natives-disable      | Disable Native                                                        | false         | Not supported              |
| velocity.linux-recast-enabled | Enable RecastLib for Linux                                            | false         | Not supported              |

example:

```shell
java -Dvelocity.linux-recast-enabled=true -jar neoforge_launcher.jar
```

### Use env instead of jvm args

Some configuration items support using environment variables instead of jvm args.

| JVM ARGS                      | Environment Variable |
|-------------------------------|----------------------|
| velocity.linux-recast-enabled | ENABLE_LINUX_RECAST  |

## Dependencies
- [Krypton Fabric](https://modrinth.com/mod/krypton) - Optional dependency. I put some optimizations here.

## License
Partially used code from [PaperMC/Paperclip](https://github.com/PaperMC/Paperclip) to use it as PluginServer Patcher. 
Licensed under MIT License.

This work has a restrictive license in addition to the original license to prevent some unexpected behavior,
see [404Setup Works Redistribution License](https://github.com/404Setup/404Setup/blob/main/LICENSE.md)