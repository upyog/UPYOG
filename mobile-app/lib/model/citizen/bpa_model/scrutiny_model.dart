import 'package:json_annotation/json_annotation.dart';

part 'scrutiny_model.g.dart';

@JsonSerializable()
class Scrutiny {
  @JsonKey(name: 'responseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'edcrDetail')
  List<EdcrDetail>? edcrDetail;
  @JsonKey(name: 'count')
  int? count;

  Scrutiny();

  factory Scrutiny.fromJson(Map<String, dynamic> json) =>
      _$ScrutinyFromJson(json);

  Map<String, dynamic> toJson() => _$ScrutinyToJson(this);
}

@JsonSerializable()
class EdcrDetail {
  @JsonKey(name: 'dxfFile')
  String? dxfFile;
  @JsonKey(name: 'updatedDxfFile')
  String? updatedDxfFile;
  @JsonKey(name: 'planReport')
  String? planReport;
  @JsonKey(name: 'transactionNumber')
  String? transactionNumber;
  @JsonKey(name: 'applicationDate')
  dynamic applicationDate;
  @JsonKey(name: 'applicationNumber')
  String? applicationNumber;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'edcrNumber')
  String? edcrNumber;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'errors')
  String? errors;
  @JsonKey(name: 'planPdfs')
  List<dynamic>? planPdfs;
  @JsonKey(name: 'planDetail')
  PlanDetail? planDetail;
  @JsonKey(name: 'permitNumber')
  String? permitNumber;
  @JsonKey(name: 'permitDate')
  String? permitDate;
  @JsonKey(name: 'appliactionType')
  String? appliactionType;
  @JsonKey(name: 'applicationSubType')
  String? applicationSubType;
  @JsonKey(name: 'comparisonEdcrNumber')
  String? comparisonEdcrNumber;

  EdcrDetail();

  factory EdcrDetail.fromJson(Map<String, dynamic> json) =>
      _$EdcrDetailFromJson(json);

  Map<String, dynamic> toJson() => _$EdcrDetailToJson(this);
}

@JsonSerializable()
class PlanDetail {
  @JsonKey(name: 'planInfoProperties')
  PlanInfoProperties? planInfoProperties;
  @JsonKey(name: 'edcrPassed')
  bool? edcrPassed;
  @JsonKey(name: 'applicationDate')
  int? applicationDate;
  @JsonKey(name: 'asOnDate')
  dynamic asOnDate;
  @JsonKey(name: 'planInformation')
  PlanInformation? planInformation;
  @JsonKey(name: 'plot')
  Plot? plot;
  @JsonKey(name: 'blocks')
  List<Block>? blocks;
  @JsonKey(name: 'accessoryBlocks')
  List<dynamic>? accessoryBlocks;
  @JsonKey(name: 'accessoryBlockDistances')
  List<dynamic>? accessoryBlockDistances;
  @JsonKey(name: 'virtualBuilding')
  VirtualBuilding? virtualBuilding;
  @JsonKey(name: 'electricLine')
  List<ElectricLine>? electricLine;
  @JsonKey(name: 'nonNotifiedRoads')
  List<dynamic>? nonNotifiedRoads;
  @JsonKey(name: 'notifiedRoads')
  List<dynamic>? notifiedRoads;
  @JsonKey(name: 'culdeSacRoads')
  List<CuldeSacRoad>? culdeSacRoads;
  @JsonKey(name: 'laneRoads')
  List<dynamic>? laneRoads;
  @JsonKey(name: 'travelDistancesToExit')
  List<dynamic>? travelDistancesToExit;
  @JsonKey(name: 'parkingDetails')
  Parking? parkingDetails;
  @JsonKey(name: 'canopyDistanceFromPlotBoundary')
  List<dynamic>? canopyDistanceFromPlotBoundary;
  @JsonKey(name: 'occupancies')
  List<OccupancyElement>? occupancies;
  @JsonKey(name: 'utility')
  Utility? utility;
  @JsonKey(name: 'coverage')
  double? coverage;
  @JsonKey(name: 'farDetails')
  FarDetails? farDetails;
  @JsonKey(name: 'drawingPreference')
  DrawingPreference? drawingPreference;
  @JsonKey(name: 'septicTanks')
  List<dynamic>? septicTanks;
  @JsonKey(name: 'plantation')
  Plantation? plantation;
  @JsonKey(name: 'guardRoom')
  GuardRoom? guardRoom;
  @JsonKey(name: 'segregatedToilet')
  SegregatedToilet? segregatedToilet;
  @JsonKey(name: 'surrenderRoads')
  List<CuldeSacRoad>? surrenderRoads;
  @JsonKey(name: 'totalSurrenderRoadArea')
  double? totalSurrenderRoadArea;
  @JsonKey(name: 'distanceToExternalEntity')
  DistanceToExternalEntity? distanceToExternalEntity;
  @JsonKey(name: 'compoundWall')
  CompoundWall? compoundWall;
  @JsonKey(name: 'roadReserves')
  List<dynamic>? roadReserves;
  @JsonKey(name: 'edcrPdfDetails')
  dynamic edcrPdfDetails;
  @JsonKey(name: 'gate')
  Gate? gate;
  @JsonKey(name: 'errors')
  Errors? errors;
  @JsonKey(name: 'reportOutput')
  ReportOutput? reportOutput;
  @JsonKey(name: 'noObjectionCertificates')
  Errors? noObjectionCertificates;
  @JsonKey(name: 'featureAmendments')
  Errors? featureAmendments;
  @JsonKey(name: 'mdmsMasterData')
  Errors? mdmsMasterData;
  @JsonKey(name: 'mainDcrPassed')
  bool? mainDcrPassed;
  @JsonKey(name: 'icts')
  List<dynamic>? icts;
  @JsonKey(name: 'depthCuttings')
  List<dynamic>? depthCuttings;

  PlanDetail();

  factory PlanDetail.fromJson(Map<String, dynamic> json) =>
      _$PlanDetailFromJson(json);

  Map<String, dynamic> toJson() => _$PlanDetailToJson(this);
}

