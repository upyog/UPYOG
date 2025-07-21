# ----------- Stage 1: Build the React App -----------
FROM upyogio/alpine-node-builder-14:yarn AS build

# Install curl and update Node.js to v14.20.0
RUN apk add --no-cache curl && \
    curl -fsSL https://nodejs.org/dist/v14.20.0/node-v14.20.0-linux-x64.tar.xz | \
    tar -xJ -C /usr/local --strip-components=1 --no-same-owner && \
    node -v && npm -v


# Update packages and install Git (ensure git > 2.30.0)
RUN apk update && apk upgrade && \
    apk add --no-cache git>2.30.0

# Install all required dependencies using yarn
RUN yarn add @upyog/digit-ui-libraries@3.0.1 \
             @demodigit/digit-ui-module-dss@1.0.0 \
             @demodigit/digit-ui-module-pgr@3.0.4 \
             @demodigit/digit-ui-module-core@1.0.3 \
             @demodigit/digit-ui-module-common@1.7.0-beta.5 \
             @demodigit/digit-ui-module-ws@3.0.6 \
             @demodigit/digit-ui-module-commonpt@3.0.3 \
             @demodigit/upyog-ui-module-asset@3.0.1 \
             @demodigit/digit-ui-module-hrms@3.0.1 \
             @demodigit/digit-ui-module-pt@3.0.6 \
             @upyog/digit-ui-module-tl@3.0.7 \
             @upyog/digit-ui-module-obps@3.0.4 \
             @upyog/digit-ui-module-noc@3.0.4 \
             @upyog/digit-ui-module-mcollect@3.0.3 \
             @upyog/digit-ui-module-fsm@3.0.4 \
             @demodigit/digit-ui-react-components@1.0.0

# Set working directory
ARG WORK_DIR
WORKDIR /app

# Set memory and source map options
ENV NODE_OPTIONS="--max-old-space-size=16384"
ENV GENERATE_SOURCEMAP="false"

# Copy app source code
COPY ${WORK_DIR} .

# Verify structure
RUN ls -lah

# Build the project
RUN cd web/ \
    && node envs.js \
    && node -e 'console.log(require("v8").getHeapStatistics().heap_size_limit/(16384*16384))' \
    && ./install-deps.sh \
    && yarn install \
    && yarn build

# ----------- Stage 2: Serve using NGINX -----------
FROM nginx:mainline-alpine

ENV WORK_DIR=/var/web/digit-ui

# Create directory for the build
RUN mkdir -p ${WORK_DIR}

# Copy built files to nginx html folder
COPY --from=build /app/web/build ${WORK_DIR}/

# Copy custom nginx config
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf
