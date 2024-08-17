# First stage: Build
FROM ghcr.io/egovernments/alpine-node-builder-14:yarn AS build

RUN apk update && apk upgrade && \
    apk add --no-cache git>2.30.0

ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS "--max-old-space-size=8168"

COPY ${WORK_DIR} .

RUN ls -lah

RUN cd web/ \
    && ./install-deps.sh \
    && yarn install --frozen-lockfile \
    && yarn build:webpack

# Second stage: Serve with NGINX
FROM nginx:mainline-alpine

ENV WORK_DIR=/var/web/digit-ui

RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/web/build ${WORK_DIR}/
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf

