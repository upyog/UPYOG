import 'package:flutter/material.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class EmptyBox extends StatelessWidget {
  final String text;
  const EmptyBox({super.key, required this.text});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(
            Icons.cancel_outlined,
            size: 40,
            color: Colors.grey,
          ),
          const SizedBox(
            height: 10,
          ),
          MediumText(
            text: text,
            color: Colors.grey,
          ),
        ],
      ),
    );
  }
}
