import 'dart:math';

import 'package:flutter/material.dart';

class ProgressPainter extends CustomPainter {
  final double progress;
  final Paint _paint = Paint();
  final Gradient gradient;

  ProgressPainter({required this.progress, required this.gradient});

  @override
  void paint(Canvas canvas, Size size) {
    final double radius = size.width / 2;
    final Offset center = Offset(radius, radius);
    final double angle = 2 * pi * progress;

    _paint.shader =
        gradient.createShader(Rect.fromCircle(center: center, radius: radius));
    _paint.strokeWidth = 6;
    _paint.strokeCap = StrokeCap.round;
    _paint.style = PaintingStyle.stroke;

    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius),
      -pi / 2,
      angle,
      false,
      _paint,
    );
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}
