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
      console.log('[시작] 세션 참가 프로세스 시작');
      const sessionInfo = boothStore.getSessionInfo();
  
      if (!sessionInfo || !sessionInfo.sessionId || !sessionInfo.token) {
        throw new Error("세션 정보가 없습니다.");
      }
  
      const { token } = sessionInfo;
      console.log('[정보] 세션 토큰 획득');
  
      const OV = new OpenVidu();
      console.log('[OV] OpenVidu 인스턴스 생성');
  
      OV.enableProdMode(false);
      const advancedConfiguration = {
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
      };
      OV.setAdvancedConfiguration(advancedConfiguration);
      console.log('[OV] OpenVidu 고급 설정 적용:', advancedConfiguration);
  
      session.value = OV.initSession();
      console.log('[세션] OpenVidu 세션 초기화');
  
      session.value.on("streamCreated", (event) => {
        console.log(`[1] 새 스트림 감지됨: ${event.stream.streamId}`);
        console.log(`[1-1] 스트림 세부 정보:`, {
          hasAudio: event.stream.hasAudio,
          hasVideo: event.stream.hasVideo,
          audioActive: event.stream.audioActive,
          videoActive: event.stream.videoActive,
          typeOfVideo: event.stream.typeOfVideo,
          frameRate: event.stream.frameRate,
          videoDimensions: event.stream.videoDimensions
        });
  
        const subscriber = session.value.subscribe(event.stream, undefined, {
          insertMode: 'APPEND',
          publishAudio: true,
          publishVideo: true
        });
        console.log(`[2] 스트림 구독 시작: ${event.stream.streamId}`);
        console.log(`[2-1] 구독자 객체:`, subscriber);
        
        subscriber.on('connectionStateChanged', (event) => {
          console.log(`[2-2] 연결 상태 변경: ${event.connectionId} - ${event.newState}`);
          if (event.newState === 'connected') {
            console.log(`[2-3] ICE 연결 성공: ${subscriber.stream.streamId}`);
          } else if (event.newState === 'disconnected' || event.newState === 'failed') {
            console.error(`[오류] ICE 연결 실패: ${subscriber.stream.streamId}`);
          }
        });
  
        subscriber.on('streamManagerStarted', () => {
          console.log(`[2-4] 스트림 매니저 시작됨: ${subscriber.stream.streamId}`);
        });
  
        subscriber.on('videoElementCreated', (event) => {
          console.log(`[4] 비디오 요소 생성됨: ${subscriber.stream.streamId}`);
          const videoElement = event.element;
          console.log(`[5] 비디오 요소 속성 - 너비: ${videoElement.width}, 높이: ${videoElement.height}`);
          const canvasElement = document.createElement('canvas');
          canvasElement.id = `canvas-${subscriber.stream.streamId}`;
          videoElement.parentNode.insertBefore(canvasElement, videoElement.nextSibling);
          console.log(`[6] 캔버스 요소 생성 및 추가됨: ${canvasElement.id}`);
  
          initializeBackgroundRemoval(videoElement, canvasElement, subscriber.stream.streamId);
        });
  
        subscriber.on('streamPlaying', () => {
          console.log(`[7] 스트림 재생 시작됨: ${subscriber.stream.streamId}`);
          const peerConnection = subscriber.getPeerConnection();
          console.log(`[7-1] ICE 연결 상태: ${peerConnection.iceConnectionState}`);
          
          peerConnection.addEventListener('iceconnectionstatechange', () => {
            console.log(`[7-2] ICE 연결 상태 변경: ${peerConnection.iceConnectionState}`);
          });
        });
  
        subscriber.on('error', (error) => {
          console.error(`[오류] 구독자 에러 발생: ${subscriber.stream.streamId}`, error);
        });
  
        subscribers.value.push({ stream: event.stream, subscriber: subscriber });
        console.log(`[3] 구독자 목록에 새 구독자 추가됨: ${event.stream.streamId}`);
      });
  
      session.value.on("streamDestroyed", ({ stream }) => {
        console.log(`[정보] 스트림 제거됨: ${stream.streamId}`);
        const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
        if (index >= 0) {
          subscribers.value.splice(index, 1);
          console.log(`[정보] 구독자 목록에서 제거됨: ${stream.streamId}`);
        }
      });
  
      session.value.on("exception", (exception) => {
        console.warn("[경고] OpenVidu 예외 발생:", exception);
      });
  
      await session.value.connect(token);
      console.log('[세션] 세션 연결 완료');
  
      const devices = await OV.getDevices();
      const videoDevices = devices.filter((device) => device.kind === "videoinput");
      console.log('[장치] 사용 가능한 비디오 장치:', videoDevices);
  
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
      console.log('[게시자] 게시자 초기화 완료');
  
      publisher.value.on('streamCreated', (event) => {
        console.log('[게시자] 로컬 스트림 생성됨:', event.stream.streamId);
      });
  
      publisher.value.on('streamPlaying', () => {
        console.log('[게시자] 로컬 스트림 재생 시작');
      });
  
      await session.value.publish(publisher.value);
      console.log('[게시자] 스트림 게시 완료');
  
      if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
        myVideo.value.srcObject = publisher.value.stream.getMediaStream();
        console.log('[비디오] 로컬 비디오 스트림 설정 완료');
      }
  
      applySegmentation(publisher);
      console.log('[세그멘테이션] 배경 제거 적용 시작');
    } catch (error) {
      console.error("[오류] 세션 참가 중 오류 발생:", error);
      if (error.name === "DEVICE_ACCESS_DENIED") {
        alert("카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.");
      } else {
        alert(`오류 발생: ${error.message}`);
      }
    }
  };

