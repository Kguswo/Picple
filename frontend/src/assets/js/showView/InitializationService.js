// InitializationService.js

import { ref } from 'vue';
import WebSocketService from '@/services/WebSocketService';
import WebRTCService from '@/services/WebRTCService';
import * as tf from '@tensorflow/tfjs';
import * as bodyPix from '@tensorflow-models/body-pix';
import Swal from 'sweetalert2';

class InitializationService {
    constructor() {
        this.isLoading = ref(true);
        this.bodyPixModel = null; // BodyPix 모델 인스턴스
        this.camera = null;
        this.videoElement = null;
        this.canvasElement = null;
        this.boothStore = null;
    }

    // 초기화 메서드
    async initialize(router, route, boothStore, photoStore) {
        console.log('shootView 마운트!');
        const startTime = Date.now();

        this.boothStore = boothStore;
        WebSocketService.setBoothStore(boothStore);

        try {
            await this.initializeWebSocketAndMedia();
            await this.initializeWebRTC();
            await this.initializeBodyPix(); // BodyPix 초기화
            this.setupEventListeners(boothStore);

            const participants = WebSocketService.participants || [];
            boothStore.setParticipants(participants);

            if (route.params.boothId) {
                await WebSocketService.joinBooth(route.params.boothId);
            }

            const elapsedTime = Date.now() - startTime;
            const remainingTime = Math.max(1000 - elapsedTime, 0);

            setTimeout(() => {
                this.isLoading.value = false;
            }, remainingTime);
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            // 오류 처리 로직 추가
        }
    }

    // WebSocket 및 미디어 초기화
    async initializeWebSocketAndMedia() {
        if (!WebSocketService.isConnected()) {
            await WebSocketService.connect('ws://localhost:8080/ws');
        }

        try {
            const mediaStream = await navigator.mediaDevices.getUserMedia({
                video: { width: 640, height: 480 },
                audio: true,
            });

            if (this.videoElement) {
                this.videoElement.srcObject = mediaStream;
                this.videoElement.onloadedmetadata = async () => {
                    console.log('비디오 메타데이터 로드됨');
                    this.videoElement.play();
                    this.videoElement.style.transform = 'scaleX(-1)';
                    this.canvasElement.style.transform = 'scaleX(-1)';
                    await this.initializeBodyPix();
                };
            } else {
                console.error('비디오 요소를 찾을 수 없음');
            }
        } catch (error) {
            console.error('카메라 피드 획득 실패:', error);
            await Swal.fire({
                title: '카메라 접근 권한이 필요합니다',
                text: '카메라 사용을 위해 브라우저 설정에서 권한을 허용해주세요.',
                icon: 'warning',
                confirmButtonText: '확인',
            });
        }
    }

    // WebRTC 초기화
    async initializeWebRTC() {
        await WebRTCService.initializeLocalStream();
        if (this.videoElement) {
            this.videoElement.srcObject = WebRTCService.localStream;
        } else {
            console.error('WebRTC 초기화 중 비디오 요소를 찾을 수 없음');
        }

        WebRTCService.onRemoteStream = (participantId, stream) => {
            const participant = WebSocketService.participants.find((p) => p.id === participantId);
            if (participant) {
                participant.stream = stream;
            }
        };
    }

    // BodyPix 모델 초기화
    async initializeBodyPix() {
        console.log('BodyPix 모델 로딩 중');
        console.log('비디오 크기:', this.videoElement.videoWidth, 'x', this.videoElement.videoHeight);
        try {
            this.bodyPixModel = await bodyPix.load({
                architecture: 'MobileNetV1',
                outputStride: 16,
                multiplier: 0.75,
                quantBytes: 2,
            });
            console.log('BodyPix 모델 로드 성공');
            this.startBodyPixSegmentation();
        } catch (error) {
            console.error('BodyPix 초기화 오류:', error);
        }
    }

    // BodyPix 세그멘테이션 시작
    startBodyPixSegmentation() {
        const segmentPerson = async () => {
            if (
                this.videoElement &&
                this.canvasElement &&
                this.bodyPixModel &&
                this.videoElement.videoWidth > 0 &&
                this.videoElement.videoHeight > 0
            ) {
                const segmentation = await this.bodyPixModel.segmentPerson(this.videoElement);
                this.drawSegmentation(segmentation);
            }
            requestAnimationFrame(segmentPerson);
        };
        segmentPerson();
    }

    // 세그멘테이션 결과 그리기
    drawSegmentation(segmentation) {
        const canvas = this.canvasElement;
        const ctx = canvas.getContext('2d');
        canvas.width = this.videoElement.width;
        canvas.height = this.videoElement.height;

        // 원본 비디오 프레임 그리기
        ctx.drawImage(this.videoElement, 0, 0, canvas.width, canvas.height);

        // 세그멘테이션 마스크 적용
        const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
        const pixel = imageData.data;
        for (let i = 0; i < pixel.length; i += 4) {
            if (segmentation.data[i / 4] === 0) {
                pixel[i + 3] = 0; // 배경 부분을 투명하게 설정
            }
        }
        ctx.putImageData(imageData, 0, 0);
    }

    // 이벤트 리스너 설정
    setupEventListeners(boothStore) {
        WebSocketService.on('participant_joined', (message) => {
            boothStore.setParticipants([...boothStore.participants, message.participant]);
        });

        WebSocketService.on('participant_left', (message) => {
            boothStore.setParticipants(boothStore.participants.filter((p) => p.id !== message.participantId));
        });

        WebSocketService.on('background_changed', (message) => {
            boothStore.setBgImage(message.backgroundImage);
        });
    }

    // 정리 메서드
    cleanup() {
        console.log('shootView 언마운트!');

        if (this.camera) {
            this.camera.stop();
            this.camera = null;
        }

        WebRTCService.closeAllConnections();
        WebRTCService.disconnect();

        WebSocketService.off('participant_joined');
        WebSocketService.off('participant_left');
        WebSocketService.close();

        WebSocketService.off('background_changed');

        this.bodyPixModel = null;
        this.videoElement = null;
        this.canvasElement = null;
    }

    setVideoElement(element) {
        this.videoElement = element;
    }

    setCanvasElement(element) {
        this.canvasElement = element;
    }
}

export default new InitializationService();
