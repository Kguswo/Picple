export default class VideoBackgroundRemoval {
    constructor() {
        this.selfieSegmentation = null;
        this.isProcessing = false;
        this.ctx = null;
    }

    async initialize() {
        try {
            this.selfieSegmentation = new window.SelfieSegmentation({
                locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
            });

            await this.selfieSegmentation.setOptions({ modelSelection: 1 });
            await this.selfieSegmentation.initialize();
        } catch (error) {
            console.error('VideoBackgroundRemoval 초기화 중 오류:', error);
            throw error;
        }
    }

    initCanvas(canvas) {
        this.ctx = canvas.getContext('2d');
        if (!this.ctx) {
            throw new Error('2D 컨텍스트를 초기화할 수 없습니다.');
        }
    }

    async processFrame(videoElement, canvas) {
        if (this.isProcessing) return;
        this.isProcessing = true;

        try {
            await this.selfieSegmentation.send({ image: videoElement });
            this.selfieSegmentation.onResults((results) => {
                this.ctx.clearRect(0, 0, canvas.width, canvas.height);
                this.ctx.drawImage(results.segmentationMask, 0, 0, canvas.width, canvas.height);
                this.ctx.globalCompositeOperation = 'source-in';
                this.ctx.drawImage(results.image, 0, 0, canvas.width, canvas.height);
                this.ctx.globalCompositeOperation = 'source-over';
            });
        } catch (error) {
            console.error('세그멘테이션 처리 중 오류:', error);
        } finally {
            this.isProcessing = false;
        }

        requestAnimationFrame(() => this.processFrame(videoElement, canvas));
    }

    async createProcessedStream(videoElement) {
        const canvas = document.createElement('canvas');
        canvas.width = videoElement.videoWidth || 640;
        canvas.height = videoElement.videoHeight || 480;
        this.initCanvas(canvas);

        this.processFrame(videoElement, canvas);

        return canvas.captureStream(30);
    }
}
