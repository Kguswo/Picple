import { OpenVidu } from 'openvidu-browser';
import { nextTick } from 'vue';
import VideoBackgroundRemoval from '@/assets/js/showView/VideoBackgroundRemoval';
import { storeToRefs } from 'pinia';

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

export const joinExistingSession = async (publisher, subscribers, myVideo, boothStore, addSubscriber, removeSubscriber) => {
  try {
    const sessionInfo = boothStore.getSessionInfo();
    const session = storeToRefs(boothStore);

    if (!sessionInfo || !sessionInfo.sessionId || !sessionInfo.token) {
      throw new Error('세션 정보가 없습니다.');
    }

    const { token } = sessionInfo;
    const OV = new OpenVidu();

    OV.setAdvancedConfiguration({
      logLevel: 'DEBUG',
      iceServers: [{ urls: 'stun:stun.l.google.com:19302' }],
    });

    session.value = OV.initSession();

    session.value.on('streamCreated', async ({ stream }) => {
      const subscriber = await session.value.subscribe(stream);
      addSubscriber(subscriber);
      subscribers.value.push({ subscriber });

      nextTick(async () => {
        const video = document.getElementById(`video-${subscriber.stream.streamId}`);
        const canvas = document.getElementById(`canvas-${subscriber.stream.streamId}`);
        if (video && canvas) {
          await initializeBackgroundRemoval(video, canvas);
        }
      });
    });

    session.value.on('streamDestroyed', ({ stream }) => {
      const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
      removeSubscriber(stream.streamId);
      if (index >= 0) {
        subscribers.value.splice(index, 1);
      }
    });

    await session.value.connect(token);

    const publisherOptions = {
      audioSource: undefined,
      videoSource: undefined,
      publishAudio: true,
      publishVideo: true,
      resolution: '640x480',
      frameRate: 30,
      insertMode: 'APPEND',
      mirror: true,
    };

    publisher.value = await OV.initPublisherAsync(undefined, publisherOptions);
    await session.value.publish(publisher.value);

    if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
      myVideo.value.srcObject = publisher.value.stream.getMediaStream();
    }

    const canvasElement = document.createElement('canvas');
    applySegmentation(publisher, canvasElement);
  } catch (error) {
    console.error('세션 참가 중 오류 발생:', error);
  }
};

const applySegmentation = (streamRef, canvasElement) => {
  const actualStreamRef = streamRef.value || streamRef;
  if (!actualStreamRef || !actualStreamRef.stream) return;

  const mediaStream = actualStreamRef.stream.getMediaStream();
  if (!mediaStream) return;

  const videoElement = document.createElement('video');
  videoElement.srcObject = mediaStream;

  const backgroundRemoval = new VideoBackgroundRemoval(canvasElement);
  backgroundRemoval.startProcessing(videoElement, canvasElement);

  const videoTrack = canvasElement.captureStream(30).getVideoTracks()[0];
  const originalStream = actualStreamRef.stream.getMediaStream();

  if (originalStream.getVideoTracks().length > 0) {
    originalStream.removeTrack(originalStream.getVideoTracks()[0]);
  }
  originalStream.addTrack(videoTrack);
};

const initializeBackgroundRemoval = async (videoElement, canvasElement) => {
  await new Promise((resolve) => {
    const checkVideo = () => {
      if (videoElement.readyState >= 2 && videoElement.videoWidth > 0 && videoElement.videoHeight > 0) {
        canvasElement.width = videoElement.videoWidth;
        canvasElement.height = videoElement.videoHeight;
        resolve();
      } else {
        requestAnimationFrame(checkVideo);
      }
    };
    checkVideo();
  });

  const backgroundRemoval = new VideoBackgroundRemoval(canvasElement);
  await backgroundRemoval.initialize();
  backgroundRemoval.startProcessing(videoElement, canvasElement);
};
