package org.upyog.Automation.Common;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Base.BaseTest;
import org.upyog.Automation.Modules.Adv.AdvEmp;
import org.upyog.Automation.Modules.Asset.AssetApprover;
import org.upyog.Automation.Modules.Asset.AssetEmp;
import org.upyog.Automation.Modules.Asset.AssetVerifier;
import org.upyog.Automation.Modules.CHB.chbEmp;
import org.upyog.Automation.Modules.CnD.CnDEmp;
import org.upyog.Automation.Modules.DesludgingService.DelsudgingEmployeeComplete;
import org.upyog.Automation.Modules.DesludgingService.DelsudgingFstpo;
import org.upyog.Automation.Modules.DesludgingService.DesludgingAssignPsso;
import org.upyog.Automation.Modules.DesludgingService.DesludgingEmployeeUpdate;
import org.upyog.Automation.Modules.EWaste.EWasteEmp;
import org.upyog.Automation.Modules.OBPAS.OBPASEmp;
import org.upyog.Automation.Modules.OBPAS.OBPASOcEmp;
import org.upyog.Automation.Modules.Pet.PetApplicationEmp;
import org.upyog.Automation.Modules.PropertyTax.PropertyTaxEmp;
import org.upyog.Automation.Modules.PublicGrievanceRedressal.PgrEmp;
import org.upyog.Automation.Modules.RequestService.MobileToiletEmp;
import org.upyog.Automation.Modules.RequestService.TreePruningEmp;
import org.upyog.Automation.Modules.RequestService.TreePruningVerifier;
import org.upyog.Automation.Modules.StreetVending.SvEmp;
import org.upyog.Automation.Modules.TradeLicense.InboxEmpTl;
import org.upyog.Automation.Modules.TradeLicense.TradeLicenseEmp;
import org.upyog.Automation.Modules.WaterAndSewerage.SewerageEmp;
import org.upyog.Automation.Modules.WaterAndSewerage.WaterEmp;
import org.upyog.Automation.Modules.RequestService.WaterTankerEmployee;
import org.upyog.Automation.Utils.DriverFactory;
import org.upyog.Automation.Utils.ModuleWrapper;

import java.time.Duration;

/**
 * Common entry point for all employee module tests
 * Routes to appropriate module based on moduleName
 */
