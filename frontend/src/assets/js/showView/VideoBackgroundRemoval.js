export default class VideoBackgroundRemoval {
  constructor() {
    this.selfieSegmentation = new window.SelfieSegmentation({
      locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
    });

    this.selfieSegmentation.setOptions({
      modelSelection: 1,
    });

    this.gl = null;
    this.program = null;
    this.textures = {
      video: null,
      mask: null,
    };
    this.buffers = {
      position: null,
      texCoord: null,
    };
    this.locations = {
      position: null,
      texCoord: null,
      videoTexture: null,
      maskTexture: null,
    };
    this.isProcessingComplete = false;
    this.onProcessingComplete = null;
    this.isRunning = false;
    this.originalVideoTrack = null;
  }

  async initialize(canvasElement) {
    console.log('배경 제거 초기화 시작');
    try {
      await this.selfieSegmentation.initialize();
      this.initWebGL(canvasElement);
      this.selfieSegmentation.onResults(this.onResults.bind(this));
      console.log('배경 제거 초기화 완료');
    } catch (error) {
      console.error('배경 제거 초기화 중 오류 발생:', error);
      throw error;
    }
  }

  initWebGL(canvas) {
    try {
      this.gl = canvas.getContext('webgl', { premultipliedAlpha: false });
      if (!this.gl) {
        throw new Error('WebGL를 초기화할 수 없습니다.');
      }
      console.log('WebGL 컨텍스트 생성 성공');

      const vertexShaderSource = `
        attribute vec2 a_position;
        attribute vec2 a_texCoord;
        varying vec2 v_texCoord;
        void main() {
          gl_Position = vec4(a_position, 0.0, 1.0);
          v_texCoord = a_texCoord;
        }
      `;

      const fragmentShaderSource = `
        precision mediump float;
        uniform sampler2D u_videoTexture;
        uniform sampler2D u_maskTexture;
        varying vec2 v_texCoord;
        void main() {
          vec4 videoColor = texture2D(u_videoTexture, v_texCoord);
          vec4 maskColor = texture2D(u_maskTexture, v_texCoord);
          gl_FragColor = vec4(videoColor.rgb, maskColor.r);
        }
      `;
      
      const vertexShader = this.createShader(this.gl.VERTEX_SHADER, vertexShaderSource);
      const fragmentShader = this.createShader(this.gl.FRAGMENT_SHADER, fragmentShaderSource);

      this.program = this.createProgram(vertexShader, fragmentShader);

      this.locations.position = this.gl.getAttribLocation(this.program, 'a_position');
      this.locations.texCoord = this.gl.getAttribLocation(this.program, 'a_texCoord');
      this.locations.videoTexture = this.gl.getUniformLocation(this.program, 'u_videoTexture');
      this.locations.maskTexture = this.gl.getUniformLocation(this.program, 'u_maskTexture');

      this.buffers.position = this.gl.createBuffer();
      this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.buffers.position);
      this.gl.bufferData(this.gl.ARRAY_BUFFER, new Float32Array([-1, -1, 1, -1, -1, 1, 1, 1]), this.gl.STATIC_DRAW);

      this.buffers.texCoord = this.gl.createBuffer();
      this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.buffers.texCoord);
      this.gl.bufferData(this.gl.ARRAY_BUFFER, new Float32Array([0, 0, 1, 0, 0, 1, 1, 1]), this.gl.STATIC_DRAW);

      this.textures.video = this.gl.createTexture();
      this.textures.mask = this.gl.createTexture();

      console.log('WebGL 초기화 완료');
    } catch (error) {
      console.error('WebGL 초기화 중 오류 발생:', error);
      throw error;
    }
  }

  createShader(type, source) {
    const shader = this.gl.createShader(type);
    this.gl.shaderSource(shader, source);
    this.gl.compileShader(shader);
    if (!this.gl.getShaderParameter(shader, this.gl.COMPILE_STATUS)) {
      const error = new Error('셰이더 컴파일 오류: ' + this.gl.getShaderInfoLog(shader));
      this.gl.deleteShader(shader);
      throw error;
    }
    return shader;
  }

  createProgram(vertexShader, fragmentShader) {
    const program = this.gl.createProgram();
    this.gl.attachShader(program, vertexShader);
    this.gl.attachShader(program, fragmentShader);
    this.gl.linkProgram(program);
    if (!this.gl.getProgramParameter(program, this.gl.LINK_STATUS)) {
      const error = new Error('프로그램 링크 오류: ' + this.gl.getProgramInfoLog(program));
      this.gl.deleteProgram(program);
      throw error;
    }
    return program;
  }

  onResults(results) {
    if (!this.gl || !this.isRunning) return;

    this.gl.bindTexture(this.gl.TEXTURE_2D, this.textures.video);
    this.gl.texImage2D(this.gl.TEXTURE_2D, 0, this.gl.RGBA, this.gl.RGBA, this.gl.UNSIGNED_BYTE, results.image);

    this.gl.bindTexture(this.gl.TEXTURE_2D, this.textures.mask);
    this.gl.texImage2D(this.gl.TEXTURE_2D, 0, this.gl.RGBA, this.gl.RGBA, this.gl.UNSIGNED_BYTE, results.segmentationMask);

    this.gl.useProgram(this.program);

    this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.buffers.position);
    this.gl.enableVertexAttribArray(this.locations.position);
    this.gl.vertexAttribPointer(this.locations.position, 2, this.gl.FLOAT, false, 0, 0);

    this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.buffers.texCoord);
    this.gl.enableVertexAttribArray(this.locations.texCoord);
    this.gl.vertexAttribPointer(this.locations.texCoord, 2, this.gl.FLOAT, false, 0, 0);

    this.gl.uniform1i(this.locations.videoTexture, 0);
    this.gl.uniform1i(this.locations.maskTexture, 1);

    this.gl.activeTexture(this.gl.TEXTURE0);
    this.gl.bindTexture(this.gl.TEXTURE_2D, this.textures.video);
    this.gl.activeTexture(this.gl.TEXTURE1);
    this.gl.bindTexture(this.gl.TEXTURE_2D, this.textures.mask);

    this.gl.drawArrays(this.gl.TRIANGLE_STRIP, 0, 4);

    if (!this.isProcessingComplete) {
      this.isProcessingComplete = true;
      if (this.onProcessingComplete) {
        this.onProcessingComplete();
      }
    }
  }

  startProcessing(videoElement, canvasElement, mediaStream) {
    if (this.isRunning) return;
    this.isRunning = true;
    console.log('비디오 처리 시작');
    
    this.originalVideoTrack = mediaStream.getVideoTracks()[0];
    this.processVideo(videoElement, canvasElement, mediaStream);
  }

  async processVideo(videoElement, canvasElement, mediaStream) {
    if (!this.isRunning) return;

    if (!videoElement || !canvasElement) {
      console.error('비디오 또는 캔버스 요소가 없음');
      return;
    }

    if (videoElement.readyState < 2) {
      console.log('비디오가 아직 로드되지 않음. 다음 프레임에서 재시도합니다.');
      requestAnimationFrame(() => this.processVideo(videoElement, canvasElement, mediaStream));
      return;
    }

    try {
      await this.selfieSegmentation.send({ image: videoElement });
      
      const processedStream = canvasElement.captureStream(30);
      const processedVideoTrack = processedStream.getVideoTracks()[0];

      if (mediaStream.getVideoTracks().length > 0) {
        mediaStream.removeTrack(mediaStream.getVideoTracks()[0]);
      }
      mediaStream.addTrack(processedVideoTrack);

    } catch (error) {
      console.error('프레임 처리 중 오류 발생:', error);
    }

    requestAnimationFrame(() => this.processVideo(videoElement, canvasElement, mediaStream));
  }

  stopProcessing(mediaStream) {
    this.isRunning = false;
    if (this.originalVideoTrack && mediaStream) {
      if (mediaStream.getVideoTracks().length > 0) {
        mediaStream.removeTrack(mediaStream.getVideoTracks()[0]);
      }
      mediaStream.addTrack(this.originalVideoTrack);
    }
    console.log('비디오 처리 중지');
  }

  setOnProcessingComplete(callback) {
    this.onProcessingComplete = callback;
  }

  dispose() {
    this.isRunning = false;
    if (this.gl) {
      this.gl.deleteProgram(this.program);
      this.gl.deleteBuffer(this.buffers.position);
      this.gl.deleteBuffer(this.buffers.texCoord);
      this.gl.deleteTexture(this.textures.video);
      this.gl.deleteTexture(this.textures.mask);
    }
    this.selfieSegmentation.close();
  }
}