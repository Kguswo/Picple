import { OpenVidu } from 'openvidu-browser';
import VideoBackgroundRemoval from '@/assets/js/showView/VideoBackgroundRemoval';

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

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

            const videoElement = document.getElementById(`video-${subscriber.stream.streamId}`);
            const canvasElement = document.getElementById(`canvas-${subscriber.stream.streamId}`);
            if (videoElement && canvasElement) {
                const videoBackgroundRemoval = new VideoBackgroundRemoval();
                const initialized = await videoBackgroundRemoval.initialize(canvasElement);
                if (initialized) {
                    videoBackgroundRemoval.startProcessing(videoElement, canvasElement);
                } else {
                    console.error(`Subscriber ${subscriber.stream.streamId}의 배경 제거 초기화에 실패했습니다.`);
                }
            }
        });

        session.value.on('streamDestroyed', ({ stream }) => {
            const index = subscribers.value.findIndex((sub) => sub.subscriber.stream.streamId === stream.streamId);
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
            frameRate: 30,
            insertMode: 'APPEND',
            mirror: true,
        };

        publisher.value = await OV.initPublisherAsync(undefined, publisherOptions);

        await session.value.publish(publisher.value);

        if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
            const videoBackgroundRemoval = new VideoBackgroundRemoval();
            const initialized = await videoBackgroundRemoval.initialize(myVideo.value);
            if (initialized) {
                videoBackgroundRemoval.startProcessing(publisher.value.video, myVideo.value);
            } else {
                console.error('Publisher의 배경 제거 초기화에 실패했습니다.');
            }
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