const applySegmentation = (streamRef) => {
  console.log('[세그멘테이션] 배경 제거 프로세스 시작');
  const actualStreamRef = streamRef.value || streamRef;

  if (!actualStreamRef || !actualStreamRef.stream) {
    console.error('[오류] 스트림 참조가 유효하지 않음');
    return;
  }

  const mediaStream = actualStreamRef.stream.getMediaStream();

  if (!mediaStream) {
    console.error('[오류] 미디어 스트림을 가져올 수 없음');
    return;
  }

  const videoElement = document.createElement("video");
  videoElement.srcObject = mediaStream;
  console.log('[세그멘테이션] 비디오 요소 생성 및 스트림 연결');

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
  console.log(`[8] 배경 제거 초기화 시작: ${streamId}`);

  const checkVideoReady = () => {
    if (videoElement.readyState >= 2 && videoElement.videoWidth > 0 && videoElement.videoHeight > 0) {
      console.log(`[9] 비디오 준비 완료: ${streamId}, 크기: ${videoElement.videoWidth}x${videoElement.videoHeight}`);
      canvasElement.width = videoElement.videoWidth;
      canvasElement.height = videoElement.videoHeight;
      console.log(`[10] 캔버스 크기 설정됨: ${canvasElement.width}x${canvasElement.height}`);

      const backgroundRemoval = new VideoBackgroundRemoval();
      console.log(`[11] VideoBackgroundRemoval 인스턴스 생성됨: ${streamId}`);
      backgroundRemoval.initialize(canvasElement)
        .then(() => {
          console.log(`[12] 배경 제거 초기화 완료: ${streamId}`);
          backgroundRemoval.startProcessing(videoElement, canvasElement);
          console.log(`[13] 배경 제거 처리 시작됨: ${streamId}`);
        })
        .catch((error) => {
          console.error(`[오류] 배경 제거 초기화 실패: ${streamId}`, error);
        });
    } else {
      console.log(`[대기] 비디오 아직 준비되지 않음: ${streamId}, 재시도...`);
      requestAnimationFrame(checkVideoReady);
    }
  };

  videoElement.addEventListener('loadedmetadata', () => {
    console.log(`[14] 비디오 메타데이터 로드됨: ${streamId}`);
    checkVideoReady();
  });

  if (videoElement.readyState >= 1) {
    console.log(`[15] 비디오 이미 준비됨, 즉시 처리 시작: ${streamId}`);
    checkVideoReady();
  }
};