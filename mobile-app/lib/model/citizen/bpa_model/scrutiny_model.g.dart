// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'scrutiny_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Scrutiny _$ScrutinyFromJson(Map<String, dynamic> json) => Scrutiny()
  ..responseInfo = json['responseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['responseInfo'] as Map<String, dynamic>)
  ..edcrDetail = (json['edcrDetail'] as List<dynamic>?)
      ?.map((e) => EdcrDetail.fromJson(e as Map<String, dynamic>))
      .toList()
  ..count = (json['count'] as num?)?.toInt();

Map<String, dynamic> _$ScrutinyToJson(Scrutiny instance) => <String, dynamic>{
      'responseInfo': instance.responseInfo,
      'edcrDetail': instance.edcrDetail,
      'count': instance.count,
    };

EdcrDetail _$EdcrDetailFromJson(Map<String, dynamic> json) => EdcrDetail()
  ..dxfFile = json['dxfFile'] as String?
  ..updatedDxfFile = json['updatedDxfFile'] as String?
  ..planReport = json['planReport'] as String?
  ..transactionNumber = json['transactionNumber'] as String?
  ..applicationDate = json['applicationDate']
  ..applicationNumber = json['applicationNumber'] as String?
  ..status = json['status'] as String?
  ..edcrNumber = json['edcrNumber'] as String?
  ..tenantId = json['tenantId'] as String?
  ..errors = json['errors'] as String?
  ..planPdfs = json['planPdfs'] as List<dynamic>?
  ..planDetail = json['planDetail'] == null
      ? null
      : PlanDetail.fromJson(json['planDetail'] as Map<String, dynamic>)
  ..permitNumber = json['permitNumber'] as String?
  ..permitDate = json['permitDate'] as String?
  ..appliactionType = json['appliactionType'] as String?
  ..applicationSubType = json['applicationSubType'] as String?
  ..comparisonEdcrNumber = json['comparisonEdcrNumber'] as String?;

Map<String, dynamic> _$EdcrDetailToJson(EdcrDetail instance) =>
    <String, dynamic>{
      'dxfFile': instance.dxfFile,
      'updatedDxfFile': instance.updatedDxfFile,
      'planReport': instance.planReport,
      'transactionNumber': instance.transactionNumber,
      'applicationDate': instance.applicationDate,
      'applicationNumber': instance.applicationNumber,
      'status': instance.status,
      'edcrNumber': instance.edcrNumber,
      'tenantId': instance.tenantId,
      'errors': instance.errors,
      'planPdfs': instance.planPdfs,
      'planDetail': instance.planDetail,
      'permitNumber': instance.permitNumber,
      'permitDate': instance.permitDate,
      'appliactionType': instance.appliactionType,
      'applicationSubType': instance.applicationSubType,
      'comparisonEdcrNumber': instance.comparisonEdcrNumber,
    };

PlanDetail _$PlanDetailFromJson(Map<String, dynamic> json) => PlanDetail()
  ..planInfoProperties = json['planInfoProperties'] == null
      ? null
      : PlanInfoProperties.fromJson(
          json['planInfoProperties'] as Map<String, dynamic>)
  ..edcrPassed = json['edcrPassed'] as bool?
  ..applicationDate = (json['applicationDate'] as num?)?.toInt()
  ..asOnDate = json['asOnDate']
  ..planInformation = json['planInformation'] == null
      ? null
      : PlanInformation.fromJson(
          json['planInformation'] as Map<String, dynamic>)
  ..plot = json['plot'] == null
      ? null
      : Plot.fromJson(json['plot'] as Map<String, dynamic>)
  ..blocks = (json['blocks'] as List<dynamic>?)
      ?.map((e) => Block.fromJson(e as Map<String, dynamic>))
      .toList()
  ..accessoryBlocks = json['accessoryBlocks'] as List<dynamic>?
  ..accessoryBlockDistances = json['accessoryBlockDistances'] as List<dynamic>?
  ..virtualBuilding = json['virtualBuilding'] == null
      ? null
      : VirtualBuilding.fromJson(
          json['virtualBuilding'] as Map<String, dynamic>)
  ..electricLine = (json['electricLine'] as List<dynamic>?)
      ?.map((e) => ElectricLine.fromJson(e as Map<String, dynamic>))
      .toList()
  ..nonNotifiedRoads = json['nonNotifiedRoads'] as List<dynamic>?
  ..notifiedRoads = json['notifiedRoads'] as List<dynamic>?
  ..culdeSacRoads = (json['culdeSacRoads'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..laneRoads = json['laneRoads'] as List<dynamic>?
  ..travelDistancesToExit = json['travelDistancesToExit'] as List<dynamic>?
  ..parkingDetails = json['parkingDetails'] == null
      ? null
      : Parking.fromJson(json['parkingDetails'] as Map<String, dynamic>)
  ..canopyDistanceFromPlotBoundary =
      json['canopyDistanceFromPlotBoundary'] as List<dynamic>?
  ..occupancies = (json['occupancies'] as List<dynamic>?)
      ?.map((e) => OccupancyElement.fromJson(e as Map<String, dynamic>))
      .toList()
  ..utility = json['utility'] == null
      ? null
      : Utility.fromJson(json['utility'] as Map<String, dynamic>)
  ..coverage = (json['coverage'] as num?)?.toDouble()
  ..farDetails = json['farDetails'] == null
      ? null
      : FarDetails.fromJson(json['farDetails'] as Map<String, dynamic>)
  ..drawingPreference = json['drawingPreference'] == null
      ? null
      : DrawingPreference.fromJson(
          json['drawingPreference'] as Map<String, dynamic>)
  ..septicTanks = json['septicTanks'] as List<dynamic>?
  ..plantation = json['plantation'] == null
      ? null
      : Plantation.fromJson(json['plantation'] as Map<String, dynamic>)
  ..guardRoom = json['guardRoom'] == null
      ? null
      : GuardRoom.fromJson(json['guardRoom'] as Map<String, dynamic>)
  ..segregatedToilet = json['segregatedToilet'] == null
      ? null
      : SegregatedToilet.fromJson(
          json['segregatedToilet'] as Map<String, dynamic>)
  ..surrenderRoads = (json['surrenderRoads'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..totalSurrenderRoadArea =
      (json['totalSurrenderRoadArea'] as num?)?.toDouble()
  ..distanceToExternalEntity = json['distanceToExternalEntity'] == null
      ? null
      : DistanceToExternalEntity.fromJson(
          json['distanceToExternalEntity'] as Map<String, dynamic>)
  ..compoundWall = json['compoundWall'] == null
      ? null
      : CompoundWall.fromJson(json['compoundWall'] as Map<String, dynamic>)
  ..roadReserves = json['roadReserves'] as List<dynamic>?
  ..edcrPdfDetails = json['edcrPdfDetails']
  ..gate = json['gate'] == null
      ? null
      : Gate.fromJson(json['gate'] as Map<String, dynamic>)
  ..errors = json['errors'] == null
      ? null
      : Errors.fromJson(json['errors'] as Map<String, dynamic>)
  ..reportOutput = json['reportOutput'] == null
      ? null
      : ReportOutput.fromJson(json['reportOutput'] as Map<String, dynamic>)
  ..noObjectionCertificates = json['noObjectionCertificates'] == null
      ? null
      : Errors.fromJson(json['noObjectionCertificates'] as Map<String, dynamic>)
  ..featureAmendments = json['featureAmendments'] == null
      ? null
      : Errors.fromJson(json['featureAmendments'] as Map<String, dynamic>)
  ..mdmsMasterData = json['mdmsMasterData'] == null
      ? null
      : Errors.fromJson(json['mdmsMasterData'] as Map<String, dynamic>)
  ..mainDcrPassed = json['mainDcrPassed'] as bool?
  ..icts = json['icts'] as List<dynamic>?
  ..depthCuttings = json['depthCuttings'] as List<dynamic>?;

Map<String, dynamic> _$PlanDetailToJson(PlanDetail instance) =>
    <String, dynamic>{
      'planInfoProperties': instance.planInfoProperties,
      'edcrPassed': instance.edcrPassed,
      'applicationDate': instance.applicationDate,
      'asOnDate': instance.asOnDate,
      'planInformation': instance.planInformation,
      'plot': instance.plot,
      'blocks': instance.blocks,
      'accessoryBlocks': instance.accessoryBlocks,
      'accessoryBlockDistances': instance.accessoryBlockDistances,
      'virtualBuilding': instance.virtualBuilding,
      'electricLine': instance.electricLine,
      'nonNotifiedRoads': instance.nonNotifiedRoads,
      'notifiedRoads': instance.notifiedRoads,
      'culdeSacRoads': instance.culdeSacRoads,
      'laneRoads': instance.laneRoads,
      'travelDistancesToExit': instance.travelDistancesToExit,
      'parkingDetails': instance.parkingDetails,
      'canopyDistanceFromPlotBoundary': instance.canopyDistanceFromPlotBoundary,
      'occupancies': instance.occupancies,
      'utility': instance.utility,
      'coverage': instance.coverage,
      'farDetails': instance.farDetails,
      'drawingPreference': instance.drawingPreference,
      'septicTanks': instance.septicTanks,
      'plantation': instance.plantation,
      'guardRoom': instance.guardRoom,
      'segregatedToilet': instance.segregatedToilet,
      'surrenderRoads': instance.surrenderRoads,
      'totalSurrenderRoadArea': instance.totalSurrenderRoadArea,
      'distanceToExternalEntity': instance.distanceToExternalEntity,
      'compoundWall': instance.compoundWall,
      'roadReserves': instance.roadReserves,
      'edcrPdfDetails': instance.edcrPdfDetails,
      'gate': instance.gate,
      'errors': instance.errors,
      'reportOutput': instance.reportOutput,
      'noObjectionCertificates': instance.noObjectionCertificates,
      'featureAmendments': instance.featureAmendments,
      'mdmsMasterData': instance.mdmsMasterData,
      'mainDcrPassed': instance.mainDcrPassed,
      'icts': instance.icts,
      'depthCuttings': instance.depthCuttings,
    };

Block _$BlockFromJson(Map<String, dynamic> json) => Block()
  ..name = json['name'] as String?
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
  ..minimumSide = (json['minimumSide'] as num?)?.toInt()
  ..length = (json['length'] as num?)?.toInt()
  ..width = (json['width'] as num?)?.toInt()
  ..height = (json['height'] as num?)?.toDouble()
  ..mean = (json['mean'] as num?)?.toInt()
  ..area = (json['area'] as num?)?.toInt()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..building = json['building'] == null
      ? null
      : Building.fromJson(json['building'] as Map<String, dynamic>)
  ..number = json['number'] as String?
  ..numberOfLifts = json['numberOfLifts'] as String?
  ..setBacks = (json['setBacks'] as List<dynamic>?)
      ?.map((e) => SetBack.fromJson(e as Map<String, dynamic>))
      .toList()
  ..coverage = (json['coverage'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..coverageDeductions = json['coverageDeductions'] as List<dynamic>?
  ..typicalFloor = json['typicalFloor'] as List<dynamic>?
  ..disBetweenBlocks = json['disBetweenBlocks'] as List<dynamic>?
  ..hallAreas = json['hallAreas'] as List<dynamic>?
  ..diningSpaces = json['diningSpaces'] as List<dynamic>?
  ..sanityDetails = json['sanityDetails'] == null
      ? null
      : SanityDetails.fromJson(json['sanityDetails'] as Map<String, dynamic>)
  ..singleFamilyBuilding = json['singleFamilyBuilding'] as bool?
  ..residentialBuilding = json['residentialBuilding'] as bool?
  ..residentialOrCommercialBuilding =
      json['residentialOrCommercialBuilding'] as bool?
  ..highRiseBuilding = json['highRiseBuilding'] as bool?
  ..completelyExisting = json['completelyExisting'] as bool?
  ..openStairs = json['openStairs'] as List<dynamic>?
  ..plinthHeight = (json['plinthHeight'] as List<dynamic>?)
      ?.map((e) => (e as num).toDouble())
      .toList()
  ..interiorCourtYard = (json['interiorCourtYard'] as List<dynamic>?)
      ?.map((e) => (e as num).toDouble())
      .toList()
  ..protectedBalconies = json['protectedBalconies'] as List<dynamic>?
  ..plantationGreenStripes = json['plantationGreenStripes'] as List<dynamic>?
  ..roofTanks = json['roofTanks'] as List<dynamic>?
  ..stairCovers = json['stairCovers'] as List<dynamic>?
  ..chimneys = json['chimneys'] as List<dynamic>?
  ..parapets = json['parapets'] as List<dynamic>?
  ..terraceUtilities = json['terraceUtilities'] as List<dynamic>?
  ..fireTenderMovement = json['fireTenderMovement']
  ..parapetWithColor = json['parapetWithColor'] as List<dynamic>?
  ..parapetV2 = json['parapetV2']
  ..chimneyV2 = json['chimneyV2']
  ..porticos = json['porticos'] as List<dynamic>?
  ..levelZeroSetBack = json['levelZeroSetBack'] == null
      ? null
      : SetBack.fromJson(json['levelZeroSetBack'] as Map<String, dynamic>)
  ..distanceBetweenBlocks = json['distanceBetweenBlocks'] as List<dynamic>?
  ..daramps = json['daramps'] as List<dynamic>?;

Map<String, dynamic> _$BlockToJson(Block instance) => <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'building': instance.building,
      'number': instance.number,
      'numberOfLifts': instance.numberOfLifts,
      'setBacks': instance.setBacks,
      'coverage': instance.coverage,
      'coverageDeductions': instance.coverageDeductions,
      'typicalFloor': instance.typicalFloor,
      'disBetweenBlocks': instance.disBetweenBlocks,
      'hallAreas': instance.hallAreas,
      'diningSpaces': instance.diningSpaces,
      'sanityDetails': instance.sanityDetails,
      'singleFamilyBuilding': instance.singleFamilyBuilding,
      'residentialBuilding': instance.residentialBuilding,
      'residentialOrCommercialBuilding':
          instance.residentialOrCommercialBuilding,
      'highRiseBuilding': instance.highRiseBuilding,
      'completelyExisting': instance.completelyExisting,
      'openStairs': instance.openStairs,
      'plinthHeight': instance.plinthHeight,
      'interiorCourtYard': instance.interiorCourtYard,
      'protectedBalconies': instance.protectedBalconies,
      'plantationGreenStripes': instance.plantationGreenStripes,
      'roofTanks': instance.roofTanks,
      'stairCovers': instance.stairCovers,
      'chimneys': instance.chimneys,
      'parapets': instance.parapets,
      'terraceUtilities': instance.terraceUtilities,
      'fireTenderMovement': instance.fireTenderMovement,
      'parapetWithColor': instance.parapetWithColor,
      'parapetV2': instance.parapetV2,
      'chimneyV2': instance.chimneyV2,
      'porticos': instance.porticos,
      'levelZeroSetBack': instance.levelZeroSetBack,
      'distanceBetweenBlocks': instance.distanceBetweenBlocks,
      'daramps': instance.daramps,
    };

Building _$BuildingFromJson(Map<String, dynamic> json) => Building()
  ..name = json['name'] as String?
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
  ..minimumSide = (json['minimumSide'] as num?)?.toInt()
  ..length = (json['length'] as num?)?.toInt()
  ..width = (json['width'] as num?)?.toInt()
  ..height = (json['height'] as num?)?.toInt()
  ..mean = (json['mean'] as num?)?.toInt()
  ..area = (json['area'] as num?)?.toInt()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..buildingHeight = (json['buildingHeight'] as num?)?.toDouble()
  ..buildingHeightAsMeasured = json['buildingHeightAsMeasured']
  ..declaredBuildingHeight =
      (json['declaredBuildingHeight'] as num?)?.toDouble()
  ..heightIncreasedBy = json['heightIncreasedBy']
  ..buildingTopMostHeight = json['buildingTopMostHeight']
  ..totalFloorArea = (json['totalFloorArea'] as num?)?.toDouble()
  ..totalExistingFloorArea = (json['totalExistingFloorArea'] as num?)?.toInt()
  ..exteriorWall = json['exteriorWall']
  ..shade = json['shade']
  ..far = json['far']
  ..coverage = (json['coverage'] as num?)?.toDouble()
  ..coverageArea = (json['coverageArea'] as num?)?.toDouble()
  ..maxFloor = (json['maxFloor'] as num?)?.toInt()
  ..totalFloors = (json['totalFloors'] as num?)?.toInt()
  ..floors = (json['floors'] as List<dynamic>?)
      ?.map((e) => Floor.fromJson(e as Map<String, dynamic>))
      .toList()
  ..floorsAboveGround = (json['floorsAboveGround'] as num?)?.toInt()
  ..distanceFromBuildingFootPrintToRoadEnd =
      json['distanceFromBuildingFootPrintToRoadEnd'] as List<dynamic>?
  ..distanceFromSetBackToBuildingLine =
      json['distanceFromSetBackToBuildingLine'] as List<dynamic>?
  ..totalBuitUpArea = (json['totalBuitUpArea'] as num?)?.toDouble()
  ..totalExistingBuiltUpArea =
      (json['totalExistingBuiltUpArea'] as num?)?.toInt()
  ..mostRestrictiveOccupancy = json['mostRestrictiveOccupancy']
  ..mostRestrictiveOccupancyType = json['mostRestrictiveOccupancyType']
  ..mostRestrictiveFarHelper = json['mostRestrictiveFarHelper'] == null
      ? null
      : MostRestrictiveFarHelper.fromJson(
          json['mostRestrictiveFarHelper'] as Map<String, dynamic>)
  ..occupancies = (json['occupancies'] as List<dynamic>?)
      ?.map((e) => OccupancyElement.fromJson(e as Map<String, dynamic>))
      .toList()
  ..totalArea = (json['totalArea'] as List<dynamic>?)
      ?.map((e) => OccupancyElement.fromJson(e as Map<String, dynamic>))
      .toList()
  ..passage = json['passage']
  ..headRoom = json['headRoom']
  ..isHighRise = json['isHighRise'] as bool?
  ..totalConstructedArea = (json['totalConstructedArea'] as num?)?.toInt();

Map<String, dynamic> _$BuildingToJson(Building instance) => <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'buildingHeight': instance.buildingHeight,
      'buildingHeightAsMeasured': instance.buildingHeightAsMeasured,
      'declaredBuildingHeight': instance.declaredBuildingHeight,
      'heightIncreasedBy': instance.heightIncreasedBy,
      'buildingTopMostHeight': instance.buildingTopMostHeight,
      'totalFloorArea': instance.totalFloorArea,
      'totalExistingFloorArea': instance.totalExistingFloorArea,
      'exteriorWall': instance.exteriorWall,
      'shade': instance.shade,
      'far': instance.far,
      'coverage': instance.coverage,
      'coverageArea': instance.coverageArea,
      'maxFloor': instance.maxFloor,
      'totalFloors': instance.totalFloors,
      'floors': instance.floors,
      'floorsAboveGround': instance.floorsAboveGround,
      'distanceFromBuildingFootPrintToRoadEnd':
          instance.distanceFromBuildingFootPrintToRoadEnd,
      'distanceFromSetBackToBuildingLine':
          instance.distanceFromSetBackToBuildingLine,
      'totalBuitUpArea': instance.totalBuitUpArea,
      'totalExistingBuiltUpArea': instance.totalExistingBuiltUpArea,
      'mostRestrictiveOccupancy': instance.mostRestrictiveOccupancy,
      'mostRestrictiveOccupancyType': instance.mostRestrictiveOccupancyType,
      'mostRestrictiveFarHelper': instance.mostRestrictiveFarHelper,
      'occupancies': instance.occupancies,
      'totalArea': instance.totalArea,
      'passage': instance.passage,
      'headRoom': instance.headRoom,
      'isHighRise': instance.isHighRise,
      'totalConstructedArea': instance.totalConstructedArea,
    };

Floor _$FloorFromJson(Map<String, dynamic> json) => Floor()
  ..name = json['name'] as String?
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
  ..minimumSide = (json['minimumSide'] as num?)?.toInt()
  ..length = (json['length'] as num?)?.toInt()
  ..width = (json['width'] as num?)?.toInt()
  ..height = (json['height'] as num?)?.toInt()
  ..mean = (json['mean'] as num?)?.toInt()
  ..area = (json['area'] as num?)?.toInt()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..occupancies = (json['occupancies'] as List<dynamic>?)
      ?.map((e) => OccupancyElement.fromJson(e as Map<String, dynamic>))
      .toList()
  ..convertedOccupancies = json['convertedOccupancies'] as List<dynamic>?
  ..units = json['units'] as List<dynamic>?
  ..daRooms = json['daRooms'] as List<dynamic>?
  ..ramps = json['ramps'] as List<dynamic>?
  ..vehicleRamps = json['vehicleRamps'] as List<dynamic>?
  ..lifts = json['lifts'] as List<dynamic>?
  ..daLifts = json['daLifts'] as List<dynamic>?
  ..exterior = json['exterior']
  ..specialWaterClosets = json['specialWaterClosets'] as List<dynamic>?
  ..coverageDeduct = json['coverageDeduct'] as List<dynamic>?
  ..number = (json['number'] as num?)?.toInt()
  ..exitWidthDoor = json['exitWidthDoor'] as List<dynamic>?
  ..exitWidthStair = json['exitWidthStair'] as List<dynamic>?
  ..mezzanineFloor = json['mezzanineFloor'] as List<dynamic>?
  ..halls = json['halls'] as List<dynamic>?
  ..fireStairs = (json['fireStairs'] as List<dynamic>?)
      ?.map((e) => FireStair.fromJson(e as Map<String, dynamic>))
      .toList()
  ..generalStairs = (json['generalStairs'] as List<dynamic>?)
      ?.map((e) => ElectricLine.fromJson(e as Map<String, dynamic>))
      .toList()
  ..spiralStairs = (json['spiralStairs'] as List<dynamic>?)
      ?.map((e) => Flight.fromJson(e as Map<String, dynamic>))
      .toList()
  ..parking = json['parking'] == null
      ? null
      : Parking.fromJson(json['parking'] as Map<String, dynamic>)
  ..floorHeights = json['floorHeights'] as List<dynamic>?
  ..acRooms = json['acRooms'] as List<dynamic>?
  ..regularRooms = json['regularRooms'] as List<dynamic>?
  ..kitchen = json['kitchen']
  ..bathRoom = json['bathRoom'] == null
      ? null
      : BathRoom.fromJson(json['bathRoom'] as Map<String, dynamic>)
  ..waterClosets = json['waterClosets'] == null
      ? null
      : BathRoom.fromJson(json['waterClosets'] as Map<String, dynamic>)
  ..bathRoomWaterClosets = json['bathRoomWaterClosets'] == null
      ? null
      : BathRoom.fromJson(json['bathRoomWaterClosets'] as Map<String, dynamic>)
  ..heightFromTheFloorToCeiling = json['heightFromTheFloorToCeiling']
  ..heightOfTheCeilingOfUpperBasement =
      json['heightOfTheCeilingOfUpperBasement']
  ..levelOfBasementUnderGround = json['levelOfBasementUnderGround']
  ..interiorOpenSpace = json['interiorOpenSpace'] == null
      ? null
      : InteriorOpenSpace.fromJson(
          json['interiorOpenSpace'] as Map<String, dynamic>)
  ..verandah = json['verandah'] == null
      ? null
      : LightAndVentilation.fromJson(json['verandah'] as Map<String, dynamic>)
  ..lightAndVentilation = json['lightAndVentilation'] == null
      ? null
      : LightAndVentilation.fromJson(
          json['lightAndVentilation'] as Map<String, dynamic>)
  ..roofAreas = json['roofAreas'] as List<dynamic>?
  ..balconies = (json['balconies'] as List<dynamic>?)
      ?.map((e) => ElectricLine.fromJson(e as Map<String, dynamic>))
      .toList()
  ..overHangs = (json['overHangs'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..constructedAreas = json['constructedAreas'] as List<dynamic>?
  ..glassFacadeOpenings = json['glassFacadeOpenings'] as List<dynamic>?
  ..doors = json['doors'] as List<dynamic>?
  ..heightFromFloorToBottomOfBeam =
      json['heightFromFloorToBottomOfBeam'] as List<dynamic>?
  ..washBasins = json['washBasins'] as List<dynamic>?
  ..terrace = json['terrace'] as bool?;

Map<String, dynamic> _$FloorToJson(Floor instance) => <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'occupancies': instance.occupancies,
      'convertedOccupancies': instance.convertedOccupancies,
      'units': instance.units,
      'daRooms': instance.daRooms,
      'ramps': instance.ramps,
      'vehicleRamps': instance.vehicleRamps,
      'lifts': instance.lifts,
      'daLifts': instance.daLifts,
      'exterior': instance.exterior,
      'specialWaterClosets': instance.specialWaterClosets,
      'coverageDeduct': instance.coverageDeduct,
      'number': instance.number,
      'exitWidthDoor': instance.exitWidthDoor,
      'exitWidthStair': instance.exitWidthStair,
      'mezzanineFloor': instance.mezzanineFloor,
      'halls': instance.halls,
      'fireStairs': instance.fireStairs,
      'generalStairs': instance.generalStairs,
      'spiralStairs': instance.spiralStairs,
      'parking': instance.parking,
      'floorHeights': instance.floorHeights,
      'acRooms': instance.acRooms,
      'regularRooms': instance.regularRooms,
      'kitchen': instance.kitchen,
      'bathRoom': instance.bathRoom,
      'waterClosets': instance.waterClosets,
      'bathRoomWaterClosets': instance.bathRoomWaterClosets,
      'heightFromTheFloorToCeiling': instance.heightFromTheFloorToCeiling,
      'heightOfTheCeilingOfUpperBasement':
          instance.heightOfTheCeilingOfUpperBasement,
      'levelOfBasementUnderGround': instance.levelOfBasementUnderGround,
      'interiorOpenSpace': instance.interiorOpenSpace,
      'verandah': instance.verandah,
      'lightAndVentilation': instance.lightAndVentilation,
      'roofAreas': instance.roofAreas,
      'balconies': instance.balconies,
      'overHangs': instance.overHangs,
      'constructedAreas': instance.constructedAreas,
      'glassFacadeOpenings': instance.glassFacadeOpenings,
      'doors': instance.doors,
      'heightFromFloorToBottomOfBeam': instance.heightFromFloorToBottomOfBeam,
      'washBasins': instance.washBasins,
      'terrace': instance.terrace,
    };

ElectricLine _$ElectricLineFromJson(Map<String, dynamic> json) => ElectricLine()
  ..name = json['name'] as String?
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
  ..minimumSide = (json['minimumSide'] as num?)?.toInt()
  ..length = (json['length'] as num?)?.toDouble()
  ..width = (json['width'] as num?)?.toDouble()
  ..height = (json['height'] as num?)?.toDouble()
  ..mean = (json['mean'] as num?)?.toInt()
  ..area = (json['area'] as num?)?.toDouble()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..number = json['number'] as String?
  ..builtUpArea = json['builtUpArea']
  ..deductions = json['deductions']
  ..measurements = (json['measurements'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..widths = (json['widths'] as List<dynamic>?)
      ?.map((e) => (e as num).toDouble())
      .toList()
  ..landings = (json['landings'] as List<dynamic>?)
      ?.map((e) => Landing.fromJson(e as Map<String, dynamic>))
      .toList()
  ..landingClosed = json['landingClosed'] as bool?
  ..lengths = (json['lengths'] as List<dynamic>?)
      ?.map((e) => (e as num).toDouble())
      .toList()
  ..stairMeasurements = (json['stairMeasurements'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..flights = (json['flights'] as List<dynamic>?)
      ?.map((e) => Flight.fromJson(e as Map<String, dynamic>))
      .toList()
  ..floorHeight = (json['floorHeight'] as num?)?.toDouble()
  ..verticalDistance = json['verticalDistance']
  ..horizontalDistance = (json['horizontalDistance'] as num?)?.toDouble()
  ..voltage = (json['voltage'] as num?)?.toInt()
  ..radius = json['radius']
  ..tankHeight = json['tankHeight']
  ..tankCapacity = json['tankCapacity'];

Map<String, dynamic> _$ElectricLineToJson(ElectricLine instance) =>
    <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'number': instance.number,
      'builtUpArea': instance.builtUpArea,
      'deductions': instance.deductions,
      'measurements': instance.measurements,
      'widths': instance.widths,
      'landings': instance.landings,
      'landingClosed': instance.landingClosed,
      'lengths': instance.lengths,
      'stairMeasurements': instance.stairMeasurements,
      'flights': instance.flights,
      'floorHeight': instance.floorHeight,
      'verticalDistance': instance.verticalDistance,
      'horizontalDistance': instance.horizontalDistance,
      'voltage': instance.voltage,
      'radius': instance.radius,
      'tankHeight': instance.tankHeight,
      'tankCapacity': instance.tankCapacity,
    };

Flight _$FlightFromJson(Map<String, dynamic> json) => Flight()
  ..name = json['name'] as String?
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
  ..minimumSide = (json['minimumSide'] as num?)?.toInt()
  ..length = (json['length'] as num?)?.toInt()
  ..width = (json['width'] as num?)?.toInt()
  ..height = (json['height'] as num?)?.toInt()
  ..mean = (json['mean'] as num?)?.toInt()
  ..area = (json['area'] as num?)?.toInt()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..number = json['number'] as String?
  ..noOfRises = (json['noOfRises'] as num?)?.toInt()
  ..flights = (json['flights'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..flightClosed = json['flightClosed'] as bool?
  ..lengthOfFlights = (json['lengthOfFlights'] as List<dynamic>?)
      ?.map((e) => (e as num).toInt())
      .toList()
  ..widthOfFlights = (json['widthOfFlights'] as List<dynamic>?)
      ?.map((e) => (e as num).toDouble())
      .toList()
  ..stairMeasurements = json['stairMeasurements']
  ..landings = json['landings'] as List<dynamic>?
  ..floorHeight = json['floorHeight']
  ..circles = (json['circles'] as List<dynamic>?)
      ?.map((e) => Circle.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$FlightToJson(Flight instance) => <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'number': instance.number,
      'noOfRises': instance.noOfRises,
      'flights': instance.flights,
      'flightClosed': instance.flightClosed,
      'lengthOfFlights': instance.lengthOfFlights,
      'widthOfFlights': instance.widthOfFlights,
      'stairMeasurements': instance.stairMeasurements,
      'landings': instance.landings,
      'floorHeight': instance.floorHeight,
      'circles': instance.circles,
    };

Circle _$CircleFromJson(Map<String, dynamic> json) =>
    Circle()..radius = (json['radius'] as num?)?.toDouble();

Map<String, dynamic> _$CircleToJson(Circle instance) => <String, dynamic>{
      'radius': instance.radius,
    };

CuldeSacRoad _$CuldeSacRoadFromJson(Map<String, dynamic> json) => CuldeSacRoad()
  ..name = json['name'] as String?
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toDouble()
  ..minimumSide = (json['minimumSide'] as num?)?.toDouble()
  ..length = (json['length'] as num?)?.toDouble()
  ..width = (json['width'] as num?)?.toDouble()
  ..height = (json['height'] as num?)?.toDouble()
  ..mean = (json['mean'] as num?)?.toDouble()
  ..area = (json['area'] as num?)?.toDouble()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..level = (json['level'] as num?)?.toInt()
  ..shortestDistanceToRoad = json['shortestDistanceToRoad'] as List<dynamic>?
  ..distancesFromCenterToPlot =
      json['distancesFromCenterToPlot'] as List<dynamic>?
  ..distanceFromAccessoryBlock =
      json['distanceFromAccessoryBlock'] as List<dynamic>?
  ..type = json['type'] as String?;

Map<String, dynamic> _$CuldeSacRoadToJson(CuldeSacRoad instance) =>
    <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'level': instance.level,
      'shortestDistanceToRoad': instance.shortestDistanceToRoad,
      'distancesFromCenterToPlot': instance.distancesFromCenterToPlot,
      'distanceFromAccessoryBlock': instance.distanceFromAccessoryBlock,
      'type': instance.type,
    };

Landing _$LandingFromJson(Map<String, dynamic> json) => Landing()
  ..name = json['name'] as String?
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
  ..minimumSide = (json['minimumSide'] as num?)?.toInt()
  ..length = (json['length'] as num?)?.toDouble()
  ..width = (json['width'] as num?)?.toInt()
  ..height = (json['height'] as num?)?.toDouble()
  ..mean = (json['mean'] as num?)?.toInt()
  ..area = (json['area'] as num?)?.toDouble()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..number = json['number'] as String?
  ..landings = (json['landings'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..landingClosed = json['landingClosed'] as bool?
  ..lengths = (json['lengths'] as List<dynamic>?)
      ?.map((e) => (e as num).toDouble())
      .toList()
  ..widths = (json['widths'] as List<dynamic>?)
      ?.map((e) => (e as num).toDouble())
      .toList();

Map<String, dynamic> _$LandingToJson(Landing instance) => <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'number': instance.number,
      'landings': instance.landings,
      'landingClosed': instance.landingClosed,
      'lengths': instance.lengths,
      'widths': instance.widths,
    };

BathRoom _$BathRoomFromJson(Map<String, dynamic> json) => BathRoom()
  ..number = json['number']
  ..closed = json['closed'] as bool?
  ..rooms = json['rooms'] as List<dynamic>?
  ..lightAndVentilation = json['lightAndVentilation'] == null
      ? null
      : LightAndVentilation.fromJson(
          json['lightAndVentilation'] as Map<String, dynamic>)
  ..mezzanineAreas = json['mezzanineAreas'] as List<dynamic>?
  ..heights = json['heights'] as List<dynamic>?;

Map<String, dynamic> _$BathRoomToJson(BathRoom instance) => <String, dynamic>{
      'number': instance.number,
      'closed': instance.closed,
      'rooms': instance.rooms,
      'lightAndVentilation': instance.lightAndVentilation,
      'mezzanineAreas': instance.mezzanineAreas,
      'heights': instance.heights,
    };

LightAndVentilation _$LightAndVentilationFromJson(Map<String, dynamic> json) =>
    LightAndVentilation()
      ..heightOrDepth = json['heightOrDepth'] as List<dynamic>?
      ..measurements = json['measurements'] as List<dynamic>?;

Map<String, dynamic> _$LightAndVentilationToJson(
        LightAndVentilation instance) =>
    <String, dynamic>{
      'heightOrDepth': instance.heightOrDepth,
      'measurements': instance.measurements,
    };

FireStair _$FireStairFromJson(Map<String, dynamic> json) => FireStair()
  ..name = json['name']
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
  ..minimumSide = (json['minimumSide'] as num?)?.toInt()
  ..length = (json['length'] as num?)?.toInt()
  ..width = (json['width'] as num?)?.toInt()
  ..height = (json['height'] as num?)?.toInt()
  ..mean = (json['mean'] as num?)?.toInt()
  ..area = (json['area'] as num?)?.toInt()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..number = json['number'] as String?
  ..stairMeasurements = (json['stairMeasurements'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..flights = (json['flights'] as List<dynamic>?)
      ?.map((e) => Flight.fromJson(e as Map<String, dynamic>))
      .toList()
  ..landings = (json['landings'] as List<dynamic>?)
      ?.map((e) => ElectricLine.fromJson(e as Map<String, dynamic>))
      .toList()
  ..floorHeight = (json['floorHeight'] as num?)?.toDouble()
  ..generalStair = json['generalStair'] as bool?
  ..abuttingBltUp = json['abuttingBltUp'] as bool?;

Map<String, dynamic> _$FireStairToJson(FireStair instance) => <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'number': instance.number,
      'stairMeasurements': instance.stairMeasurements,
      'flights': instance.flights,
      'landings': instance.landings,
      'floorHeight': instance.floorHeight,
      'generalStair': instance.generalStair,
      'abuttingBltUp': instance.abuttingBltUp,
    };

InteriorOpenSpace _$InteriorOpenSpaceFromJson(Map<String, dynamic> json) =>
    InteriorOpenSpace()
      ..ventilationShaft = json['ventilationShaft'] == null
          ? null
          : LightAndVentilation.fromJson(
              json['ventilationShaft'] as Map<String, dynamic>)
      ..innerCourtYard = json['innerCourtYard'] == null
          ? null
          : LightAndVentilation.fromJson(
              json['innerCourtYard'] as Map<String, dynamic>)
      ..outerCourtYard = json['outerCourtYard'] == null
          ? null
          : LightAndVentilation.fromJson(
              json['outerCourtYard'] as Map<String, dynamic>)
      ..sunkenCourtYard = json['sunkenCourtYard'] == null
          ? null
          : LightAndVentilation.fromJson(
              json['sunkenCourtYard'] as Map<String, dynamic>);

Map<String, dynamic> _$InteriorOpenSpaceToJson(InteriorOpenSpace instance) =>
    <String, dynamic>{
      'ventilationShaft': instance.ventilationShaft,
      'innerCourtYard': instance.innerCourtYard,
      'outerCourtYard': instance.outerCourtYard,
      'sunkenCourtYard': instance.sunkenCourtYard,
    };

OccupancyElement _$OccupancyElementFromJson(Map<String, dynamic> json) =>
    OccupancyElement()
      ..name = json['name']
      ..presentInDxf = json['presentInDxf'] as bool?
      ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
      ..minimumSide = (json['minimumSide'] as num?)?.toInt()
      ..length = (json['length'] as num?)?.toInt()
      ..width = (json['width'] as num?)?.toInt()
      ..height = (json['height'] as num?)?.toInt()
      ..mean = (json['mean'] as num?)?.toInt()
      ..area = (json['area'] as num?)?.toInt()
      ..isValid = json['isValid']
      ..colorCode = (json['colorCode'] as num?)?.toInt()
      ..type = json['type'] as String?
      ..typeHelper = json['typeHelper'] == null
          ? null
          : MostRestrictiveFarHelper.fromJson(
              json['typeHelper'] as Map<String, dynamic>)
      ..deduction = (json['deduction'] as num?)?.toInt()
      ..builtUpArea = (json['builtUpArea'] as num?)?.toDouble()
      ..floorArea = (json['floorArea'] as num?)?.toDouble()
      ..carpetArea = (json['carpetArea'] as num?)?.toDouble()
      ..carpetAreaDeduction = (json['carpetAreaDeduction'] as num?)?.toInt()
      ..existingBuiltUpArea = (json['existingBuiltUpArea'] as num?)?.toInt()
      ..existingFloorArea = (json['existingFloorArea'] as num?)?.toInt()
      ..existingCarpetArea = (json['existingCarpetArea'] as num?)?.toInt()
      ..existingCarpetAreaDeduction =
          (json['existingCarpetAreaDeduction'] as num?)?.toInt()
      ..existingDeduction = (json['existingDeduction'] as num?)?.toInt()
      ..withAttachedBath = json['withAttachedBath'] as bool?
      ..withOutAttachedBath = json['withOutAttachedBath'] as bool?
      ..withDinningSpace = json['withDinningSpace'] as bool?
      ..recreationalSpace = json['recreationalSpace'] as List<dynamic>?
      ..mezzanineNumber = json['mezzanineNumber']
      ..isMezzanine = json['isMezzanine'] as bool?;

Map<String, dynamic> _$OccupancyElementToJson(OccupancyElement instance) =>
    <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'type': instance.type,
      'typeHelper': instance.typeHelper,
      'deduction': instance.deduction,
      'builtUpArea': instance.builtUpArea,
      'floorArea': instance.floorArea,
      'carpetArea': instance.carpetArea,
      'carpetAreaDeduction': instance.carpetAreaDeduction,
      'existingBuiltUpArea': instance.existingBuiltUpArea,
      'existingFloorArea': instance.existingFloorArea,
      'existingCarpetArea': instance.existingCarpetArea,
      'existingCarpetAreaDeduction': instance.existingCarpetAreaDeduction,
      'existingDeduction': instance.existingDeduction,
      'withAttachedBath': instance.withAttachedBath,
      'withOutAttachedBath': instance.withOutAttachedBath,
      'withDinningSpace': instance.withDinningSpace,
      'recreationalSpace': instance.recreationalSpace,
      'mezzanineNumber': instance.mezzanineNumber,
      'isMezzanine': instance.isMezzanine,
    };

MostRestrictiveFarHelper _$MostRestrictiveFarHelperFromJson(
        Map<String, dynamic> json) =>
    MostRestrictiveFarHelper()
      ..type = json['type'] == null
          ? null
          : Type.fromJson(json['type'] as Map<String, dynamic>)
      ..subtype = json['subtype'] == null
          ? null
          : Type.fromJson(json['subtype'] as Map<String, dynamic>)
      ..usage = json['usage']
      ..convertedType = json['convertedType']
      ..convertedSubtype = json['convertedSubtype']
      ..convertedUsage = json['convertedUsage'];

Map<String, dynamic> _$MostRestrictiveFarHelperToJson(
        MostRestrictiveFarHelper instance) =>
    <String, dynamic>{
      'type': instance.type,
      'subtype': instance.subtype,
      'usage': instance.usage,
      'convertedType': instance.convertedType,
      'convertedSubtype': instance.convertedSubtype,
      'convertedUsage': instance.convertedUsage,
    };

Type _$TypeFromJson(Map<String, dynamic> json) => Type()
  ..color = (json['color'] as num?)?.toInt()
  ..code = json['code'] as String?
  ..name = json['name'] as String?;

Map<String, dynamic> _$TypeToJson(Type instance) => <String, dynamic>{
      'color': instance.color,
      'code': instance.code,
      'name': instance.name,
    };

Parking _$ParkingFromJson(Map<String, dynamic> json) => Parking()
  ..cars = json['cars'] as List<dynamic>?
  ..openCars = (json['openCars'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..coverCars = json['coverCars'] as List<dynamic>?
  ..basementCars = json['basementCars'] as List<dynamic>?
  ..visitors = json['visitors'] as List<dynamic>?
  ..validCarParkingSlots = (json['validCarParkingSlots'] as num?)?.toInt()
  ..validOpenCarSlots = (json['validOpenCarSlots'] as num?)?.toInt()
  ..validCoverCarSlots = (json['validCoverCarSlots'] as num?)?.toInt()
  ..validBasementCarSlots = (json['validBasementCarSlots'] as num?)?.toInt()
  ..diningSeats = (json['diningSeats'] as num?)?.toInt()
  ..loadUnload = json['loadUnload'] as List<dynamic>?
  ..mechParking = json['mechParking'] as List<dynamic>?
  ..twoWheelers = json['twoWheelers'] as List<dynamic>?
  ..disabledPersons = json['disabledPersons'] as List<dynamic>?
  ..validDAParkingSlots = (json['validDAParkingSlots'] as num?)?.toInt()
  ..distFromDAToMainEntrance =
      (json['distFromDAToMainEntrance'] as num?)?.toInt()
  ..special = json['special'] as List<dynamic>?
  ..validSpecialSlots = (json['validSpecialSlots'] as num?)?.toInt()
  ..stilts = json['stilts'] as List<dynamic>?
  ..mechanicalLifts = json['mechanicalLifts'] as List<dynamic>?;

Map<String, dynamic> _$ParkingToJson(Parking instance) => <String, dynamic>{
      'cars': instance.cars,
      'openCars': instance.openCars,
      'coverCars': instance.coverCars,
      'basementCars': instance.basementCars,
      'visitors': instance.visitors,
      'validCarParkingSlots': instance.validCarParkingSlots,
      'validOpenCarSlots': instance.validOpenCarSlots,
      'validCoverCarSlots': instance.validCoverCarSlots,
      'validBasementCarSlots': instance.validBasementCarSlots,
      'diningSeats': instance.diningSeats,
      'loadUnload': instance.loadUnload,
      'mechParking': instance.mechParking,
      'twoWheelers': instance.twoWheelers,
      'disabledPersons': instance.disabledPersons,
      'validDAParkingSlots': instance.validDAParkingSlots,
      'distFromDAToMainEntrance': instance.distFromDAToMainEntrance,
      'special': instance.special,
      'validSpecialSlots': instance.validSpecialSlots,
      'stilts': instance.stilts,
      'mechanicalLifts': instance.mechanicalLifts,
    };

SetBack _$SetBackFromJson(Map<String, dynamic> json) => SetBack()
  ..frontYard = json['frontYard'] == null
      ? null
      : CuldeSacRoad.fromJson(json['frontYard'] as Map<String, dynamic>)
  ..rearYard = json['rearYard'] == null
      ? null
      : CuldeSacRoad.fromJson(json['rearYard'] as Map<String, dynamic>)
  ..sideYard1 = json['sideYard1'] == null
      ? null
      : CuldeSacRoad.fromJson(json['sideYard1'] as Map<String, dynamic>)
  ..sideYard2 = json['sideYard2'] == null
      ? null
      : CuldeSacRoad.fromJson(json['sideYard2'] as Map<String, dynamic>)
  ..level = (json['level'] as num?)?.toInt()
  ..buildingFootPrint = json['buildingFootPrint'] == null
      ? null
      : CuldeSacRoad.fromJson(
          json['buildingFootPrint'] as Map<String, dynamic>);

Map<String, dynamic> _$SetBackToJson(SetBack instance) => <String, dynamic>{
      'frontYard': instance.frontYard,
      'rearYard': instance.rearYard,
      'sideYard1': instance.sideYard1,
      'sideYard2': instance.sideYard2,
      'level': instance.level,
      'buildingFootPrint': instance.buildingFootPrint,
    };

SanityDetails _$SanityDetailsFromJson(Map<String, dynamic> json) =>
    SanityDetails()
      ..maleWaterClosets = json['maleWaterClosets'] as List<dynamic>?
      ..femaleWaterClosets = json['femaleWaterClosets'] as List<dynamic>?
      ..commonWaterClosets = json['commonWaterClosets'] as List<dynamic>?
      ..urinals = json['urinals'] as List<dynamic>?
      ..maleBathRooms = json['maleBathRooms'] as List<dynamic>?
      ..femaleBathRooms = json['femaleBathRooms'] as List<dynamic>?
      ..commonBathRooms = json['commonBathRooms'] as List<dynamic>?
      ..maleRoomsWithWaterCloset =
          json['maleRoomsWithWaterCloset'] as List<dynamic>?
      ..femaleRoomsWithWaterCloset =
          json['femaleRoomsWithWaterCloset'] as List<dynamic>?
      ..commonRoomsWithWaterCloset =
          json['commonRoomsWithWaterCloset'] as List<dynamic>?
      ..drinkingWater = json['drinkingWater'] as List<dynamic>?
      ..totalSpecialWC = json['totalSpecialWC'] as List<dynamic>?
      ..totalSPWC = (json['totalSPWC'] as num?)?.toInt()
      ..totalwashBasins = (json['totalwashBasins'] as num?)?.toInt();

Map<String, dynamic> _$SanityDetailsToJson(SanityDetails instance) =>
    <String, dynamic>{
      'maleWaterClosets': instance.maleWaterClosets,
      'femaleWaterClosets': instance.femaleWaterClosets,
      'commonWaterClosets': instance.commonWaterClosets,
      'urinals': instance.urinals,
      'maleBathRooms': instance.maleBathRooms,
      'femaleBathRooms': instance.femaleBathRooms,
      'commonBathRooms': instance.commonBathRooms,
      'maleRoomsWithWaterCloset': instance.maleRoomsWithWaterCloset,
      'femaleRoomsWithWaterCloset': instance.femaleRoomsWithWaterCloset,
      'commonRoomsWithWaterCloset': instance.commonRoomsWithWaterCloset,
      'drinkingWater': instance.drinkingWater,
      'totalSpecialWC': instance.totalSpecialWC,
      'totalSPWC': instance.totalSPWC,
      'totalwashBasins': instance.totalwashBasins,
    };

CompoundWall _$CompoundWallFromJson(Map<String, dynamic> json) => CompoundWall()
  ..wallHeights = json['wallHeights'] as List<dynamic>?
  ..railingHeights = json['railingHeights'] as List<dynamic>?;

Map<String, dynamic> _$CompoundWallToJson(CompoundWall instance) =>
    <String, dynamic>{
      'wallHeights': instance.wallHeights,
      'railingHeights': instance.railingHeights,
    };

DistanceToExternalEntity _$DistanceToExternalEntityFromJson(
        Map<String, dynamic> json) =>
    DistanceToExternalEntity()
      ..monuments = (json['monuments'] as List<dynamic>?)
          ?.map((e) => (e as num).toDouble())
          .toList()
      ..govtBuildings = (json['govtBuildings'] as List<dynamic>?)
          ?.map((e) => (e as num).toDouble())
          .toList()
      ..rivers = json['rivers'] as List<dynamic>?
      ..drinage = json['drinage']
      ..footpath = json['footpath'];

Map<String, dynamic> _$DistanceToExternalEntityToJson(
        DistanceToExternalEntity instance) =>
    <String, dynamic>{
      'monuments': instance.monuments,
      'govtBuildings': instance.govtBuildings,
      'rivers': instance.rivers,
      'drinage': instance.drinage,
      'footpath': instance.footpath,
    };

DrawingPreference _$DrawingPreferenceFromJson(Map<String, dynamic> json) =>
    DrawingPreference()
      ..northDirection = json['northDirection'] == null
          ? null
          : NorthDirection.fromJson(
              json['northDirection'] as Map<String, dynamic>)
      ..locationPlans = (json['locationPlans'] as List<dynamic>?)
          ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
          .toList()
      ..uom = json['uom'] as String?;

Map<String, dynamic> _$DrawingPreferenceToJson(DrawingPreference instance) =>
    <String, dynamic>{
      'northDirection': instance.northDirection,
      'locationPlans': instance.locationPlans,
      'uom': instance.uom,
    };

NorthDirection _$NorthDirectionFromJson(Map<String, dynamic> json) =>
    NorthDirection()
      ..direction = json['direction'] as String?
      ..directions = (json['directions'] as List<dynamic>?)
          ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$NorthDirectionToJson(NorthDirection instance) =>
    <String, dynamic>{
      'direction': instance.direction,
      'directions': instance.directions,
    };

Errors _$ErrorsFromJson(Map<String, dynamic> json) => Errors();

Map<String, dynamic> _$ErrorsToJson(Errors instance) => <String, dynamic>{};

FarDetails _$FarDetailsFromJson(Map<String, dynamic> json) => FarDetails()
  ..permissableFar = (json['permissableFar'] as num?)?.toDouble()
  ..providedFar = (json['providedFar'] as num?)?.toDouble();

Map<String, dynamic> _$FarDetailsToJson(FarDetails instance) =>
    <String, dynamic>{
      'permissableFar': instance.permissableFar,
      'providedFar': instance.providedFar,
    };

Gate _$GateFromJson(Map<String, dynamic> json) => Gate()
  ..gates = json['gates'] as List<dynamic>?
  ..heights = json['heights'];

Map<String, dynamic> _$GateToJson(Gate instance) => <String, dynamic>{
      'gates': instance.gates,
      'heights': instance.heights,
    };

GuardRoom _$GuardRoomFromJson(Map<String, dynamic> json) => GuardRoom()
  ..guardRooms = json['guardRooms'] as List<dynamic>?
  ..cabinHeights = json['cabinHeights'] as List<dynamic>?;

Map<String, dynamic> _$GuardRoomToJson(GuardRoom instance) => <String, dynamic>{
      'guardRooms': instance.guardRooms,
      'cabinHeights': instance.cabinHeights,
    };

PlanInfoProperties _$PlanInfoPropertiesFromJson(Map<String, dynamic> json) =>
    PlanInfoProperties()
      ..buildingNearGovtBldg = json['BUILDING_NEAR_GOVT_BLDG'] as String?
      ..provisionForGreenBuildingsAndSustainability =
          json['PROVISION_FOR_GREEN_BUILDINGS_AND_SUSTAINABILITY'] as String?
      ..leaseholdLand = json['LEASEHOLD_LAND'] as String?
      ..nocRailway = json['NOC_RAILWAY'] as String?
      ..roadWidth = json['ROAD_WIDTH'] as String?
      ..nocIrrigationDept = json['NOC_IRRIGATION_DEPT'] as String?
      ..nocForConstructionNearAirport =
          json['NOC_FOR_CONSTRUCTION_NEAR_AIRPORT'] as String?
      ..nocFireDept = json['NOC_FIRE_DEPT'] as String?
      ..existingFloorAreaToBeDemolishedM2 =
          json['EXISTING_FLOOR_AREA_TO_BE_DEMOLISHED_M2'] as String?
      ..avgPlotDepth = json['AVG_PLOT_DEPTH'] as String?
      ..nocForConstructionNearDefenceAerodomes =
          json['NOC_FOR_CONSTRUCTION_NEAR_DEFENCE_AERODOMES'] as String?
      ..nocForConstructionNearMonument =
          json['NOC_FOR_CONSTRUCTION_NEAR_MONUMENT'] as String?
      ..buildingNearToRiver = json['BUILDING_NEAR_TO_RIVER'] as String?
      ..district = json['DISTRICT'] as String?
      ..nocCollectorGvtLand = json['NOC_COLLECTOR_GVT_LAND'] as String?
      ..barrierFreeAccessForPhysicallyChallengedPeople =
          json['BARRIER_FREE_ACCESS_FOR_PHYSICALLY_CHALLENGED_PEOPLE']
              as String?
      ..nocStateEnvImpact = json['NOC_STATE_ENV_IMPACT'] as String?
      ..khataNo = json['KHATA_NO'] as String?
      ..mauza = json['MAUZA'] as String?
      ..buildingNearMonument = json['BUILDING_NEAR_MONUMENT'] as String?
      ..areaType = json['AREA_TYPE'] as String?
      ..avgPlotWidth = json['AVG_PLOT_WIDTH'] as String?
      ..landUseZone = json['LAND_USE_ZONE'] as String?
      ..rwhDeclared = json['RWH_DECLARED'] as String?
      ..plotNo = json['PLOT_NO'] as String?
      ..plotAreaM2 = json['PLOT_AREA_M2'] as String?;

Map<String, dynamic> _$PlanInfoPropertiesToJson(PlanInfoProperties instance) =>
    <String, dynamic>{
      'BUILDING_NEAR_GOVT_BLDG': instance.buildingNearGovtBldg,
      'PROVISION_FOR_GREEN_BUILDINGS_AND_SUSTAINABILITY':
          instance.provisionForGreenBuildingsAndSustainability,
      'LEASEHOLD_LAND': instance.leaseholdLand,
      'NOC_RAILWAY': instance.nocRailway,
      'ROAD_WIDTH': instance.roadWidth,
      'NOC_IRRIGATION_DEPT': instance.nocIrrigationDept,
      'NOC_FOR_CONSTRUCTION_NEAR_AIRPORT':
          instance.nocForConstructionNearAirport,
      'NOC_FIRE_DEPT': instance.nocFireDept,
      'EXISTING_FLOOR_AREA_TO_BE_DEMOLISHED_M2':
          instance.existingFloorAreaToBeDemolishedM2,
      'AVG_PLOT_DEPTH': instance.avgPlotDepth,
      'NOC_FOR_CONSTRUCTION_NEAR_DEFENCE_AERODOMES':
          instance.nocForConstructionNearDefenceAerodomes,
      'NOC_FOR_CONSTRUCTION_NEAR_MONUMENT':
          instance.nocForConstructionNearMonument,
      'BUILDING_NEAR_TO_RIVER': instance.buildingNearToRiver,
      'DISTRICT': instance.district,
      'NOC_COLLECTOR_GVT_LAND': instance.nocCollectorGvtLand,
      'BARRIER_FREE_ACCESS_FOR_PHYSICALLY_CHALLENGED_PEOPLE':
          instance.barrierFreeAccessForPhysicallyChallengedPeople,
      'NOC_STATE_ENV_IMPACT': instance.nocStateEnvImpact,
      'KHATA_NO': instance.khataNo,
      'MAUZA': instance.mauza,
      'BUILDING_NEAR_MONUMENT': instance.buildingNearMonument,
      'AREA_TYPE': instance.areaType,
      'AVG_PLOT_WIDTH': instance.avgPlotWidth,
      'LAND_USE_ZONE': instance.landUseZone,
      'RWH_DECLARED': instance.rwhDeclared,
      'PLOT_NO': instance.plotNo,
      'PLOT_AREA_M2': instance.plotAreaM2,
    };

PlanInformation _$PlanInformationFromJson(Map<String, dynamic> json) =>
    PlanInformation()
      ..plotArea = (json['plotArea'] as num?)?.toInt()
      ..ownerName = json['ownerName']
      ..occupancy = json['occupancy'] as String?
      ..serviceType = json['serviceType']
      ..amenities = json['amenities']
      ..architectInformation = json['architectInformation']
      ..applicantName = json['applicantName'] as String?
      ..crzZoneArea = json['crzZoneArea'] as bool?
      ..demolitionArea = (json['demolitionArea'] as num?)?.toInt()
      ..depthCutting = json['depthCutting']
      ..governmentOrAidedSchool = json['governmentOrAidedSchool']
      ..securityZone = json['securityZone'] as bool?
      ..accessWidth = json['accessWidth']
      ..noOfBeds = json['noOfBeds']
      ..nocToAbutSideDesc = json['nocToAbutSideDesc'] as String?
      ..nocToAbutRearDesc = json['nocToAbutRearDesc'] as String?
      ..openingOnSide = json['openingOnSide'] as bool?
      ..openingOnRear = json['openingOnRear'] as bool?
      ..noOfSeats = (json['noOfSeats'] as num?)?.toInt()
      ..noOfMechanicalParking = (json['noOfMechanicalParking'] as num?)?.toInt()
      ..singleFamilyBuilding = json['singleFamilyBuilding']
      ..reSurveyNo = json['reSurveyNo']
      ..revenueWard = json['revenueWard']
      ..desam = json['desam']
      ..village = json['village']
      ..landUseZone = json['landUseZone'] as String?
      ..leaseHoldLand = json['leaseHoldLand'] as String?
      ..roadWidth = (json['roadWidth'] as num?)?.toInt()
      ..roadLength = (json['roadLength'] as num?)?.toInt()
      ..typeOfArea = json['typeOfArea'] as String?
      ..depthOfPlot = (json['depthOfPlot'] as num?)?.toInt()
      ..widthOfPlot = (json['widthOfPlot'] as num?)?.toInt()
      ..buildingNearMonument = json['buildingNearMonument'] as String?
      ..buildingNearGovtBuilding = json['buildingNearGovtBuilding'] as String?
      ..nocNearMonument = json['nocNearMonument'] as String?
      ..nocNearAirport = json['nocNearAirport'] as String?
      ..nocNearDefenceAerodomes = json['nocNearDefenceAerodomes'] as String?
      ..nocStateEnvImpact = json['nocStateEnvImpact'] as String?
      ..nocRailways = json['nocRailways'] as String?
      ..nocCollectorGvtLand = json['nocCollectorGvtLand'] as String?
      ..nocIrrigationDept = json['nocIrrigationDept'] as String?
      ..nocFireDept = json['nocFireDept'] as String?
      ..buildingNearToRiver = json['buildingNearToRiver'] as String?
      ..barrierFreeAccessForPhyChlngdPpl =
          json['barrierFreeAccessForPhyChlngdPpl'] as String?
      ..provisionsForGreenBuildingsAndSustainability =
          json['provisionsForGreenBuildingsAndSustainability'] as String?
      ..fireProtectionAndFireSafetyRequirements =
          json['fireProtectionAndFireSafetyRequirements'] as String?
      ..plotNo = json['plotNo'] as String?
      ..khataNo = json['khataNo'] as String?
      ..mauza = json['mauza'] as String?
      ..district = json['district'] as String?
      ..rwhDeclared = json['rwhDeclared'] as String?;

Map<String, dynamic> _$PlanInformationToJson(PlanInformation instance) =>
    <String, dynamic>{
      'plotArea': instance.plotArea,
      'ownerName': instance.ownerName,
      'occupancy': instance.occupancy,
      'serviceType': instance.serviceType,
      'amenities': instance.amenities,
      'architectInformation': instance.architectInformation,
      'applicantName': instance.applicantName,
      'crzZoneArea': instance.crzZoneArea,
      'demolitionArea': instance.demolitionArea,
      'depthCutting': instance.depthCutting,
      'governmentOrAidedSchool': instance.governmentOrAidedSchool,
      'securityZone': instance.securityZone,
      'accessWidth': instance.accessWidth,
      'noOfBeds': instance.noOfBeds,
      'nocToAbutSideDesc': instance.nocToAbutSideDesc,
      'nocToAbutRearDesc': instance.nocToAbutRearDesc,
      'openingOnSide': instance.openingOnSide,
      'openingOnRear': instance.openingOnRear,
      'noOfSeats': instance.noOfSeats,
      'noOfMechanicalParking': instance.noOfMechanicalParking,
      'singleFamilyBuilding': instance.singleFamilyBuilding,
      'reSurveyNo': instance.reSurveyNo,
      'revenueWard': instance.revenueWard,
      'desam': instance.desam,
      'village': instance.village,
      'landUseZone': instance.landUseZone,
      'leaseHoldLand': instance.leaseHoldLand,
      'roadWidth': instance.roadWidth,
      'roadLength': instance.roadLength,
      'typeOfArea': instance.typeOfArea,
      'depthOfPlot': instance.depthOfPlot,
      'widthOfPlot': instance.widthOfPlot,
      'buildingNearMonument': instance.buildingNearMonument,
      'buildingNearGovtBuilding': instance.buildingNearGovtBuilding,
      'nocNearMonument': instance.nocNearMonument,
      'nocNearAirport': instance.nocNearAirport,
      'nocNearDefenceAerodomes': instance.nocNearDefenceAerodomes,
      'nocStateEnvImpact': instance.nocStateEnvImpact,
      'nocRailways': instance.nocRailways,
      'nocCollectorGvtLand': instance.nocCollectorGvtLand,
      'nocIrrigationDept': instance.nocIrrigationDept,
      'nocFireDept': instance.nocFireDept,
      'buildingNearToRiver': instance.buildingNearToRiver,
      'barrierFreeAccessForPhyChlngdPpl':
          instance.barrierFreeAccessForPhyChlngdPpl,
      'provisionsForGreenBuildingsAndSustainability':
          instance.provisionsForGreenBuildingsAndSustainability,
      'fireProtectionAndFireSafetyRequirements':
          instance.fireProtectionAndFireSafetyRequirements,
      'plotNo': instance.plotNo,
      'khataNo': instance.khataNo,
      'mauza': instance.mauza,
      'district': instance.district,
      'rwhDeclared': instance.rwhDeclared,
    };

Plantation _$PlantationFromJson(Map<String, dynamic> json) =>
    Plantation()..plantations = json['plantations'] as List<dynamic>?;

Map<String, dynamic> _$PlantationToJson(Plantation instance) =>
    <String, dynamic>{
      'plantations': instance.plantations,
    };

Plot _$PlotFromJson(Map<String, dynamic> json) => Plot()
  ..name = json['name']
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
  ..minimumSide = (json['minimumSide'] as num?)?.toInt()
  ..length = (json['length'] as num?)?.toInt()
  ..width = (json['width'] as num?)?.toInt()
  ..height = (json['height'] as num?)?.toInt()
  ..mean = (json['mean'] as num?)?.toInt()
  ..area = (json['area'] as num?)?.toInt()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..frontYard = json['frontYard']
  ..rearYard = json['rearYard']
  ..sideYard1 = json['sideYard1']
  ..sideYard2 = json['sideYard2']
  ..setBacks = json['setBacks'] as List<dynamic>?
  ..buildingFootPrint = json['buildingFootPrint'] == null
      ? null
      : CuldeSacRoad.fromJson(json['buildingFootPrint'] as Map<String, dynamic>)
  ..bsmtFrontYard = json['bsmtFrontYard']
  ..bsmtRearYard = json['bsmtRearYard']
  ..bsmtSideYard1 = json['bsmtSideYard1']
  ..bsmtSideYard2 = json['bsmtSideYard2']
  ..smallPlot = json['smallPlot'] as bool?
  ..plotBndryArea = (json['plotBndryArea'] as num?)?.toDouble()
  ..levelZeroSetBack = json['levelZeroSetBack'];

Map<String, dynamic> _$PlotToJson(Plot instance) => <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'frontYard': instance.frontYard,
      'rearYard': instance.rearYard,
      'sideYard1': instance.sideYard1,
      'sideYard2': instance.sideYard2,
      'setBacks': instance.setBacks,
      'buildingFootPrint': instance.buildingFootPrint,
      'bsmtFrontYard': instance.bsmtFrontYard,
      'bsmtRearYard': instance.bsmtRearYard,
      'bsmtSideYard1': instance.bsmtSideYard1,
      'bsmtSideYard2': instance.bsmtSideYard2,
      'smallPlot': instance.smallPlot,
      'plotBndryArea': instance.plotBndryArea,
      'levelZeroSetBack': instance.levelZeroSetBack,
    };

ReportOutput _$ReportOutputFromJson(Map<String, dynamic> json) => ReportOutput()
  ..scrutinyDetails = (json['scrutinyDetails'] as List<dynamic>?)
      ?.map((e) => ScrutinyDetail.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$ReportOutputToJson(ReportOutput instance) =>
    <String, dynamic>{
      'scrutinyDetails': instance.scrutinyDetails,
    };

ScrutinyDetail _$ScrutinyDetailFromJson(Map<String, dynamic> json) =>
    ScrutinyDetail()
      ..key = json['key'] as String?
      ..heading = json['heading'] as String?
      ..remarks = json['remarks']
      ..subHeading = json['subHeading']
      ..columnHeading = (json['columnHeading'] as Map<String, dynamic>?)?.map(
        (k, e) =>
            MapEntry(k, ColumnHeading.fromJson(e as Map<String, dynamic>)),
      )
      ..detail = (json['detail'] as List<dynamic>?)
          ?.map((e) => Detail.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$ScrutinyDetailToJson(ScrutinyDetail instance) =>
    <String, dynamic>{
      'key': instance.key,
      'heading': instance.heading,
      'remarks': instance.remarks,
      'subHeading': instance.subHeading,
      'columnHeading': instance.columnHeading,
      'detail': instance.detail,
    };

ColumnHeading _$ColumnHeadingFromJson(Map<String, dynamic> json) =>
    ColumnHeading()..name = json['name'] as String?;

Map<String, dynamic> _$ColumnHeadingToJson(ColumnHeading instance) =>
    <String, dynamic>{
      'name': instance.name,
    };

Detail _$DetailFromJson(Map<String, dynamic> json) => Detail()
  ..status = json['Status'] as String?
  ..areaType = json['Area Type'] as String?
  ..occupancy = json['Occupancy'] as String?
  ..permissible = json['Permissible'] as String?
  ..byelaw = json['Byelaw'] as String?
  ..provided = json['Provided'] as String?
  ..roadWidth = json['Road Width'] as String?
  ..fieldVerified = json['Field Verified'] as String?
  ..setback = json['Setback'] as String?
  ..level = json['Level'] as String?
  ..description = json['Description'] as String?
  ..required = json['Required'] as String?
  ..permitted = json['Permitted'] as String?
  ..distance = json['Distance'] as String?
  ..floor = json['Floor'] as String?
  ..remarks = json['Remarks'] as String?
  ..voltage = json['Voltage'] as String?;

Map<String, dynamic> _$DetailToJson(Detail instance) => <String, dynamic>{
      'Status': instance.status,
      'Area Type': instance.areaType,
      'Occupancy': instance.occupancy,
      'Permissible': instance.permissible,
      'Byelaw': instance.byelaw,
      'Provided': instance.provided,
      'Road Width': instance.roadWidth,
      'Field Verified': instance.fieldVerified,
      'Setback': instance.setback,
      'Level': instance.level,
      'Description': instance.description,
      'Required': instance.required,
      'Permitted': instance.permitted,
      'Distance': instance.distance,
      'Floor': instance.floor,
      'Remarks': instance.remarks,
      'Voltage': instance.voltage,
    };

SegregatedToilet _$SegregatedToiletFromJson(Map<String, dynamic> json) =>
    SegregatedToilet()
      ..segregatedToilets = json['segregatedToilets'] as List<dynamic>?
      ..distancesToMainEntrance =
          json['distancesToMainEntrance'] as List<dynamic>?;

Map<String, dynamic> _$SegregatedToiletToJson(SegregatedToilet instance) =>
    <String, dynamic>{
      'segregatedToilets': instance.segregatedToilets,
      'distancesToMainEntrance': instance.distancesToMainEntrance,
    };

Utility _$UtilityFromJson(Map<String, dynamic> json) => Utility()
  ..name = json['name']
  ..presentInDxf = json['presentInDxf'] as bool?
  ..minimumDistance = (json['minimumDistance'] as num?)?.toInt()
  ..minimumSide = (json['minimumSide'] as num?)?.toInt()
  ..length = (json['length'] as num?)?.toInt()
  ..width = (json['width'] as num?)?.toInt()
  ..height = (json['height'] as num?)?.toInt()
  ..mean = (json['mean'] as num?)?.toInt()
  ..area = (json['area'] as num?)?.toInt()
  ..isValid = json['isValid']
  ..colorCode = (json['colorCode'] as num?)?.toInt()
  ..wasteDisposalUnits = (json['wasteDisposalUnits'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..wasteWaterRecyclePlant = json['wasteWaterRecyclePlant'] as List<dynamic>?
  ..liquidWasteTreatementPlant =
      json['liquidWasteTreatementPlant'] as List<dynamic>?
  ..wells = (json['wells'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..wellDistance = (json['wellDistance'] as List<dynamic>?)
      ?.map((e) => WellDistance.fromJson(e as Map<String, dynamic>))
      .toList()
  ..rainWaterHarvest = (json['rainWaterHarvest'] as List<dynamic>?)
      ?.map((e) => ElectricLine.fromJson(e as Map<String, dynamic>))
      .toList()
  ..solar = (json['solar'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..rainWaterHarvestingTankCapacity = json['rainWaterHarvestingTankCapacity']
  ..biometricWasteTreatment = json['biometricWasteTreatment'] as List<dynamic>?
  ..solidLiqdWasteTrtmnt = json['solidLiqdWasteTrtmnt'] as List<dynamic>?
  ..solarWaterHeatingSystems =
      (json['solarWaterHeatingSystems'] as List<dynamic>?)
          ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
          .toList()
  ..segregationOfWaste = (json['segregationOfWaste'] as List<dynamic>?)
      ?.map((e) => CuldeSacRoad.fromJson(e as Map<String, dynamic>))
      .toList()
  ..waterTankCapacity = (json['waterTankCapacity'] as num?)?.toInt()
  ..supplyLine = json['supplyLine'];

Map<String, dynamic> _$UtilityToJson(Utility instance) => <String, dynamic>{
      'name': instance.name,
      'presentInDxf': instance.presentInDxf,
      'minimumDistance': instance.minimumDistance,
      'minimumSide': instance.minimumSide,
      'length': instance.length,
      'width': instance.width,
      'height': instance.height,
      'mean': instance.mean,
      'area': instance.area,
      'isValid': instance.isValid,
      'colorCode': instance.colorCode,
      'wasteDisposalUnits': instance.wasteDisposalUnits,
      'wasteWaterRecyclePlant': instance.wasteWaterRecyclePlant,
      'liquidWasteTreatementPlant': instance.liquidWasteTreatementPlant,
      'wells': instance.wells,
      'wellDistance': instance.wellDistance,
      'rainWaterHarvest': instance.rainWaterHarvest,
      'solar': instance.solar,
      'rainWaterHarvestingTankCapacity':
          instance.rainWaterHarvestingTankCapacity,
      'biometricWasteTreatment': instance.biometricWasteTreatment,
      'solidLiqdWasteTrtmnt': instance.solidLiqdWasteTrtmnt,
      'solarWaterHeatingSystems': instance.solarWaterHeatingSystems,
      'segregationOfWaste': instance.segregationOfWaste,
      'waterTankCapacity': instance.waterTankCapacity,
      'supplyLine': instance.supplyLine,
    };

WellDistance _$WellDistanceFromJson(Map<String, dynamic> json) => WellDistance()
  ..colourCode = json['colourCode'] as String?
  ..roadDistainceToPlot = json['roadDistainceToPlot']
  ..distance = (json['distance'] as num?)?.toDouble();

Map<String, dynamic> _$WellDistanceToJson(WellDistance instance) =>
    <String, dynamic>{
      'colourCode': instance.colourCode,
      'roadDistainceToPlot': instance.roadDistainceToPlot,
      'distance': instance.distance,
    };

VirtualBuilding _$VirtualBuildingFromJson(Map<String, dynamic> json) =>
    VirtualBuilding()
      ..buildingHeight = json['buildingHeight']
      ..occupancies = json['occupancies'] as List<dynamic>?
      ..occupancyTypes = (json['occupancyTypes'] as List<dynamic>?)
          ?.map((e) =>
              MostRestrictiveFarHelper.fromJson(e as Map<String, dynamic>))
          .toList()
      ..totalBuitUpArea = (json['totalBuitUpArea'] as num?)?.toDouble()
      ..totalFloorArea = (json['totalFloorArea'] as num?)?.toDouble()
      ..totalCarpetArea = (json['totalCarpetArea'] as num?)?.toDouble()
      ..totalExistingBuiltUpArea =
          (json['totalExistingBuiltUpArea'] as num?)?.toInt()
      ..totalExistingFloorArea =
          (json['totalExistingFloorArea'] as num?)?.toInt()
      ..totalExistingCarpetArea =
          (json['totalExistingCarpetArea'] as num?)?.toInt()
      ..mostRestrictiveFar = json['mostRestrictiveFar']
      ..mostRestrictiveCoverage = json['mostRestrictiveCoverage']
      ..mostRestrictiveFarHelper = json['mostRestrictiveFarHelper'] == null
          ? null
          : MostRestrictiveFarHelper.fromJson(
              json['mostRestrictiveFarHelper'] as Map<String, dynamic>)
      ..mostRestrictiveCoverageHelper = json['mostRestrictiveCoverageHelper']
      ..floorsAboveGround = json['floorsAboveGround']
      ..totalCoverageArea = (json['totalCoverageArea'] as num?)?.toDouble()
      ..residentialOrCommercialBuilding =
          json['residentialOrCommercialBuilding'] as bool?
      ..residentialBuilding = json['residentialBuilding'] as bool?
      ..totalConstructedArea = (json['totalConstructedArea'] as num?)?.toInt();

Map<String, dynamic> _$VirtualBuildingToJson(VirtualBuilding instance) =>
    <String, dynamic>{
      'buildingHeight': instance.buildingHeight,
      'occupancies': instance.occupancies,
      'occupancyTypes': instance.occupancyTypes,
      'totalBuitUpArea': instance.totalBuitUpArea,
      'totalFloorArea': instance.totalFloorArea,
      'totalCarpetArea': instance.totalCarpetArea,
      'totalExistingBuiltUpArea': instance.totalExistingBuiltUpArea,
      'totalExistingFloorArea': instance.totalExistingFloorArea,
      'totalExistingCarpetArea': instance.totalExistingCarpetArea,
      'mostRestrictiveFar': instance.mostRestrictiveFar,
      'mostRestrictiveCoverage': instance.mostRestrictiveCoverage,
      'mostRestrictiveFarHelper': instance.mostRestrictiveFarHelper,
      'mostRestrictiveCoverageHelper': instance.mostRestrictiveCoverageHelper,
      'floorsAboveGround': instance.floorsAboveGround,
      'totalCoverageArea': instance.totalCoverageArea,
      'residentialOrCommercialBuilding':
          instance.residentialOrCommercialBuilding,
      'residentialBuilding': instance.residentialBuilding,
      'totalConstructedArea': instance.totalConstructedArea,
    };

ResponseInfo _$ResponseInfoFromJson(Map<String, dynamic> json) => ResponseInfo()
  ..apiId = json['apiId'] as String?
  ..ver = json['ver']
  ..ts = json['ts']
  ..resMsgId = json['resMsgId'] as String?
  ..msgId = json['msgId'] as String?
  ..status = json['status'] as String?;

Map<String, dynamic> _$ResponseInfoToJson(ResponseInfo instance) =>
    <String, dynamic>{
      'apiId': instance.apiId,
      'ver': instance.ver,
      'ts': instance.ts,
      'resMsgId': instance.resMsgId,
      'msgId': instance.msgId,
      'status': instance.status,
    };
