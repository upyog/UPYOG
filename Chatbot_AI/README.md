
# Chatbot with Multilingual Support and FAQ Retrieval

![Chatbot Banner](https://img.freepik.com/free-vector/voice-assistant-abstract-concept-vector-illustration_335657-3969.jpg)

## Overview

This project is a Flask-based chatbot designed to handle FAQs using semantic similarity techniques. It supports multilingual queries and responses through Google Translate and Bhashini APIs, ensuring accessibility for users in diverse languages.

---

## Features

- **Natural Language Processing**:
  - Sentence Transformers for semantic embeddings.
  - FAISS for efficient similarity search.
  - Cosine similarity for precise matching.
  
- **Multilingual Support**:
  - Real-time language translation using Google Translate and Bhashini APIs.
  
- **RESTful API**:
  - Easily integrates with other systems via a POST API.

- **Customizable FAQ Dataset**:
  - Modify the FAQ dataset (`UpyogFAQ.csv`) for specific use cases.

---

## Technologies Used

- **Backend**: Flask
- **AI/ML**: Sentence Transformers, FAISS
- **Language Detection**: LangDetect
- **Multilingual Translation**: Google Translate API, Bhashini API
- **Data Processing**: Pandas, NumPy

---

## Installation

### Prerequisites

- Python 3.6 or later
- Pip (Python package installer)

### Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/chatbot-project.git
   cd chatbot-project
   ```

2. **Create a Virtual Environment**:
   ```bash
   python3 -m venv env
   source env/bin/activate  # On Windows, use env\Scripts\activate
   ```

3. **Install Dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

4. **Add the FAQ Dataset**:
   Place `UpyogFAQ.csv` in the root directory.

5. **Run the Application**:
   ```bash
   python app7.py
   ```

6. **Access the Application**:
   Open your browser and navigate to `http://127.0.0.1:7002`.

---

## Usage

### API Endpoint

**Endpoint**: `/chatbot`  
**Method**: POST  
**Request Format**:
```json
{
  "user_input": "Your query here"
}
```

**Response Format**:
```json
{
  "response": "Chatbot's response here"
}
```

### Example

#### Request
```json
{
  "user_input": "What is the purpose of this chatbot?"
}
```

#### Response
```json
{
  "response": "This chatbot helps retrieve FAQs and supports multilingual queries."
}
```

---

## FAQ Dataset Format

The chatbot uses a CSV file (`UpyogFAQ.csv`) with the following columns:

- `prompt`: The question or trigger text.
- `response`: The corresponding answer.

---

## Environment Variables

Replace sensitive API keys and tokens with environment variables for better security:

- `ulcaApiKey`
- `userID`
- `Authorization`

Use a `.env` file and the `python-dotenv` library to manage these variables.

---

## Dependencies

Here is the list of Python libraries used in this project:

```plaintext
flask
flask-cors
sentence-transformers
faiss-cpu
numpy
pandas
requests
langdetect
scikit-learn
googletrans==4.0.0-rc1
```

To install all dependencies:
```bash
pip install -r requirements.txt
```

---

## Future Enhancements

- Add a user-friendly front-end interface.
- Enhance FAQ retrieval accuracy with custom fine-tuned models.
- Enable voice query capabilities using speech-to-text APIs.

---

## Contact

For any questions or feedback, feel free to contact:

- **Name**: Nisheet Agrawal  
- **Email**: [Nisheetag@gmail.com](mailto:Nisheetag@gmail.com)  

---


