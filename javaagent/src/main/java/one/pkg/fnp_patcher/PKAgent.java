
package one.pkg.fnp_patcher;

import java.lang.instrument.Instrumentation;

public class PKAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[FNP-Patcher] JavaAgent loaded");
        inst.addTransformer(new JarLoadTransformer(), true);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("[FNP-Patcher] JavaAgent attached");
        inst.addTransformer(new JarLoadTransformer(), true);
    }
}