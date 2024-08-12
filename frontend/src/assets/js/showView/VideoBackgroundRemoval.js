export default class VideoBackgroundRemoval {
    constructor() {
        this.selfieSegmentation = null;
        this.ctx = null;
    }

    async initialize() {
        try {
            const { SelfieSegmentation } = await import('@mediapipe/selfie_segmentation');
            this.selfieSegmentation = new SelfieSegmentation({
                locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
            });

            this.selfieSegmentation.setOptions({
                modelSelection: 1,
            });

            this.selfieSegmentation.onResults(this.onResults.bind(this));

            await this.selfieSegmentation.initialize();
        } catch (error) {
            console.error('SelfieSegmentation 초기화 중 오류 발생:', error);
            throw error;
        }
    }

    initCanvas(canvas) {
        this.ctx = canvas.getContext('2d');
        if (!this.ctx) {
            console.error('2D 컨텍스트를 초기화할 수 없습니다.');
            return;
        }
    }

    onResults(results) {
        if (!this.ctx) return;

        this.ctx.clearRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
        this.ctx.drawImage(results.segmentationMask, 0, 0, this.ctx.canvas.width, this.ctx.canvas.height);

        this.ctx.globalCompositeOperation = 'source-in';
        this.ctx.drawImage(results.image, 0, 0, this.ctx.canvas.width, this.ctx.canvas.height);

        this.ctx.globalCompositeOperation = 'source-over';
    }

    async processVideo(videoElement, canvasElement) {
        if (!videoElement || !canvasElement) return;

        if (videoElement.videoWidth === 0 || videoElement.videoHeight === 0) {
            console.error('비디오 크기가 0입니다.');
            return;
        }

        canvasElement.width = videoElement.videoWidth;
        canvasElement.height = videoElement.videoHeight;

        if (this.selfieSegmentation) {
            await this.selfieSegmentation.send({ image: videoElement });
        }

        requestAnimationFrame(() => this.processVideo(videoElement, canvasElement));
    }

    async startProcessing(videoElement, canvasElement) {
        if (!this.selfieSegmentation) {
            await this.initialize();
        }
        this.initCanvas(canvasElement);
        this.processVideo(videoElement, canvasElement);
    }
}
