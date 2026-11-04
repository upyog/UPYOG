package org.upyog.Automation.Utils;

public class ModuleTask {

    private final String moduleName;
    private final Runnable moduleLogic;

    public ModuleTask(String moduleName,
                      Runnable moduleLogic) {

        this.moduleName = moduleName;
        this.moduleLogic = moduleLogic;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Runnable getModuleLogic() {
        return moduleLogic;
    }
}