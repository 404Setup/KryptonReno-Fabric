package one.pkg.fnp_patcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

public class PKMain {
    public static List<File> getJarFiles(Path dir) {
        try (final Stream<Path> files = Files.list(dir)) {
            return files.filter(path ->
                            !path.toString().contains("fnp_patcher") && path.toString().endsWith(".jar") && path.toFile().isFile()
                    )
                    .map(Path::toFile).toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    private static File findTargetJar() throws IOException {
        Path currentDir = new File("").toPath();
        List<File> jarFiles = getJarFiles(currentDir);

        if (jarFiles != null) {
            for (File jar : jarFiles) {
                try (JarFile jarFile = new JarFile(jar)) {
                    if (jarFile.getEntry("krypton.mixins.json") != null) {
                        return jar;
                    }
                }
            }
        }
        return null;
    }

    private static void patchJarFiles(File targetJar) throws IOException {
        URL location = PKMain.class.getProtectionDomain().getCodeSource().getLocation();
        File currentJar;
        try {
            var uri = location.toURI();
            currentJar = Paths.get(uri).toFile();
        } catch (Exception e) {
            currentJar = new File(location.getPath());
        }
        File tempFile = new File(targetJar.getParent(), targetJar.getName() + ".tmp");

        try (JarFile sourceJar = new JarFile(currentJar);
             JarFile originalJar = new JarFile(targetJar);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempFile))) {

            originalJar.stream().forEach(entry -> {
                try {
                    if (!entry.getName().startsWith("META-INF/jars/")) {
                        jos.putNextEntry(new JarEntry(entry.getName()));
                        originalJar.getInputStream(entry).transferTo(jos);
                        jos.closeEntry();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            sourceJar.stream()
                    .filter(entry -> entry.getName().startsWith("META-INF/jars/"))
                    .forEach(entry -> {
                        try {
                            jos.putNextEntry(new JarEntry(entry.getName()));
                            sourceJar.getInputStream(entry).transferTo(jos);
                            jos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        Files.move(tempFile.toPath(), targetJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void main(String[] args) {
        try {
            File targetJar = findTargetJar();
            if (targetJar != null) {
                patchJarFiles(targetJar);
                System.out.println("Successfully patched " + targetJar.getName());
            } else {
                System.out.println("No suitable target JAR found");
            }
        } catch (IOException e) {
            System.err.println("Error during patching: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
