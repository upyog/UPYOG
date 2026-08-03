FROM node:22-alpine
FROM node:22-alpine

ARG WORK_DIR
ENV npm_config_cache=/tmp
WORKDIR /app

COPY ${WORK_DIR}/package.json ./

RUN yarn

COPY ${WORK_DIR} .
COPY ${WORK_DIR} .

CMD ["yarn", "start"]
