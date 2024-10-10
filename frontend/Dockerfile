FROM node:21 as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
RUN npm install @mediapipe/selfie_segmentation @mediapipe/camera_utils

COPY . .
RUN npm run build

FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
