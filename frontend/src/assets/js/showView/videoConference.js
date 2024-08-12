import { OpenVidu } from 'openvidu-browser';
import { nextTick } from 'vue';
import VideoBackgroundRemoval from '@/assets/js/showView/VideoBackgroundRemoval';
import * as camerUtils from '@mediapipe/camera_utils';
const { Camera } = camerUtils;

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

const initializeSelfieSegmentation = async () => {
    try {
        await window.loadSelfieSegmentation();
        const { SelfieSegmentation } = window;
        if (!SelfieSegmentation) {
            throw new Error('SelfieSegmentation is not loaded');
        }
        const selfieSegmentation = new SelfieSegmentation({
            locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
        });
        await selfieSegmentation.setOptions({
            modelSelection: 1,
        });
        await selfieSegmentation.initialize();
        return selfieSegmentation;
    } catch (error) {
        console.error('SelfieSegmentation 초기화 중 오류 발생:', error);
        throw error;
    }
};

export const joinExistingSession = async (session, publisher, subscribers, myVideo, sessionId, boothStore) => {
    try {
        const sessionInfo = boothStore.getSessionInfo();

        if (!sessionInfo || !sessionInfo.sessionId || !sessionInfo.token) {
            throw new Error('세션 정보가 없습니다.');
        }

        const { token } = sessionInfo;

        const OV = new OpenVidu();

        OV.enableProdMode(false);
        OV.setAdvancedConfiguration({
            logLevel: 'DEBUG',
            noStreamPlayingEventExceptionTimeout: 8000,
        });

        session.value = OV.initSession();

        session.value.on('streamCreated', async ({ stream }) => {
            const subscriber = await session.value.subscribe(stream);
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
            if (index >= 0) {
                subscribers.value.splice(index, 1);
            }
        });

        await session.value.connect(token);

        const devices = await OV.getDevices();
        const videoDevices = devices.filter((device) => device.kind === 'videoinput');

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

        publisher.value = await OV.initPublisherAsync(undefined, publisherOptions);

        await session.value.publish(publisher.value);

        if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
            myVideo.value.srcObject = publisher.value.stream.getMediaStream();
        }

        try {
            await applySegmentation(publisher);
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

// applySegmentation 함수 수정
const applySegmentation = async (streamRef) => {
    console.log('applySegmentation 함수 시작');
    const actualStreamRef = streamRef.value || streamRef;
    console.log('actualStreamRef:', actualStreamRef);

    if (!actualStreamRef || !actualStreamRef.stream) {
        console.log('actualStreamRef 또는 stream이 없음');
        return;
    }

    const mediaStream = actualStreamRef.stream.getMediaStream();
    console.log('mediaStream:', mediaStream);

    if (!mediaStream) {
        console.log('mediaStream이 없음');
        return;
    }

    const videoElement = document.createElement('video');
    videoElement.srcObject = mediaStream;
    videoElement.muted = true;
    videoElement.playsInline = true;
    await videoElement.play();
    console.log('videoElement 생성 및 재생 시작');

    console.log('SelfieSegmentation 초기화 시작');
    const selfieSegmentation = await initializeSelfieSegmentation();
    console.log('selfieSegmentation 초기화 완료:', selfieSegmentation);

    const Camera = await initializeCamera();
    console.log('Camera 초기화 완료:', Camera);

    const canvasElement = document.createElement('canvas');
    canvasElement.width = videoElement.videoWidth || 640;
    canvasElement.height = videoElement.videoHeight || 480;
    const canvasCtx = canvasElement.getContext('2d');

    selfieSegmentation.onResults((results) => {
        console.log('onResults 함수 호출됨');

        canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
        canvasCtx.drawImage(results.segmentationMask, 0, 0, canvasElement.width, canvasElement.height);

        canvasCtx.globalCompositeOperation = 'source-in';
        canvasCtx.drawImage(results.image, 0, 0, canvasElement.width, canvasElement.height);

        const videoStream = canvasElement.captureStream(30);
        console.log('videoStream 생성됨:', videoStream);
        const videoTrack = videoStream.getVideoTracks()[0];
        console.log('videoTrack:', videoTrack);
        const originalStream = actualStreamRef.stream.getMediaStream();
        console.log('originalStream:', originalStream);

        if (originalStream.getVideoTracks().length > 0) {
            console.log('기존 비디오 트랙 제거');
            originalStream.removeTrack(originalStream.getVideoTracks()[0]);
        }

        console.log('새 비디오 트랙 추가');
        originalStream.addTrack(videoTrack);
    });

    console.log('selfieSegmentation.onResults 설정됨');

    const camera = new Camera(videoElement, {
        onFrame: async () => {
            console.log('onFrame 호출됨');
            await selfieSegmentation.send({ image: videoElement });
        },
        width: videoElement.videoWidth || 640,
        height: videoElement.videoHeight || 480,
    });

    console.log('Camera 객체 생성됨');
    console.log('camera.start() 호출');
    try {
        await camera.start();
        console.log('camera.start() 성공');
    } catch (error) {
        console.error('camera.start() 실패:', error);
        throw error;
    }
};

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

const initializeCamera = async () => {
    await window.loadSelfieSegmentation();
    const cameraModule = await import('@mediapipe/camera_utils');
    return cameraModule.Camera;
};
