# KryptonReno's Fabric Patcher

[![](https://badges.moddingx.org/modrinth/downloads/kreno-fpatcher)](https://modrinth.com/mod/kreno-fpatcher)
[![](https://badges.moddingx.org/curseforge/downloads/1294543)](https://www.curseforge.com/minecraft/mc-mods/kreno-fpatcher)

[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/kreno-fpatcher)
[![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/kreno-fpatcher)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/404Setup/KryptonReno-Fabric)

This is an extension mod for Krypton Fabric, ported from KryptonReno.

## Feature

- More basic optimizations
- RecastLib provides acceleration features for **Windows** (x64/arm64)

## Install

Install directly as a Mod.

Starting from version 26.1.2, the Mod body no longer includes the Patcher and JavaAgent modules. If you still want them
to take effect, please see [README](https://github.com/404Setup/KryptonReno-Fabric/blob/26.1/javaagent/README.md).

## Config

Add the following parameters to the Java startup parameters to control the mixin enablement:

| Parameter                | Description    | Default value | Configuration in file form |
|--------------------------|----------------|---------------|----------------------------|
| velocity.natives-disable | Disable Native | false         | Not supported              |

example:

```shell
java -Dvelocity.natives-disable=true -jar neoforge_launcher.jar
```

## Dependencies

- [Krypton Fabric](https://modrinth.com/mod/krypton) - Optional dependency. I put some optimizations here.

## License

> This work has a restrictive license in addition to the original license to prevent some unexpected behavior,
> see [404Setup Public License](https://github.com/404Setup/404Setup/blob/main/LICENSE.md)

2025-2026 404Setup. All rights reserved. Source code is licensed under a LGPL-3.0 Only.