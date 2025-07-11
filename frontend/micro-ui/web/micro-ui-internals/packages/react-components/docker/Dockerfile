FROM egovio/alpine-node-builder-14:yarn AS build
RUN apk update && apk upgrade
RUN apk add --no-cache git>2.30.0
RUN yarn add @upyog/digit-ui-libraries@1.7.0-beta.5
RUN yarn add @upyog/digit-ui-module-pt@1.7.0-beta.4
RUN yarn add @upyog/digit-ui-module-fsm@1.7.0-beta.18
RUN yarn add @upyog/digit-ui-module-tl@1.7.0-beta.8
RUN yarn add @demodigit/digit-ui-module-dss@1.0.0
RUN yarn add @upyog/digit-ui-module-mcollect
RUN yarn add @demodigit/digit-ui-module-pgr@3.0.4
RUN yarn add @demodigit/digit-ui-module-engagement@3.0.3
RUN yarn add @upyog/digit-ui-module-hrms
RUN yarn add @demodigit/digit-ui-module-core@1.7.0-beta.3
RUN yarn add @upyog/digit-ui-module-obps
RUN yarn add @demodigit/digit-ui-module-common
RUN yarn add @demodigit/digit-ui-module-ws
RUN yarn add @demodigit/digit-ui-module-commonpt
RUN yarn add @upyog/digit-ui-module-bills
RUN yarn add @upyog-niua/upyog-ui-module-ptr

ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS "--max-old-space-size=1792"

COPY ${WORK_DIR} .

#RUN node web/envs.js
RUN node envs.js \
    && yarn install \
    && yarn build-storybook

FROM nginx:mainline-alpine
ENV WORK_DIR=/var/web/storybook

RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/build ${WORK_DIR}/
COPY --from=build /app/docker/nginx.conf /etc/nginx/conf.d/default.conf