@JsonSerializable()
class Block {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  int? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'height')
  double? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  int? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'building')
  Building? building;
  @JsonKey(name: 'number')
  String? number;
  @JsonKey(name: 'numberOfLifts')
  String? numberOfLifts;
  @JsonKey(name: 'setBacks')
  List<SetBack>? setBacks;
  @JsonKey(name: 'coverage')
  List<CuldeSacRoad>? coverage;
  @JsonKey(name: 'coverageDeductions')
  List<dynamic>? coverageDeductions;
  @JsonKey(name: 'typicalFloor')
  List<dynamic>? typicalFloor;
  @JsonKey(name: 'disBetweenBlocks')
  List<dynamic>? disBetweenBlocks;
  @JsonKey(name: 'hallAreas')
  List<dynamic>? hallAreas;
  @JsonKey(name: 'diningSpaces')
  List<dynamic>? diningSpaces;
  @JsonKey(name: 'sanityDetails')
  SanityDetails? sanityDetails;
  @JsonKey(name: 'singleFamilyBuilding')
  bool? singleFamilyBuilding;
  @JsonKey(name: 'residentialBuilding')
  bool? residentialBuilding;
  @JsonKey(name: 'residentialOrCommercialBuilding')
  bool? residentialOrCommercialBuilding;
  @JsonKey(name: 'highRiseBuilding')
  bool? highRiseBuilding;
  @JsonKey(name: 'completelyExisting')
  bool? completelyExisting;
  @JsonKey(name: 'openStairs')
  List<dynamic>? openStairs;
  @JsonKey(name: 'plinthHeight')
  List<double>? plinthHeight;
  @JsonKey(name: 'interiorCourtYard')
  List<double>? interiorCourtYard;
  @JsonKey(name: 'protectedBalconies')
  List<dynamic>? protectedBalconies;
  @JsonKey(name: 'plantationGreenStripes')
  List<dynamic>? plantationGreenStripes;
  @JsonKey(name: 'roofTanks')
  List<dynamic>? roofTanks;
  @JsonKey(name: 'stairCovers')
  List<dynamic>? stairCovers;
  @JsonKey(name: 'chimneys')
  List<dynamic>? chimneys;
  @JsonKey(name: 'parapets')
  List<dynamic>? parapets;
  @JsonKey(name: 'terraceUtilities')
  List<dynamic>? terraceUtilities;
  @JsonKey(name: 'fireTenderMovement')
  dynamic fireTenderMovement;
  @JsonKey(name: 'parapetWithColor')
  List<dynamic>? parapetWithColor;
  @JsonKey(name: 'parapetV2')
  dynamic parapetV2;
  @JsonKey(name: 'chimneyV2')
  dynamic chimneyV2;
  @JsonKey(name: 'porticos')
  List<dynamic>? porticos;
  @JsonKey(name: 'levelZeroSetBack')
  SetBack? levelZeroSetBack;
  @JsonKey(name: 'distanceBetweenBlocks')
  List<dynamic>? distanceBetweenBlocks;
  @JsonKey(name: 'daramps')
  List<dynamic>? daramps;

  Block();

  factory Block.fromJson(Map<String, dynamic> json) => _$BlockFromJson(json);

  Map<String, dynamic> toJson() => _$BlockToJson(this);
}

@JsonSerializable()
class Building {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  int? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'height')
  int? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  int? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'buildingHeight')
  double? buildingHeight;
  @JsonKey(name: 'buildingHeightAsMeasured')
  dynamic buildingHeightAsMeasured;
  @JsonKey(name: 'declaredBuildingHeight')
  double? declaredBuildingHeight;
  @JsonKey(name: 'heightIncreasedBy')
  dynamic heightIncreasedBy;
  @JsonKey(name: 'buildingTopMostHeight')
  dynamic buildingTopMostHeight;
  @JsonKey(name: 'totalFloorArea')
  double? totalFloorArea;
  @JsonKey(name: 'totalExistingFloorArea')
  int? totalExistingFloorArea;
  @JsonKey(name: 'exteriorWall')
  dynamic exteriorWall;
  @JsonKey(name: 'shade')
  dynamic shade;
  @JsonKey(name: 'far')
  dynamic far;
  @JsonKey(name: 'coverage')
  double? coverage;
  @JsonKey(name: 'coverageArea')
  double? coverageArea;
  @JsonKey(name: 'maxFloor')
  int? maxFloor;
  @JsonKey(name: 'totalFloors')
  int? totalFloors;
  @JsonKey(name: 'floors')
  List<Floor>? floors;
  @JsonKey(name: 'floorsAboveGround')
  int? floorsAboveGround;
  @JsonKey(name: 'distanceFromBuildingFootPrintToRoadEnd')
  List<dynamic>? distanceFromBuildingFootPrintToRoadEnd;
  @JsonKey(name: 'distanceFromSetBackToBuildingLine')
  List<dynamic>? distanceFromSetBackToBuildingLine;
  @JsonKey(name: 'totalBuitUpArea')
  double? totalBuitUpArea;
  @JsonKey(name: 'totalExistingBuiltUpArea')
  int? totalExistingBuiltUpArea;
  @JsonKey(name: 'mostRestrictiveOccupancy')
  dynamic mostRestrictiveOccupancy;
  @JsonKey(name: 'mostRestrictiveOccupancyType')
  dynamic mostRestrictiveOccupancyType;
  @JsonKey(name: 'mostRestrictiveFarHelper')
  MostRestrictiveFarHelper? mostRestrictiveFarHelper;
  @JsonKey(name: 'occupancies')
  List<OccupancyElement>? occupancies;
  @JsonKey(name: 'totalArea')
  List<OccupancyElement>? totalArea;
  @JsonKey(name: 'passage')
  dynamic passage;
  @JsonKey(name: 'headRoom')
  dynamic headRoom;
  @JsonKey(name: 'isHighRise')
  bool? isHighRise;
  @JsonKey(name: 'totalConstructedArea')
  int? totalConstructedArea;

  Building();

  factory Building.fromJson(Map<String, dynamic> json) =>
      _$BuildingFromJson(json);

  Map<String, dynamic> toJson() => _$BuildingToJson(this);
}

@JsonSerializable()
class Floor {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  int? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'height')
  int? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  int? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'occupancies')
  List<OccupancyElement>? occupancies;
  @JsonKey(name: 'convertedOccupancies')
  List<dynamic>? convertedOccupancies;
  @JsonKey(name: 'units')
  List<dynamic>? units;
  @JsonKey(name: 'daRooms')
  List<dynamic>? daRooms;
  @JsonKey(name: 'ramps')
  List<dynamic>? ramps;
  @JsonKey(name: 'vehicleRamps')
  List<dynamic>? vehicleRamps;
  @JsonKey(name: 'lifts')
  List<dynamic>? lifts;
  @JsonKey(name: 'daLifts')
  List<dynamic>? daLifts;
  @JsonKey(name: 'exterior')
  dynamic exterior;
  @JsonKey(name: 'specialWaterClosets')
  List<dynamic>? specialWaterClosets;
  @JsonKey(name: 'coverageDeduct')
  List<dynamic>? coverageDeduct;
  @JsonKey(name: 'number')
  int? number;
  @JsonKey(name: 'exitWidthDoor')
  List<dynamic>? exitWidthDoor;
  @JsonKey(name: 'exitWidthStair')
  List<dynamic>? exitWidthStair;
  @JsonKey(name: 'mezzanineFloor')
  List<dynamic>? mezzanineFloor;
  @JsonKey(name: 'halls')
  List<dynamic>? halls;
  @JsonKey(name: 'fireStairs')
  List<FireStair>? fireStairs;
  @JsonKey(name: 'generalStairs')
  List<ElectricLine>? generalStairs;
  @JsonKey(name: 'spiralStairs')
  List<Flight>? spiralStairs;
  @JsonKey(name: 'parking')
  Parking? parking;
  @JsonKey(name: 'floorHeights')
  List<dynamic>? floorHeights;
  @JsonKey(name: 'acRooms')
  List<dynamic>? acRooms;
  @JsonKey(name: 'regularRooms')
  List<dynamic>? regularRooms;
  @JsonKey(name: 'kitchen')
  dynamic kitchen;
  @JsonKey(name: 'bathRoom')
  BathRoom? bathRoom;
  @JsonKey(name: 'waterClosets')
  BathRoom? waterClosets;
  @JsonKey(name: 'bathRoomWaterClosets')
  BathRoom? bathRoomWaterClosets;
  @JsonKey(name: 'heightFromTheFloorToCeiling')
  dynamic heightFromTheFloorToCeiling;
  @JsonKey(name: 'heightOfTheCeilingOfUpperBasement')
  dynamic heightOfTheCeilingOfUpperBasement;
  @JsonKey(name: 'levelOfBasementUnderGround')
  dynamic levelOfBasementUnderGround;
  @JsonKey(name: 'interiorOpenSpace')
  InteriorOpenSpace? interiorOpenSpace;
  @JsonKey(name: 'verandah')
  LightAndVentilation? verandah;
  @JsonKey(name: 'lightAndVentilation')
  LightAndVentilation? lightAndVentilation;
  @JsonKey(name: 'roofAreas')
  List<dynamic>? roofAreas;
  @JsonKey(name: 'balconies')
  List<ElectricLine>? balconies;
  @JsonKey(name: 'overHangs')
  List<CuldeSacRoad>? overHangs;
  @JsonKey(name: 'constructedAreas')
  List<dynamic>? constructedAreas;
  @JsonKey(name: 'glassFacadeOpenings')
  List<dynamic>? glassFacadeOpenings;
  @JsonKey(name: 'doors')
  List<dynamic>? doors;
  @JsonKey(name: 'heightFromFloorToBottomOfBeam')
  List<dynamic>? heightFromFloorToBottomOfBeam;
  @JsonKey(name: 'washBasins')
  List<dynamic>? washBasins;
  @JsonKey(name: 'terrace')
  bool? terrace;

  Floor();

  factory Floor.fromJson(Map<String, dynamic> json) => _$FloorFromJson(json);

  Map<String, dynamic> toJson() => _$FloorToJson(this);
}

@JsonSerializable()
class ElectricLine {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  double? length;
  @JsonKey(name: 'width')
  double? width;
  @JsonKey(name: 'height')
  double? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  double? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'number')
  String? number;
  @JsonKey(name: 'builtUpArea')
  dynamic builtUpArea;
  @JsonKey(name: 'deductions')
  dynamic deductions;
  @JsonKey(name: 'measurements')
  List<CuldeSacRoad>? measurements;
  @JsonKey(name: 'widths')
  List<double>? widths;
  @JsonKey(name: 'landings')
  List<Landing>? landings;
  @JsonKey(name: 'landingClosed')
  bool? landingClosed;
  @JsonKey(name: 'lengths')
  List<double>? lengths;
  @JsonKey(name: 'stairMeasurements')
  List<CuldeSacRoad>? stairMeasurements;
  @JsonKey(name: 'flights')
  List<Flight>? flights;
  @JsonKey(name: 'floorHeight')
  double? floorHeight;
  @JsonKey(name: 'verticalDistance')
  dynamic verticalDistance;
  @JsonKey(name: 'horizontalDistance')
  double? horizontalDistance;
  @JsonKey(name: 'voltage')
  int? voltage;
  @JsonKey(name: 'radius')
  dynamic radius;
  @JsonKey(name: 'tankHeight')
  dynamic tankHeight;
  @JsonKey(name: 'tankCapacity')
  dynamic tankCapacity;

  ElectricLine();

  factory ElectricLine.fromJson(Map<String, dynamic> json) =>
      _$ElectricLineFromJson(json);

  Map<String, dynamic> toJson() => _$ElectricLineToJson(this);
}

@JsonSerializable()
class Flight {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  int? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'height')
  int? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  int? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'number')
  String? number;
  @JsonKey(name: 'noOfRises')
  int? noOfRises;
  @JsonKey(name: 'flights')
  List<CuldeSacRoad>? flights;
  @JsonKey(name: 'flightClosed')
  bool? flightClosed;
  @JsonKey(name: 'lengthOfFlights')
  List<int>? lengthOfFlights;
  @JsonKey(name: 'widthOfFlights')
  List<double>? widthOfFlights;
  @JsonKey(name: 'stairMeasurements')
  dynamic stairMeasurements;
  @JsonKey(name: 'landings')
  List<dynamic>? landings;
  @JsonKey(name: 'floorHeight')
  dynamic floorHeight;
  @JsonKey(name: 'circles')
  List<Circle>? circles;

  Flight();

  factory Flight.fromJson(Map<String, dynamic> json) => _$FlightFromJson(json);

  Map<String, dynamic> toJson() => _$FlightToJson(this);
}

@JsonSerializable()
class Circle {
  @JsonKey(name: 'radius')
  double? radius;

  Circle();

  factory Circle.fromJson(Map<String, dynamic> json) => _$CircleFromJson(json);

  Map<String, dynamic> toJson() => _$CircleToJson(this);
}

@JsonSerializable()
class CuldeSacRoad {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  double? minimumDistance;
  @JsonKey(name: 'minimumSide')
  double? minimumSide;
  @JsonKey(name: 'length')
  double? length;
  @JsonKey(name: 'width')
  double? width;
  @JsonKey(name: 'height')
  double? height;
  @JsonKey(name: 'mean')
  double? mean;
  @JsonKey(name: 'area')
  double? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'level')
  int? level;
  @JsonKey(name: 'shortestDistanceToRoad')
  List<dynamic>? shortestDistanceToRoad;
  @JsonKey(name: 'distancesFromCenterToPlot')
  List<dynamic>? distancesFromCenterToPlot;
  @JsonKey(name: 'distanceFromAccessoryBlock')
  List<dynamic>? distanceFromAccessoryBlock;
  @JsonKey(name: 'type')
  String? type;

  CuldeSacRoad();

  factory CuldeSacRoad.fromJson(Map<String, dynamic> json) =>
      _$CuldeSacRoadFromJson(json);

  Map<String, dynamic> toJson() => _$CuldeSacRoadToJson(this);
}

