import { OpenVidu } from "openvidu-browser";
import { nextTick } from "vue";
import VideoBackgroundRemoval from "@/assets/js/showView/VideoBackgroundRemoval";

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

let selfieSegmentation;

export async function waitForVideoSize(videoElement, maxAttempts = 20, interval = 250) {
    return new Promise((resolve, reject) => {
        let attempts = 0;
        const checkSize = () => {
        attempts++;
        if (videoElement.readyState >= 2 && videoElement.videoWidth > 0 && videoElement.videoHeight > 0) {
            console.log(`비디오 크기 확인됨: ${videoElement.videoWidth}x${videoElement.videoHeight}`);
            resolve({ width: videoElement.videoWidth, height: videoElement.videoHeight });
        } else if (attempts >= maxAttempts) {
            console.error('최대 시도 횟수 초과: 비디오 크기를 확인할 수 없음');
            reject(new Error('비디오 크기를 확인할 수 없음'));
        } else {
            console.log(`비디오 크기 대기 중... 시도: ${attempts}`);
            setTimeout(checkSize, interval);
        }
        };
        checkSize();
    });
}

export const joinExistingSession = async (session, publisher, subscribers, myVideo, sessionId, boothStore) => {
  try {
    const sessionInfo = boothStore.getSessionInfo();

    if (!sessionInfo || !sessionInfo.sessionId || !sessionInfo.token) {
      throw new Error("세션 정보가 없습니다.");
    }

    const { token } = sessionInfo;

    const OV = new OpenVidu();

    OV.enableProdMode(false);
    OV.setAdvancedConfiguration({
      logLevel: "DEBUG",
      noStreamPlayingEventExceptionTimeout: 8000,
      iceServers: [
        { urls: "stun:stun.l.google.com:19302" },
        {
          urls: [
            "turn:i11a503.p.ssafy.io:3478",
            "turn:i11a503.p.ssafy.io:3478?transport=tcp",
            "turns:i11a503.p.ssafy.io:3479",
          ],
          username: "picplessafy",
          credential: "ssafya503@picple",
        },
      ],
      iceTransportPolicy: "all",
      forceMediaReconnectionAfterNetworkDrop: true,
      publisherSpeakingEventsOptions: {
        interval: 100,
        threshold: -50,
      },
      videoSimulcast: false,
      videoSendInitialDelay: 0,
      videoDimensions: "640x480",
      minVideoBitrate: 300,
      maxVideoBitrate: 1000,
    });

    session.value = OV.initSession();

    session.value.on("streamCreated", (event) => {
        console.log(`새 스트림 생성됨: ${event.stream.streamId}`);
        const subscriber = session.value.subscribe(event.stream, undefined);
        subscribers.value.push({ stream: event.stream, subscriber: subscriber });
        console.log(`구독자 목록에 새 구독자 추가됨: ${event.stream.streamId}`);
  
        subscriber.on('videoElementCreated', (event) => {
          console.log(`비디오 요소 생성됨: ${subscriber.stream.streamId}`);
          const videoElement = event.element;
          const canvasElement = document.createElement('canvas');
          canvasElement.id = `canvas-${subscriber.stream.streamId}`;
          videoElement.parentNode.insertBefore(canvasElement, videoElement.nextSibling);
  
          initializeBackgroundRemoval(videoElement, canvasElement, subscriber.stream.streamId);
        });
      });

    session.value.on("streamDestroyed", ({ stream }) => {
        const index = subscribers.value.findIndex( (sub) => sub.stream.streamId === stream.streamId);
        if (index >= 0) {
            subscribers.value.splice(index, 1);
        }
    });

    await session.value.connect(token);

    const devices = await OV.getDevices();
    const videoDevices = devices.filter(
      (device) => device.kind === "videoinput"
    );

    const publisherOptions = {
      audioSource: undefined,
      videoSource: undefined,
      publishAudio: true,
      publishVideo: true,
      resolution: "640x480",
      frameRate: 60,
      insertMode: "APPEND",
      mirror: true,
    };

    publisher.value = await OV.initPublisherAsync(undefined, publisherOptions);

    await session.value.publish(publisher.value);

    if (
      myVideo.value &&
      publisher.value.stream &&
      publisher.value.stream.getMediaStream()
    ) {
      myVideo.value.srcObject = publisher.value.stream.getMediaStream();
    }

    applySegmentation(publisher);
  } catch (error) {
    console.error("세션 참가 중 오류 발생:", error);
    if (error.name === "DEVICE_ACCESS_DENIED") {
      alert(
        "카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요."
      );
    } else {
      alert(`오류 발생: ${error.message}`);
    }
  }
};

const applySegmentation = (streamRef) => {
  const actualStreamRef = streamRef.value || streamRef;

  if (!actualStreamRef || !actualStreamRef.stream) return;

  const mediaStream = actualStreamRef.stream.getMediaStream();

  if (!mediaStream) return;

  const videoElement = document.createElement("video");
  videoElement.srcObject = mediaStream;

  selfieSegmentation = new window.SelfieSegmentation({
    locateFile: (file) =>
      `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
  });

  selfieSegmentation.setOptions({
    modelSelection: 1,
  });

  const onResults = (results) => {
    const canvasElement = document.createElement("canvas");
    const canvasCtx = canvasElement.getContext("2d");

    canvasElement.width = results.image.width;
    canvasElement.height = results.image.height;

    canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
    canvasCtx.drawImage(
      results.segmentationMask,
      0,
      0,
      canvasElement.width,
      canvasElement.height
    );

    canvasCtx.globalCompositeOperation = "source-in";
    canvasCtx.drawImage(
      results.image,
      0,
      0,
      canvasElement.width,
      canvasElement.height
    );

    const videoStream = canvasElement.captureStream(30);
    const videoTrack = videoStream.getVideoTracks()[0];
    const originalStream = actualStreamRef.stream.getMediaStream();

    if (originalStream.getVideoTracks().length > 0) {
      originalStream.removeTrack(originalStream.getVideoTracks()[0]);
    }

    originalStream.addTrack(videoTrack);
  };

  selfieSegmentation.onResults(onResults);

  const camera = new window.Camera(videoElement, {
    onFrame: async () => {
      await selfieSegmentation.send({ image: videoElement });
    },
    width: 640,
    height: 480,
  });

  camera.start();
};

const initializeBackgroundRemoval = (videoElement, canvasElement, streamId) => {
    const checkVideoReady = () => {
      if (videoElement.readyState >= 2 && videoElement.videoWidth > 0 && videoElement.videoHeight > 0) {
        console.log(`비디오 준비 완료: ${streamId}, 크기: ${videoElement.videoWidth}x${videoElement.videoHeight}`);
        canvasElement.width = videoElement.videoWidth;
        canvasElement.height = videoElement.videoHeight;
  
        const backgroundRemoval = new VideoBackgroundRemoval();
        backgroundRemoval.initialize(canvasElement)
          .then(() => {
            backgroundRemoval.startProcessing(videoElement, canvasElement);
          })
          .catch((error) => {
            console.error(`배경 제거 초기화 실패: ${streamId}`, error);
          });
      } else {
        console.log(`비디오 아직 준비되지 않음: ${streamId}, 재시도...`);
        requestAnimationFrame(checkVideoReady);
      }
    };
  
    videoElement.addEventListener('loadedmetadata', () => {
      console.log(`비디오 메타데이터 로드됨: ${streamId}`);
      checkVideoReady();
    });
  
    // 이미 메타데이터가 로드되었을 경우를 대비
    if (videoElement.readyState >= 1) {
      checkVideoReady();
    }
  };
