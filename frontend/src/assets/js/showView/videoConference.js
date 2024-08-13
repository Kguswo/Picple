import { OpenVidu } from 'openvidu-browser';
import { nextTick } from 'vue';
import VideoBackgroundRemoval from './VideoBackgroundRemoval';

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
        console.log('1. 세션 정보 가져오기 시작');
        const sessionInfo = boothStore.getSessionInfo();
        if (!sessionInfo || !sessionInfo.sessionId || !sessionInfo.token) {
            throw new Error('세션 정보가 없습니다.');
        }
        const { token } = sessionInfo;
        console.log('1. 세션 정보 가져오기 완료');

        console.log('2. OpenVidu 객체 초기화 시작');
        const OV = new OpenVidu();
        OV.enableProdMode(false);
        console.log('2. OpenVidu 객체 초기화 완료');

        console.log('3. OpenVidu 고급 설정 시작');
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
        });
        console.log('3. OpenVidu 고급 설정 완료');

        console.log('4. 세션 초기화 시작');
        session.value = OV.initSession();
        console.log('4. 세션 초기화 완료');

        console.log('5. 이벤트 리스너 등록 시작');
        session.value.on('streamCreated', async ({ stream }) => {
            console.log('5-1. streamCreated 이벤트 처리 시작');
            const subscriber = await session.value.subscribe(stream);
            subscribers.value.push({ subscriber });

            nextTick(async () => {
                const video = document.getElementById(`video-${subscriber.stream.streamId}`);
                const canvas = document.getElementById(`canvas-${subscriber.stream.streamId}`);
                if (video && canvas) {
                    console.log('5-2. background 제거 초기화 시작');
                    await initializeBackgroundRemoval(video, canvas);
                    console.log('5-2. background 제거 초기화 완료');
                }
            });
            console.log('5-1. streamCreated 이벤트 처리 완료');
        });

        session.value.on('streamDestroyed', ({ stream }) => {
            console.log('5-3. streamDestroyed 이벤트 처리 시작');
            const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
            if (index >= 0) {
                subscribers.value.splice(index, 1);
            }
            console.log('5-3. streamDestroyed 이벤트 처리 완료');
        });
        console.log('5. 이벤트 리스너 등록 완료');

        console.log('6. 세션 연결 시작');
        await session.value.connect(token);
        console.log('6. 세션 연결 완료');

        console.log('7. 장치 정보 가져오기 시작');
        const devices = await OV.getDevices();
        const videoDevices = devices.filter((device) => device.kind === 'videoinput');
        console.log('7. 장치 정보 가져오기 완료');

        console.log('8. 퍼블리셔 옵션 설정 시작');
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
        console.log('8. 퍼블리셔 옵션 설정 완료');

        console.log('9. 퍼블리셔 초기화 시작');
        publisher.value = await OV.initPublisherAsync(undefined, publisherOptions);
        console.log('9. 퍼블리셔 초기화 완료');

        console.log('10. 배경 제거 적용 시작');
        if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
            const videoElement = myVideo.value;
            const canvasElement = document.createElement('canvas');
            canvasElement.width = videoElement.videoWidth || 640;
            canvasElement.height = videoElement.videoHeight || 480;
            
            await initializeBackgroundRemoval(videoElement, canvasElement);

            const processedStream = canvasElement.captureStream(30);
            const processedVideoTrack = processedStream.getVideoTracks()[0];
            const originalStream = publisher.value.stream.getMediaStream();

            if (originalStream.getVideoTracks().length > 0) {
                originalStream.removeTrack(originalStream.getVideoTracks()[0]);
            }
            originalStream.addTrack(processedVideoTrack);

            myVideo.value.srcObject = originalStream;
        }
        console.log('10. 배경 제거 적용 완료');

        console.log('11. 퍼블리셔 발행 시작');
        await session.value.publish(publisher.value);
        console.log('11. 퍼블리셔 발행 완료');

        console.log('12. WebGL 지원 확인 및 세그멘테이션 적용 시작');
        try {
            if (!checkWebGLSupport()) {
                console.warn('WebGL이 지원되지 않습니다. 배경 제거 기능을 사용할 수 없습니다.');
                return;
            }

            await applySegmentation(publisher);
            console.log('12. 세그멘테이션 적용 완료');
        } catch (error) {
            console.error('세그멘테이션 적용 중 오류 발생:', error);
            showErrorToUser('세그멘테이션 기능을 적용하는 데 문제가 발생했습니다. 새로고침 후 다시 시도해주세요.');
        }

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
    console.log('세그멘테이션 적용 시작');
    let isProcessing = false;
    let selfieSegmentation;
    let camera;

    try {
        const actualStreamRef = streamRef.value || streamRef;
        if (!actualStreamRef || !actualStreamRef.stream) {
            throw new Error('스트림 참조가 유효하지 않습니다.');
        }

        const mediaStream = actualStreamRef.stream.getMediaStream();

        console.log('mediaStream 확인:', mediaStream);

        if (!mediaStream) {
            throw new Error('미디어 스트림을 가져올 수 없습니다.');
        }

        console.log('비디오 엘리먼트 생성');
        const videoElement = document.createElement('video');
        videoElement.srcObject = mediaStream;
        videoElement.muted = true;
        videoElement.playsInline = true;

        console.log('세그멘테이션 모델 초기화');
        selfieSegmentation = new window.SelfieSegmentation({
            locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
        });

        await selfieSegmentation.setOptions({ modelSelection: 1 });
        await selfieSegmentation.initialize();

        console.log('세그멘테이션 결과 처리 함수 설정');
        selfieSegmentation.onResults((results) => {
            if (isProcessing) return;
            isProcessing = true;

            try {
                console.log('세그멘테이션 결과 처리 시작');
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

                if (originalStream.getVideoTracks().length > 0) {
                    originalStream.removeTrack(originalStream.getVideoTracks()[0]);
                }

                originalStream.addTrack(videoTrack);
                console.log('세그멘테이션 결과 처리 완료');
            } catch (error) {
                console.error('세그멘테이션 처리 중 오류:', error);
            } finally {
                isProcessing = false;
            }
        });

        console.log('카메라 설정');
        camera = new window.Camera(videoElement, {
            onFrame: async () => {
                if (!isProcessing) {
                    await selfieSegmentation.send({ image: videoElement });
                }
            },
            width: videoElement.videoWidth || 640,
            height: videoElement.videoHeight || 480,
        });

        console.log('카메라 시작');
        await camera.start();

        console.log('세그멘테이션 적용 완료');
    } catch (error) {
        console.error('세그멘테이션 적용 중 오류 발생:', error);
        throw error;
    } finally {
        if (camera) {
            camera.stop();
        }
        if (selfieSegmentation) {
            selfieSegmentation.close();
        }
    }
};

export const initializeBackgroundRemoval = async (videoElement, canvasElement) => {
    console.log('배경 제거 초기화 시작');
    if (!videoElement || !canvasElement) {
        console.log('비디오 또는 캔버스 엘리먼트가 없습니다.');
        return;
    }

    console.log('비디오 준비 상태 확인');
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
        console.log('VideoBackgroundRemoval 클래스 초기화');
        const newBackgroundRemoval = new VideoBackgroundRemoval();
        await newBackgroundRemoval.initialize();
        console.log('배경 제거 처리 시작');
        newBackgroundRemoval.startProcessing(videoElement, canvasElement);
        console.log('배경 제거 처리 시작 완료');
    } catch (error) {
        console.error('MediaPipe 초기화 중 오류 발생:', error);
    }
    console.log('배경 제거 초기화 완료');
};