@JsonSerializable()
class Landing {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  double? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'height')
  double? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  double? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'number')
  String? number;
  @JsonKey(name: 'landings')
  List<CuldeSacRoad>? landings;
  @JsonKey(name: 'landingClosed')
  bool? landingClosed;
  @JsonKey(name: 'lengths')
  List<double>? lengths;
  @JsonKey(name: 'widths')
  List<double>? widths;

  Landing();

  factory Landing.fromJson(Map<String, dynamic> json) =>
      _$LandingFromJson(json);

  Map<String, dynamic> toJson() => _$LandingToJson(this);
}

@JsonSerializable()
class BathRoom {
  @JsonKey(name: 'number')
  dynamic number;
  @JsonKey(name: 'closed')
  bool? closed;
  @JsonKey(name: 'rooms')
  List<dynamic>? rooms;
  @JsonKey(name: 'lightAndVentilation')
  LightAndVentilation? lightAndVentilation;
  @JsonKey(name: 'mezzanineAreas')
  List<dynamic>? mezzanineAreas;
  @JsonKey(name: 'heights')
  List<dynamic>? heights;

  BathRoom();

  factory BathRoom.fromJson(Map<String, dynamic> json) =>
      _$BathRoomFromJson(json);

  Map<String, dynamic> toJson() => _$BathRoomToJson(this);
}

@JsonSerializable()
class LightAndVentilation {
  @JsonKey(name: 'heightOrDepth')
  List<dynamic>? heightOrDepth;
  @JsonKey(name: 'measurements')
  List<dynamic>? measurements;

  LightAndVentilation();

  factory LightAndVentilation.fromJson(Map<String, dynamic> json) =>
      _$LightAndVentilationFromJson(json);

  Map<String, dynamic> toJson() => _$LightAndVentilationToJson(this);
}

@JsonSerializable()
class FireStair {
  @JsonKey(name: 'name')
  dynamic name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  int? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'height')
  int? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  int? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'number')
  String? number;
  @JsonKey(name: 'stairMeasurements')
  List<CuldeSacRoad>? stairMeasurements;
  @JsonKey(name: 'flights')
  List<Flight>? flights;
  @JsonKey(name: 'landings')
  List<ElectricLine>? landings;
  @JsonKey(name: 'floorHeight')
  double? floorHeight;
  @JsonKey(name: 'generalStair')
  bool? generalStair;
  @JsonKey(name: 'abuttingBltUp')
  bool? abuttingBltUp;

  FireStair();

  factory FireStair.fromJson(Map<String, dynamic> json) =>
      _$FireStairFromJson(json);

  Map<String, dynamic> toJson() => _$FireStairToJson(this);
}

@JsonSerializable()
class InteriorOpenSpace {
  @JsonKey(name: 'ventilationShaft')
  LightAndVentilation? ventilationShaft;
  @JsonKey(name: 'innerCourtYard')
  LightAndVentilation? innerCourtYard;
  @JsonKey(name: 'outerCourtYard')
  LightAndVentilation? outerCourtYard;
  @JsonKey(name: 'sunkenCourtYard')
  LightAndVentilation? sunkenCourtYard;

  InteriorOpenSpace();

  factory InteriorOpenSpace.fromJson(Map<String, dynamic> json) =>
      _$InteriorOpenSpaceFromJson(json);

  Map<String, dynamic> toJson() => _$InteriorOpenSpaceToJson(this);
}

@JsonSerializable()
class OccupancyElement {
  @JsonKey(name: 'name')
  dynamic name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  int? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'height')
  int? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  int? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'typeHelper')
  MostRestrictiveFarHelper? typeHelper;
  @JsonKey(name: 'deduction')
  int? deduction;
  @JsonKey(name: 'builtUpArea')
  double? builtUpArea;
  @JsonKey(name: 'floorArea')
  double? floorArea;
  @JsonKey(name: 'carpetArea')
  double? carpetArea;
  @JsonKey(name: 'carpetAreaDeduction')
  int? carpetAreaDeduction;
  @JsonKey(name: 'existingBuiltUpArea')
  int? existingBuiltUpArea;
  @JsonKey(name: 'existingFloorArea')
  int? existingFloorArea;
  @JsonKey(name: 'existingCarpetArea')
  int? existingCarpetArea;
  @JsonKey(name: 'existingCarpetAreaDeduction')
  int? existingCarpetAreaDeduction;
  @JsonKey(name: 'existingDeduction')
  int? existingDeduction;
  @JsonKey(name: 'withAttachedBath')
  bool? withAttachedBath;
  @JsonKey(name: 'withOutAttachedBath')
  bool? withOutAttachedBath;
  @JsonKey(name: 'withDinningSpace')
  bool? withDinningSpace;
  @JsonKey(name: 'recreationalSpace')
  List<dynamic>? recreationalSpace;
  @JsonKey(name: 'mezzanineNumber')
  dynamic mezzanineNumber;
  @JsonKey(name: 'isMezzanine')
  bool? isMezzanine;

  OccupancyElement();

  factory OccupancyElement.fromJson(Map<String, dynamic> json) =>
      _$OccupancyElementFromJson(json);

  Map<String, dynamic> toJson() => _$OccupancyElementToJson(this);
}

@JsonSerializable()
class MostRestrictiveFarHelper {
  @JsonKey(name: 'type')
  Type? type;
  @JsonKey(name: 'subtype')
  Type? subtype;
  @JsonKey(name: 'usage')
  dynamic usage;
  @JsonKey(name: 'convertedType')
  dynamic convertedType;
  @JsonKey(name: 'convertedSubtype')
  dynamic convertedSubtype;
  @JsonKey(name: 'convertedUsage')
  dynamic convertedUsage;

  MostRestrictiveFarHelper();

  factory MostRestrictiveFarHelper.fromJson(Map<String, dynamic> json) =>
      _$MostRestrictiveFarHelperFromJson(json);

  Map<String, dynamic> toJson() => _$MostRestrictiveFarHelperToJson(this);
}

@JsonSerializable()
class Type {
  @JsonKey(name: 'color')
  int? color;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;

  Type();

