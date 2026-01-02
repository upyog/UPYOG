import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:mobile_app/MyCity/screens/home/calender_home/calender_utils.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:table_calendar/table_calendar.dart';

class CalenderHomeScreen extends StatefulWidget {
  const CalenderHomeScreen({super.key});

  @override
  State<CalenderHomeScreen> createState() => _CalenderHomeScreenState();
}

class _CalenderHomeScreenState extends State<CalenderHomeScreen> {
  late final ValueNotifier<List<Event>> _selectedEvents;
  CalendarFormat _calendarFormat = CalendarFormat.month;
  RangeSelectionMode _rangeSelectionMode = RangeSelectionMode
      .toggledOff; // Can be toggled on/off by longpressing a date
  DateTime _focusedDay = DateTime.now();
  DateTime? _selectedDay;
  DateTime? _rangeStart;
  DateTime? _rangeEnd;

  @override
  void initState() {
    super.initState();

    _selectedDay = _focusedDay;
    _selectedEvents = ValueNotifier(_getEventsForDay(_selectedDay!));
  }

  @override
  void dispose() {
    _selectedEvents.dispose();
    super.dispose();
  }

  List<Event> _getEventsForDay(DateTime day) {
    return kEvents[day] ?? [];
  }

  List<Event> _getEventsForRange(DateTime start, DateTime end) {
    final days = daysInRange(start, end);

    return [
      for (final d in days) ..._getEventsForDay(d),
    ];
  }

  void _onDaySelected(DateTime selectedDay, DateTime focusedDay) {
    if (!isSameDay(_selectedDay, selectedDay)) {
      setState(() {
        _selectedDay = selectedDay;
        _focusedDay = focusedDay;
        _rangeStart = null; // Important to clean those
        _rangeEnd = null;
        _rangeSelectionMode = RangeSelectionMode.toggledOff;
      });

      _selectedEvents.value = _getEventsForDay(selectedDay);
    }
  }

  void _onRangeSelected(DateTime? start, DateTime? end, DateTime focusedDay) {
    setState(() {
      _selectedDay = null;
      _focusedDay = focusedDay;
      _rangeStart = start;
      _rangeEnd = end;
      _rangeSelectionMode = RangeSelectionMode.toggledOn;
    });

    if (start != null && end != null) {
      _selectedEvents.value = _getEventsForRange(start, end);
    } else if (start != null) {
      _selectedEvents.value = _getEventsForDay(start);
    } else if (end != null) {
      _selectedEvents.value = _getEventsForDay(end);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TableCalendar<Event>(
          firstDay: kFirstDay,
          lastDay: kLastDay,
          focusedDay: _focusedDay,
          selectedDayPredicate: (day) => isSameDay(_selectedDay, day),
          rangeStartDay: _rangeStart,
          rangeEndDay: _rangeEnd,
          calendarFormat: _calendarFormat,
          rangeSelectionMode: _rangeSelectionMode,
          eventLoader: _getEventsForDay,
          startingDayOfWeek: StartingDayOfWeek.monday,
          calendarStyle: CalendarStyle(
            outsideDaysVisible: false,
            markerSize: 6,
            markerDecoration: const BoxDecoration(
              color: BaseConfig.redColor1,
              shape: BoxShape.circle,
            ),
            todayTextStyle: const TextStyle(color: BaseConfig.appThemeColor1),
            todayDecoration: BoxDecoration(
              // color: BaseConfig.appThemeColor1.withValues(alpha:0.6),
              // shape: BoxShape.circle,
              border: Border.all(color: BaseConfig.appThemeColor1, width: 2),
            ),
            selectedTextStyle:
                const TextStyle(color: BaseConfig.appThemeColor1),
            selectedDecoration: BoxDecoration(
              // color: BaseConfig.appThemeColor1,
              // shape: BoxShape.circle,
              border: Border.all(
                color: BaseConfig.appThemeColor1,
              ),
            ),
            rangeStartDecoration: const BoxDecoration(
              color: BaseConfig.appThemeColor1,
              shape: BoxShape.circle,
            ),
            rangeEndDecoration: const BoxDecoration(
              color: BaseConfig.appThemeColor1,
              shape: BoxShape.circle,
            ),
            rangeHighlightColor:
                BaseConfig.appThemeColor1.withValues(alpha: 0.2),
          ),
          onDaySelected: _onDaySelected,
          onRangeSelected: _onRangeSelected,
          enabledDayPredicate: (date) {
            return !date
                .isBefore(DateTime(kToday.year, kToday.month, kToday.day));
          },
          onFormatChanged: (format) {
            if (_calendarFormat != format) {
              setState(() {
                _calendarFormat = format;
              });
            }
          },
          onPageChanged: (focusedDay) {
            _focusedDay = focusedDay;
          },
          calendarBuilders: CalendarBuilders(
            dowBuilder: (context, day) {
              if (day.weekday == DateTime.sunday) {
                final text = DateFormat.E().format(day);

                return Center(
                  child: Text(
                    text,
                    style: const TextStyle(color: BaseConfig.redColor),
                  ),
                );
              }

              return null;
            },
            markerBuilder: (context, day, events) {
              if (events.isNotEmpty) {
                return const Positioned(
                  right: 0,
                  left: 0,
                  bottom: 7,
                  child: Icon(
                    Icons.star,
                    color: BaseConfig.redColor1,
                    size: 14,
                  ),
                );
              }

              return null;
            },
            headerTitleBuilder: (context, day) {
              final text = DateFormat.yMMM().format(day);

              return Center(
                child: MediumTextNotoSans(
                  text: text,
                  fontWeight: FontWeight.bold,
                ),
              );
            },
          ),
        ),
        const SizedBox(height: 8.0),
        Expanded(
          child: ValueListenableBuilder<List<Event>>(
            valueListenable: _selectedEvents,
            builder: (context, events, _) {
              return Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: 12.0,
                  vertical: 4.0,
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    if (events.isEmpty) ...[
                      const Divider(),
                      const SmallTextNotoSans(
                        text: 'No events found for this day.',
                      ),
                    ],
                    if (events.isNotEmpty)
                      const Wrap(
                        crossAxisAlignment: WrapCrossAlignment.center,
                        spacing: 4,
                        children: [
                          MediumTextNotoSans(
                            text: 'Important Events',
                            size: 16,
                          ),
                          Icon(
                            Icons.star,
                            color: BaseConfig.redColor1,
                            size: 18,
                          ),
                        ],
                      ),
                    const SizedBox(height: 12.0),
                    Expanded(
                      child: ListView.builder(
                        itemCount: events.length,
                        physics: const AlwaysScrollableScrollPhysics(),
                        itemBuilder: (context, index) {
                          final event = events[index];
                          return Container(
                            margin: const EdgeInsets.only(bottom: 8.0),
                            decoration: BoxDecoration(
                              border: Border.all(),
                              borderRadius: BorderRadius.circular(12.0),
                            ),
                            child: ListTile(
                              onTap: () => dPrint(event.toString()),
                              title: MediumTextNotoSans(
                                text: event.label,
                                fontWeight: FontWeight.bold,
                              ),
                              subtitle: SmallTextNotoSans(
                                text: event.text,
                              ),
                            ),
                          );
                        },
                      ),
                    ),
                  ],
                ),
              );
            },
          ),
        ),
      ],
    );
  }
}
