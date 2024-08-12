import { OpenVidu } from 'openvidu-browser';
import { nextTick } from 'vue';
import VideoBackgroundRemoval from '@/assets/js/showView/VideoBackgroundRemoval';
//import * as cameraUtils from '@mediapipe/camera_utils'; // 카메라 유틸리티 임포트
//import * as SelfieSegmentation from '@mediapipe/selfie_segmentation'; // SelfieSegmentation 클래스 임포트

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER; // OpenVidu 서버 URL 환경 변수
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET; // OpenVidu 서버 시크릿 환경 변수

// WebGL 지원 여부를 확인하는 함수
function checkWebGLSupport() {
    const canvas = document.createElement('canvas');
    const gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');
    return !!gl; // WebGL 컨텍스트가 생성되었는지 여부를 반환
}

// 기존 세션에 참여하는 함수
export const joinExistingSession = async (session, publisher, subscribers, myVideo, sessionId, boothStore) => {
    try {
        const sessionInfo = boothStore.getSessionInfo(); // 부스 스토어에서 세션 정보 가져오기

        if (!sessionInfo || !sessionInfo.sessionId || !sessionInfo.token) {
            throw new Error('세션 정보가 없습니다.'); // 세션 정보가 없을 때 오류 발생
        }

        const { token } = sessionInfo; // 세션 토큰 가져오기

        const OV = new OpenVidu(); // OpenVidu 객체 생성

        OV.enableProdMode(false); // 프로덕션 모드를 비활성화
        OV.setAdvancedConfiguration({
            logLevel: 'DEBUG', // 디버그 로그 레벨 설정
            noStreamPlayingEventExceptionTimeout: 8000,
        });

        session.value = OV.initSession(); // 세션 초기화

        // 스트림이 생성되었을 때 실행되는 이벤트 핸들러
        session.value.on('streamCreated', async ({ stream }) => {
            const subscriber = await session.value.subscribe(stream); // 스트림 구독
            subscribers.value.push({ subscriber }); // 구독자 리스트에 추가

            // DOM이 업데이트된 후 실행
            nextTick(async () => {
                const video = document.getElementById(`video-${subscriber.stream.streamId}`);
                const canvas = document.getElementById(`canvas-${subscriber.stream.streamId}`);
                if (video && canvas) {
                    await initializeBackgroundRemoval(video, canvas); // 배경 제거 초기화
                }
            });
        });

        // 스트림이 종료되었을 때 실행되는 이벤트 핸들러
        session.value.on('streamDestroyed', ({ stream }) => {
            const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
            if (index >= 0) {
                subscribers.value.splice(index, 1); // 구독자 리스트에서 제거
            }
        });

        await session.value.connect(token); // 세션에 연결

        const devices = await OV.getDevices(); // 사용 가능한 장치 가져오기
        const videoDevices = devices.filter((device) => device.kind === 'videoinput'); // 비디오 입력 장치 필터링

        const publisherOptions = {
            audioSource: undefined,
            videoSource: undefined,
            publishAudio: true,
            publishVideo: true,
            resolution: '640x480',
            frameRate: 60,
            insertMode: 'APPEND',
            mirror: true,
        };

        publisher.value = await OV.initPublisherAsync(undefined, publisherOptions); // 퍼블리셔 초기화

        await session.value.publish(publisher.value); // 세션에 퍼블리셔 게시

        if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
            myVideo.value.srcObject = publisher.value.stream.getMediaStream(); // 내 비디오 엘리먼트에 스트림 연결
        }

        try {
            await applySegmentation(publisher); // 세그멘테이션 적용
        } catch (error) {
            console.error('세그멘테이션 적용 중 오류 발생:', error);
            alert('세그멘테이션 기능을 적용하는 데 문제가 발생했습니다. 새로고침 후 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('세션 참가 중 오류 발생:', error);
        if (error.name === 'DEVICE_ACCESS_DENIED') {
            alert('카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.');
        } else {
            alert(`오류 발생: ${error.message}`);
        }
    }
};

// 세그멘테이션을 적용하는 함수
export const applySegmentation = async (streamRef) => {
    try {
        if (!checkWebGLSupport()) {
            throw new Error('WebGL이 지원되지 않습니다. 세그멘테이션 기능을 사용할 수 없습니다.');
        }

        const actualStreamRef = streamRef.value || streamRef;

        if (!actualStreamRef || !actualStreamRef.stream) {
            throw new Error('스트림 참조가 유효하지 않습니다.');
        }

        const mediaStream = actualStreamRef.stream.getMediaStream();

        if (!mediaStream) {
            throw new Error('미디어 스트림을 가져올 수 없습니다.');
        }

        const videoElement = document.createElement('video');
        videoElement.srcObject = mediaStream;
        videoElement.muted = true;
        videoElement.playsInline = true;
        await videoElement.play(); // 비디오 엘리먼트 재생

        const selfieSegmentation = new window.SelfieSegmentation({
            locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
        });

        await selfieSegmentation.setOptions({ modelSelection: 1 }); // 세그멘테이션 옵션 설정
        await selfieSegmentation.initialize(); // 세그멘테이션 초기화

        const canvasElement = document.createElement('canvas');
        canvasElement.width = videoElement.videoWidth || 640;
        canvasElement.height = videoElement.videoHeight || 480;
        const canvasCtx = canvasElement.getContext('2d');

        // 세그멘테이션 결과를 처리하는 함수
        selfieSegmentation.onResults((results) => {
            try {
                canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
                canvasCtx.drawImage(results.segmentationMask, 0, 0, canvasElement.width, canvasElement.height);

                canvasCtx.globalCompositeOperation = 'source-in';
                canvasCtx.drawImage(results.image, 0, 0, canvasElement.width, canvasElement.height);

                const videoStream = canvasElement.captureStream(30); // 캔버스에서 비디오 스트림 생성
                const videoTrack = videoStream.getVideoTracks()[0];
                const originalStream = actualStreamRef.stream.getMediaStream();

                if (originalStream.getVideoTracks().length > 0) {
                    originalStream.removeTrack(originalStream.getVideoTracks()[0]); // 기존 비디오 트랙 제거
                }

                originalStream.addTrack(videoTrack); // 새 비디오 트랙 추가
            } catch (error) {
                console.error('onResults 콜백 내부 오류:', error);
            }
        });

        const camera = new window.Camera(videoElement, {
            onFrame: async () => {
                await selfieSegmentation.send({ image: videoElement }); // 세그멘테이션에 이미지 전송
            },
            width: videoElement.videoWidth || 640,
            height: videoElement.videoHeight || 480,
        });

        console.log('16. Camera 객체 생성됨');
        console.log('17. camera.start() 호출');
        await camera.start(); // 카메라 시작
        console.log('18. camera.start() 성공');

        return new Promise((resolve) => {
            setTimeout(() => {
                console.log('19. 세그멘테이션 처리 완료');
                resolve();
            }, 1000); // 1초 후에 완료로 간주
        });
    } catch (error) {
        console.error('세그멘테이션 적용 중 오류 발생:', error.message, error.stack);
        alert(`세그멘테이션 기능을 적용하는 데 문제가 발생했습니다: ${error.message}`);
        throw error;
    }
};

// 배경 제거를 초기화하는 함수
const initializeBackgroundRemoval = async (videoElement, canvasElement) => {
    if (!videoElement || !canvasElement) return;

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

    try {
        const newBackgroundRemoval = new VideoBackgroundRemoval();
        await newBackgroundRemoval.initialize();
        newBackgroundRemoval.startProcessing(videoElement, canvasElement);
    } catch (error) {
        console.error('MediaPipe 초기화 중 오류 발생:', error);
    }
};

