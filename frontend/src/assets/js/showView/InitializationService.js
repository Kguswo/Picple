import { ref } from 'vue';
import WebSocketService from '@/services/WebSocketService';
import WebRTCService from '@/services/WebRTCService';
import { Camera } from '@mediapipe/camera_utils';
import { SelfieSegmentation } from '@mediapipe/selfie_segmentation';
import Swal from 'sweetalert2';

class InitializationService {
	constructor() {
		this.isLoading = ref(true);
		this.selfieSegmentationInstance = null;
		this.camera = null;
		this.selfieSegmentation = null;
		this.videoElement = null;
		this.canvasElement = null;
		this.boothStore = null;
	}

	async initialize(router, route, boothStore, photoStore) {
		console.log('shootView Mounted!');
		const startTime = Date.now();

		this.boothStore = boothStore; // boothStore 저장
		WebSocketService.setBoothStore(boothStore); // WebSocketService에 boothStore 설정

		try {
			await this.initializeWebSocketAndMedia();
			await this.initializeWebRTC();
			await this.initializeSelfieSegmentation();
			this.setupEventListeners(boothStore);

			const participants = WebSocketService.participants || [];
			boothStore.setParticipants(participants);

			// boothId가 route.params에 있다면 joinBooth 호출
			if (route.params.boothId) {
				await WebSocketService.joinBooth(route.params.boothId);
			}

			const elapsedTime = Date.now() - startTime;
			const remainingTime = Math.max(1000 - elapsedTime, 0);

			setTimeout(() => {
				this.isLoading.value = false;
			}, remainingTime);
		} catch (error) {
			console.error('Error in component initialization:', error);
		}
	}

	async initializeWebSocketAndMedia() {
		if (!WebSocketService.isConnected()) {
			await WebSocketService.connect(import.meta.env.VITE_WS);
		}

		try {
			const mediaStream = await navigator.mediaDevices.getUserMedia({
				video: { width: 640, height: 480 },
				audio: true,
			});

			if (this.videoElement) {
				this.videoElement.srcObject = mediaStream;
				this.videoElement.onloadedmetadata = async () => {
					console.log('Video metadata loaded');
					await this.loadSelfieSegmentation();
					this.videoElement.play();

					this.videoElement.style.transform = 'scaleX(-1)';
					this.canvasElement.style.transform = 'scaleX(-1)';
				};
			} else {
				console.error('Video element not found');
			}
		} catch (error) {
			console.error('Failed to acquire camera feed:', error);
			await Swal.fire({
				title: '카메라 접근 권한이 필요합니다',
				text: '카메라 사용을 위해 브라우저 설정에서 권한을 허용해주세요.',
				icon: 'warning',
				confirmButtonText: '확인',
			});
		}
	}

	async initializeWebRTC() {
		await WebRTCService.initializeLocalStream();
		if (this.videoElement) {
			this.videoElement.srcObject = WebRTCService.localStream;
		} else {
			console.error('Video element not found during WebRTC initialization');
		}

		WebRTCService.onRemoteStream = (participantId, stream) => {
			const participant = WebSocketService.participants.find((p) => p.id === participantId);
			if (participant) {
				participant.stream = stream;
			}
		};
	}

	async loadSelfieSegmentation() {
		console.log('Loading Selfie Segmentation model');
		if (typeof SelfieSegmentation === 'undefined') {
			console.error('SelfieSegmentation is not defined.');
			return;
		}

		try {
			this.selfieSegmentationInstance = new SelfieSegmentation({
				locateFile: (file) => {
					return `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`;
				},
			});
			this.selfieSegmentationInstance.setOptions({
				modelSelection: 1,
				selfieMode: false,
			});
			this.selfieSegmentationInstance.onResults(this.onResults.bind(this));
			console.log('Selfie Segmentation model loaded successfully');
		} catch (error) {
			console.error('Error initializing SelfieSegmentation:', error);
		}
	}

	async initializeSelfieSegmentation() {
		console.log('Initializing Selfie Segmentation');
		this.selfieSegmentation = new SelfieSegmentation({
			locateFile: (file) => {
				return `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`;
			},
		});

		this.selfieSegmentation.setOptions({
			modelSelection: 1,
			selfieMode: false,
		});

		this.selfieSegmentation.onResults(this.onResults.bind(this));

		if (this.videoElement) {
			this.camera = new Camera(this.videoElement, {
				onFrame: async () => {
					if (this.selfieSegmentation) {
						await this.selfieSegmentation.send({
							image: this.videoElement,
						});
					}
				},
				width: 640, // 너비 설정
				height: 480, // 높이 설정
			});
			this.camera.start();
		}
	}

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

	onResults(results) {
		if (!results || !results.segmentationMask || !results.image) return;

		const canvasCtx = this.canvasElement.getContext('2d');
		canvasCtx.save();
		canvasCtx.clearRect(0, 0, this.canvasElement.width, this.canvasElement.height);

		const tempCanvas = document.createElement('canvas');
		tempCanvas.width = results.segmentationMask.width;
		tempCanvas.height = results.segmentationMask.height;
		const tempCtx = tempCanvas.getContext('2d');
		tempCtx.drawImage(results.segmentationMask, 0, 0);

		tempCtx.filter = 'blur(4px)';
		tempCtx.drawImage(tempCanvas, 0, 0);
		tempCtx.filter = 'none';

		const imageData = tempCtx.getImageData(0, 0, tempCanvas.width, tempCanvas.height);

		const threshold = 128;

		for (let i = 0; i < imageData.data.length; i += 4) {
			if (imageData.data[i] < threshold) {
				imageData.data[i + 3] = 0;
			}
		}

		const maskCanvas = document.createElement('canvas');
		maskCanvas.width = imageData.width;
		maskCanvas.height = imageData.height;
		const maskCtx = maskCanvas.getContext('2d');
		maskCtx.putImageData(imageData, 0, 0);

		canvasCtx.drawImage(results.image, 0, 0, this.canvasElement.width, this.canvasElement.height);

		canvasCtx.globalCompositeOperation = 'destination-in';
		canvasCtx.drawImage(maskCanvas, 0, 0, this.canvasElement.width, this.canvasElement.height);

		canvasCtx.globalCompositeOperation = 'source-over';
		canvasCtx.restore();
	}

	cleanup() {
		console.log('shootView unMounted!');

		if (this.camera) {
			this.camera.stop();
			this.camera = null; // 참조 해제
		}
		if (this.selfieSegmentation) {
			this.selfieSegmentation.close();
			this.selfieSegmentation = null; // 참조 해제
		}

		WebRTCService.closeAllConnections();
		WebRTCService.disconnect();

		WebSocketService.off('participant_joined');
		WebSocketService.off('participant_left');
		WebSocketService.close();

		WebSocketService.off('background_changed');

		this.selfieSegmentationInstance = null;
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
