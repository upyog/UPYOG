import 'dart:collection';

import 'package:table_calendar/table_calendar.dart';

/// Example event class.
class Event {
  final String label;
  final String text;

  const Event({required this.label, required this.text});

  @override
  String toString() => '$label: $text';
}

/// Using a [LinkedHashMap] is highly recommended if you decide to use a map.
final kEvents = LinkedHashMap<DateTime, List<Event>>(
  equals: isSameDay,
  hashCode: getHashCode,
)..addAll(_kEventSource);

final Map<DateTime, List<Event>> _kEventSource = {
  for (int i = 0; i < 50; i++)
    DateTime.utc(kFirstDay.year, kFirstDay.month, kFirstDay.day + i * 5):
        List.generate(
      (i % 4) + 1,
      (j) => Event(
        label: 'Category-${j + 1}',
        text:
            'Sample description for Event ${j + 1} on day ${kFirstDay.day + i * 5}.',
      ),
    ),
}..addAll({
    kToday: kEventList,
  });

int getHashCode(DateTime key) {
  return key.day * 1000000 + key.month * 10000 + key.year;
}

/// Returns a list of [DateTime] objects from [first] to [last], inclusive.
List<DateTime> daysInRange(DateTime first, DateTime last) {
  final dayCount = last.difference(first).inDays + 1;
  return List.generate(
    dayCount,
    (index) => DateTime.utc(first.year, first.month, first.day + index),
  );
}

final kToday = DateTime.now();

final kFirstDay = DateTime(kToday.year, kToday.month - 3, kToday.day);
final kLastDay = DateTime(kToday.year, kToday.month + 3, kToday.day);

final kEventList = [
  const Event(
    label: 'School',
    text: 'Schools to remain closed . . . . . ',
  ),
  const Event(
    label: 'Public Parking',
    text: 'Public parking spaces available . . . . . ',
  ),
  const Event(
    label: 'Diesel Vehicle Allowance',
    text:
        'Govt. has banned use of diesel vehicles in the city owing to high pollution levels.',
  ),
];
