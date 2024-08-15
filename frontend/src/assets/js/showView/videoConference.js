import { OpenVidu } from 'openvidu-browser';
import { nextTick } from 'vue';
import VideoBackgroundRemoval from '@/assets/js/showView/VideoBackgroundRemoval';
import { storeToRefs } from 'pinia';

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

let videoBackgroundRemoval;

export const joinExistingSession = async (publisher, subscribers, myVideo, boothStore, addSubscriber, removeSubscriber) => {
    try {
        console.log('세션 참가 시작');
        const sessionInfo = boothStore.getSessionInfo();
        const session = storeToRefs(boothStore);

        if (!sessionInfo || !sessionInfo.sessionId || !sessionInfo.token) {
            throw new Error('세션 정보가 없습니다.');
        }
        console.log('세션 정보 확인 완료');

        const { token } = sessionInfo;

        const OV = new OpenVidu();
        console.log('OpenVidu 초기화 완료');

        OV.enableProdMode(false);
        OV.setAdvancedConfiguration({
            logLevel: 'DEBUG',
            noStreamPlayingEventExceptionTimeout: 8000,
            iceServers: [
                { urls: 'stun:stun.l.google.com:19302' },
                {
                    urls: [
                        'turn:i11a503.p.ssafy.io:3478',
                        'turn:i11a503.p.ssafy.io:3478?transport=tcp',
                        'turns:i11a503.p.ssafy.io:3479',
                    ],
                    username: 'picplessafy',
                    credential: 'ssafya503@picple',
                },
            ],
            iceTransportPolicy: 'all',
            forceMediaReconnectionAfterNetworkDrop: true,
            publisherSpeakingEventsOptions: {
                interval: 100,
                threshold: -50,
            },
            videoSimulcast: false,
            videoSendInitialDelay: 0,
            videoDimensions: '640x480',
            minVideoBitrate: 300,
            maxVideoBitrate: 1000,
        });
        console.log('OpenVidu 고급 설정 완료');

        session.value = OV.initSession();
        console.log('세션 초기화 완료');

        session.value.on('streamCreated', async ({ stream }) => {
            console.log('새로운 스트림 생성됨');
            const subscriber = await session.value.subscribe(stream);
            addSubscriber(subscriber);
            subscribers.value.push({ subscriber });

            nextTick(async () => {
                const video = document.getElementById(`video-${subscriber.stream.streamId}`);
                const canvas = document.getElementById(`canvas-${subscriber.stream.streamId}`);
                if (video && canvas) {
                    console.log('배경 제거 시작');
                    await initializeBackgroundRemoval(video, canvas);
                }
            });
        });

        session.value.on('streamDestroyed', ({ stream }) => {
            console.log('스트림 파괴됨');
            const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
            removeSubscriber(stream.streamId);
            if (index >= 0) {
                subscribers.value.splice(index, 1);
            }
        });

        session.value.on('connectionDestroyed', (event) => {
            console.log('연결 파괴됨');
            removeSubscriber(event.stream.streamId);
        });

        console.log('세션 연결 시작');
        await session.value.connect(token);
        console.log('세션 연결 완료');

        const devices = await OV.getDevices();
        const videoDevices = devices.filter((device) => device.kind === 'videoinput');
        console.log('디바이스 정보 가져오기 완료');

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
        console.log('퍼블리셔 초기화 완료');

        await session.value.publish(publisher.value);
        console.log('퍼블리셔 세션에 발행 완료');

        if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
            myVideo.value.srcObject = publisher.value.stream.getMediaStream();
        }

        applySegmentation(publisher);
    } catch (error) {
        console.error('세션 참가 중 오류 발생:', error);
        if (error.name === 'DEVICE_ACCESS_DENIED') {
            alert('카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.');
        } else {
            alert(`오류 발생: ${error.message}`);
        }
    }
};

const applySegmentation = async (streamRef) => {
    console.log('비디오 세그먼트 적용 시작');
    const actualStreamRef = streamRef.value || streamRef;

    if (!actualStreamRef || !actualStreamRef.stream) {
        console.error('스트림 참조가 유효하지 않습니다.');
        return;
    }

    const mediaStream = actualStreamRef.stream.getMediaStream();

    if (!mediaStream) {
        console.error('미디어 스트림이 유효하지 않습니다.');
        return;
    }

    const videoElement = document.createElement('video');
    videoElement.srcObject = mediaStream;

    await new Promise((resolve) => {
        videoElement.onloadedmetadata = () => {
            resolve();
        };
    });

    const canvasElement = document.createElement('canvas');
    canvasElement.width = videoElement.videoWidth;
    canvasElement.height = videoElement.videoHeight;

    try {
        console.log('VideoBackgroundRemoval 초기화 시작');
        if (!videoBackgroundRemoval) {
            videoBackgroundRemoval = new VideoBackgroundRemoval();
            await videoBackgroundRemoval.initialize();
        }
        videoBackgroundRemoval.startProcessing(videoElement, canvasElement);

        const videoStream = canvasElement.captureStream(30);
        const videoTrack = videoStream.getVideoTracks()[0];
        const originalStream = actualStreamRef.stream.getMediaStream();

        if (originalStream && originalStream.getVideoTracks().length > 0) {
            originalStream.removeTrack(originalStream.getVideoTracks()[0]);
        }

        if (originalStream) {
            originalStream.addTrack(videoTrack);
        } else {
            console.error('originalStream is undefined or null');
        }
    } catch (error) {
        console.error('배경 제거 처리 중 오류 발생:', error);
    }
};

const initializeBackgroundRemoval = async (videoElement, canvasElement) => {
    console.log('새 비디오 배경 제거 초기화 시작');
    if (!videoElement || !canvasElement) {
        console.error('비디오 또는 캔버스 엘리먼트가 유효하지 않습니다.');
        return;
    }

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
        console.log('새 비디오 배경 제거 초기화 완료');
    } catch (error) {
        console.error('MediaPipe 초기화 중 오류 발생:', error);
    }
};
