import { useState, useCallback, useRef } from "react";

const useSessionStorage = (key, initialValue) => {
  const initialValueRef = useRef(initialValue);

  const [storedValue, setStoredValue] = useState(() => {
    try {
      const data = Digit.SessionStorage.get(key);
      return data ? data : initialValueRef.current;
    } catch (err) {
      return initialValueRef.current;
    }
  });

  const setValue = useCallback((value) => {
    setStoredValue((prevStoredValue) => {
      try {
        const valueToStore = value instanceof Function ? value(prevStoredValue) : value;
        Digit.SessionStorage.set(key, valueToStore);
        return valueToStore;
      } catch (err) {
        return prevStoredValue;
      }
    });
  }, [key]);

  const clearValue = useCallback(() => {
    setStoredValue(initialValueRef.current);
    try {
      Digit.SessionStorage.set(key, initialValueRef.current);
    } catch (err) {
    }
  }, [key]);

  return [storedValue, setValue, clearValue];
};

export default useSessionStorage;
