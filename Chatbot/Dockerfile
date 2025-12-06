# Use a slim Python image
FROM python:3.9-slim

# Set the working directory in the Docker container
WORKDIR /app

# Copy the requirements.txt file into the container
COPY requirements.txt /app/

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of your application's code
COPY . /app/

# Create directory for SSL certificates
RUN mkdir -p /etc/letsencrypt/live/upyogchatbot.niua.in

# Copy SSL certificates
COPY ssl /etc/letsencrypt/live/upyogchatbot.niua.in/

# Expose port 8000 for the Flask app
EXPOSE 8000

# Command to run the Flask application
CMD ["python", "app7.py"]
