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

            await this.selfieSegmentation.initialize();
            this.selfieSegmentation.setOptions({
                modelSelection: 1,
            });
            this.selfieSegmentation.onResults(this.onResults.bind(this));
        } catch (error) {
            console.error('VideoBackgroundRemoval 초기화 중 오류:', error);
        }
    }

    initCanvas(canvas) {
        this.ctx = canvas.getContext('2d');
        if (!this.ctx) {
            throw new Error('2D 컨텍스트를 초기화할 수 없습니다.');
        }
    }

    onResults(results) {
        if (!this.ctx) return;

        this.ctx.save();
        this.ctx.clearRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
        
        this.ctx.drawImage(results.segmentationMask, 0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
        
        this.ctx.globalCompositeOperation = 'source-in';
        this.ctx.drawImage(results.image, 0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
        
        this.ctx.globalCompositeOperation = 'destination-atop';
        this.ctx.fillStyle = '#00FF00';  // 초록색 배경, 필요에 따라 변경 가능
        this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
        
        this.ctx.restore();
    }

    async createProcessedStream(videoElement) {
        const canvas = document.createElement('canvas');
        canvas.width = videoElement.videoWidth;
        canvas.height = videoElement.videoHeight;
        this.initCanvas(canvas);

        const processFrame = async () => {
            if (videoElement.videoWidth > 0 && videoElement.videoHeight > 0) {
                await this.selfieSegmentation.send({ image: videoElement });
            }
            requestAnimationFrame(processFrame);
        };

        processFrame();

        return canvas.captureStream(30);
    }
}
