## FNP Patcher JavaAgent

This is the JavaAgent version of FNP Patcher. Due to review policies,
this component cannot be published on Modrinth and CurseForge,
so you cannot include it in any Modpack.

----

$\color{Red}\Huge{\textbf{You should not include }}$
$\color{Red}\Huge{\textbf{the }}$
$\color{Gold}\Huge{\textbf{modified }}$
$\color{Red}\Huge{\textbf{Krypton Fabric/PaperServer }}$
$\color{Red}\Huge{\textbf{in your modpack!}}$

$\color{Red}\Huge{\textbf{Instead install both }}$
$\color{Red}\Huge{\textbf{Krypton Fabric/PaperServer and KryptonFNP }}$
$\color{Red}\Huge{\textbf{Patcher and give instructions}}$
$\color{Red}\Huge{\textbf{ on how to use them.}}$

## Warning

- It is forbidden to distribute FNP Patcher Agent in any way
- It is forbidden to distribute the products of FNP Patcher in any way, such as Krypton Fabric Patched or PaperLike
  Patched
- Prohibition of monetization

## Install

Patcher and Javaagent are currently incompatible with servers using the Paperclip fork.

Native replacement is only compatible with Velocity version 3.2.0-SNAPSHOT and later.

### Javaagent

> **About Fabric Loader**:
>
> Compatible range: 0.17.2 - 0.18.4, other versions untested but theoretically compatible.
>
> Quilt compatibility is unknown. May conflict with some anti-cheat implementations, use with caution.
>
> **About PaperServer**
>
> Only applicable to server implementations that do not use the Paperclip fork
>
> Incompatible with other PaperClip forks due to package name changes and other unknown modifications.

Javaagent mode allows dynamic Native replacement without modifying Krypton Fabric.

**Installation**:

1. Download FNP Patcher JavaAgent
2. Place it in the game root directory (not the mods directory)
3. Modify the game launch configuration, add `-javaagent:fnp_patcher.jar` to the JVM startup arguments
4. Launch the game

### Patcher (Deprecated)

> Patcher mode will be deprecated soon, expected to fully transition to Javaagent mode starting from version 26.2
>
> When performing the patch operation, you need to close the running game and make sure that no other processes are
> occupying the Mods directory.

1. Install FNP Patcher JavaAgent and Paper Server/Krypton Fabric in the same directory
2. Open Terminal
3. Use command: `java -jar kryptonfnp_patcher.jar` (Please use actual file names!)
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