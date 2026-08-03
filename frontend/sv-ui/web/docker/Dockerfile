# =============================================================
# This is a multi-stage Dockerfile for the Street Vending UI app.
#
# Build Stage:
#   - Uses Node.js 22 Alpine as the build environment.
#   - Accepts WORK_DIR as a build argument to copy the source code.
#   - Increases Node.js heap memory to avoid out-of-memory errors.
#   - Runs install-deps.sh, yarn install, and yarn build to produce
#     the production build output.
#
# Production Stage:
#   - Copies the build output and custom nginx.conf from the build stage.
#   - Custom nginx.conf handles routing and serving of the React app.
# =============================================================

FROM node:22.0.0-alpine AS build
RUN apk update && apk upgrade
RUN apk add --no-cache git>2.30.0

ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS="--max-old-space-size=3100"

COPY ${WORK_DIR} .
RUN ls -lah

RUN cd web/ \
    && node -e 'const v8=require("v8"); console.log("Heap (MB):", v8.getHeapStatistics().heap_size_limit/(1024*1024))' \
    && yarn install \
    && yarn build 


FROM nginx:mainline-alpine
ENV WORK_DIR=/var/web/sv-ui

RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/web/build ${WORK_DIR}/
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf