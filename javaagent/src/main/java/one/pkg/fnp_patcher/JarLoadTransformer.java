package one.pkg.fnp_patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class JarLoadTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if ("io/papermc/paperclip/Paperclip".equals(className)) {
            try {
                System.out.println("[FNP-Patcher] Transforming Paperclip class");
                return transformPaperclip(classfileBuffer);
            } catch (Exception e) {
                System.err.println("[FNP-Patcher] Failed to transform Paperclip: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if ("net/fabricmc/loader/impl/metadata/V1ModMetadata$JarEntry".equals(className)) {
            try {
                System.out.println("[FNP-Patcher] Transforming V1ModMetadata$JarEntry class");
                return transformJarEntry(classfileBuffer);
            } catch (Exception e) {
                System.err.println("[FNP-Patcher] Failed to transform JarEntry: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if ("net/fabricmc/loader/impl/launch/knot/KnotClassLoader".equals(className)) {
            try {
                System.out.println("[FNP-Patcher] Transforming KnotClassLoader class with ASM");
                return KnotClassLoaderTransformer.transform(classfileBuffer);
            } catch (Exception e) {
                System.err.println("[FNP-Patcher] Failed to transform KnotClassLoader: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return null;
    }

    private byte[] transformPaperclip(byte[] classfileBuffer) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));

        CtMethod setupClasspathMethod = ctClass.getDeclaredMethod("setupClasspath");

        setupClasspathMethod.insertBefore(
                "{ " +
                        "  System.out.println(\"[FNP-Patcher] setupClasspath called, preparing to hijack...\");" +
                        "}"
        );

        setupClasspathMethod.insertAfter(
                "{ " +
                        "  System.out.println(\"[FNP-Patcher] Hijacking velocity-native jars in classpath...\");" +
                        "  $_ = one.pkg.fnp_patcher.JarHijacker.hijackClasspathUrls($_);" +
                        "}"
        );

        byte[] byteCode = ctClass.toBytecode();
        ctClass.detach();

        System.out.println("[FNP-Patcher] Paperclip transformation completed");
        return byteCode;
    }

    private byte[] transformJarEntry(byte[] classfileBuffer) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));

        CtConstructor[] constructors = ctClass.getDeclaredConstructors();

        for (CtConstructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 1 &&
                    constructor.getParameterTypes()[0].getName().equals("java.lang.String")) {

                System.out.println("[FNP-Patcher] Found JarEntry(String) constructor, injecting hijack logic");

                constructor.insertBefore(
                        "{ " +
                                "  if ($1 != null && $1.contains(\"velocity-native\")) {" +
                                "    System.out.println(\"[FNP-Patcher] Hijacking velocity-native jar: \" + $1);" +
                                "    $1 = \"\";" +
                                "  }" +
                                "}"
                );

                break;
            }
        }

        byte[] byteCode = ctClass.toBytecode();
        ctClass.detach();

        System.out.println("[FNP-Patcher] JarEntry transformation completed");
        return byteCode;
    }
}