# Stage 1: Build
FROM upyogio/alpine-node-builder-14:yarn AS build

# Update and upgrade apk packages
RUN apk update && apk upgrade

# Install a specific version of Git
RUN apk add --no-cache git>2.30.0

# Install dependencies
RUN yarn add @egovernments/digit-ui-module-utilities \
    @egovernments/digit-ui-react-components \
    @egovernments/digit-ui-components \
    @egovernments/digit-ui-libraries \
    @egovernments/digit-ui-module-dss \
    @egovernments/digit-ui-module-mcollect \
    @egovernments/digit-ui-module-pgr \
    @egovernments/digit-ui-module-noc \
    @egovernments/digit-ui-module-engagement \
    @egovernments/digit-ui-module-hrms \
    @egovernments/digit-ui-module-core \
    @egovernments/digit-ui-module-common \
    @egovernments/digit-ui-module-commonpt \
    @egovernments/digit-ui-module-workbench \
    @tattvafoundation/digit-ui-module-bmc \
    @tattvafoundation/digit-ui-module-deonar \
    @upyog/digit-ui-module-ws \
    @upyog/digit-ui-module-pt \
    @upyog/digit-ui-module-fsm \
    @upyog/digit-ui-module-tl \
    @upyog/digit-ui-module-obps

# Set the working directory
ARG WORK_DIR
WORKDIR /app

# Environment variables
ENV NODE_OPTIONS "--max-old-space-size=4096"
ENV GENERATE_SOURCEMAP "false"

# Copy the source code
COPY ${WORK_DIR} .

# Run the necessary commands
RUN cd web/ \
    && node envs.js \
    && node -e 'console.log(v8.getHeapStatistics().heap_size_limit/(4096*4096))' \
    && ./install-deps.sh \
    && yarn install \
    && yarn build

# Stage 2: Serve with Nginx
FROM nginx:mainline-alpine

# Set the working directory for the Nginx server
ENV WORK_DIR=/var/web/digit-ui

# Create the directory if it doesn’t exist
RUN mkdir -p ${WORK_DIR}

# Copy the build files from the previous stage
COPY --from=build /app/web/build ${WORK_DIR}/

# Copy the custom Nginx configuration
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf

