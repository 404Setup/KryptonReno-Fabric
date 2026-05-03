package one.pkg.kreno_fpatcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import one.pkg.kreno_fpatcher.paperclip.ClipType;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class JarLoadTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        for (ClipType clip : ClipType.values()) {
            if (clip.toPackageName().equals(className)) {
                String clipName = clip.name();
                try {
                    System.out.println("[KRENO FPATCHER] Transforming " + clipName + " class");
                    return transformPaperclip(classfileBuffer, clipName);
                } catch (Exception e) {
                    System.err.println("[KRENO FPATCHER] Failed to transform " + clipName + ": " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            }
        }

        if ("net/fabricmc/loader/impl/metadata/V1ModMetadata$JarEntry".equals(className)) {
            try {
                System.out.println("[KRENO FPATCHER] Transforming V1ModMetadata$JarEntry class");
                return transformJarEntry(classfileBuffer);
            } catch (Exception e) {
                System.err.println("[KRENO FPATCHER] Failed to transform JarEntry: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if ("net/fabricmc/loader/impl/launch/knot/KnotClassLoader".equals(className)) {
            try {
                System.out.println("[KRENO FPATCHER] Transforming KnotClassLoader class with ASM");
                return KnotClassLoaderTransformer.transform(classfileBuffer);
            } catch (Exception e) {
                System.err.println("[KRENO FPATCHER] Failed to transform KnotClassLoader: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return null;
    }

    private byte[] transformPaperclip(byte[] classfileBuffer, String clipName) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

        CtMethod setupClasspathMethod = ctClass.getDeclaredMethod("setupClasspath");

        setupClasspathMethod.insertBefore(
                "{ " +
                        "  System.out.println(\"[KRENO FPATCHER] setupClasspath called, preparing to hijack...\");" +
                        "}"
        );

        setupClasspathMethod.insertAfter(
                "{ " +
                        "  System.out.println(\"[KRENO FPATCHER] Hijacking velocity-native jars in classpath...\");" +
                        "  $_ = one.pkg.kreno_fpatcher.JarHijacker.hijackClasspathUrls($_);" +
                        "}"
        );

        byte[] byteCode = ctClass.toBytecode();
        ctClass.detach();

        System.out.println("[KRENO FPATCHER] " + clipName + " transformation completed");
        return byteCode;
    }

    private byte[] transformJarEntry(byte[] classfileBuffer) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

        CtConstructor[] constructors = ctClass.getDeclaredConstructors();

        for (CtConstructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 1 &&
                    constructor.getParameterTypes()[0].getName().equals("java.lang.String")) {

                System.out.println("[KRENO FPATCHER] Found JarEntry(String) constructor, injecting hijack logic");

                constructor.insertBefore(
                        "{ " +
                                "  if ($1 != null && $1.contains(\"velocity-native\")) {" +
                                "    System.out.println(\"[KRENO FPATCHER] Hijacking velocity-native jar: \" + $1);" +
                                "    $1 = \"\";" +
                                "  }" +
                                "}"
                );

                break;
            }
        }

        byte[] byteCode = ctClass.toBytecode();
        ctClass.detach();

        System.out.println("[KRENO FPATCHER] JarEntry transformation completed");
        return byteCode;
    }
}