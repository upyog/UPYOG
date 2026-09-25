package org.upyog.Automation.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upyog.Automation.Base.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Modules.Adv.AdvBookingCreate;
import org.upyog.Automation.Modules.CHB.chbCreate;
import org.upyog.Automation.Modules.CnD.CnDRequest;
import org.upyog.Automation.Modules.DesludgingService.DesludgingCitizenPayment;
import org.upyog.Automation.Modules.DesludgingService.DesludgingCitizenPayment2;
import org.upyog.Automation.Modules.DesludgingService.DesludgingCreate;
import org.upyog.Automation.Modules.EWaste.EWasteCreate;
import org.upyog.Automation.Modules.OBPAS.OBPASCreate;
import org.upyog.Automation.Modules.OBPAS.OBPASOcCreate;
import org.upyog.Automation.Modules.Pet.PetCreateApplication;
import org.upyog.Automation.Modules.PublicGrievanceRedressal.PgrCreate;
import org.upyog.Automation.Modules.PropertyTax.PropertyTaxCreate;
import org.upyog.Automation.Modules.StreetVending.SvCreateApplication;
import org.upyog.Automation.Modules.TradeLicense.TradeLicenseCreate;
import org.upyog.Automation.Modules.RequestService.TreePruningCitizen;
import org.upyog.Automation.Modules.RequestService.WaterTankerCitizen;
import org.upyog.Automation.Modules.RequestService.MobileToiletCitizen;
import org.upyog.Automation.Modules.WaterAndSewerage.WAndSCreate;
import org.upyog.Automation.Utils.ModuleWrapper;
import org.upyog.Automation.Utils.ModuleTask;
import java.util.List;
import java.util.ArrayList;


/**
 * Common entry point for all citizen module tests
 * Routes to appropriate module based on moduleName
 */

