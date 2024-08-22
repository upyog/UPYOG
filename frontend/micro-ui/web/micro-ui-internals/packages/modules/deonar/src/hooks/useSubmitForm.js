import { useState } from "react";

const useSubmitForm = (url) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [response, setResponse] = useState(null);
  const [error, setError] = useState(null);

  const submitForm = async (formData) => {
    setIsSubmitting(true);
    setError(null);

    try {
      const jsonData = JSON.stringify(formData);
      console.log("Generated JSON:", jsonData);

      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: jsonData,
      });

      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }

      const responseData = await response.json();
      console.log("Response from server:", responseData);
      setResponse(responseData);

      return responseData;
    } catch (err) {
      console.error("Failed to submit form data:", err);
      setError(err);
      throw err;
    } finally {
      setIsSubmitting(false);
    }
  };

  return { submitForm, isSubmitting, response, error };
};

export default useSubmitForm;
