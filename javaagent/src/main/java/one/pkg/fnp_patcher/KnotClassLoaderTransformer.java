package one.pkg.fnp_patcher;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

public class KnotClassLoaderTransformer {
    public static byte[] transform(byte[] classfileBuffer) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        ClassVisitor cv = new KnotClassVisitor(Opcodes.ASM9, cw);

        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }

    public static class KnotClassVisitor extends ClassVisitor {
        public KnotClassVisitor(int api,
                                ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

            if ("<init>".equals(name)) {
                System.out.println("[FNP-Patcher] Found KnotClassLoader constructor: " + descriptor);
                return new ConstructorAdapter(Opcodes.ASM9, mv, access, name, descriptor);
            }

            return mv;
        }
    }

    private static class ConstructorAdapter extends AdviceAdapter {

        protected ConstructorAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodExit(int opcode) {
            if (opcode == RETURN) {
                System.out.println("[FNP-Patcher] Injecting velocity-native loading code into constructor");

                // try {
                //     System.out.println("[FNP-Patcher] Injecting velocity-native into KnotClassLoader");
                //     URL velocityUrl = JarHijacker.getBuiltinJarUrl();
                //     if (velocityUrl != null) {
                //         this.urlLoader.addURL(velocityUrl);
                //         System.out.println("[FNP-Patcher] Successfully added velocity-native: " + velocityUrl);
                //     } else {
                //         System.err.println("[FNP-Patcher] Failed to get velocity-native URL");
                //     }
                // } catch (Exception e) {
                //     System.err.println("[FNP-Patcher] Error injecting velocity-native: " + e.getMessage());
                //e.printStackTrace();
                // }
                Label tryStart = new Label();
                Label tryEnd = new Label();
                Label catchStart = new Label();
                Label end = new Label();

                mv.visitTryCatchBlock(tryStart, tryEnd, catchStart, "java/lang/Exception");
                mv.visitLabel(tryStart);

                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("[FNP-Patcher] Injecting velocity-native into KnotClassLoader");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                mv.visitMethodInsn(INVOKESTATIC, "one/pkg/fnp_patcher/JarHijacker", "getBuiltinJarUrl", "()Ljava/net/URL;", false);
                int velocityUrlVar = newLocal(Type.getType("Ljava/net/URL;"));
                mv.visitVarInsn(ASTORE, velocityUrlVar);
                mv.visitVarInsn(ALOAD, velocityUrlVar);
                Label nullLabel = new Label();
                mv.visitJumpInsn(IFNULL, nullLabel);

                mv.visitVarInsn(ALOAD, 0); // this
                mv.visitFieldInsn(GETFIELD, "net/fabricmc/loader/impl/launch/knot/KnotClassLoader",
                        "urlLoader", "Lnet/fabricmc/loader/impl/launch/knot/KnotClassLoader$DynamicURLClassLoader;");
                mv.visitVarInsn(ALOAD, velocityUrlVar);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/fabricmc/loader/impl/launch/knot/KnotClassLoader$DynamicURLClassLoader",
                        "addURL", "(Ljava/net/URL;)V", false);

                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitLdcInsn("[FNP-Patcher] Successfully added velocity-native: ");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(ALOAD, velocityUrlVar);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                        "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                Label afterIf = new Label();
                mv.visitJumpInsn(GOTO, afterIf);

                // else
                mv.visitLabel(nullLabel);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("[FNP-Patcher] Failed to get velocity-native URL");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                mv.visitLabel(afterIf);
                mv.visitLabel(tryEnd);
                mv.visitJumpInsn(GOTO, end);

                mv.visitLabel(catchStart);
                int exceptionVar = newLocal(Type.getType("Ljava/lang/Exception;"));
                mv.visitVarInsn(ASTORE, exceptionVar);

                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitLdcInsn("[FNP-Patcher] Error injecting velocity-native: ");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(ALOAD, exceptionVar);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "getMessage",
                        "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                        "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                mv.visitVarInsn(ALOAD, exceptionVar);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);

                mv.visitLabel(end);
            }
        }
    }
}