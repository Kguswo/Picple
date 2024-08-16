import { OpenVidu } from 'openvidu-browser';
import { nextTick } from 'vue';
import VideoBackgroundRemoval from '@/assets/js/showView/VideoBackgroundRemoval';
import { storeToRefs } from 'pinia';

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

let selfieSegmentation;

export const joinExistingSession = async (publisher, subscribers, myVideo, boothStore) => {
	try {
		const sessionInfo = boothStore.getSessionInfo();
		const session = storeToRefs(boothStore);

        if (!sessionInfo || !sessionInfo.sessionId || !sessionInfo.token) {
            throw new Error('세션 정보가 없습니다.');
        }

        const { token } = sessionInfo;

        const OV = new OpenVidu();

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

        session.value.on('connectionDestroyed', (event) => {
            removeSubscriber(event.stream.streamId);
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

const applySegmentation = (streamRef) => {
    const actualStreamRef = streamRef.value || streamRef;

    if (!actualStreamRef || !actualStreamRef.stream) return;

    const mediaStream = actualStreamRef.stream.getMediaStream();

    if (!mediaStream) return;

    const videoElement = document.createElement('video');
    videoElement.srcObject = mediaStream;

    selfieSegmentation = new window.SelfieSegmentation({
        locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
    });

    selfieSegmentation.setOptions({
        modelSelection: 1,
    });

    const onResults = (results) => {
        const canvasElement = document.createElement('canvas');
        const canvasCtx = canvasElement.getContext('2d');

        canvasElement.width = results.image.width;
        canvasElement.height = results.image.height;

        canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
        canvasCtx.drawImage(results.segmentationMask, 0, 0, canvasElement.width, canvasElement.height);

        canvasCtx.globalCompositeOperation = 'source-in';
        canvasCtx.drawImage(results.image, 0, 0, canvasElement.width, canvasElement.height);

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
