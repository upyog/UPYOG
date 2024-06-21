# Use the official Nginx base image
FROM nginx:latest

# Copy the xyz file to the Nginx HTML directory
COPY . /usr/share/nginx/html/

# Expose port 80
EXPOSE 80

# Start Nginx when the container starts
CMD ["nginx", "-g", "daemon off;"]
