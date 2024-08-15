import WebGLHelper from './WebGLHelper';

export default class VideoBackgroundRemoval {
  constructor(canvas) {
    this.selfieSegmentation = new window.SelfieSegmentation({
      locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
    });

    this.selfieSegmentation.setOptions({
      modelSelection: 1,
    });

    this.selfieSegmentation.onResults(this.onResults.bind(this));

    this.glHelper = new WebGLHelper(canvas);
  }

  async initialize() {
    await this.selfieSegmentation.initialize();
  }

  onResults(results) {
    if (!this.glHelper) return;

    this.glHelper.setTexture(results.image);
    this.glHelper.draw();
  }

  async processVideo(videoElement, canvasElement) {
    if (!videoElement || !canvasElement) return;

    if (videoElement.videoWidth === 0 || videoElement.videoHeight === 0) {
      console.error('비디오 크기가 0입니다.');
      return;
    }

    canvasElement.width = videoElement.videoWidth;
    canvasElement.height = videoElement.videoHeight;

    await this.selfieSegmentation.send({ image: videoElement });

    requestAnimationFrame(() => this.processVideo(videoElement, canvasElement));
  }

  startProcessing(videoElement, canvasElement) {
    this.processVideo(videoElement, canvasElement);
  }
}
