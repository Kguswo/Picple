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

export const initializeLocalStream = async (myVideo) => {
    console.log('로컬 스트림 초기화 시작');
    try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true, video: true });
        console.log('사용자 미디어 스트림 획득 성공');

        const videoElement = document.createElement('video');
        videoElement.srcObject = stream;
        videoElement.muted = true;
        videoElement.playsInline = true;
        await videoElement.play();

        const canvasElement = document.createElement('canvas');
        canvasElement.width = videoElement.videoWidth || 640;
        canvasElement.height = videoElement.videoHeight || 480;

        const backgroundRemoval = new VideoBackgroundRemoval();
        await backgroundRemoval.initialize();
        backgroundRemoval.startProcessing(videoElement, canvasElement);

        const processedStream = canvasElement.captureStream(30);
        if (myVideo.value) {
            myVideo.value.srcObject = processedStream;
            await myVideo.value.play();
        }

        console.log('로컬 스트림 초기화 및 배경 제거 완료');
        return { stream: processedStream, backgroundRemoval };
    } catch (error) {
        console.error('로컬 스트림 초기화 중 오류 발생:', error);
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

        console.log('1. 로컬 스트림 초기화 시작');
        const { stream: localStream, backgroundRemoval } = await initializeLocalStream(myVideo);
        console.log('1. 로컬 스트림 초기화 완료');

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
            
            nextTick(async () => {
                const video = document.getElementById(`video-${subscriber.stream.streamId}`);
                const canvas = document.getElementById(`canvas-${subscriber.stream.streamId}`);
                if (video && canvas) {
                    console.log('5-2. 원격 참가자 background 제거 초기화 시작');
                    const remoteBackgroundRemoval = new VideoBackgroundRemoval();
                    await remoteBackgroundRemoval.initialize();
                    remoteBackgroundRemoval.startProcessing(video, canvas);

                    const processedStream = canvas.captureStream(30);
                    video.srcObject = processedStream;
                    await video.play();

                    console.log('5-2. 원격 참가자 background 제거 초기화 완료');
                }
            });
            
            subscribers.value.push({ subscriber, backgroundRemoval: null });
            console.log('5-1. streamCreated 이벤트 처리 완료');
        });

        session.value.on('streamDestroyed', ({ stream }) => {
            console.log('5-3. streamDestroyed 이벤트 처리 시작');
            const index = subscribers.value.findIndex((sub) => sub.subscriber.stream.streamId === stream.streamId);
            if (index >= 0) {
                subscribers.value.splice(index, 1);
            }
            console.log('5-3. streamDestroyed 이벤트 처리 완료');
        });
        console.log('5. 이벤트 리스너 등록 완료');

        console.log('6. 세션 연결 시작');
        await session.value.connect(token);
        console.log('6. 세션 연결 완료');

        console.log('7. 퍼블리셔 옵션 설정 시작');
        const publisherOptions = {
            audioSource: localStream.getAudioTracks()[0],
            videoSource: localStream.getVideoTracks()[0],
            publishAudio: true,
            publishVideo: true,
            resolution: '640x480',
            frameRate: 30,
            insertMode: 'APPEND',
            mirror: true,
        };
        console.log('7. 퍼블리셔 옵션 설정 완료');

        console.log('8. 퍼블리셔 초기화 시작');
        publisher.value = await OV.initPublisherAsync(undefined, publisherOptions);
        console.log('8. 퍼블리셔 초기화 완료');

        console.log('9. 퍼블리셔 발행 시작');
        await session.value.publish(publisher.value);
        console.log('9. 퍼블리셔 발행 완료');

    } catch (error) {
        console.error('세션 참가 중 오류 발생:', error);
        if (error.name === 'DEVICE_ACCESS_DENIED') {
            showErrorToUser('카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.');
        } else {
            showErrorToUser(`오류 발생: ${error.message}`);
        }
    }
};

export const initializeBackgroundRemoval = async (videoElement, canvasElement) => {
    if (!videoElement || !canvasElement) {
        console.log('비디오 또는 캔버스 엘리먼트가 없습니다.');
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
        return newBackgroundRemoval;  
    } catch (error) {
        console.error('배경 제거 초기화 중 오류 발생:', error);
        throw error;
    }
};
