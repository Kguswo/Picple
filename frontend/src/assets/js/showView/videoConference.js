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

async function waitForVideoElement(videoElement, maxAttempts = 60, interval = 500) {
    return new Promise((resolve, reject) => {
        let attempts = 0;
        const checkVideo = () => {
            attempts++;
            if (videoElement.readyState >= 2 && videoElement.videoWidth > 0 && videoElement.videoHeight > 0) {
                console.log('Video loaded:', videoElement.videoWidth, videoElement.videoHeight);
                resolve(videoElement);
            } else if (attempts >= maxAttempts) {
                reject(new Error('비디오 메타데이터 로딩 타임아웃'));
            } else {
                setTimeout(checkVideo, interval);
            }
        };
        checkVideo();
    });
}

export async function initializePublisherVideo(publisher, videoElement) {
    const mediaStream = publisher.stream.getMediaStream();
    videoElement.srcObject = mediaStream;
    await videoElement.play();
    console.log('Publisher video playback started');

    if (checkWebGLSupport()) {
        try {
            await applySegmentation(publisher);
            console.log('Segmentation applied to publisher');
        } catch (error) {
            console.error('Publisher 비디오 처리 중 오류:', error);
        }
    }
}

export async function initializeSubscriberVideo(subscriber, videoElement) {
    const maxRetries = 5;
    for (let i = 0; i < maxRetries; i++) {
        try {
            const mediaStream = subscriber.stream.getMediaStream();
            if (!mediaStream) {
                throw new Error('MediaStream is null or undefined');
            }
            videoElement.srcObject = mediaStream;
            await videoElement.play();
            console.log('Subscriber video playback started');

            // 배경 처리를 비동기적으로 적용
            if (checkWebGLSupport()) {
                applySegmentation(subscriber, videoElement).catch((error) => {
                    console.warn('Background segmentation failed:', error);
                    // 배경 처리 실패 시 사용자에게 알림
                    showNotification('배경 제거 기능을 적용할 수 없습니다. 기본 비디오로 표시됩니다.');
                });
            }

            return; // 성공적으로 초기화되면 함수 종료
        } catch (error) {
            console.warn(`Attempt ${i + 1} failed:`, error);
            await new Promise((resolve) => setTimeout(resolve, 2000)); // 2초 대기 후 재시도
        }
    }
    throw new Error('Failed to initialize subscriber video after multiple attempts');
}

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
            console.log('New stream created:', stream);
            const subscriber = await session.value.subscribe(stream, {
                videoEnabled: true,
                audioEnabled: true,
            });
            console.log('Subscribed to stream:', subscriber);
            subscribers.value.push(subscriber); // subscriber 객체 전체를 추가
        });

        session.value.on('streamDestroyed', ({ stream }) => {
            console.log('Stream destroyed:', stream);
            const index = subscribers.value.findIndex((sub) => sub.subscriber.stream.streamId === stream.streamId);
            if (index >= 0) {
                subscribers.value.splice(index, 1);
            }
        });

        await session.value.connect(token);
        console.log('Connected to session');

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
        console.log('Publisher initialized:', publisher.value);

        await session.value.publish(publisher.value);

        if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
            await initializePublisherVideo(publisher.value, myVideo.value);
        }

        if (!checkWebGLSupport()) {
            console.warn('WebGL이 지원되지 않습니다. 배경 제거 기능을 사용할 수 없습니다.');
            return;
        }

        await applySegmentation(publisher);
        console.log('Segmentation applied to publisher');
    } catch (error) {
        console.error('세션 참가 중 오류 발생:', error);
        showErrorToUser(`오류 발생: ${error.message}`);
    }
};

// export const applySegmentation = async (streamRef) => {
//     let isProcessing = false;
//     let selfieSegmentation;
//     let camera;

//     try {
//         console.log('Applying segmentation to:', streamRef);
//         const actualStreamRef = streamRef.value || streamRef;
//         if (!actualStreamRef || !actualStreamRef.stream) {
//             throw new Error('스트림 참조가 유효하지 않습니다.');
//         }

//         let mediaStream;
//         if (actualStreamRef.stream.getMediaStream) {
//             mediaStream = actualStreamRef.stream.getMediaStream();
//         } else if (actualStreamRef.stream.mediaStream) {
//             mediaStream = actualStreamRef.stream.mediaStream;
//         } else if (actualStreamRef.stream.streamManager && actualStreamRef.stream.streamManager.stream) {
//             mediaStream = actualStreamRef.stream.streamManager.stream.getMediaStream();
//         } else {
//             throw new Error('미디어 스트림을 가져올 수 없습니다.');
//         }

