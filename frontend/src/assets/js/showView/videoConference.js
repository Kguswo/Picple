import { OpenVidu } from 'openvidu-browser';
import { nextTick } from 'vue';

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

function checkWebGLSupport() {
    const canvas = document.createElement('canvas');
    const gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');
    return !!gl;
}

const showErrorToUser = (message) => {
    console.error(message);
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
        });

        session.value = OV.initSession();

        session.value.on('streamCreated', async ({ stream }) => {
            const subscriber = await session.value.subscribe(stream);
            subscribers.value.push({ subscriber });

            nextTick(async () => {
                const video = document.getElementById(`video-${subscriber.stream.streamId}`);
                const canvas = document.getElementById(`canvas-${subscriber.stream.streamId}`);
                if (video && canvas) {
                    await applySegmentation({ stream: subscriber });
                }
            });
        });

        session.value.on('streamDestroyed', ({ stream }) => {
            const index = subscribers.value.findIndex((sub) => sub.subscriber.stream.streamId === stream.streamId);
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

        if (!checkWebGLSupport()) {
            console.warn('WebGL이 지원되지 않습니다. 배경 제거 기능을 사용할 수 없습니다.');
            return;
        }

        await applySegmentation(publisher);
    } catch (error) {
        console.error('세션 참가 중 오류 발생:', error);
        if (error.name === 'DEVICE_ACCESS_DENIED') {
            showErrorToUser('카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.');
        } else {
            showErrorToUser(`오류 발생: ${error.message}`);
        }
    }
};

export const applySegmentation = async (streamRef) => {
    let isProcessing = false;
    let selfieSegmentation;
    let camera;

    try {
        const actualStreamRef = streamRef.value || streamRef;
        if (!actualStreamRef || !actualStreamRef.stream) {
            throw new Error('스트림 참조가 유효하지 않습니다.');
        }

        const mediaStream = actualStreamRef.stream.getMediaStream
            ? actualStreamRef.stream.getMediaStream()
            : actualStreamRef.stream.streamManager.stream.getMediaStream();

        if (!mediaStream) {
            throw new Error('미디어 스트림을 가져올 수 없습니다.');
        }

        const videoElement = document.createElement('video');
        videoElement.srcObject = mediaStream;
        videoElement.muted = true;
        videoElement.playsInline = true;

        const canvasElement = document.createElement('canvas');
        canvasElement.width = videoElement.videoWidth || 640;
        canvasElement.height = videoElement.videoHeight || 480;
        const canvasCtx = canvasElement.getContext('2d');

        selfieSegmentation = new window.SelfieSegmentation({
            locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
        });

        await selfieSegmentation.setOptions({ modelSelection: 1 });
        await selfieSegmentation.initialize();

        selfieSegmentation.onResults((results) => {
            if (isProcessing) return;
            isProcessing = true;

            try {
                canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
                canvasCtx.drawImage(results.segmentationMask, 0, 0, canvasElement.width, canvasElement.height);
                canvasCtx.globalCompositeOperation = 'source-in';
                canvasCtx.drawImage(results.image, 0, 0, canvasElement.width, canvasElement.height);
                canvasCtx.globalCompositeOperation = 'source-over';

                const videoStream = canvasElement.captureStream(30);
                const videoTrack = videoStream.getVideoTracks()[0];
                const originalStream = actualStreamRef.stream.getMediaStream();

                if (originalStream.getVideoTracks().length > 0) {
                    originalStream.removeTrack(originalStream.getVideoTracks()[0]);
                }
                originalStream.addTrack(videoTrack);
            } catch (error) {
                console.error('세그멘테이션 처리 중 오류:', error);
            } finally {
                isProcessing = false;
            }
        });

        camera = new window.Camera(videoElement, {
            onFrame: async () => {
                if (!isProcessing) {
                    await selfieSegmentation.send({ image: videoElement });
                }
            },
            width: videoElement.videoWidth || 640,
            height: videoElement.videoHeight || 480,
        });

        await camera.start();
    } catch (error) {
        console.error('세그멘테이션 적용 중 오류 발생:', error);
        throw error;
    }
};
