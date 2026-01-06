package one.pkg.fnp_patcher;

import io.papermc.paperclip.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import static one.pkg.fnp_patcher.PKShared.TARGET_JARS;

public class PKMain {
    private PKMain() {
    }

    private static List<File> getJarFiles(Path dir) {
        try (final Stream<Path> files = Files.list(dir)) {
            return files.filter(path ->
                            !path.toString().contains("fnp_patcher") && path.toString().endsWith(".jar") && path.toFile().isFile()
                    )
                    .map(Path::toFile).toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    private static boolean isFabricMod(JarFile jarFile) {
        return jarFile.getEntry("fabric.mod.json") != null;
    }

    private static boolean isKryptonMod(JarFile jarFile) {
        return jarFile.getEntry("krypton.mixins.json") != null;
    }

    private static boolean unPatched(JarFile jarFile) {
        return jarFile.getEntry("fnp.patched") == null;
    }

    private static boolean isPluginServerEntry(JarFile jarFile) {
        return jarFile.getEntry("io/papermc/paperclip/Util.class") != null;
    }

    private static PKEntry findTargetJar() throws IOException {
        Path currentDir = new File("").toPath();
        List<File> jarFiles = getJarFiles(currentDir);

        if (jarFiles != null) {
            for (File jar : jarFiles) {
                try (JarFile jarFile = new JarFile(jar)) {
                    if (!unPatched(jarFile)) continue;
                    if (isFabricMod(jarFile) && isKryptonMod(jarFile)) {
                        return new PKEntry(jar, PKType.ModServer);
                    }
                    if (isPluginServerEntry(jarFile)) {
                        return new PKEntry(jar, PKType.PluginServer);
                    }
                }
            }
        }
        return new PKEntry(null, PKType.UNKNOWN);
    }

    private static void patchModServer(File currentJar, File targetJar) throws IOException {
        File tempFile = new File(targetJar.getParent(), targetJar.getName() + ".tmp");

        try (JarFile sourceJar = new JarFile(currentJar);
             JarFile originalJar = new JarFile(targetJar);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempFile))) {

            originalJar.stream().forEach(entry -> {
                try {
                    if (!entry.getName().equals("META-INF/jars/" + TARGET_JARS[0]) &&
                            !entry.getName().equals("META-INF/jars/" + TARGET_JARS[1]) &&
                            !entry.getName().equals("META-INF/jars/" + TARGET_JARS[2])) {
                        jos.putNextEntry(new JarEntry(entry.getName()));
                        originalJar.getInputStream(entry).transferTo(jos);
                        jos.closeEntry();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            sourceJar.stream()
                    .filter(entry -> entry.getName().equals("META-INF/jars/" + TARGET_JARS[2]))
                    .forEach(entry -> {
                        try {
                            jos.putNextEntry(new JarEntry(entry.getName()));
                            sourceJar.getInputStream(entry).transferTo(jos);
                            jos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

            jos.putNextEntry(new JarEntry("fnp.patched"));
            jos.write("FNP Patcher applied".getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();
        }

        Files.move(tempFile.toPath(), targetJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void patchPluginServer(File currentJar, File targetJar) throws IOException {
        File tempFile = new File(targetJar.getParent(), targetJar.getName() + ".tmp");

        try (JarFile sourceJar = new JarFile(currentJar);
             JarFile originalJar = new JarFile(targetJar);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempFile))) {

            String librariesList = null;
            ZipEntry librariesEntry = originalJar.getEntry("META-INF/libraries.list");
            if (librariesEntry != null)
                librariesList = new String(originalJar.getInputStream(librariesEntry).readAllBytes(), StandardCharsets.UTF_8);

            originalJar.stream().forEach(entry -> {
                try {
                    if (!entry.getName().equals("META-INF/libraries.list")) {
                        jos.putNextEntry(new JarEntry(entry.getName()));
                        originalJar.getInputStream(entry).transferTo(jos);
                        jos.closeEntry();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            if (librariesList != null) {
                ZipEntry nativeEntry = sourceJar.getEntry("META-INF/jars/" + TARGET_JARS[2]);
                if (nativeEntry != null) {
                    byte[] nativeBytes = sourceJar.getInputStream(nativeEntry).readAllBytes();
                    byte[] hash = Util.sha256Digest.digest(nativeBytes);

                    StringBuilder hexHash = new StringBuilder();
                    for (byte b : hash) {
                        hexHash.append(String.format("%02x", b));
                    }

                    String[] lines = librariesList.split("\n");
                    StringBuilder newLibrariesList = new StringBuilder();

                    for (String line : lines) {
                        if (line.contains("com.velocitypowered:velocity-native")) {
                            newLibrariesList.append(hexHash).append("\tone.pkg.velocity_rc:velocity-native:3.4.0-SNAPSHOT\tone/pkg/velocity_rc/velocity-native/3.4.0-SNAPSHOT/velocity-native-3.4.0-SNAPSHOT.jar\n");
                        } else {
                            newLibrariesList.append(line).append("\n");
                        }
                    }

                    jos.putNextEntry(new JarEntry("META-INF/libraries.list"));
                    jos.write(newLibrariesList.toString().getBytes(StandardCharsets.UTF_8));
                    jos.closeEntry();

                    jos.putNextEntry(new JarEntry("META-INF/libraries/one/pkg/velocity_rc/velocity-native/3.4.0-SNAPSHOT/velocity-native-3.4.0-SNAPSHOT.jar"));
                    sourceJar.getInputStream(nativeEntry).transferTo(jos);
                    jos.closeEntry();
                }
            }

            jos.putNextEntry(new JarEntry("fnp.patched"));
            jos.write("FNP Patcher applied".getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();

        }

        Files.move(tempFile.toPath(), targetJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void patchJarFiles(PKEntry target) throws IOException {
        URL location = PKMain.class.getProtectionDomain().getCodeSource().getLocation();
        File currentJar;
        try {
            var uri = location.toURI();
            currentJar = Paths.get(uri).toFile();
        } catch (Exception e) {
            currentJar = new File(location.getPath());
        }

        if (target.type == PKType.ModServer) {
            patchModServer(currentJar, target.jarFile);
        } else if (target.type == PKType.PluginServer) {
            patchPluginServer(currentJar, target.jarFile);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--agent-mode".equals(args[0])) {
            System.out.println("Please use this jar with -javaagent parameter");
            System.out.println("Example: java -javaagent:fnp_patcher.jar -jar server.jar");
            return;
        }

        try {
            PKEntry target = findTargetJar();
            if (target.type() == PKType.UNKNOWN) {
                System.err.println("No suitable target JAR found");
                return;
            }
            patchJarFiles(target);
            System.out.println("Successfully patched " + target.type + ":" + target.jarFile.getName());
        } catch (IOException e) {
            System.err.println("Error during patching: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private enum PKType {
        ModServer,
        PluginServer,
        UNKNOWN
    }

    private record PKEntry(File jarFile, PKType type) {
    }
}
