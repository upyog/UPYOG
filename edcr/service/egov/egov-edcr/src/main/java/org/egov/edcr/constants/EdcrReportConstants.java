package org.egov.edcr.constants;

import java.math.BigDecimal;

public class EdcrReportConstants {

    // Basement
    public static final String RULE_46_6A = "46-6a";
    public static final String RULE_46_6C = "46-6c";
    public static final String BASEMENT_DESCRIPTION_ONE = "Height from the floor to the soffit of the roof slab or ceiling";
    public static final String BASEMENT_DESCRIPTION_TWO = "Minimum height of the ceiling of upper basement above ground level";

    // Balcony
    public static final String RULE45_IV = "4.4.4 (iii)";
    public static final String WIDTH_BALCONY_DESCRIPTION = "Minimum width for balcony %s";

    // Additional Feature
    public static final String RULE_4_4_4 = "4.4.4 (ii)";
    public static final String RULE_39 = "39";
    public static final String RULE = "5.2.1";
    public static final String RULE_41_I_B = "41-i-b";
    public static final String RULE_47 = "47";
    public static final String RULE_50 = "50";
    public static final String RULE_56 = "56";
    public static final BigDecimal TWO = BigDecimal.valueOf(2);
    public static final BigDecimal ONE_POINTFIVE = BigDecimal.valueOf(1.5);
    public static final BigDecimal THREE = BigDecimal.valueOf(3);
    public static final BigDecimal FOUR = BigDecimal.valueOf(4);
    public static final BigDecimal SIX = BigDecimal.valueOf(6);
    public static final BigDecimal SEVEN = BigDecimal.valueOf(7);
    public static final BigDecimal TEN = BigDecimal.valueOf(10);
    public static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    public static final BigDecimal NINETEEN = BigDecimal.valueOf(19);
    public static final BigDecimal ROAD_WIDTH_TWO_POINTFOUR = BigDecimal.valueOf(2.4);
    public static final BigDecimal ROAD_WIDTH_TWO_POINTFOURFOUR = BigDecimal.valueOf(2.44);
    public static final BigDecimal ROAD_WIDTH_THREE_POINTSIX = BigDecimal.valueOf(3.6);
    public static final BigDecimal ROAD_WIDTH_FOUR_POINTEIGHT = BigDecimal.valueOf(4.8);
    public static final BigDecimal ROAD_WIDTH_SIX_POINTONE = BigDecimal.valueOf(6.1);
    public static final BigDecimal ROAD_WIDTH_NINE_POINTONE = BigDecimal.valueOf(9.1);
    public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);
    public static final int PLOTAREA_100 = 100;
    public static final int PLOTAREA_300 = 300;
    public static final int PLOTAREA_500 = 500;
    public static final int PLOTAREA_1000 = 1000;
    public static final int PLOTAREA_3000 = 3000;
    public static final String OLD = "OLD";
    public static final String NEW = "NEW";
    public static final String OLD_AREA_ERROR = "road width old area";
    public static final String NEW_AREA_ERROR = "road width new area";
    public static final String OLD_AREA_ERROR_MSG = "No construction shall be permitted if the road width is less than 2.4m for old area.";
    public static final String NEW_AREA_ERROR_MSG = "No construction shall be permitted if the road width is less than 6.1m for new area.";
    public static final String NO_OF_FLOORS = "Maximum number of floors allowed";
    public static final String HEIGHT_BUILDING = "Maximum height of building allowed";
    public static final String MIN_PLINTH_HEIGHT_DESC = "Minimum plinth height";
    public static final String MAX_BSMNT_CELLAR = "Number of basement/cellar allowed";
    public static final String MIN_INT_COURT_YARD = "0.15";
    public static final String MIN_INT_COURT_YARD_DESC = "Minimum interior courtyard";
    public static final String BARRIER_FREE_ACCESS_FOR_PHYSICALLY_CHALLENGED_PEOPLE_DESC = "Barrier free access for physically challenged people";
    public static final String GREEN_BUILDINGS_AND_SUSTAINABILITY_PROVISIONS_ERROR_CODE = "Green buildings and sustainability provisions";
    public static final String GREEN_BUILDINGS_AND_SUSTAINABILITY_PROVISIONS_ERROR_MSG = "Green buildings and sustainability provision should be YES";
    public static final String GREEN_BUILDINGS_AND_SUSTAINABILITY = "Green buildings and sustainability provisions";
    public static final String FIRE_PROTECTION_AND_FIRE_SAFETY_REQUIREMENTS_DESC = "Fire Protection And Fire Safety Requirements";

    // Accessory Building Service
    public static final String SUBRULE_88_1_DESC = "Maximum area of accessory block %s";
    public static final String SUBRULE_88_3_DESC = "Maximum height of accessory block %s";
    public static final String SUBRULE_88_1 = "88-1";
    public static final String SUBULE_88_3 = "88-3";
    public static final String SUBRULE_88_4 = "88-4";
    public static final String SUBRULE_88_5 = "88-5";
    public static final String MIN_DIS_NOTIFIED_ROAD_FROM_ACC_BLDG = "Minimum distance from accessory block to notified road";
    public static final String MIN_DIS_NON_NOTIFIED_ROAD_FROM_ACC_BLDG = "Minimum distance from accessory building to non notified road";
    public static final String MIN_DIS_CULDESAC_ROAD_FROM_ACC_BLDG = "Minimum distance from accessory building to culdesac road";
    public static final String MIN_DIS_LANE_ROAD_FROM_ACC_BLDG = "Minimum distance from accessory building to lane road";
    public static final String SUBRULE_88_5_DESC = "Minimum distance from accessory block %s to plot boundary";

    // Bathroom
    // Rule identifier and description for bathroom scrutiny
    public static final String RULE_41_IV = "41-iv";
    public static final String BATHROOM_DESCRIPTION = "Bathroom";
    public static final String BathroomWaterClosets_DESCRIPTION = "Bathroom Water Closets";
    public static final String TOTAL_AREA = ", Total Area >= ";
    public static final String WIDTH = ", Width >= ";
    public static final String HEIGHT = "Height >= ";

    // BlockDistanceService
    public static final String SUBRULE_54_3 = "54-3";
    public static final String SUBRULE_55_2 = "55-2";
    public static final String SUBRULE_57_4 = "57-4";
    public static final String SUBRULE_58_3_A = "58-3-a";
    public static final String SUBRULE_59_3 = "59-3";
    public static final String SUBRULE_117_3 = "117-3";
    public static final String BLK_NUMBER = "blkNumber";
    public static final String SUBRULE = "subrule";
    public static final String MIN_DISTANCE = "minimumDistance";
    public static final String OCCUPANCY = "occupancy";
    public static final String SUBRULE_37_1 = "37-1";
    public static final String SUB_RULE_DES = "Minimum distance between blocks %s and %s";
    public static final String MINIMUM_DISTANCE_SETBACK = "Minimum distance should not be less than setback of tallest building or 3m";
    public static final String MINIMUM_DISTANCE_BUILDING = "Minimum distance should not be less than 1/3 of height of tallest building or 18m";

    // Building Height 
    public static final String RULE_EXPECTED_KEY = "buildingheight.expected";
    public static final String RULE_ACTUAL_KEY = "buildingheight.actual";
    public static final String SECURITYZONE_RULE_EXPECTED_KEY = "securityzone.expected";
    public static final String SECURITYZONE_RULE_ACTUAL_KEY = "securityzone.actual";
    public static final String SUB_RULE_32_1A = "32-1A";
    public static final String SUB_RULE_32_3 = "32-3";

    // Rule identifier and description for chimney scrutiny
    public static final String RULE_44_D = "44-d";
    public static final String CHIMNEY_DESCRIPTION = "Chimney";
    public static final String CHIMNEY_VERIFY_DESCRIPTION = "Verified whether chimney height is <= ";
    public static final String METERS = " meters";

    public static final String SUBRULE_48_3_DESC = "Minimum number of lifts";
    public static final String SUBRULE_42_5_V = "42-5-v";

    public static final String RULE_DESCRIPTION_KEY = "coverage.description";
    public static final String COVERAGE_RULE_EXPECTED_KEY = "coverage.expected";
    public static final String COVERAGE_RULE_ACTUAL_KEY = "coverage.actual";

    public static final String SUBRULE_11_A_DESC = "Maximum depth of cutting from ground level";
    public static final String SUBRULE_11_A = "11-A";

    public static final String SUB_RULE_25_1 = "25-1";
    public static final String SUB_RULE_25_1_PROVISIO = "25-1 Provisio";
    public static final String SUB_RULE_25_1_PROVISIO_DESC = "Distance from building to street boundary";
    public static final String SUB_RULE_26_DESCRIPTION = "Prohibition for constructions abutting public roads.";
    public static final String SUB_RULE_62_1DESCRIPTION = "Minimum distance between plot boundary and abutting Street.";
    public static final String SUB_RULE_26 = "26";
    public static final String RULE_62 = "62";
    public static final String SUB_RULE_62_1 = "62-1";
    public static BigDecimal FIVE = BigDecimal.valueOf(5);
    public static BigDecimal ONEPOINTFIVE = BigDecimal.valueOf(1.5);
    public static final String MOA_RULE_EXPECTED_KEY = "meanofaccess.expected";
    public static final String MOA_RULE_ACTUAL_KEY = "meanofaccess.actual";

    public static final String EXIT_WIDTH_DESC = "Exit Width";
    public static final String SUBRULE_42_3 = "42-3";
    public static final String E_OCCUPANCY = "Occupancy";
    public static final String EXIT_WIDTH = "Exit Width";
    
    public static final String VALIDATION_NEGATIVE_FLOOR_AREA = "msg.error.negative.floorarea.occupancy.floor";
    public static final String VALIDATION_NEGATIVE_EXISTING_FLOOR_AREA = "msg.error.negative.existing.floorarea.occupancy.floor";
    public static final String VALIDATION_NEGATIVE_BUILTUP_AREA = "msg.error.negative.builtuparea.occupancy.floor";
    public static final String VALIDATION_NEGATIVE_EXISTING_BUILTUP_AREA = "msg.error.negative.existing.builtuparea.occupancy.floor";
    public static final String RULE_31_1 = "31-1";
    public static final String RULE_38 = "38";

    public static final BigDecimal POINTTWO = BigDecimal.valueOf(0.2);
    public static final BigDecimal POINTFOUR = BigDecimal.valueOf(0.4);
    public static final BigDecimal POINTFIVE = BigDecimal.valueOf(0.5);
    public static final BigDecimal POINTSIX = BigDecimal.valueOf(0.6);
    public static final BigDecimal POINTSEVEN = BigDecimal.valueOf(0.7);
    public static final BigDecimal ONE = BigDecimal.valueOf(1);
    public static final BigDecimal ONE_POINTTWO = BigDecimal.valueOf(1.2);
    public static final BigDecimal ONE_POINTEIGHT = BigDecimal.valueOf(1.8);
    public static final BigDecimal TWO_POINTFIVE = BigDecimal.valueOf(2.5);
    public static final BigDecimal THREE_POINTTWOFIVE = BigDecimal.valueOf(3.25);
    public static final BigDecimal THREE_POINTFIVE = BigDecimal.valueOf(3.5);
    public static final BigDecimal FIFTEEN = BigDecimal.valueOf(15);

    public static final BigDecimal ROAD_WIDTH_EIGHTEEN_POINTTHREE = BigDecimal.valueOf(18.3);
    public static final BigDecimal ROAD_WIDTH_TWENTYFOUR_POINTFOUR = BigDecimal.valueOf(24.4);
    public static final BigDecimal ROAD_WIDTH_TWENTYSEVEN_POINTFOUR = BigDecimal.valueOf(27.4);
    public static final BigDecimal ROAD_WIDTH_THIRTY_POINTFIVE = BigDecimal.valueOf(30.5);
    
    // Constants for Residential FAR Added by Bimal Kumar
    public static final BigDecimal FAR_UP_TO_2_00 = new BigDecimal("2.00");
    public static final BigDecimal FAR_UP_TO_1_90 = new BigDecimal("1.90");
    public static final BigDecimal FAR_UP_TO_1_75 = new BigDecimal("1.75");
    public static final BigDecimal FAR_UP_TO_1_65 = new BigDecimal("1.65");
    public static final BigDecimal FAR_UP_TO_1_50 = new BigDecimal("1.50");
    public static final BigDecimal FAR_UP_TO_1_25 = new BigDecimal("1.25");

    // Plot Area Categories (Integer Values) Added by Bimal Kumar on 11 March 2024
    // for residential far updation
    public static final BigDecimal PLOT_AREA_UP_TO_100_SQM = new BigDecimal("100");
    public static final BigDecimal PLOT_AREA_100_150_SQM = new BigDecimal("150");
    public static final BigDecimal PLOT_AREA_150_200_SQM = new BigDecimal("200");
    public static final BigDecimal PLOT_AREA_200_300_SQM = new BigDecimal("300");
    public static final BigDecimal PLOT_AREA_300_500_SQM = new BigDecimal("500");
    public static final BigDecimal PLOT_AREA_500_1000_SQM = new BigDecimal("1000");
    public static final BigDecimal PLOT_AREA_ABOVE_1000_SQM = new BigDecimal(Integer.MAX_VALUE);

    public static final String DECLARED = "Declared";

    // Constants for rule identifiers and descriptions
    public static final String RULE42_5_II = "42-5-iii-f";
    public static final String NO_OF_RISER_DESCRIPTION = "Maximum no of risers required per flight for fire stair %s flight %s";
    public static final String WIDTH_DESCRIPTION_FIRE_STAIR = "Minimum width for fire stair %s flight %s";
    public static final String TREAD_DESCRIPTION = "Minimum tread for fire stair %s flight %s";
    public static final String NO_OF_RISERS = "Number of risers ";
    public static final String FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION = "Flight polyline is not defined in layer ";
    public static final String FLIGHT_LENGTH_DEFINED_DESCRIPTION = "Flight polyline length is not defined in layer ";
    public static final String FLIGHT_WIDTH_DEFINED_DESCRIPTION = "Flight polyline width is not defined in layer ";
    public static final String WIDTH_LANDING_DESCRIPTION = "Minimum width for fire stair %s mid landing %s";
    public static final String FLIGHT_NOT_DEFINED_DESCRIPTION = "Fire stair flight is not defined in block %s floor %s";

    public static final String RULE_36_3 = "36-3";
    public static final String RULE_35 = "35 Table-8";
    public static final String RULE_36 = "36";
    public static final String RULE_37_TWO_A = "37-2-A";
    public static final String RULE_37_TWO_B = "37-2-B";
    public static final String RULE_37_TWO_C = "37-2-C";
    public static final String RULE_37_TWO_D = "37-2-D";
    public static final String RULE_37_TWO_G = "37-2-G";
    public static final String RULE_37_TWO_H = "37-2-H";
    public static final String RULE_37_TWO_I = "37-2-I";
    public static final String RULE_4_4_4_I = "4.4.4";

    public static final String MINIMUMLABEL = "Minimum distance ";

    public static final BigDecimal MIN_PLOT_AREA = BigDecimal.valueOf(30);
    public static final BigDecimal MIN_VAL_100_SQM = BigDecimal.valueOf(1.54);
    public static final BigDecimal MIN_VAL_150_SQM = BigDecimal.valueOf(1.8);
    public static final BigDecimal MIN_VAL_200_SQM = BigDecimal.valueOf(2.16);
    public static final BigDecimal MIN_VAL_300_PlUS_SQM = BigDecimal.valueOf(3.0);
    public static final BigDecimal PLOT_AREA_100_SQM = BigDecimal.valueOf(100);
    public static final BigDecimal PLOT_AREA_150_SQM = BigDecimal.valueOf(150);
    public static final BigDecimal PLOT_AREA_200_SQM = BigDecimal.valueOf(200);
    public static final BigDecimal PLOT_AREA_300_SQM = BigDecimal.valueOf(300);
    public static final BigDecimal PLOT_AREA_500_SQM = BigDecimal.valueOf(500);
    public static final BigDecimal PLOT_AREA_1000_SQM = BigDecimal.valueOf(1000);

    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_1_5 = BigDecimal.valueOf(1.5);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_1_8 = BigDecimal.valueOf(1.8);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_2_5 = BigDecimal.valueOf(2.5);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_3 = BigDecimal.valueOf(3);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_3_6 = BigDecimal.valueOf(3.6);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_4 = BigDecimal.valueOf(4);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_4_5 = BigDecimal.valueOf(4.5);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_5 = BigDecimal.valueOf(5);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_5_5 = BigDecimal.valueOf(5.5);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_6 = BigDecimal.valueOf(6);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_6_5 = BigDecimal.valueOf(6.5);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_7 = BigDecimal.valueOf(7);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_7_5 = BigDecimal.valueOf(7.5);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_8 = BigDecimal.valueOf(8);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_9 = BigDecimal.valueOf(9);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_10 = BigDecimal.valueOf(10);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_11 = BigDecimal.valueOf(11);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_12 = BigDecimal.valueOf(12);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_13 = BigDecimal.valueOf(13);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_14 = BigDecimal.valueOf(14);
    public static final BigDecimal FRONTYARDMINIMUM_DISTANCE_15 = BigDecimal.valueOf(15);
    public static final String BSMT_FRONT_YARD_DESC = "Basement Front Yard";

    public static final String RULERISER = "5.15.4.1";
    public static final String RULETREAD = "5.15.3";
    public static final String EXPECTED_NO_OF_RISER = "12";
    public static final String NO_OF_RISER_DESCRIPTION_GENERAL_STAIR = "Maximum no of risers required per flight for general stair %s flight %s";
    public static final String WIDTH_DESCRIPTION_GEN_STAIR = "Minimum width for general stair %s flight %s";
    public static final String TREAD_DESCRIPTION_GEN_STAIR = "Minimum tread for general stair %s flight %s";

    // Rule identifier and descriptions for guard room scrutiny
    public static final String RULE_48_A = "48-A";
    public static final String GUARD_ROOM_DIMENSION_DESCRIPTION = "Guard Room Dimension";
    public static final String GUARD_ROOM_AREA_DESCRIPTION = "Guard Room Area";
    public static final String GUARD_ROOM_HEIGHT_DESCRIPTION = "Guard Room Height";
    public static final String HEIGHT_UNSPACED = "Height";
    public static final String DIMENSION = "Dimension";

    // Constants for rule identifiers and descriptions
    public static final String RULE42_5_ii = "42-5-ii";
    public static final String RULE_42_5_ii_DESCRIPTION = "Minimum clear of stair head-room";

    public static final String RULE9 = "4.4.4 (ix)";
    public static final String SUBRULE_41_II_B = "41-ii-b";

    public static final String RULE_AC_DESC = "Minimum height of ac room";
    public static final String RULE_REGULAR_DESC = "Minimum height of regular room";
    public static final String SUBRULE_41_II_B_AREA_DESC = "Total area of rooms";
    public static final String SUBRULE_41_II_B_TOTAL_WIDTH = "Minimum Width of room";

    public static final BigDecimal MINIMUM_HEIGHT_3_6 = BigDecimal.valueOf(3.6);
    public static final BigDecimal MINIMUM_HEIGHT_3 = BigDecimal.valueOf(3);
    public static final BigDecimal MINIMUM_HEIGHT_2_75 = BigDecimal.valueOf(2.75);
    public static final BigDecimal MINIMUM_HEIGHT_2_4 = BigDecimal.valueOf(2.4);
    public static final BigDecimal MINIMUM_AREA_9_5 = BigDecimal.valueOf(9.5);
    public static final BigDecimal MINIMUM_AREA_9_2 = BigDecimal.valueOf(9.2);
    public static final BigDecimal MINIMUM_WIDTH_2_4 = BigDecimal.valueOf(2.4);
    public static final BigDecimal MINIMUM_WIDTH_2_7 = BigDecimal.valueOf(2.7);
    public static final BigDecimal MINIMUM_WIDTH_2_1 = BigDecimal.valueOf(2.1);
    public static final BigDecimal MINIMUM_AREA_7_5 = BigDecimal.valueOf(7.5);
    public static final BigDecimal MAXIMUM_AREA_46_45 = BigDecimal.valueOf(46.45);
    public static final BigDecimal VENTILATION_PERCENTAGE = BigDecimal.valueOf(20); // 20% ventilation requirement
    public static final String ROOM_HEIGHT_NOTDEFINED = "Room height is not defined in layer ";
    public static final String LAYER_ROOM_HEIGHT = "BLK_%s_FLR_%s_%s";

    public static final BigDecimal MIN_WINDOW_HEIGHT = BigDecimal.valueOf(0.50);
    public static final BigDecimal MIN_DOOR_HEIGHT = BigDecimal.valueOf(2.0);
    public static final BigDecimal MIN_WINDOW_WIDTH = BigDecimal.valueOf(0.50);
    public static final BigDecimal MIN_DOOR_WIDTH = BigDecimal.valueOf(1.0);
    public static final BigDecimal MIN_NON_HABITATIONAL_DOOR_WIDTH = BigDecimal.valueOf(0.76);


    public static String subRuleDoor = "6.4.1";
    public static String subRuleDesc = "Minimum Area and Width of Room";
    public static String subRuleDesc1 = "Room Wise Ventialtion";
    public static String subRuleDesc5 = "Door Ventialtion";
    public static String subRuleDesc2 = "Room Wise Window Area";
    public static String subRuleDesc3 = "Window Area";
    public static String subRuleDesc4 = "Room wise Door Area";
    public static String subRuleDesc6 = "Door Area";

    // Constants for rule identifiers and descriptions
    public static final String RULE_43A = "43A";
    public static final String RULE_43 = "43";
    public static final String INTERNALCOURTYARD_DESCRIPTION = "Internal Courtyard";
    public static final String VENTILATIONSHAFT_DESCRIPTION = "Ventilation Shaft";
    public static final String AREA = "Area  ";
    public static final String MINIMUM_AREA = "Minimum area ";
    public static final String MINIMUM_WIDTH = "Minimum width ";
    public static final String AT_FLOOR = " at floor ";

    public static final String SUBRULE_41_III = "5.4.1";
    public static final String SUBRULE_41_III_DESC = "Minimum height of kitchen";
    public static final String SUBRULE_41_III_AREA_DESC = "Total area of %s";
    public static final String SUBRULE_41_III_TOTAL_WIDTH = "Minimum Width of %s";
    public static final BigDecimal MINIMUM_AREA_4_5 = BigDecimal.valueOf(4.5);
    public static final BigDecimal MINIMUM_AREA_5 = BigDecimal.valueOf(5);
    public static final BigDecimal MINIMUM_WIDTH_1_8 = BigDecimal.valueOf(1.8);
    public static final String KITCHEN = "kitchen";
    public static final String KITCHEN_STORE = "kitchen with store room";
    public static final String KITCHEN_DINING = "kitchen with dining hall";

    // Constants for rule identifiers and descriptions
    public static final String RULE_28 = "28";
    public static final String ROAD_WIDTH = "Road Width";

    public static final String SUBRULE_48 = "48";
    public static final String SUBRULE_48_DESCRIPTION = "Minimum number of lifts";
    public static final String SUBRULE_118 = "118";
    public static final String SUBRULE_118_DESCRIPTION = "Dimension Of lift";
    public static final String LOCATION_PLAN_DESCRIPTION = "Location Plan";

    public static final BigDecimal VAL_4000 = BigDecimal.valueOf(4000);
    public static final String ACCESS_WIDTH = "Access Width";
    public static final String SUBRULE_57_5 = "57-5";
    public static final String SUBRULE_58_3b = "58-3-b";
    public static final String SUBRULE_59_4 = "59-4";
    public static final String SUB_RULE_DESCRIPTION = "Minimum access width for plan for %s";
    public static final String SUBRULE_33_1 = "33-1";
    public static final BigDecimal VAL_300 = BigDecimal.valueOf(300);
    public static final BigDecimal VAL_600 = BigDecimal.valueOf(600);
    public static final BigDecimal VAL_1000 = BigDecimal.valueOf(1000);
    public static final BigDecimal VAL_8000 = BigDecimal.valueOf(8000);
    public static final BigDecimal VAL_18000 = BigDecimal.valueOf(18000);
    public static final BigDecimal VAL_24000 = BigDecimal.valueOf(24000);
    public static final BigDecimal VAL_1500 = BigDecimal.valueOf(1500);
    public static final BigDecimal VAL_6000 = BigDecimal.valueOf(6000);
    public static final BigDecimal VAL_12000 = BigDecimal.valueOf(12000);
    public static final String SUBRULE_116 = "116";
    public static final String SUB_RULE_DESC = "Minimum access width";

    public static final String SUBRULE_46 = "46";
    public static final String RULE46_MAXAREA_DESC = "Maximum allowed area of mezzanine floor";
    public static final String RULE46_MINAREA_DESC = "Minimum area of mezzanine floor";
    public static final String RULE46_DIM_DESC = "Minimum height of mezzanine floor";
    public static final String SUB_RULE_55_7_DESC = "Maximum allowed area of balcony";
    public static final String SUB_RULE_55_7 = "55-7";
    public static final String HALL_NUMBER = "Hall Number";

    // Constants for rule identifiers and descriptions
    public static final String RULE_20 = "20";
    public static final String MONUMENT_DESCRIPTION = "Distance from monument";
    public static final String NORTH_DIRECTION_DESCRIPTION = "North Direction";

    public static final String SUB_RULE_24_11 = "24-11";
    public static final BigDecimal OPENSTAIR_DISTANCE = BigDecimal.valueOf(0.60);

    public static final String RULE_45 = "45";
    public static final String OVERHANGS_DESCRIPTION = "Minimum width of chajja";

    public static final String SUB_RULE_31 = "31";
    public static final String REMARKS = "Remarks";
    public static final String VOLTAGE = "Voltage";

    public static final String RULE_41_V = "41-v";
    public static final String PARAPET_DESCRIPTION = "Parapet";
    public static final String AND_HEIGHT = " and height <= ";

    public static final String OUT_OF = "Out of ";
    public static final String SLOT_HAVING_GT_4_PTS = " number of polygon not having only 4 points.";
    public static final String LOADING_UNLOADING_DESC = "Minimum required Loading/Unloading area";
    public static final String MINIMUM_AREA_OF_EACH_DA_PARKING = " Minimum width of Each Special parking";
    public static final String SP_PARKING_SLOT_AREA = "Special Parking Area";
    public static final String NO_VIOLATION_OF_AREA = "No violation of area in ";
    public static final String MIN_AREA_EACH_CAR_PARKING = " Minimum Area of Each ECS parking";
    public static final String PARKING_VIOLATED_MINIMUM_AREA = " parking violated minimum area.";
    public static final String PARKING = " parking ";
    public static final String NUMBERS = " Numbers ";
    public static final String MECHANICAL_PARKING = "Mechanical parking";
    public static final String MAX_ALLOWED_MECH_PARK = "Maximum allowed mechanical parking";
    public static final String TWO_WHEELER_PARK_AREA = "Two Wheeler Parking Area";
    public static final String LOADING_UNLOADING_AREA = "Loading Unloading Area";
    public static final String SP_PARKING = "Special parking";
    public static final String SUB_RULE_34_1_DESCRIPTION = "Parking Slots Area";
    public static final String SUB_RULE_34_2 = "34-2";
    public static final String SUB_RULE_40_8 = "40-8";
    public static final String T_RULE = "Table 4.4.4";
    public static final String PARKING_MIN_AREA = "5 M x 2 M";
    public static final double PARKING_SLOT_WIDTH = 2;
    public static final double PARKING_SLOT_HEIGHT = 5;
    public static final double SP_PARK_SLOT_MIN_SIDE = 3.6;
    public static final String DA_PARKING_MIN_AREA = " 3.60 M ";
    public static final String NO_OF_UNITS = "No of apartment units";
    public static final double TWO_WHEEL_PARKING_AREA_WIDTH = 1.5;
    public static final double TWO_WHEEL_PARKING_AREA_HEIGHT = 2.0;
    public static final double MECH_PARKING_WIDTH = 2.7;
    public static final double MECH_PARKING_HEIGHT = 5.5;

    public static final double OPEN_ECS = 22.15;
    public static final double COVER_ECS = 27.17;
    public static final double BSMNT_ECS = 38.5;
    public static final double STILT_ECS = 32.5;
    public static final double PARK_A = 0.25;
    public static final double PARK_F = 0.30;
    public static final double PARK_VISITOR = 0.15;
    public static final String SUB_RULE_40 = "40";
    public static final String RULE_ = "4.2.1";
    public static final String RULE__DESCRIPTION = "Parking space";
    public static final String SUB_RULE_40_10 = "40-10";
    public static final String SUB_RULE_40_10_DESCRIPTION = "Visitor’s Parking";
    public static final String OPEN_PARKING_DIM_DESC = "Open parking ECS dimension ";
    public static final String COVER_PARKING_DIM_DESC = "Cover parking ECS dimension ";
    public static final String BSMNT_PARKING_DIM_DESC = "Basement parking ECS dimension ";
    public static final String VISITOR_PARKING = "Visitor parking";
    public static final String SPECIAL_PARKING_DIM_DESC = "Special parking ECS dimension ";
    public static final String TWO_WHEELER_DIM_DESC = "Two wheeler parking dimension ";
    public static final String MECH_PARKING_DESC = "Mechanical parking dimension ";
    public static final String MECH_PARKING_DIM_DESC = "All Mechanical parking polylines should have dimension 2.7*5.5 m²";
    public static final String MECH_PARKING_DIM_DESC_NA = " mechanical parking polyines does not have dimensions 2.7*5.5 m²";

    public static final String PARKING_VIOLATED_DIM = " parking violated dimension.";
    public static final String PARKING_AREA_DIM = "1.5 M x 2 M";

    public static final String RULE41 = "41";
    public static final String RULE39_6 = "39(6)";
    public static final String RULE39_6_DESCRIPTION = "The minimum passage giving access to stair";
    public static final String RULE_41_DESCRIPTION = "The minimum width of corridors/ verandhas";

    public static final String SUBRULE_59_10 = "59-10";
    public static final String SUBRULE_59_10_DESC = "Minimum distance from canopy to plot boundary";

    public static final String RULE_32 = "4.4.4 (XI)";
    public static final String PLANTATION_TREECOVER_DESCRIPTION = "Plantation tree cover";
    public static final String RULE_37_6 = "37-6";
    public static final String RULE_34 = "34-1";
    public static final String PLOTAREA_DESCRIPTION = "Minimum Plot Area";
    public static final String SUBRULE_PORTICO = "PORTICO";
    public static final String SUBRULE_PORTICO_MAX_LENGTHDESCRIPTION = "Maximum Portico length for portico %s ";
    public static final String PORTICO_DISTANCETO_EXTERIORWALL = "Block %s Portico %s Portico distance to exteriorwall";

    public static final String RULE_51 = "10.3";
    public static final String RULE_51_DESCRIPTION = "Rain Water Harvesting";
    public static final String RWH_DECLARATION_ERROR = DxfFileConstants.RWH_DECLARED
            + " in PLAN_INFO layer must be declared as YES for plot area greater than 100 sqm.";

    public static final String SUBRULE_50_C_4_B = " 50-c-4-b";
    public static final String SUBRULE_40 = "40";
    public static final String SUBRULE_50_C_4_B_DESCRIPTION = "Maximum slope of ramp %s";
    public static final String SUBRULE_50_C_4_B_SLOPE_DESCRIPTION = "Maximum Slope of DA Ramp %s";
    public static final String SUBRULE_50_C_4_B_SLOPE_MAN_DESC = "Slope of DA Ramp";

    public static final BigDecimal REARYARDMINIMUM_DISTANCE_0_9 = BigDecimal.valueOf(0.9);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_1_2 = BigDecimal.valueOf(1.2);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_1_5 = BigDecimal.valueOf(1.5);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_1_8 = BigDecimal.valueOf(1.8);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_2 = BigDecimal.valueOf(2);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_2_5 = BigDecimal.valueOf(2.5);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_3 = BigDecimal.valueOf(3);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_3_6 = BigDecimal.valueOf(3.6);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_4 = BigDecimal.valueOf(4);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_4_5 = BigDecimal.valueOf(4.5);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_5 = BigDecimal.valueOf(5);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_6 = BigDecimal.valueOf(6);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_7 = BigDecimal.valueOf(7);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_8 = BigDecimal.valueOf(8);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_9 = BigDecimal.valueOf(9);
    public static final BigDecimal REARYARDMINIMUM_DISTANCE_12 = BigDecimal.valueOf(12);

    public static final String BSMT_REAR_YARD_DESC = "Basement Rear Setback";
    public static final String SUB_RULE_50_DESC = "Recreational space for Residential Apartment ";
    public static final String SUB_RULE_50_DESC_CELLER = " Ground floor Recreational space ";
    public static final String SUB_RULE_50 = "50";
    public static final String SUB_RULE_50_2 = "50-2";
    public static final String RECREATION = "RECREATION";
    public static final int TOTALNUMBEROFUNITS = 12;

    public static final BigDecimal ONETHOUSANDFIVEHUNDER = BigDecimal.valueOf(1500);
    public static final String SUB_RULE_53_6_DESCRIPTION = "Recycling and reuse of waste water generated facility ";
    public static final String SUB_RULE_53_6 = "53-6";
    public static final BigDecimal TWOTHOUSANDFIVEHUNDER = BigDecimal.valueOf(2500);

    // Rule number and description constants
    public static final String RULE_22 = "22";
    public static final String MAIN_RIVER_DESCRIPTION = "Distance from main river";
    public static final String SUB_RIVER_DESCRIPTION = "Distance from sub river";
    public static final String MAIN_RIVER_PROTECTION_WALL_DESCRIPTION = "Distance from main river protection wall";
    public static final String MAIN_RIVER_EMBANKMENT_DESCRIPTION = "Distance from main river embankment";
    public static final String NO_DISTANCT_MENTIONED = "No distance is provided from protection wall embankment/river main edge or sub river";

    // Color codes for river types
    public static final Integer MAIN_RIVER = 1;
    public static final Integer SUB_RIVER = 2;
    public static final String ROADWIDTH_DESCRIPTION = "Minimum Road Width";

    public static final String RULE_44_A = "44-A";
    public static final String ROOFTANK_DESCRIPTION = "Roof Tanks";
    public static final String ROOFTANK_HEIGHT_DESC = "Verified whether roof tank height is <= ";
    public static final String MTS = " meters";

    public static final String MSG_ERROR_MANDATORY = "msg.error.mandatory.object.not.defined";
    public static final String FEMALE = "Female ";
    public static final String MALE = "Male ";
    public static final String SANITY_RULE_DESC = "Sanity facility for Occupancy ";
    public static final String NEWLINE = "\n";
    public static final String SANITATION = "Sanitation";
    public static final String BLOCK_U_S = "Block_";
    public static final String WITH = " with ";
    public static final String BLDG_PART_WATER_CLOSET = "Water Closet";
    public static final String BLDG_PART_SPECIAL_WATER_CLOSET = "Special Water Closet";
    public static final String BLDG_PART_URINAL = "Urinal";
    public static final String BLDG_PART_BATHROOM = "Bath Room";
    public static final String MALE_BATH_WITH_WC = BLDG_PART_BATHROOM + WITH + BLDG_PART_WATER_CLOSET;
    public static final String BLDG_PART_WASHBASIN = "Wash Basin";
    public static final String MINIMUM_SIDE_DIMENSION_VIOLATED = "Minimum Side Dimension of {0} M violated";
    public static final String MINIMUM_AREA_DIMENSION_VIOLATED = "Minimum Area of {0} M violated";
    public static final String DIMESION_DESC_KEY = "msg.sanity.dimension.desc";
    public static final String FEATURE_NAME = "Sanitary Detail";
    public static final String RULE_38_1 = "38-1";
    public static final String NOOFBEDS = "No Of Beds";
    public static final String RULE_55_12 = "55-12";
    public static final String RULE_40_A_4 = "40A-4";
    public static final String RULE_54_6 = "54-6";
    public static final BigDecimal MINAREAOFSPWC = BigDecimal.valueOf(2.625);
    public static final BigDecimal MINDIMENSIONOFSPWC = BigDecimal.valueOf(1.5);
    public static final String MINIMUM_AREA_SPWC = "2.625 M2";
    public static final String MINIMUM_DIMENSION_SPWC = "1.5 M";


    public static final String RULE_59_10  = "59-10-i";
    public static final String SEGREGATEDTOILET_DESCRIPTION = "Num. of segregated toilets";
    public static final String SEGREGATEDTOILET_DIMENSION_DESCRIPTION = "Segregated toilet distance from main entrance";

    // Constants for rule number and field descriptions
    public static final String RULE_45_E = "45-e";
    public static final String DISTANCE_FROM_WATERSOURCE = "Distance from watersource";
    public static final String DISTANCE_FROM_BUILDING = "Distance from Building";
    public static final String MIN_DISTANCE_FROM_GOVTBUILDING_DESC = "Minimum distance fcrom government building";

    // Default minimum distances (fallback values)
    public static final BigDecimal MIN_DIS_WATERSRC = BigDecimal.valueOf(18);
    public static final BigDecimal MIN_DIS_BUILDING = BigDecimal.valueOf(6);

    public static final BigDecimal SIDEVALUE_ONE = BigDecimal.valueOf(1);
    public static final BigDecimal SIDEVALUE_ONE_TWO = BigDecimal.valueOf(1.2);
    public static final BigDecimal SIDEVALUE_ONEPOINTFIVE = BigDecimal.valueOf(1.5);
    public static final BigDecimal SIDEVALUE_ONEPOINTEIGHT = BigDecimal.valueOf(1.8);
    public static final BigDecimal SIDEVALUE_TWO = BigDecimal.valueOf(2);
    public static final BigDecimal SIDEVALUE_TWOPOINTFIVE = BigDecimal.valueOf(2.5);
    public static final BigDecimal SIDEVALUE_THREE = BigDecimal.valueOf(3);
    public static final BigDecimal SIDEVALUE_THREEPOINTSIX = BigDecimal.valueOf(3.66);
    public static final BigDecimal SIDEVALUE_FOUR = BigDecimal.valueOf(4);
    public static final BigDecimal SIDEVALUE_FOURPOINTFIVE = BigDecimal.valueOf(4.5);
    public static final BigDecimal SIDEVALUE_FIVE = BigDecimal.valueOf(5);
    public static final BigDecimal SIDEVALUE_SIX = BigDecimal.valueOf(6);
    public static final BigDecimal SIDEVALUE_SEVEN = BigDecimal.valueOf(7);
    public static final BigDecimal SIDEVALUE_SEVENTYFIVE = BigDecimal.valueOf(0.75);
    public static final BigDecimal SIDEVALUE_EIGHT = BigDecimal.valueOf(8);
    public static final BigDecimal SIDEVALUE_NINE = BigDecimal.valueOf(9);
    public static final BigDecimal SIDEVALUE_TEN = BigDecimal.valueOf(10);

    public static final String SIDENUMBER = "Side Number";
    public static final String RULE_35_T9 = "35 Table-9";
    public static final String SIDE_YARD_2_NOTDEFINED = "side2yardNodeDefined";
    public static final String SIDE_YARD_1_NOTDEFINED = "side1yardNodeDefined";
    public static final String BSMT_SIDE_YARD_DESC = "Basement Side Yard";
    public static final double FIVE_MTR = 5;
    public static final double TWO_MTR = 2.0;
    public static final double THREE_MTR = 3.0;
    public static final String SUB_RULE_109_C_DESCRIPTION = "Solar Assisted Water Heating / Lighting system ";
    public static final String SUB_RULE_109_C = "109-C";
    public static final String SUBRULE_55_11_DESC = "Collection and disposal of solid and liquid Waste";
    public static final String SUBRULE_55_11 = "55-11";
    public static final String RULE42_5_IV = "42-5-iv";
    public static final String DIAMETER_DESCRIPTION = "Minimum diameter for spiral fire stair %s";
    public static final String RULE_44_C = "44-c";
    public static final String STAIRCOVER_DESCRIPTION = "Mumty";
    public static final String STAIRCOVER_HEIGHT_DESC = "Verified whether stair cover height is <= ";
    // Rule identifier for terrace utility check
    public static final String RULE_43_1 = "43-1";
    // Feature key used in MDMS and for internal processing
    public static final String TERRACEUTILITIESDISTANCE = "TerraceUtilitiesDistance";
    // Error message key (not used in this version but defined for standardization)
    public static final String ERROR_MSG = "Minimum_distance";
    public static final String RULE_41_5_5 = "5.5.2";
    public static final String TOILET_DESCRIPTION = "Toilet";
    // Rule identifier and description for reporting
    public static final String SUBRULE_42_2 = "42-2";
    public static final String SUBRULE_42_2_DESC = "Maximum travel distance to emergency exit";
    public static final String SUBRULE_40_8 = "40-8";
    public static final String DESCRIPTION = "Vehicle Ramp";
    public static final String LIGHT_VENTILATION_DESCRIPTION = "Light and Ventilation";
    public static final String VERANDAH_DESCRIPTION = "Verandah";
    public static final String SUB_RULE_26A_DESCRIPTION = "Waste Disposal";
    public static final String SUB_RULE_26A = "26-A";
    public static final String SUB_RULE_104_4_WD = "104-4";
    public static final String SUB_RULE_104_4_PLOT_DESCRIPTION_WD = "Minimum distance from waste treatment facility like: leach pit,soak pit etc to nearest point on the plot boundary";
    public static final String WATERCLOSETS_DESCRIPTION = "Water Closets";
    public static final String RULE_59_10_vii = "59-10-vii";
    public static final String RULE_59_10_vii_DESCRIPTION = "Water tank capacity";
    public static final String WATER_TANK_CAPACITY = "Minimum capacity of Water tank";
    public static final String SUB_RULE_53_5_DESCRIPTION = "Liquid waste management treatment plant ";
    public static final String SUB_RULE_53_5 = "53-5";

    public static final String SUB_RULE_104_4_PLOT_DESCRIPTION = "Minimum distance from waste treatment facility like: leach pit,soak pit etc to nearest point on the plot boundary";
    public static final String WELL_DISTANCE_FROM_ROAD = "Minimum distance from well to road";
    public static final String SUB_RULE_104_1_DESCRIPTION = "Open well: Minimum distance between street boundary and the well ";
    public static final String SUB_RULE_104_2_DESCRIPTION = "Minimum distance from well to nearest point on plot boundary";
    public static final String SUB_RULE_104_4_DESCRIPTION = "Minimum distance from well to nearest point on leach pit, soak pit, refuse pit, earth closet or septic tanks ";

    public static final String SUB_RULE_104_1 = "104-1";
    public static final String SUB_RULE_104_2 = "104-2";
    public static final String SUB_RULE_104_4 = "104-4";

    public static final BigDecimal three = BigDecimal.valueOf(3);
    public static final BigDecimal ONE_ANDHALF_MTR = BigDecimal.valueOf(1.5);

    // Solar constants
    public static final String SOLAR_VALUE_ONE = "solarValueOne";
    public static final String SOLAR_VALUE_TWO = "solarValueTwo";

}
