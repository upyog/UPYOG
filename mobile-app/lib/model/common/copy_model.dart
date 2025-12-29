class CopyModel {
  String? originalCode;
  String? replaceCode;

  CopyModel({
    this.originalCode,
    this.replaceCode,
  });

  @override
  String toString() {
    return 'CopyModel{originalCode: $originalCode, replaceCode: $replaceCode}';
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is CopyModel &&
          runtimeType == other.runtimeType &&
          originalCode == other.originalCode;

  @override
  int get hashCode => originalCode.hashCode;
}
