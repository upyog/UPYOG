import 'package:flutter/material.dart';
import 'package:flutter_rating_bar/flutter_rating_bar.dart';
import 'package:mobile_app/config/base_config.dart';

class RatingBarApp extends StatelessWidget {
  const RatingBarApp({
    super.key,
    required this.rating,
    required this.onRatingUpdate,
    this.size = 24,
  });

  final int rating;
  final void Function(double) onRatingUpdate;
  final double size;

  @override
  Widget build(BuildContext context) {
    return RatingBar.builder(
      initialRating: double.parse(rating.toString()),
      direction: Axis.horizontal,
      itemCount: 5,
      itemPadding: const EdgeInsets.symmetric(horizontal: 2.0),
      itemBuilder: (context, _) => const Icon(
        Icons.star,
        color: BaseConfig.ratingColor,
      ),
      ignoreGestures: true,
      itemSize: size,
      unratedColor: BaseConfig.ratingUnratedColor,
      onRatingUpdate: onRatingUpdate,
    );
  }
}