  factory Type.fromJson(Map<String, dynamic> json) => _$TypeFromJson(json);

  Map<String, dynamic> toJson() => _$TypeToJson(this);
}

@JsonSerializable()
class Parking {
  @JsonKey(name: 'cars')
  List<dynamic>? cars;
  @JsonKey(name: 'openCars')
  List<CuldeSacRoad>? openCars;
  @JsonKey(name: 'coverCars')
  List<dynamic>? coverCars;
  @JsonKey(name: 'basementCars')
  List<dynamic>? basementCars;
  @JsonKey(name: 'visitors')
  List<dynamic>? visitors;
  @JsonKey(name: 'validCarParkingSlots')
  int? validCarParkingSlots;
  @JsonKey(name: 'validOpenCarSlots')
  int? validOpenCarSlots;
  @JsonKey(name: 'validCoverCarSlots')
  int? validCoverCarSlots;
  @JsonKey(name: 'validBasementCarSlots')
  int? validBasementCarSlots;
  @JsonKey(name: 'diningSeats')
  int? diningSeats;
  @JsonKey(name: 'loadUnload')
  List<dynamic>? loadUnload;
  @JsonKey(name: 'mechParking')
  List<dynamic>? mechParking;
  @JsonKey(name: 'twoWheelers')
  List<dynamic>? twoWheelers;
  @JsonKey(name: 'disabledPersons')
  List<dynamic>? disabledPersons;
  @JsonKey(name: 'validDAParkingSlots')
  int? validDAParkingSlots;
  @JsonKey(name: 'distFromDAToMainEntrance')
  int? distFromDAToMainEntrance;
  @JsonKey(name: 'special')
  List<dynamic>? special;
  @JsonKey(name: 'validSpecialSlots')
  int? validSpecialSlots;
  @JsonKey(name: 'stilts')
  List<dynamic>? stilts;
  @JsonKey(name: 'mechanicalLifts')
  List<dynamic>? mechanicalLifts;

  Parking();

  factory Parking.fromJson(Map<String, dynamic> json) =>
      _$ParkingFromJson(json);

  Map<String, dynamic> toJson() => _$ParkingToJson(this);
}

@JsonSerializable()
class SetBack {
  @JsonKey(name: 'frontYard')
  CuldeSacRoad? frontYard;
  @JsonKey(name: 'rearYard')
  CuldeSacRoad? rearYard;
  @JsonKey(name: 'sideYard1')
  CuldeSacRoad? sideYard1;
  @JsonKey(name: 'sideYard2')
  CuldeSacRoad? sideYard2;
  @JsonKey(name: 'level')
  int? level;
  @JsonKey(name: 'buildingFootPrint')
  CuldeSacRoad? buildingFootPrint;

  SetBack();

  factory SetBack.fromJson(Map<String, dynamic> json) =>
      _$SetBackFromJson(json);

  Map<String, dynamic> toJson() => _$SetBackToJson(this);
}

@JsonSerializable()
class SanityDetails {
  @JsonKey(name: 'maleWaterClosets')
  List<dynamic>? maleWaterClosets;
  @JsonKey(name: 'femaleWaterClosets')
  List<dynamic>? femaleWaterClosets;
  @JsonKey(name: 'commonWaterClosets')
  List<dynamic>? commonWaterClosets;
  @JsonKey(name: 'urinals')
  List<dynamic>? urinals;
  @JsonKey(name: 'maleBathRooms')
  List<dynamic>? maleBathRooms;
  @JsonKey(name: 'femaleBathRooms')
  List<dynamic>? femaleBathRooms;
  @JsonKey(name: 'commonBathRooms')
  List<dynamic>? commonBathRooms;
  @JsonKey(name: 'maleRoomsWithWaterCloset')
  List<dynamic>? maleRoomsWithWaterCloset;
  @JsonKey(name: 'femaleRoomsWithWaterCloset')
  List<dynamic>? femaleRoomsWithWaterCloset;
  @JsonKey(name: 'commonRoomsWithWaterCloset')
  List<dynamic>? commonRoomsWithWaterCloset;
  @JsonKey(name: 'drinkingWater')
  List<dynamic>? drinkingWater;
  @JsonKey(name: 'totalSpecialWC')
  List<dynamic>? totalSpecialWC;
  @JsonKey(name: 'totalSPWC')
  int? totalSPWC;
  @JsonKey(name: 'totalwashBasins')
  int? totalwashBasins;

  SanityDetails();

  factory SanityDetails.fromJson(Map<String, dynamic> json) =>
      _$SanityDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$SanityDetailsToJson(this);
}

@JsonSerializable()
class CompoundWall {
  @JsonKey(name: 'wallHeights')
  List<dynamic>? wallHeights;
  @JsonKey(name: 'railingHeights')
  List<dynamic>? railingHeights;

  CompoundWall();

  factory CompoundWall.fromJson(Map<String, dynamic> json) =>
      _$CompoundWallFromJson(json);

  Map<String, dynamic> toJson() => _$CompoundWallToJson(this);
}

@JsonSerializable()
class DistanceToExternalEntity {
  @JsonKey(name: 'monuments')
  List<double>? monuments;
  @JsonKey(name: 'govtBuildings')
  List<double>? govtBuildings;
  @JsonKey(name: 'rivers')
  List<dynamic>? rivers;
  @JsonKey(name: 'drinage')
  dynamic drinage;
  @JsonKey(name: 'footpath')
  dynamic footpath;

  DistanceToExternalEntity();

  factory DistanceToExternalEntity.fromJson(Map<String, dynamic> json) =>
      _$DistanceToExternalEntityFromJson(json);

  Map<String, dynamic> toJson() => _$DistanceToExternalEntityToJson(this);
}

@JsonSerializable()
class DrawingPreference {
  @JsonKey(name: 'northDirection')
  NorthDirection? northDirection;
  @JsonKey(name: 'locationPlans')
  List<CuldeSacRoad>? locationPlans;
  @JsonKey(name: 'uom')
  String? uom;

  DrawingPreference();

  factory DrawingPreference.fromJson(Map<String, dynamic> json) =>
      _$DrawingPreferenceFromJson(json);

  Map<String, dynamic> toJson() => _$DrawingPreferenceToJson(this);
}

@JsonSerializable()
class NorthDirection {
  @JsonKey(name: 'direction')
  String? direction;
  @JsonKey(name: 'directions')
  List<CuldeSacRoad>? directions;

  NorthDirection();

  factory NorthDirection.fromJson(Map<String, dynamic> json) =>
      _$NorthDirectionFromJson(json);

