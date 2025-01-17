class DateOpenedFilter {
  final String label;
  final DateTime startDate;
  final DateTime endDate;
  bool isSelected;

  DateOpenedFilter({
    required this.label,
    required this.startDate,
    required this.endDate,
    this.isSelected = false,
  });
}