@Component
public class CommonEmployeeTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonEmployeeTest.class);

    @Autowired
    private SvEmp svEmp;

    @Autowired
    private PetApplicationEmp petApplicationEmp;
    
    @Autowired
    private TradeLicenseEmp tradeLicenseEmp;

    @Autowired
    private InboxEmpTl inboxEmpTl;
    
    @Autowired
    private AssetEmp assetEmp;

    @Autowired
    private AssetVerifier assetVerifier;

    @Autowired
    private AssetApprover assetApprover;

    @Autowired
    private AdvEmp advEmp;

    @Autowired
    private PropertyTaxEmp propertyTaxEmp;

    @Autowired
    private EWasteEmp eWasteEmp;

    @Autowired
    private OBPASEmp obpasEmp;

    @Autowired
    private OBPASOcEmp obpasOcEmp;

    @Autowired
    private WaterTankerEmployee waterTankerEmployee;

    @Autowired
    private TreePruningEmp treePruningEmp;

    @Autowired
    private TreePruningVerifier treePruningVerifier;

    @Autowired
    private MobileToiletEmp mobileToiletEmp;

    @Autowired
    private chbEmp chbEmp;

    @Autowired
    private CnDEmp cndEmp;

    @Autowired
    private PgrEmp pgrEmp;

    @Autowired
    private SewerageEmp sewerageEmp;

    @Autowired
    private WaterEmp waterEmp;

    @Autowired
    private DesludgingEmployeeUpdate desludgingEmployeeUpdate;

    @Autowired
    private DelsudgingEmployeeComplete desludgingEmployeeComplete;

    @Autowired
    private DesludgingAssignPsso desludgingAssignPsso;

    @Autowired
    private DelsudgingFstpo delsudgingFstpo;


    private void employeeSetUp(String baseUrl) {
        driver = DriverFactory.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        js = (JavascriptExecutor) driver;

        driver.get(baseUrl);
    }


    public void runEmployeeTest(String baseUrl,
                                String moduleName,
                                String username,
                                String password,
                                String applicationNumber) {

        employeeSetUp(baseUrl);

        logger.info("Starting {} employee test", moduleName);

        try {
            switch (moduleName.toUpperCase()) {

                case "STREET_VENDING":

                    ModuleWrapper.execute(
                            "STREET_VENDING",
                            () -> svEmp.inboxEmpSv(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "PET_REGISTRATION":

                    ModuleWrapper.execute(
                            "PET_REGISTRATION",
                            () -> petApplicationEmp.petInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "TRADE_LICENSE":

                    ModuleWrapper.execute(
                            "TRADE_LICENSE",
                            () -> tradeLicenseEmp.tlInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "TRADE_LICENSE1":
                    inboxEmpTl.inboxEmpTl(baseUrl, username, password, applicationNumber);
                    break;


                case "ASSET_MANAGEMENT_SYSTEM":

                    ModuleWrapper.execute(
                            "ASSET_MANAGEMENT_SYSTEM",
                            () -> assetEmp.assetEmployeeFlow(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "ASSET_MANAGEMENT_SYSTEM_VERIFIER":

                    ModuleWrapper.execute(
                            "ASSET_MANAGEMENT_SYSTEM",
                            () -> assetVerifier.assetEmployeeVerifier(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "ASSET_MANAGEMENT_SYSTEM_APPROVER":

                    ModuleWrapper.execute(
                            "ASSET_MANAGEMENT_SYSTEM",
                            () -> assetApprover.assetEmployeeApprover(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "ADVERTISEMENT":

                    ModuleWrapper.execute(
                            "ADVERTISEMENT",
                            () -> advEmp.advApproval(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "PROPERTY_TAX":

                    ModuleWrapper.execute(
                            "PROPERTY_TAX",
                            () -> propertyTaxEmp.propertyInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "EWASTE_MANAGEMENT_SYSTEM":

                    ModuleWrapper.execute(
                            "EWASTE_MANAGEMENT_SYSTEM",
                            () -> eWasteEmp.eWasteApproval(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "DESLUDGING_EMPLOYEE_UPDATE":

                    ModuleWrapper.execute(
                            "DESLUDGING_EMPLOYEE_UPDATE",
                            () -> desludgingEmployeeUpdate.desludgingUpdate(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "DESLUDGING_EMPLOYEE_COMPLETE":

                    ModuleWrapper.execute(
                            "DESLUDGING_EMPLOYEE_COMPLETE",
                            () -> desludgingEmployeeComplete.desludgingCom(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "DESLUDGING_EMPLOYEE_PSSO":

                    ModuleWrapper.execute(
                            "DESLUDGING_EMPLOYEE_PSSO",
                            () -> desludgingAssignPsso.desludgingPss(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "DESLUDGING_EMPLOYEE_FSTPO":

                    ModuleWrapper.execute(
                            "DESLUDGING_EMPLOYEE_FSTPO",
                            () -> delsudgingFstpo.desludgingFstp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM":

                    ModuleWrapper.execute(
                            "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM",
                            () -> obpasEmp.OBPASInbox(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC":

                    ModuleWrapper.execute(
                            "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC",
                            () -> obpasOcEmp.OBPASOcInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "WATER_TANKER":

                    ModuleWrapper.execute(
                            "WATER_TANKER",
                            () -> waterTankerEmployee.waterTankerInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "TREE_PRUNING":

                    ModuleWrapper.execute(
                            "TREE_PRUNING",
                            () -> treePruningEmp.treePruningInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "MOBILE_TOILET":

                    ModuleWrapper.execute(
                            "MOBILE_TOILET",
                            () -> mobileToiletEmp.mobileToiletInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "COMMUNITY_HALL_BOOKING":

                    ModuleWrapper.execute(
                            "CHB",
                            () -> chbEmp.chbInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "CONSTRUCTION_AND_DEMOLITION":

                    ModuleWrapper.execute(
                            "CND_EMP",
                            () -> cndEmp.cndApproval(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "PUBLIC_GRIEVANCE_REDRESSAL":

                    ModuleWrapper.execute(
                            "PUBLIC_GRIEVANCE_REDRESSAL",
                            () -> pgrEmp.pgrInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "SEWERAGE_EMP":

                    ModuleWrapper.execute(
                            "SEWERAGE",
                            () -> sewerageEmp.sewerageInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "WATER_EMP":

                    ModuleWrapper.execute(
                            "WATER",
                            () -> waterEmp.waterInboxEmp(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                default:
                    logger.error("Unknown module: {}", moduleName);
                    throw new RuntimeException("Unknown module: " + moduleName);
            }

            logger.info("{} employee test completed", moduleName);

        } catch (Exception e) {
            logger.error("Error in {} employee test: {}", moduleName, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}