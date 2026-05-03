
package one.pkg.kreno_fpatcher;

import java.lang.instrument.Instrumentation;

public class PKAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[KRENO FPATCHER] JavaAgent loaded");
        inst.addTransformer(new JarLoadTransformer(), true);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("[KRENO FPATCHER] JavaAgent attached");
        inst.addTransformer(new JarLoadTransformer(), true);
    }
}