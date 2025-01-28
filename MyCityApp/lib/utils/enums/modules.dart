// ignore_for_file: constant_identifier_names

enum Modules {
  COMMON,
  PGR,
  TL,
  PT,
  BND,
  BPA,
  BPAREG,
  PG,
  WS,
  SW,
  ABG,
  UC,
  FSM,
  NOC
}

extension ModulesExtension on Modules {
  static const actionNames = {
    Modules.COMMON: "rainmaker-common",
    Modules.PGR: "rainmaker-pgr",
    Modules.TL: "rainmaker-tl",
    Modules.PT: "rainmaker-pt",
    Modules.BND: "rainmaker-bnd",
    Modules.BPA: "rainmaker-bpa",
    Modules.BPAREG: "rainmaker-bpareg",
    Modules.PG: "rainmaker-pg",
    Modules.WS: "rainmaker-ws",
    Modules.SW: "rainmaker-sw",
    Modules.ABG: "rainmaker-abg",
    Modules.UC: "rainmaker-uc",
    Modules.FSM: "rainmaker-fsm",
    Modules.NOC: "rainmaker-noc",
  };
  String get name => actionNames[this]!;
}

Modules getModules(String service) {
  switch (service.toLowerCase()) {
    case "pgr":
      return Modules.PGR;
    case "tl":
      return Modules.TL;
    case "pt":
      return Modules.PT;
    case "bnd":
      return Modules.BND;
    case "bpa":
      return Modules.BPA;
    case "bpareg":
      return Modules.BPAREG;
    case "pg":
      return Modules.PG;
    case "ws":
      return Modules.WS;
    case "sw":
      return Modules.WS;
    // case "abg":
    //   return Modules.ABG;
    // case "uc":
    //   return Modules.UC;
    case "fsm":
      return Modules.FSM;
    case "noc":
      return Modules.NOC;
    default:
      return Modules.COMMON;
  }
}
