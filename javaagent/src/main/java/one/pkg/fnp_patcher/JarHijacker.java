package one.pkg.fnp_patcher;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static one.pkg.fnp_patcher.PKShared.TARGET_JARS;

public class JarHijacker {
    public static URL[] hijackClasspathUrls(URL[] originalUrls) {
        if (originalUrls == null || originalUrls.length == 0) {
            return originalUrls;
        }

        boolean modified = false;
        URL[] newUrls = new URL[originalUrls.length];

        for (int i = 0; i < originalUrls.length; i++) {
            URL url = originalUrls[i];
            String urlPath = url.getPath();
            boolean hijacked = false;

            for (String targetJar : TARGET_JARS) {
                if (urlPath.endsWith(targetJar)) {
                    URL hijackedUrl = getBuiltinJarUrl();
                    if (hijackedUrl != null) {
                        newUrls[i] = hijackedUrl;
                        System.out.println("[FNP-Patcher] Hijacked: " + urlPath + " -> " + hijackedUrl);
                        modified = true;
                        hijacked = true;
                        break;
                    }
                }
            }

            if (!hijacked) {
                newUrls[i] = url;
            }
        }

        return modified ? newUrls : originalUrls;
    }

    public static Path getBuiltinJarPath() {
        String jarName = TARGET_JARS[2];
        try {
            URL resource = JarHijacker.class.getResource("/META-INF/jars/" + jarName);
            if (resource != null) {
                System.out.println("[FNP-Patcher] Found builtin jar resource: " + resource);

                Path tempFile = extractToTemp(resource, jarName);
                if (tempFile != null) {
                    return tempFile;
                }
            } else {
                System.err.println("[FNP-Patcher] Builtin jar not found in resources: /META-INF/jars/" + jarName);
            }
        } catch (Exception e) {
            System.err.println("[FNP-Patcher] Failed to load builtin jar " + jarName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static URL getBuiltinJarUrl() {
        try {
            Path jarPath = getBuiltinJarPath();
            if (jarPath != null) {
                return jarPath.toUri().toURL();
            }
        } catch (Exception e) {
            System.err.println("[FNP-Patcher] Failed to load builtin jar " + TARGET_JARS[2] + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static Path extractToTemp(URL resource, String jarName) {
        try {
            Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "fnp-patcher");
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }

            Path tempFile = tempDir.resolve(jarName);

            if (Files.exists(tempFile)) {
                System.out.println("[FNP-Patcher] Using cached jar: " + tempFile);
                return tempFile;
            }

            System.out.println("[FNP-Patcher] Extracting jar to: " + tempFile);
            try (var in = resource.openStream();
                 var out = Files.newOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }

            tempFile.toFile().deleteOnExit();

            System.out.println("[FNP-Patcher] Successfully extracted builtin jar to: " + tempFile);
            return tempFile;

        } catch (Exception e) {
            System.err.println("[FNP-Patcher] Failed to extract jar to temp: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}