# Use the official Nginx base image
FROM nginx:latest

CMD mkdir /usr/share/nginx/html/common
# Copy the xyz file to the Nginx HTML directory
COPY . /usr/share/nginx/html/common/

# Expose port 80
EXPOSE 80

# Start Nginx when the container starts
CMD ["nginx", "-g", "daemon off;"]