  Map<String, dynamic> toJson() => _$NorthDirectionToJson(this);
}

@JsonSerializable()
class Errors {
  Errors();

  factory Errors.fromJson(Map<String, dynamic> json) => _$ErrorsFromJson(json);

  Map<String, dynamic> toJson() => _$ErrorsToJson(this);
}

@JsonSerializable()
class FarDetails {
  @JsonKey(name: 'permissableFar')
  double? permissableFar;
  @JsonKey(name: 'providedFar')
  double? providedFar;

  FarDetails();

  factory FarDetails.fromJson(Map<String, dynamic> json) =>
      _$FarDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$FarDetailsToJson(this);
}

@JsonSerializable()
class Gate {
  @JsonKey(name: 'gates')
  List<dynamic>? gates;
  @JsonKey(name: 'heights')
  dynamic heights;

  Gate();

  factory Gate.fromJson(Map<String, dynamic> json) => _$GateFromJson(json);

  Map<String, dynamic> toJson() => _$GateToJson(this);
}

@JsonSerializable()
class GuardRoom {
  @JsonKey(name: 'guardRooms')
  List<dynamic>? guardRooms;
  @JsonKey(name: 'cabinHeights')
  List<dynamic>? cabinHeights;

  GuardRoom();

  factory GuardRoom.fromJson(Map<String, dynamic> json) =>
      _$GuardRoomFromJson(json);

  Map<String, dynamic> toJson() => _$GuardRoomToJson(this);
}

@JsonSerializable()
class PlanInfoProperties {
  @JsonKey(name: 'BUILDING_NEAR_GOVT_BLDG')
  String? buildingNearGovtBldg;
  @JsonKey(name: 'PROVISION_FOR_GREEN_BUILDINGS_AND_SUSTAINABILITY')
  String? provisionForGreenBuildingsAndSustainability;
  @JsonKey(name: 'LEASEHOLD_LAND')
  String? leaseholdLand;
  @JsonKey(name: 'NOC_RAILWAY')
  String? nocRailway;
  @JsonKey(name: 'ROAD_WIDTH')
  String? roadWidth;
  @JsonKey(name: 'NOC_IRRIGATION_DEPT')
  String? nocIrrigationDept;
  @JsonKey(name: 'NOC_FOR_CONSTRUCTION_NEAR_AIRPORT')
  String? nocForConstructionNearAirport;
  @JsonKey(name: 'NOC_FIRE_DEPT')
  String? nocFireDept;
  @JsonKey(name: 'EXISTING_FLOOR_AREA_TO_BE_DEMOLISHED_M2')
  String? existingFloorAreaToBeDemolishedM2;
  @JsonKey(name: 'AVG_PLOT_DEPTH')
  String? avgPlotDepth;
  @JsonKey(name: 'NOC_FOR_CONSTRUCTION_NEAR_DEFENCE_AERODOMES')
  String? nocForConstructionNearDefenceAerodomes;
  @JsonKey(name: 'NOC_FOR_CONSTRUCTION_NEAR_MONUMENT')
  String? nocForConstructionNearMonument;
  @JsonKey(name: 'BUILDING_NEAR_TO_RIVER')
  String? buildingNearToRiver;
  @JsonKey(name: 'DISTRICT')
  String? district;
  @JsonKey(name: 'NOC_COLLECTOR_GVT_LAND')
  String? nocCollectorGvtLand;
  @JsonKey(name: 'BARRIER_FREE_ACCESS_FOR_PHYSICALLY_CHALLENGED_PEOPLE')
  String? barrierFreeAccessForPhysicallyChallengedPeople;
  @JsonKey(name: 'NOC_STATE_ENV_IMPACT')
  String? nocStateEnvImpact;
  @JsonKey(name: 'KHATA_NO')
  String? khataNo;
  @JsonKey(name: 'MAUZA')
  String? mauza;
  @JsonKey(name: 'BUILDING_NEAR_MONUMENT')
  String? buildingNearMonument;
  @JsonKey(name: 'AREA_TYPE')
  String? areaType;
  @JsonKey(name: 'AVG_PLOT_WIDTH')
  String? avgPlotWidth;
  @JsonKey(name: 'LAND_USE_ZONE')
  String? landUseZone;
  @JsonKey(name: 'RWH_DECLARED')
  String? rwhDeclared;
  @JsonKey(name: 'PLOT_NO')
  String? plotNo;
  @JsonKey(name: 'PLOT_AREA_M2')
  String? plotAreaM2;

  PlanInfoProperties();

  factory PlanInfoProperties.fromJson(Map<String, dynamic> json) =>
      _$PlanInfoPropertiesFromJson(json);

  Map<String, dynamic> toJson() => _$PlanInfoPropertiesToJson(this);
}

