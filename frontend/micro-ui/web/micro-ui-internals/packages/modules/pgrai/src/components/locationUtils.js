
/**
 * Location Utilities
 * 
 * This file contains utility functions for handling location-related operations.
 * 
 * Functions:
 * 
 * 1. fetchCurrentLocation:
 *    - Fetches the current geographic location of the user using the browser's geolocation API.
 *    - Returns a promise that resolves with the latitude, longitude, and formatted coordinates.
 *    - Rejects with an error message if geolocation is not supported or if fetching the location fails.
 * 
 *    Parameters:
 *    - t: Translation function for internationalization.
 * 
 * 2. reverseGeocode:
 *    - Performs reverse geocoding to fetch address details based on latitude and longitude.
 *    - Uses the OpenStreetMap Nominatim API to retrieve address information.
 *    - Returns a formatted address string and detailed address components.
 *    - Throws an error if the API request fails or if the response is invalid.
 * 
 *    Parameters:
 *    - lat: Latitude of the location.
 *    - lng: Longitude of the location.
 *    - t: Translation function for internationalization.
 * 
 * Usage:
 * - These utilities can be used to fetch the user's current location and convert geographic coordinates into human-readable addresses.
 */
export const fetchCurrentLocation = async (t) => {
        return new Promise(async (resolve, reject) => {
          if (!("geolocation" in navigator)) {
            reject({ error: t("GEOLOCATION_NOT_SUPPORTED") });
            return;
          }
      
          try {
            const position = await new Promise((posResolve, posReject) => {
              navigator.geolocation.getCurrentPosition(posResolve, posReject);
            });
      
            const { latitude, longitude } = position.coords;
            const coords = `${latitude}, ${longitude}`;
            resolve({ coords, latitude, longitude });
      
          } catch (error) {
            console.error("Error getting location:", error);
            reject({ error: t("LOCATION_FETCH_FAILED") });
          }
        });
      };
      
      export const reverseGeocode = async (lat, lng, t) => {
        try {
          const response = await fetch(
            `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`
          );
          
          if (!response.ok) throw new Error("Failed to fetch address");
          
          const data = await response.json();
          if (data && data.address) {

            const addr = [
              data.address?.amenity,
              data.address?.road,
              data.address?.neighbourhood,
              data.address?.suburb,
              data.address?.city,
              data.address?.state,
              data.address?.postcode
            ]
            .filter(Boolean) // Removes undefined or null values
            .join(", ");
            const addressDetails = {
              houseNumber: data.address?.amenity,
              streetName: data.address?.road,
              city: data.address?.city,
              state: data.address?.state,
              state_Code: data.address?.state_district,
              pincode: data.address?.postcode
            };
            return { formatted: addr, addressDetails:addressDetails };
          }
        } catch (error) {
          console.error("Reverse geocoding error:", error);
          throw new Error(t("ADDRESS_FETCH_FAILED"));
        }
        return { formatted: "Address not available" };
      };