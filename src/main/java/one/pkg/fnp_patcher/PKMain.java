package one.pkg.fnp_patcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

public class PKMain {
    private static final String[] natives = new String[]{
            "velocity-native-3.3.0-SNAPSHOT.jar",
            "velocity-native-3.4.0-SNAPSHOT.jar"
    };

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

    private static boolean bePatched(JarFile jarFile) {
        return jarFile.getEntry("fnp.patched") != null;
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
                    if (isFabricMod(jarFile) && isKryptonMod(jarFile) && !bePatched(jarFile)) {
                        return new PKEntry(jar, PKType.ModServer);
                    }
                    if (isPluginServerEntry(jarFile) && !bePatched(jarFile)) {
                        return new PKEntry(jar, PKType.PluginServer);
                    }
                }
            }
        }
        return new PKEntry(null, PKType.UNKNOWN);
    }

    private static void patchModServer(String jarPath, File targetJar) throws IOException {
        File currentJar = new File(jarPath);
        File tempFile = new File(targetJar.getParent(), targetJar.getName() + ".tmp");

        try (JarFile sourceJar = new JarFile(currentJar);
             JarFile originalJar = new JarFile(targetJar);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempFile))) {

            originalJar.stream().forEach(entry -> {
                try {
                    if (!entry.getName().equals("META-INF/jars/" + natives[0]) && !entry.getName().equals("META-INF/jars/" + natives[1])) {
                        jos.putNextEntry(new JarEntry(entry.getName()));
                        originalJar.getInputStream(entry).transferTo(jos);
                        jos.closeEntry();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            sourceJar.stream()
                    .filter(entry -> entry.getName().equals("META-INF/jars/" + natives[1]))
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

    //TODO
    private static void patchPluginServer(String jarPath, File targetJar) throws IOException {
        File currentJar = new File(jarPath);
        File tempFile = new File(targetJar.getParent(), targetJar.getName() + ".tmp");

        try (JarFile sourceJar = new JarFile(currentJar);
             JarFile originalJar = new JarFile(targetJar);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempFile))) {


        }

        Files.move(tempFile.toPath(), targetJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void patchJarFiles(PKEntry target) throws IOException {
        String jarPath = PKMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (target.type == PKType.ModServer) {
            patchModServer(jarPath, target.jarFile);
        } else if (target.type == PKType.PluginServer) {
            patchPluginServer(jarPath, target.jarFile);
        }
    }

    public static void main(String[] args) {
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
        UNKNOWN;
    }

    private record PKEntry(File jarFile, PKType type) {
    }
}
