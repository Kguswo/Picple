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
export const applySegmentation = async (streamRef) => {
    let isProcessing = false;
    let selfieSegmentation;
    let camera;

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
        await videoElement.play();

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

        return new Promise((resolve) => {
            const checkProcessing = () => {
                if (!isProcessing) {
                    console.log('세그멘테이션 처리 완료');
                    resolve();
                } else {
                    requestAnimationFrame(checkProcessing);
                }
            };
            checkProcessing();
        });
    } catch (error) {
        console.error('세그멘테이션 적용 중 오류 발생:', error);
        throw error;
    } finally {
        // 리소스 정리
        if (camera) {
            camera.stop();
        }
        if (selfieSegmentation) {
            selfieSegmentation.close();
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
            if (isProcessing) return;
            
            isProcessing = true;

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
                isProcessing = false;
            } catch (error) {
                console.error('onResults 콜백 내부 오류:', error);
                isProcessing = false;
            }
        });

        const camera = new window.Camera(videoElement, {
            onFrame: async () => {
                await selfieSegmentation.send({ image: videoElement }); // 세그멘테이션에 이미지 전송
            },
            width: videoElement.videoWidth || 640,
            height: videoElement.videoHeight || 480,
        });

        await camera.start(); // 카메라 시작

        return new Promise((resolve) => {
            const checkProcessing = () => {
                if (!isProcessing) {
                    console.log('세그멘테이션 처리 완료');
                    resolve();
                } 
                else {
                    setTimeout(checkProcessing, 100);
                }
            };
            checkProcessing();
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

