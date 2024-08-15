export default class VideoBackgroundRemoval {
    constructor() {
      const workerBlob = new Blob([`importScripts('${location.origin}/assets/js/showView/BackgroundRemovalWorker.js');`], { type: 'application/javascript' });
      const worker = new Worker(URL.createObjectURL(workerBlob));
      this.workerReady = false;
      this.worker.onmessage = (e) => {
        if (e.data.type === 'ready') {
          console.log('Worker is ready');
          this.workerReady = true;
        } else if (e.data.type === 'segmentation') {
          this.drawSegmentation(e.data.segmentation);
        } else if (e.data.type === 'error') {
          console.error('Worker error:', e.data.message);
        }
      };
      this.worker.onerror = (error) => {
        console.error('Worker error:', error);
      };
      this.worker.postMessage({ type: 'init' });
      
      this.gl = null;
      this.program = null;
      this.positionBuffer = null;
      this.texCoordBuffer = null;
      this.videoTexture = null;
      this.segmentationTexture = null;
    }
  
    async initialize(canvas) {
      if (!canvas) {
        console.error('Canvas element is null or undefined');
        return false;
      }
  
      this.gl = canvas.getContext('webgl');
      if (!this.gl) {
        console.error('WebGL를 초기화할 수 없습니다. 브라우저가 WebGL을 지원하는지 확인해주세요.');
        return false;
      }
  
      const vertexShaderSource = `
        attribute vec2 a_position;
        attribute vec2 a_texCoord;
        varying vec2 v_texCoord;
        void main() {
          gl_Position = vec4(a_position, 0, 1);
          v_texCoord = a_texCoord;
        }
      `;
  
      const fragmentShaderSource = `
        precision mediump float;
        uniform sampler2D u_image;
        uniform sampler2D u_segmentation;
        varying vec2 v_texCoord;
        void main() {
          vec4 color = texture2D(u_image, v_texCoord);
          float alpha = texture2D(u_segmentation, v_texCoord).r;
          gl_FragColor = vec4(color.rgb, alpha);
        }
      `;
  
      this.program = this.createProgram(vertexShaderSource, fragmentShaderSource);
      this.gl.useProgram(this.program);
  
      this.positionBuffer = this.gl.createBuffer();
      this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.positionBuffer);
      this.gl.bufferData(this.gl.ARRAY_BUFFER, new Float32Array([
        -1, -1,
        1, -1,
        -1, 1,
        1, 1,
      ]), this.gl.STATIC_DRAW);
  
      this.texCoordBuffer = this.gl.createBuffer();
      this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.texCoordBuffer);
      this.gl.bufferData(this.gl.ARRAY_BUFFER, new Float32Array([
        0, 0,
        1, 0,
        0, 1,
        1, 1,
      ]), this.gl.STATIC_DRAW);
  
      this.videoTexture = this.gl.createTexture();
      this.gl.bindTexture(this.gl.TEXTURE_2D, this.videoTexture);
      this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_WRAP_S, this.gl.CLAMP_TO_EDGE);
      this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_WRAP_T, this.gl.CLAMP_TO_EDGE);
      this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_MIN_FILTER, this.gl.NEAREST);
      this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_MAG_FILTER, this.gl.NEAREST);
  
      this.segmentationTexture = this.gl.createTexture();
      this.gl.bindTexture(this.gl.TEXTURE_2D, this.segmentationTexture);
      this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_WRAP_S, this.gl.CLAMP_TO_EDGE);
      this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_WRAP_T, this.gl.CLAMP_TO_EDGE);
      this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_MIN_FILTER, this.gl.NEAREST);
      this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_MAG_FILTER, this.gl.NEAREST);
  
      return new Promise((resolve) => {
        const checkWorkerReady = () => {
          if (this.workerReady) {
            resolve(true);
          } else {
            setTimeout(checkWorkerReady, 100);
          }
        };
        checkWorkerReady();
      });
    }
  
    createShader(type, source) {
      const shader = this.gl.createShader(type);
      this.gl.shaderSource(shader, source);
      this.gl.compileShader(shader);
      if (!this.gl.getShaderParameter(shader, this.gl.COMPILE_STATUS)) {
        console.error('An error occurred compiling the shaders: ' + this.gl.getShaderInfoLog(shader));
        this.gl.deleteShader(shader);
        return null;
      }
      return shader;
    }
  
    createProgram(vertexShaderSource, fragmentShaderSource) {
      const vertexShader = this.createShader(this.gl.VERTEX_SHADER, vertexShaderSource);
      const fragmentShader = this.createShader(this.gl.FRAGMENT_SHADER, fragmentShaderSource);
      const program = this.gl.createProgram();
      this.gl.attachShader(program, vertexShader);
      this.gl.attachShader(program, fragmentShader);
      this.gl.linkProgram(program);
      if (!this.gl.getProgramParameter(program, this.gl.LINK_STATUS)) {
        console.error('Unable to initialize the shader program: ' + this.gl.getProgramInfoLog(program));
        return null;
      }
      return program;
    }
  
    processVideo(videoElement, canvasElement) {
      if (!this.workerReady || !videoElement || !canvasElement) {
        console.log('Not ready for processing:', { workerReady: this.workerReady, videoElement, canvasElement });
        return;
      }
  
      const tempCanvas = document.createElement('canvas');
      tempCanvas.width = videoElement.videoWidth;
      tempCanvas.height = videoElement.videoHeight;
      const tempCtx = tempCanvas.getContext('2d');
      tempCtx.drawImage(videoElement, 0, 0, tempCanvas.width, tempCanvas.height);
      const imageData = tempCtx.getImageData(0, 0, tempCanvas.width, tempCanvas.height);
  
      this.worker.postMessage({ type: 'segment', imageData: imageData }, [imageData.data.buffer]);
  
      this.gl.bindTexture(this.gl.TEXTURE_2D, this.videoTexture);
      this.gl.texImage2D(this.gl.TEXTURE_2D, 0, this.gl.RGBA, this.gl.RGBA, this.gl.UNSIGNED_BYTE, videoElement);
  
      requestAnimationFrame(() => this.processVideo(videoElement, canvasElement));
    }
  
    drawSegmentation(segmentation) {
      const width = this.gl.canvas.width;
      const height = this.gl.canvas.height;
  
      this.gl.viewport(0, 0, width, height);
      this.gl.clear(this.gl.COLOR_BUFFER_BIT);
  
      const positionLocation = this.gl.getAttribLocation(this.program, 'a_position');
      this.gl.enableVertexAttribArray(positionLocation);
      this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.positionBuffer);
      this.gl.vertexAttribPointer(positionLocation, 2, this.gl.FLOAT, false, 0, 0);
  
      const texCoordLocation = this.gl.getAttribLocation(this.program, 'a_texCoord');
      this.gl.enableVertexAttribArray(texCoordLocation);
      this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.texCoordBuffer);
      this.gl.vertexAttribPointer(texCoordLocation, 2, this.gl.FLOAT, false, 0, 0);
  
      this.gl.bindTexture(this.gl.TEXTURE_2D, this.segmentationTexture);
      this.gl.texImage2D(this.gl.TEXTURE_2D, 0, this.gl.ALPHA, width, height, 0, this.gl.ALPHA, this.gl.UNSIGNED_BYTE, segmentation);
  
      const u_image = this.gl.getUniformLocation(this.program, 'u_image');
      const u_segmentation = this.gl.getUniformLocation(this.program, 'u_segmentation');
      this.gl.uniform1i(u_image, 0);
      this.gl.uniform1i(u_segmentation, 1);
  
      this.gl.drawArrays(this.gl.TRIANGLE_STRIP, 0, 4);
    }
  
    startProcessing(videoElement, canvasElement) {
      this.processVideo(videoElement, canvasElement);
    }
}