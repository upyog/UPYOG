import { useState } from "react";

const useSessionStorage = (key, initialValue) => {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const data = Digit.SessionStorage.get(key);
      return data ? data : initialValue;
    } catch (err) {
      return initialValue;
    }
  });

  // Avoid unnecessary state updates and sessionStorage writes when the new value
  // is the same as the existing value. For objects, reference comparison is not
  // sufficient because two objects with the same data have different references,
  // so deep comparison is used.
  const setValue = (value) => {
    try {
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      if (typeof valueToStore === "object" && valueToStore !== null) {
        if (JSON.stringify(valueToStore) === JSON.stringify(storedValue)) {
          return;
        }
      } else if (valueToStore === storedValue) {
        return;
      }
      setStoredValue(valueToStore);
      Digit.SessionStorage.set(key, valueToStore);
    } catch (err) {
    }
  };
  // Prevent resetting the state and writing to sessionStorage if the value is
  // already equal to the initial value. This avoids unnecessary re-renders and storage operations.
  const clearValue = () => {
    if (typeof initialValue === "object" && initialValue !== null) {
      if (JSON.stringify(initialValue) === JSON.stringify(storedValue)) {
        return;
      }
    } else if (initialValue === storedValue) {
      return;
    }
    setStoredValue(initialValue);
    Digit.SessionStorage.set(key, initialValue);
  };

  return [storedValue, setValue, clearValue];
};

export default useSessionStorage;
