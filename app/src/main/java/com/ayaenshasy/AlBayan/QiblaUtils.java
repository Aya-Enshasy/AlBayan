package com.ayaenshasy.AlBayan;

public class QiblaUtils {
    public static final double KAABA_LATITUDE = 21.4225;
    public static final double KAABA_LONGITUDE = 39.8262;

    public static double getQiblaDirection(double latitude, double longitude) {
        double latitudeRad = Math.toRadians(latitude);
        double longitudeRad = Math.toRadians(longitude);
        double kaabaLatitudeRad = Math.toRadians(KAABA_LATITUDE);
        double kaabaLongitudeRad = Math.toRadians(KAABA_LONGITUDE);

        double y = Math.sin(kaabaLongitudeRad - longitudeRad);
        double x = Math.cos(latitudeRad) * Math.tan(kaabaLatitudeRad) - Math.sin(latitudeRad) * Math.cos(kaabaLongitudeRad - longitudeRad);
        double qiblaDirection = Math.atan2(y, x);

        return Math.toDegrees(qiblaDirection);
    }
}