@JsonSerializable()
class PlanInformation {
  @JsonKey(name: 'plotArea')
  int? plotArea;
  @JsonKey(name: 'ownerName')
  dynamic ownerName;
  @JsonKey(name: 'occupancy')
  String? occupancy;
  @JsonKey(name: 'serviceType')
  dynamic serviceType;
  @JsonKey(name: 'amenities')
  dynamic amenities;
  @JsonKey(name: 'architectInformation')
  dynamic architectInformation;
  @JsonKey(name: 'applicantName')
  String? applicantName;
  @JsonKey(name: 'crzZoneArea')
  bool? crzZoneArea;
  @JsonKey(name: 'demolitionArea')
  int? demolitionArea;
  @JsonKey(name: 'depthCutting')
  dynamic depthCutting;
  @JsonKey(name: 'governmentOrAidedSchool')
  dynamic governmentOrAidedSchool;
  @JsonKey(name: 'securityZone')
  bool? securityZone;
  @JsonKey(name: 'accessWidth')
  dynamic accessWidth;
  @JsonKey(name: 'noOfBeds')
  dynamic noOfBeds;
  @JsonKey(name: 'nocToAbutSideDesc')
  String? nocToAbutSideDesc;
  @JsonKey(name: 'nocToAbutRearDesc')
  String? nocToAbutRearDesc;
  @JsonKey(name: 'openingOnSide')
  bool? openingOnSide;
  @JsonKey(name: 'openingOnRear')
  bool? openingOnRear;
  @JsonKey(name: 'noOfSeats')
  int? noOfSeats;
  @JsonKey(name: 'noOfMechanicalParking')
  int? noOfMechanicalParking;
  @JsonKey(name: 'singleFamilyBuilding')
  dynamic singleFamilyBuilding;
  @JsonKey(name: 'reSurveyNo')
  dynamic reSurveyNo;
  @JsonKey(name: 'revenueWard')
  dynamic revenueWard;
  @JsonKey(name: 'desam')
  dynamic desam;
  @JsonKey(name: 'village')
  dynamic village;
  @JsonKey(name: 'landUseZone')
  String? landUseZone;
  @JsonKey(name: 'leaseHoldLand')
  String? leaseHoldLand;
  @JsonKey(name: 'roadWidth')
  int? roadWidth;
  @JsonKey(name: 'roadLength')
  int? roadLength;
  @JsonKey(name: 'typeOfArea')
  String? typeOfArea;
  @JsonKey(name: 'depthOfPlot')
  int? depthOfPlot;
  @JsonKey(name: 'widthOfPlot')
  int? widthOfPlot;
  @JsonKey(name: 'buildingNearMonument')
  String? buildingNearMonument;
  @JsonKey(name: 'buildingNearGovtBuilding')
  String? buildingNearGovtBuilding;
  @JsonKey(name: 'nocNearMonument')
  String? nocNearMonument;
  @JsonKey(name: 'nocNearAirport')
  String? nocNearAirport;
  @JsonKey(name: 'nocNearDefenceAerodomes')
  String? nocNearDefenceAerodomes;
  @JsonKey(name: 'nocStateEnvImpact')
  String? nocStateEnvImpact;
  @JsonKey(name: 'nocRailways')
  String? nocRailways;
  @JsonKey(name: 'nocCollectorGvtLand')
  String? nocCollectorGvtLand;
  @JsonKey(name: 'nocIrrigationDept')
  String? nocIrrigationDept;
  @JsonKey(name: 'nocFireDept')
  String? nocFireDept;
  @JsonKey(name: 'buildingNearToRiver')
  String? buildingNearToRiver;
  @JsonKey(name: 'barrierFreeAccessForPhyChlngdPpl')
  String? barrierFreeAccessForPhyChlngdPpl;
  @JsonKey(name: 'provisionsForGreenBuildingsAndSustainability')
  String? provisionsForGreenBuildingsAndSustainability;
  @JsonKey(name: 'fireProtectionAndFireSafetyRequirements')
  String? fireProtectionAndFireSafetyRequirements;
  @JsonKey(name: 'plotNo')
  String? plotNo;
  @JsonKey(name: 'khataNo')
  String? khataNo;
  @JsonKey(name: 'mauza')
  String? mauza;
  @JsonKey(name: 'district')
  String? district;
  @JsonKey(name: 'rwhDeclared')
  String? rwhDeclared;

  PlanInformation();

  factory PlanInformation.fromJson(Map<String, dynamic> json) =>
      _$PlanInformationFromJson(json);

  Map<String, dynamic> toJson() => _$PlanInformationToJson(this);
}

@JsonSerializable()
class Plantation {
  @JsonKey(name: 'plantations')
  List<dynamic>? plantations;

  Plantation();

  factory Plantation.fromJson(Map<String, dynamic> json) =>
      _$PlantationFromJson(json);

  Map<String, dynamic> toJson() => _$PlantationToJson(this);
}

@JsonSerializable()
class Plot {
  @JsonKey(name: 'name')
  dynamic name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  int? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'height')
  int? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  int? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'frontYard')
  dynamic frontYard;
  @JsonKey(name: 'rearYard')
  dynamic rearYard;
  @JsonKey(name: 'sideYard1')
  dynamic sideYard1;
  @JsonKey(name: 'sideYard2')
  dynamic sideYard2;
  @JsonKey(name: 'setBacks')
  List<dynamic>? setBacks;
  @JsonKey(name: 'buildingFootPrint')
  CuldeSacRoad? buildingFootPrint;
  @JsonKey(name: 'bsmtFrontYard')
  dynamic bsmtFrontYard;
  @JsonKey(name: 'bsmtRearYard')
  dynamic bsmtRearYard;
  @JsonKey(name: 'bsmtSideYard1')
  dynamic bsmtSideYard1;
  @JsonKey(name: 'bsmtSideYard2')
  dynamic bsmtSideYard2;
  @JsonKey(name: 'smallPlot')
  bool? smallPlot;
  @JsonKey(name: 'plotBndryArea')
  double? plotBndryArea;
  @JsonKey(name: 'levelZeroSetBack')
  dynamic levelZeroSetBack;

  Plot();

  factory Plot.fromJson(Map<String, dynamic> json) => _$PlotFromJson(json);

  Map<String, dynamic> toJson() => _$PlotToJson(this);
}

@JsonSerializable()
class ReportOutput {
  @JsonKey(name: 'scrutinyDetails')
  List<ScrutinyDetail>? scrutinyDetails;

  ReportOutput();

  factory ReportOutput.fromJson(Map<String, dynamic> json) =>
      _$ReportOutputFromJson(json);

  Map<String, dynamic> toJson() => _$ReportOutputToJson(this);
}

@JsonSerializable()
class ScrutinyDetail {
  @JsonKey(name: 'key')
  String? key;
  @JsonKey(name: 'heading')
  String? heading;
  @JsonKey(name: 'remarks')
  dynamic remarks;
  @JsonKey(name: 'subHeading')
  dynamic subHeading;
  @JsonKey(name: 'columnHeading')
  Map<String, ColumnHeading>? columnHeading;
  @JsonKey(name: 'detail')
  List<Detail>? detail;

  ScrutinyDetail();

  factory ScrutinyDetail.fromJson(Map<String, dynamic> json) =>
      _$ScrutinyDetailFromJson(json);

  Map<String, dynamic> toJson() => _$ScrutinyDetailToJson(this);
}

@JsonSerializable()
class ColumnHeading {
  @JsonKey(name: 'name')
  String? name;

  ColumnHeading();

  factory ColumnHeading.fromJson(Map<String, dynamic> json) =>
      _$ColumnHeadingFromJson(json);

  Map<String, dynamic> toJson() => _$ColumnHeadingToJson(this);
}

@JsonSerializable()
class Detail {
  @JsonKey(name: 'Status')
  String? status;
  @JsonKey(name: 'Area Type')
  String? areaType;
  @JsonKey(name: 'Occupancy')
  String? occupancy;
  @JsonKey(name: 'Permissible')
  String? permissible;
  @JsonKey(name: 'Byelaw')
  String? byelaw;
  @JsonKey(name: 'Provided')
  String? provided;
  @JsonKey(name: 'Road Width')
  String? roadWidth;
  @JsonKey(name: 'Field Verified')
  String? fieldVerified;
  @JsonKey(name: 'Setback')
  String? setback;
  @JsonKey(name: 'Level')
  String? level;
  @JsonKey(name: 'Description')
  String? description;
  @JsonKey(name: 'Required')
  String? required;
  @JsonKey(name: 'Permitted')
  String? permitted;
  @JsonKey(name: 'Distance')
  String? distance;
  @JsonKey(name: 'Floor')
  String? floor;
  @JsonKey(name: 'Remarks')
  String? remarks;
  @JsonKey(name: 'Voltage')
  String? voltage;

