import { OpenVidu } from 'openvidu-browser';
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
    const backgroundRemoval = new VideoBackgroundRemoval();
    await backgroundRemoval.initialize();

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
            forceTurn: true,
            turnCreditRestoreTime: 120000, // 2분
            maxTries: 3,
            reconnectionPeriod: 5000, // 5초
        });

        session.value = OV.initSession();

        session.value.on('streamCreated', async ({ stream }) => {
            const subscriber = await session.value.subscribe(stream);
            subscribers.value.push({ subscriber });

            // Subscriber의 비디오 요소 가져오기
            let subscriberVideoElement = null;
            if (subscriber && subscriber.videos && subscriber.videos.length > 0) {
                subscriberVideoElement = subscriber.videos[0].video;
            } else if (subscriber && subscriber.videoElement) {
                subscriberVideoElement = subscriber.videoElement;
            }

            if (subscriberVideoElement) {
                // subscriber에 대한 배경 제거 적용
                const processedSubscriberStream = await backgroundRemoval.createProcessedStream(subscriberVideoElement);
                subscriberVideoElement.srcObject = processedSubscriberStream;
            } else {
                console.warn('Subscriber 비디오 요소를 찾을 수 없습니다.');
            }
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
            mirror: false,
        };

        publisher.value = await OV.initPublisherAsync(undefined, publisherOptions);
        console.log("Publisher initialized:", publisher.value);
        
        // Publisher의 비디오 요소 가져오기
        let publisherVideoElement = null;
        if (publisher.value && publisher.value.videos && publisher.value.videos.length > 0) {
            publisherVideoElement = publisher.value.videos[0].video;
        } else if (publisher.value && publisher.value.videoElement) {
            publisherVideoElement = publisher.value.videoElement;
        }

        if (publisherVideoElement) {
            // 배경 제거 적용
            const processedStream = await backgroundRemoval.createProcessedStream(publisherVideoElement);
            const videoTrack = processedStream.getVideoTracks()[0];
            
            if (publisher.value.stream.getVideoTracks().length > 0) {
                publisher.value.stream.removeTrack(publisher.value.stream.getVideoTracks()[0]);
            }
            publisher.value.stream.addTrack(videoTrack);
        } else {
            console.warn('Publisher 비디오 요소를 찾을 수 없습니다.');
        }

        await session.value.publish(publisher.value);

        if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
            myVideo.value.srcObject = publisher.value.stream.getMediaStream();
        }

        if (!checkWebGLSupport()) {
            console.warn('WebGL이 지원되지 않습니다. 배경 제거 기능을 사용할 수 없습니다.');
        }

    } catch (error) {
        console.error('세션 참가 중 오류 발생:', error);
        if (error.name === 'DEVICE_ACCESS_DENIED') {
            showErrorToUser('카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.');
        } else {
            showErrorToUser(`오류 발생: ${error.message}`);
        }
        throw error; 
    }
};
