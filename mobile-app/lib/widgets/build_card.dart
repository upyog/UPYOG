import 'package:flutter/material.dart';

class BuildCard extends StatelessWidget {
  const BuildCard({super.key, required this.child, this.padding = 10.0});

  final Widget child;
  final double padding;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: EdgeInsets.all(padding),
        child: ListTile(
          contentPadding: EdgeInsets.zero,
          subtitle: child,
        ),
      ),
    );
  }
}