  Detail();

  factory Detail.fromJson(Map<String, dynamic> json) => _$DetailFromJson(json);

  Map<String, dynamic> toJson() => _$DetailToJson(this);
}

@JsonSerializable()
class SegregatedToilet {
  @JsonKey(name: 'segregatedToilets')
  List<dynamic>? segregatedToilets;
  @JsonKey(name: 'distancesToMainEntrance')
  List<dynamic>? distancesToMainEntrance;

  SegregatedToilet();

  factory SegregatedToilet.fromJson(Map<String, dynamic> json) =>
      _$SegregatedToiletFromJson(json);

  Map<String, dynamic> toJson() => _$SegregatedToiletToJson(this);
}

@JsonSerializable()
class Utility {
  @JsonKey(name: 'name')
  dynamic name;
  @JsonKey(name: 'presentInDxf')
  bool? presentInDxf;
  @JsonKey(name: 'minimumDistance')
  int? minimumDistance;
  @JsonKey(name: 'minimumSide')
  int? minimumSide;
  @JsonKey(name: 'length')
  int? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'height')
  int? height;
  @JsonKey(name: 'mean')
  int? mean;
  @JsonKey(name: 'area')
  int? area;
  @JsonKey(name: 'isValid')
  dynamic isValid;
  @JsonKey(name: 'colorCode')
  int? colorCode;
  @JsonKey(name: 'wasteDisposalUnits')
  List<CuldeSacRoad>? wasteDisposalUnits;
  @JsonKey(name: 'wasteWaterRecyclePlant')
  List<dynamic>? wasteWaterRecyclePlant;
  @JsonKey(name: 'liquidWasteTreatementPlant')
  List<dynamic>? liquidWasteTreatementPlant;
  @JsonKey(name: 'wells')
  List<CuldeSacRoad>? wells;
  @JsonKey(name: 'wellDistance')
  List<WellDistance>? wellDistance;
  @JsonKey(name: 'rainWaterHarvest')
  List<ElectricLine>? rainWaterHarvest;
  @JsonKey(name: 'solar')
  List<CuldeSacRoad>? solar;
  @JsonKey(name: 'rainWaterHarvestingTankCapacity')
  dynamic rainWaterHarvestingTankCapacity;
  @JsonKey(name: 'biometricWasteTreatment')
  List<dynamic>? biometricWasteTreatment;
  @JsonKey(name: 'solidLiqdWasteTrtmnt')
  List<dynamic>? solidLiqdWasteTrtmnt;
  @JsonKey(name: 'solarWaterHeatingSystems')
  List<CuldeSacRoad>? solarWaterHeatingSystems;
  @JsonKey(name: 'segregationOfWaste')
  List<CuldeSacRoad>? segregationOfWaste;
  @JsonKey(name: 'waterTankCapacity')
  int? waterTankCapacity;
  @JsonKey(name: 'supplyLine')
  dynamic supplyLine;

  Utility();

  factory Utility.fromJson(Map<String, dynamic> json) =>
      _$UtilityFromJson(json);

  Map<String, dynamic> toJson() => _$UtilityToJson(this);
}

@JsonSerializable()
class WellDistance {
  @JsonKey(name: 'colourCode')
  String? colourCode;
  @JsonKey(name: 'roadDistainceToPlot')
  dynamic roadDistainceToPlot;
  @JsonKey(name: 'distance')
  double? distance;

  WellDistance();

  factory WellDistance.fromJson(Map<String, dynamic> json) =>
      _$WellDistanceFromJson(json);

  Map<String, dynamic> toJson() => _$WellDistanceToJson(this);
}

@JsonSerializable()
class VirtualBuilding {
  @JsonKey(name: 'buildingHeight')
  dynamic buildingHeight;
  @JsonKey(name: 'occupancies')
  List<dynamic>? occupancies;
  @JsonKey(name: 'occupancyTypes')
  List<MostRestrictiveFarHelper>? occupancyTypes;
  @JsonKey(name: 'totalBuitUpArea')
  double? totalBuitUpArea;
  @JsonKey(name: 'totalFloorArea')
  double? totalFloorArea;
  @JsonKey(name: 'totalCarpetArea')
  double? totalCarpetArea;
  @JsonKey(name: 'totalExistingBuiltUpArea')
  int? totalExistingBuiltUpArea;
  @JsonKey(name: 'totalExistingFloorArea')
  int? totalExistingFloorArea;
  @JsonKey(name: 'totalExistingCarpetArea')
  int? totalExistingCarpetArea;
  @JsonKey(name: 'mostRestrictiveFar')
  dynamic mostRestrictiveFar;
  @JsonKey(name: 'mostRestrictiveCoverage')
  dynamic mostRestrictiveCoverage;
  @JsonKey(name: 'mostRestrictiveFarHelper')
  MostRestrictiveFarHelper? mostRestrictiveFarHelper;
  @JsonKey(name: 'mostRestrictiveCoverageHelper')
  dynamic mostRestrictiveCoverageHelper;
  @JsonKey(name: 'floorsAboveGround')
  dynamic floorsAboveGround;
  @JsonKey(name: 'totalCoverageArea')
  double? totalCoverageArea;
  @JsonKey(name: 'residentialOrCommercialBuilding')
  bool? residentialOrCommercialBuilding;
  @JsonKey(name: 'residentialBuilding')
  bool? residentialBuilding;
  @JsonKey(name: 'totalConstructedArea')
  int? totalConstructedArea;

  VirtualBuilding();

  factory VirtualBuilding.fromJson(Map<String, dynamic> json) =>
      _$VirtualBuildingFromJson(json);

  Map<String, dynamic> toJson() => _$VirtualBuildingToJson(this);
}

@JsonSerializable()
class ResponseInfo {
  @JsonKey(name: 'apiId')
  String? apiId;
  @JsonKey(name: 'ver')
  dynamic ver;
  @JsonKey(name: 'ts')
  dynamic ts;
  @JsonKey(name: 'resMsgId')
  String? resMsgId;
  @JsonKey(name: 'msgId')
  String? msgId;
  @JsonKey(name: 'status')
  String? status;

  ResponseInfo();

  factory ResponseInfo.fromJson(Map<String, dynamic> json) =>
      _$ResponseInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ResponseInfoToJson(this);
}
