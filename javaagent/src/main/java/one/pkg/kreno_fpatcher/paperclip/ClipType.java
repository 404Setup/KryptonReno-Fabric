package one.pkg.kreno_fpatcher.paperclip;

public enum ClipType {
    PaperClip("io.papermc.paperclip.Paperclip"), LightClip("dev.menthamc.lightclip.Lightclip"),
    LeavesClip("org.leavesmc.leavesclip.Leavesclip"), QuantumLeaper("cn.dreeam.leaper.QuantumLeaper"),
    HyacinthusClip("moe.luminolmc.hyacinthusclip.Hyacinthusclip");

    private final String packageName;

    ClipType(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String toPackageName() {
        return packageName.replace(".", "/");
    }
}
