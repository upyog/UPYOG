FROM python:3.12-slim-bookworm

RUN apt-get update && apt-get install -y ffmpeg build-essential && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .

# Install CPU-only torch first to avoid pulling the full 2GB GPU version
RUN pip install --no-cache-dir torch --index-url https://download.pytorch.org/whl/cpu
RUN pip install --no-cache-dir -r requirements.txt

# Pre-download both models to ensure the pod boots instantly
RUN python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('all-mpnet-base-v2')"
RUN python -c "import whisper; whisper.load_model('tiny')"

COPY . .

EXPOSE 8090

CMD ["python", "app.py"]
