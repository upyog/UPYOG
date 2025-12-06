import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/notification_controller.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class NotificationScreen extends StatefulWidget {
  const NotificationScreen({super.key});

  @override
  State<NotificationScreen> createState() => _NotificationScreenState();
}

class _NotificationScreenState extends State<NotificationScreen> {
  final _notificationController = Get.find<NotificationController>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: 'Notification',
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: FutureBuilder(
              future: _notificationController.getNotifications(),
              builder: (context, snapshot) {
                if (snapshot.hasData && snapshot.data!.isNotEmpty) {
                  var notifications = snapshot.data;
                  return ListView.builder(
                    itemCount: notifications!.length,
                    shrinkWrap: true,
                    itemBuilder: (context, index) {
                      final notification = notifications[index];
                      return Card(
                        child: ListTile(
                          title: Row(
                            children: [
                              const BigText(text: 'Title: '),
                              const SizedBox(
                                width: 20,
                              ),
                              BigText(text: notification.title!),
                            ],
                          ),
                          subtitle: Row(
                            children: [
                              const BigText(text: 'Body: '),
                              const SizedBox(
                                width: 20,
                              ),
                              BigText(text: notification.title!),
                            ],
                          ),
                          onTap: () {},
                        ),
                      );
                    },
                  );
                } else {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return showCircularIndicator();
                  } else if (snapshot.connectionState ==
                      ConnectionState.active) {
                    return showCircularIndicator();
                  } else {
                    return const Column(
                      children: [
                        Icon(Icons.notifications_off_outlined),
                        SizedBox(
                          height: 20.0,
                        ),
                        Center(
                          child: Text(
                            'Notification not available!',
                            style: TextStyle(
                              color: Colors.grey,
                              fontSize: 20.0,
                            ),
                          ),
                        ),
                      ],
                    );
                  }
                }
              },
            ),
          ),
        ),
      ),
    );
  }
}