//         if (!mediaStream) {
//             throw new Error('미디어 스트림이 null 또는 undefined입니다.');
//         }
//         console.log('Media stream obtained:', mediaStream);

//         const videoElement = document.createElement('video');
//         videoElement.srcObject = mediaStream;
//         videoElement.muted = true;
//         videoElement.playsInline = true;

//         await new Promise((resolve) => {
//             videoElement.onloadedmetadata = () => {
//                 videoElement.play().then(resolve);
//             };
//         });

//         const canvasElement = document.createElement('canvas');
//         canvasElement.width = videoElement.videoWidth;
//         canvasElement.height = videoElement.videoHeight;
//         const canvasCtx = canvasElement.getContext('2d');

//         selfieSegmentation = new window.SelfieSegmentation({
//             locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
//         });

//         await selfieSegmentation.setOptions({ modelSelection: 1 });
//         await selfieSegmentation.initialize();
//         console.log('Selfie segmentation initialized');

//         selfieSegmentation.onResults((results) => {
//             if (isProcessing) return;
//             isProcessing = true;

//             try {
//                 canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
//                 canvasCtx.drawImage(results.segmentationMask, 0, 0, canvasElement.width, canvasElement.height);
//                 canvasCtx.globalCompositeOperation = 'source-in';
//                 canvasCtx.drawImage(results.image, 0, 0, canvasElement.width, canvasElement.height);
//                 canvasCtx.globalCompositeOperation = 'source-over';

//                 const videoStream = canvasElement.captureStream(30);
//                 const videoTrack = videoStream.getVideoTracks()[0];

//                 if (mediaStream.getVideoTracks().length > 0) {
//                     mediaStream.removeTrack(mediaStream.getVideoTracks()[0]);
//                 }
//                 mediaStream.addTrack(videoTrack);

//                 // 스트림 업데이트를 알림
//                 if (streamRef.stream && typeof streamRef.stream.updateMediaStream === 'function') {
//                     streamRef.stream.updateMediaStream(mediaStream);
//                 }
//             } catch (error) {
//                 console.error('세그멘테이션 처리 중 오류:', error);
//             } finally {
//                 isProcessing = false;
//             }
//         });

//         camera = new window.Camera(videoElement, {
//             onFrame: async () => {
//                 if (!isProcessing) {
//                     await selfieSegmentation.send({ image: videoElement });
//                 }
//             },
//             width: videoElement.videoWidth,
//             height: videoElement.videoHeight,
//         });

//         await camera.start();
//         console.log('Camera started for segmentation');
//     } catch (error) {
//         console.error('세그멘테이션 적용 중 오류 발생:', error);
//         throw error;
//     }
// };

export const applySegmentation = async (streamRef, videoElement) => {
    let isProcessing = false;
    let selfieSegmentation;
    let camera;

    try {
        console.log('Applying segmentation to:', streamRef);
        const actualStreamRef = streamRef.value || streamRef;
        if (!actualStreamRef || !actualStreamRef.stream) {
            throw new Error('스트림 참조가 유효하지 않습니다.');
        }

        let mediaStream = actualStreamRef.stream.getMediaStream();
        if (!mediaStream) {
            throw new Error('미디어 스트림이 null 또는 undefined입니다.');
        }

        selfieSegmentation = new window.SelfieSegmentation({
            locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
        });

        await selfieSegmentation.setOptions({ modelSelection: 1 });
        await selfieSegmentation.initialize();

        const canvasElement = document.createElement('canvas');
        canvasElement.width = videoElement.videoWidth;
        canvasElement.height = videoElement.videoHeight;
        const canvasCtx = canvasElement.getContext('2d');

        selfieSegmentation.onResults((results) => {
            if (isProcessing) return;
            isProcessing = true;

            try {
                canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
                canvasCtx.drawImage(results.segmentationMask, 0, 0, canvasElement.width, canvasElement.height);
                canvasCtx.globalCompositeOperation = 'source-in';
                canvasCtx.drawImage(results.image, 0, 0, canvasElement.width, canvasElement.height);

                const imageData = canvasCtx.getImageData(0, 0, canvasElement.width, canvasElement.height);
                canvasCtx.putImageData(imageData, 0, 0);

                videoElement.srcObject = canvasElement.captureStream(30);
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
            width: videoElement.videoWidth,
            height: videoElement.videoHeight,
        });

        await camera.start();
        console.log('Camera started for segmentation');
    } catch (error) {
        console.error('세그멘테이션 적용 중 오류 발생:', error);
        throw error;
    }
};
