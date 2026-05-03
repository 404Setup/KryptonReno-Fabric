## KryptonReno's Fabric Patcher's JavaAgent

This is the JavaAgent version of `KryptonReno's Fabric Patcher`. Due to review policies,
this component cannot be published on Modrinth and CurseForge,
so you cannot include it in any Modpack.

----

$\color{Red}\Huge{\textbf{You should not include }}$
$\color{Red}\Huge{\textbf{the }}$
$\color{Gold}\Huge{\textbf{modified }}$
$\color{Red}\Huge{\textbf{Krypton Fabric/PaperServer }}$
$\color{Red}\Huge{\textbf{in your modpack!}}$

$\color{Red}\Huge{\textbf{Instead install both }}$
$\color{Red}\Huge{\textbf{Krypton Fabric/PaperServer and KryptonReno's }}$
$\color{Red}\Huge{\textbf{Fabric Patcher and give instructions}}$
$\color{Red}\Huge{\textbf{ on how to use them.}}$

## Warning

- It is forbidden to distribute KryptonReno's Fabric Patcher Agent in any way
- It is forbidden to distribute the products of KryptonReno's Fabric Patcher in any way, such as 
Krypton Fabric Patched or PaperLike Patched
- Prohibition of monetization

## Install

Native replacement is only compatible with Velocity version 3.2.0-SNAPSHOT and later.

### Javaagent

> **About Fabric Loader**:
>
> Compatible range: 0.17.2 - 0.19.2, other versions untested but theoretically compatible.
>
> Quilt compatibility is unknown. May conflict with some anti-cheat implementations, use with caution.
>
> **About PaperServer**
>
> Currently, it should be compatible with most Paper forks, and you can use it with confidence.
>
> JavaAgent does not have any built-in update checker and it is recommended to star this repository to receive updates.

Javaagent mode allows dynamic Native replacement without modifying Krypton Fabric.

**Installation**:

1. Download FNP Patcher JavaAgent
2. Place it in the game root directory (not the mods directory)
3. Modify the game launch configuration, add `-javaagent:kreno_fpatcher.jar` to the JVM startup arguments
4. Launch the game

### Patcher (Deprecated)

> Patcher mode will be deprecated soon, expected to fully transition to Javaagent mode starting from version 26.2
>
> When performing the patch operation, you need to close the running game and make sure that no other processes are
> occupying the Mods directory.

1. Install FNP Patcher JavaAgent and Paper Server/Krypton Fabric in the same directory
2. Open Terminal
3. Use command: `java -jar kryptonreno_patcher.jar` (Please use actual file names!)
4. Done

## Download

Github Release

## Credit

- [PaperMC/Paperclip](https://github.com/PaperMC/Paperclip) - MIT License
- [RecastSSL](https://github.com/404Setup/RecastSSL) - BSD-3-Clause License
- [RecastXZ](https://github.com/404Setup/RecastXZ) - Mozilla Public License 2.0
- [javassist](https://github.com/jboss-javassist/javassist) - Apache-2.0 License
- [ASM](https://asm.ow2.io/) - BSD-3-Clause License

## License

> This work has a restrictive license in addition to the original license to prevent some unexpected behavior,
> see [404Setup Public License](https://github.com/404Setup/404Setup/blob/main/LICENSE.md)

2025-2026 404Setup. All rights reserved. Source code is licensed under a Apache 2.0 License.