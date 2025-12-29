Meripehchaan meripehchaanFromJson(dynamic str) => Meripehchaan.fromJson(str);

class Meripehchaan {
  String? redirectUrl;
  String? dlReqRef;

  Meripehchaan({
    this.redirectUrl,
    this.dlReqRef,
  });

  factory Meripehchaan.fromJson(Map<String, dynamic> json) => Meripehchaan(
        redirectUrl: json["redirectURL"],
        dlReqRef: json["dlReqRef"],
      );
}