@Component
public class CommonCitizenTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonCitizenTest.class);

    @Autowired
    private SvCreateApplication svCreateApplication;

    @Autowired
    private TradeLicenseCreate tradeLicenseCreate;

    @Autowired
    private PetCreateApplication petCreateApplication;

    @Autowired
    private AdvBookingCreate advBookingCreate;

    @Autowired
    private TreePruningCitizen treePruningCitizen;

    @Autowired
    private WaterTankerCitizen waterTankerCitizen;

    @Autowired
    private MobileToiletCitizen mobileToiletCitizen;

    @Autowired
    private PropertyTaxCreate propertyTaxCreate;

    @Autowired
    private PgrCreate pgrCreate;

    @Autowired
    private OBPASCreate obpasCreate;

    @Autowired
    private EWasteCreate eWasteCreate;

    @Autowired
    private chbCreate chbCreate;

    @Autowired
    private CnDRequest cndRequest;

    @Autowired
    private OBPASOcCreate obpasOCCreate;

    @Autowired
    private DesludgingCreate desludgingCreate;

    @Autowired
    private DesludgingCitizenPayment desludgingCitizenPayment;

    @Autowired
    private DesludgingCitizenPayment2 desludgingCitizenPayment2;

    @Autowired
    private WAndSCreate wAndSCreate;

    public void runCitizenTest(String baseUrl, String moduleName, String mobileNumber, String otp, String cityName, String permitNumber) throws InterruptedException {
        setUp();
        logger.info("Starting {} citizen test", moduleName);

        try {
            switch (moduleName.toUpperCase()) {



                case "STREET_VENDING":

                    ModuleWrapper.execute(
                            "STREET_VENDING",
                            () -> svCreateApplication.svCreateReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "TRADE_LICENSE":

                    ModuleWrapper.execute(
                            "TRADE_LICENSE",
                            () -> tradeLicenseCreate.tradeLicenceCitizenReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "PET_REGISTRATION":
                    ModuleWrapper.execute(
                            "PET_REGISTRATION",
                            () -> petCreateApplication.petApptest(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "ADVERTISEMENT":

                    ModuleWrapper.execute(
                            "ADVERTISEMENT",
                            () -> advBookingCreate.advBookingReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;



                case "TREE_PRUNING":

                    ModuleWrapper.execute(
                            "TREE_PRUNING",
                            () -> treePruningCitizen.treePruningCreate(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;



                case "WATER_TANKER":

                    ModuleWrapper.execute(
                            "WATER_TANKER",
                            () -> waterTankerCitizen.waterTankerCreate(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;



                case "MOBILE_TOILET":

                    ModuleWrapper.execute(
                            "MOBILE_TOILET",
                            () -> mobileToiletCitizen.mobileToiletCreate(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;



                case "PROPERTY_TAX":

                    ModuleWrapper.execute(
                            "PROPERTY_TAX",
                            () -> propertyTaxCreate.newPropertyReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;



                case "PUBLIC_GRIEVANCE_REDRESSAL":

                    ModuleWrapper.execute(
                            "PUBLIC_GRIEVANCE_REDRESSAL",
                            () -> pgrCreate.pgrReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM":

                    ModuleWrapper.execute(
                            "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM",
                            () -> obpasCreate.obpasReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC":

                    ModuleWrapper.execute(
                            "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC",
                            () -> obpasOCCreate.obpasOCReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "EWASTE_MANAGEMENT_SYSTEM":
                    ModuleWrapper.execute(
                            "EWASTE_MANAGEMENT_SYSTEM",
                            () -> eWasteCreate.eWasteReg(driver, wait, js)
                    );
                    break;

                case "COMMUNITY_HALL_BOOKING":

                    ModuleWrapper.execute(
                            "COMMUNITY_HALL_BOOKING",
                            () -> chbCreate.chbReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "CONSTRUCTION_AND_DEMOLITION":

                    ModuleWrapper.execute(
                            "CONSTRUCTION_AND_DEMOLITION",
                            () -> cndRequest.cndReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "DESLUDGING_SERVICE":

                    ModuleWrapper.execute(
                            "DESLUDGING_SERVICE",
                            () -> desludgingCreate.desludgingReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "DESLUDGING_SERVICE_PAYMENT":

                    ModuleWrapper.execute(
                            "DESLUDGING_SERVICE",
                            () -> desludgingCitizenPayment.desludgingPaymentReg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;

                case "DESLUDGING_SERVICE_PAYMENT2":

                    ModuleWrapper.execute(
                            "DESLUDGING_SERVICE",
                            () -> desludgingCitizenPayment2.desludgingPayment2Reg(
                                    driver,
                                    wait,
                                    js
                            )
                    );

                    break;


                case "WATER_AND_SEWERAGE":

                    ModuleWrapper.execute(
                            "WATER_AND_SEWERAGE",
                            () -> wAndSCreate.wandSReg(
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
            logger.info("{} test completed", moduleName);

        } catch (Exception e) {
            logger.error("Error in {} test: {}", moduleName, e.getMessage());
            throw new RuntimeException(e);
        }finally {

            tearDown();
        }
    }

    public void runMultipleModules(String baseUrl,
                                   List<String> selectedModules,
                                   String mobileNumber,
                                   String otp,
                                   String cityName,
                                   String permitNumber) throws InterruptedException {

        setUp();

        logger.info("Starting multiple citizen modules: {}", selectedModules);

        try {

            List<ModuleTask> modules = new ArrayList<>();

            for (String moduleName : selectedModules) {

                switch (moduleName.toUpperCase()) {



                    case "STREET_VENDING":

                        modules.add(
                                new ModuleTask(
                                "STREET_VENDING",
                                () -> svCreateApplication.svCreateReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;



                    case "TRADE_LICENSE":

                        modules.add(
                                new ModuleTask(
                                "TRADE_LICENSE",
                                () -> tradeLicenseCreate.tradeLicenceCitizenReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;



                    case "PET_REGISTRATION":

                        modules.add(
                                new ModuleTask(
                                        "PET_REGISTRATION",
                                        () -> petCreateApplication.petApptest(driver, wait, js)
                                )
                        );

                        break;



                    case "ADVERTISEMENT":

                        modules.add(
                                new ModuleTask(
                                "ADVERTISEMENT",
                                () -> advBookingCreate.advBookingReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;




                    case "TREE_PRUNING":

                        modules.add(
                                new ModuleTask(
                                "TREE_PRUNING",
                                () -> treePruningCitizen.treePruningCreate(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;



                    case "WATER_TANKER":

                        modules.add(
                                new ModuleTask(
                                "WATER_TANKER",
                                () -> waterTankerCitizen.waterTankerCreate(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;



                    case "MOBILE_TOILET":

                        modules.add(
                                new ModuleTask(
                                "MOBILE_TOILET",
                                () -> mobileToiletCitizen.mobileToiletCreate(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;



                    case "PROPERTY_TAX":

                        modules.add(
                                new ModuleTask(
                                "PROPERTY_TAX",
                                () -> propertyTaxCreate.newPropertyReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;



                    case "PUBLIC_GRIEVANCE_REDRESSAL":

                        modules.add(
                                new ModuleTask(
                                "PUBLIC_GRIEVANCE_REDRESSAL",
                                () -> pgrCreate.pgrReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;


                    case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM":

                        modules.add(
                                new ModuleTask(
                                "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM",
                                () -> obpasCreate.obpasReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;



                    case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC":

                        modules.add(
                                new ModuleTask(
                                "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC",
                                () -> obpasOCCreate.obpasOCReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;

                    case "EWASTE_MANAGEMENT_SYSTEM":

                        modules.add(
                                new ModuleTask(
                                "EWASTE_MANAGEMENT_SYSTEM",
                                () -> eWasteCreate.eWasteReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;


                    case "COMMUNITY_HALL_BOOKING":

                        modules.add(
                                new ModuleTask(
                                "COMMUNITY_HALL_BOOKING",
                                () -> chbCreate.chbReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;


                    case "CONSTRUCTION_AND_DEMOLITION":

                        modules.add(
                                new ModuleTask(
                                "CONSTRUCTION_AND_DEMOLITION",
                                () -> cndRequest.cndReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;


                    case "DESLUDGING_SERVICE":

                        modules.add(
                                new ModuleTask(
                                "DESLUDGING_SERVICE",
                                () -> desludgingCreate.desludgingReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;

                    case "DESLUDGING_SERVICE_PAYMENT":

                        modules.add(
                                new ModuleTask(
                                "DESLUDGING_SERVICE_PAYMENT",
                                () -> desludgingCitizenPayment.desludgingPaymentReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;

                    case "DESLUDGING_SERVICE_PAYMENT2":

                        modules.add(
                                new ModuleTask(
                                "DESLUDGING_SERVICE_PAYMENT2",
                                () -> desludgingCitizenPayment2.desludgingPayment2Reg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;



                    case "WATER_AND_SEWERAGE":

                        modules.add(
                                new ModuleTask(
                                "WATER_AND_SEWERAGE",
                                () -> wAndSCreate.wandSReg(
                                        driver,
                                        wait,
                                        js
                                ))
                        );

                        break;

                    default:
                        logger.warn("Skipping unknown module: {}", moduleName);
                }
            }

            ModuleWrapper.executeBatch(modules);

        } finally {

            tearDown();
        }
    }
}