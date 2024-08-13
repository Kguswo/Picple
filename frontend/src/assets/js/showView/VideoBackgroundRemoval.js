//import { SelfieSegmentation } from '@mediapipe/selfie_segmentation/selfie_segmentation.js';

export default class VideoBackgroundRemoval {
    constructor() {
        this.selfieSegmentation = null;
    }

    async initialize() {
        console.log('VideoBackgroundRemoval 초기화 시작');
        this.selfieSegmentation = new window.SelfieSegmentation({
            locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
        });

        await this.selfieSegmentation.initialize();
        console.log('SelfieSegmentation 초기화 완료');
        this.selfieSegmentation.setOptions({
            modelSelection: 1,
        });
        console.log('SelfieSegmentation 옵션 설정 완료');
        this.selfieSegmentation.onResults(this.onResults.bind(this));
        console.log('VideoBackgroundRemoval 초기화 완료');
    }

    initCanvas(canvas) {
        console.log('캔버스 초기화 시작');
        this.ctx = canvas.getContext('2d');
        if (!this.ctx) {
            console.error('2D 컨텍스트를 초기화할 수 없습니다.');
            return;
        }
        console.log('캔버스 초기화 완료');
    }

    onResults(results) {
        console.log('세그멘테이션 결과 처리 시작');
        if (!this.ctx) {
            console.error('컨텍스트가 초기화되지 않았습니다.');
            return;
        }

        this.ctx.clearRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
        this.ctx.drawImage(results.segmentationMask, 0, 0, this.ctx.canvas.width, this.ctx.canvas.height);

        this.ctx.globalCompositeOperation = 'source-in';
        this.ctx.drawImage(results.image, 0, 0, this.ctx.canvas.width, this.ctx.canvas.height);

        this.ctx.globalCompositeOperation = 'source-over';
        console.log('세그멘테이션 결과 처리 완료');
    }

    async processVideo(videoElement, canvasElement) {
        console.log('비디오 처리 시작');
        if (!videoElement || !canvasElement) {
            console.error('비디오 또는 캔버스 요소가 없습니다.');
            return;
        }

        if (videoElement.videoWidth === 0 || videoElement.videoHeight === 0) {
            console.error('비디오 크기가 0입니다.');
            return;
        }

        canvasElement.width = videoElement.videoWidth;
        canvasElement.height = videoElement.videoHeight;
        console.log(`캔버스 크기 설정: ${canvasElement.width}x${canvasElement.height}`);

        await this.selfieSegmentation.send({ image: videoElement });
        console.log('세그멘테이션 처리 요청 완료');

        requestAnimationFrame(() => this.processVideo(videoElement, canvasElement));
    }

    startProcessing(videoElement, canvasElement) {
        console.log('비디오 처리 시작');
        this.initCanvas(canvasElement);
        this.processVideo(videoElement, canvasElement);
    }
}

