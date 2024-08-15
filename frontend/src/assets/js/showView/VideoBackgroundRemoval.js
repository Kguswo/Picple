export default class VideoBackgroundRemoval {
  constructor() {
    console.log('VideoBackgroundRemoval: 초기화 시작');

    this.selfieSegmentation = new window.SelfieSegmentation({
      locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
    });

    this.selfieSegmentation.setOptions({
      modelSelection: 1,
    });

    this.selfieSegmentation.onResults(this.onResults.bind(this));

    this.ctx = null;
    this.gl = null;

    console.log('VideoBackgroundRemoval: 초기화 완료');
  }

  async initialize() {
    console.log('VideoBackgroundRemoval: MediaPipe 초기화 시작');
    await this.selfieSegmentation.initialize();
    console.log('VideoBackgroundRemoval: MediaPipe 초기화 완료');
  }

  initCanvas(canvas) {
    console.log('VideoBackgroundRemoval: 캔버스 초기화 시작');
    this.gl = canvas.getContext("webgl");
    if (!this.gl) {
      console.error("WebGL 컨텍스트를 초기화할 수 없습니다.");
      return;
    }
    console.log('VideoBackgroundRemoval: WebGL 컨텍스트 초기화 완료');

    const vertexShaderSource = `
      attribute vec4 a_position;
      attribute vec2 a_texCoord;
      varying vec2 v_texCoord;
      void main() {
          gl_Position = a_position;
          v_texCoord = a_texCoord;
      }
    `;

    const fragmentShaderSource = `
      precision mediump float;
      varying vec2 v_texCoord;
      uniform sampler2D u_image;
      uniform sampler2D u_mask;
      void main() {
          vec4 maskColor = texture2D(u_mask, v_texCoord);
          vec4 imageColor = texture2D(u_image, v_texCoord);
          gl_FragColor = mix(vec4(0.0), imageColor, maskColor.a);
      }
    `;

    this.program = this.createProgram(this.gl, vertexShaderSource, fragmentShaderSource);
    this.initBuffers();
    console.log('VideoBackgroundRemoval: 셰이더 및 버퍼 초기화 완료');
  }

  createShader(gl, type, source) {
    console.log(`VideoBackgroundRemoval: 셰이더 생성 중 - 타입: ${type === gl.VERTEX_SHADER ? 'VERTEX_SHADER' : 'FRAGMENT_SHADER'}`);
    const shader = gl.createShader(type);
    gl.shaderSource(shader, source);
    gl.compileShader(shader);
    const success = gl.getShaderParameter(shader, gl.COMPILE_STATUS);
    if (!success) {
      console.error(gl.getShaderInfoLog(shader));
      gl.deleteShader(shader);
      return null;
    }
    console.log('VideoBackgroundRemoval: 셰이더 생성 완료');
    return shader;
  }

  createProgram(gl, vertexShaderSource, fragmentShaderSource) {
    console.log('VideoBackgroundRemoval: 프로그램 생성 중');
    const vertexShader = this.createShader(gl, gl.VERTEX_SHADER, vertexShaderSource);
    const fragmentShader = this.createShader(gl, gl.FRAGMENT_SHADER, fragmentShaderSource);
    const program = gl.createProgram();
    gl.attachShader(program, vertexShader);
    gl.attachShader(program, fragmentShader);
    gl.linkProgram(program);
    const success = gl.getProgramParameter(program, gl.LINK_STATUS);
    if (!success) {
      console.error(gl.getProgramInfoLog(program));
      gl.deleteProgram(program);
      return null;
    }
    console.log('VideoBackgroundRemoval: 프로그램 생성 및 링크 완료');
    return program;
  }

  initBuffers() {
    console.log('VideoBackgroundRemoval: 버퍼 초기화 시작');
    const gl = this.gl;
    const positionBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer);
    const positions = [-1, -1, 1, -1, -1, 1, 1, 1];
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(positions), gl.STATIC_DRAW);

    const texCoordBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, texCoordBuffer);
    const texCoords = [0, 0, 1, 0, 0, 1, 1, 1];
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(texCoords), gl.STATIC_DRAW);
    console.log('VideoBackgroundRemoval: 버퍼 초기화 완료');
  }

  setTexture(image, textureUnit) {
    console.log(`VideoBackgroundRemoval: 텍스처 설정 중 - 유닛: ${textureUnit}`);
    const gl = this.gl;
    const texture = gl.createTexture();
    gl.activeTexture(textureUnit);
    gl.bindTexture(gl.TEXTURE_2D, texture);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, image);
    console.log('VideoBackgroundRemoval: 텍스처 설정 완료');
    return texture;
  }

  onResults(results) {
    console.log('VideoBackgroundRemoval: 결과 수신');
    const gl = this.gl;
    if (!gl || !this.program) {
      console.error('WebGL 컨텍스트 또는 프로그램이 초기화되지 않았습니다.');
      return;
    }

    const maskTexture = this.setTexture(results.segmentationMask, gl.TEXTURE0);
    const imageTexture = this.setTexture(results.image, gl.TEXTURE1);

    gl.clear(gl.COLOR_BUFFER_BIT);
    gl.useProgram(this.program);

    gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer());
    gl.enableVertexAttribArray(gl.getAttribLocation(this.program, "a_position"));
    gl.vertexAttribPointer(gl.getAttribLocation(this.program, "a_position"), 2, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer());
    gl.enableVertexAttribArray(gl.getAttribLocation(this.program, "a_texCoord"));
    gl.vertexAttribPointer(gl.getAttribLocation(this.program, "a_texCoord"), 2, gl.FLOAT, false, 0, 0);

    gl.uniform1i(gl.getUniformLocation(this.program, "u_image"), 1);
    gl.uniform1i(gl.getUniformLocation(this.program, "u_mask"), 0);

    gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);

    gl.deleteTexture(maskTexture);
    gl.deleteTexture(imageTexture);
    console.log('VideoBackgroundRemoval: WebGL 그리기 완료');
  }

  async processVideo(videoElement, canvasElement) {
    console.log('VideoBackgroundRemoval: 비디오 처리 시작');
    if (!videoElement || !canvasElement) {
      console.error('비디오 엘리먼트 또는 캔버스 엘리먼트가 없습니다.');
      return;
    }

    if (videoElement.videoWidth === 0 || videoElement.videoHeight === 0) {
      console.error('비디오 크기가 0입니다.');
      return;
    }

    canvasElement.width = videoElement.videoWidth;
    canvasElement.height = videoElement.videoHeight;

    console.log('VideoBackgroundRemoval: 비디오 크기 설정 완료, MediaPipe로 프레임 전송');
    await this.selfieSegmentation.send({ image: videoElement });

    console.log('VideoBackgroundRemoval: 프레임 전송 완료, 다음 프레임 처리 대기 중');
    requestAnimationFrame(() => this.processVideo(videoElement, canvasElement));
  }

  startProcessing(videoElement, canvasElement) {
    console.log('VideoBackgroundRemoval: 비디오 배경 제거 시작');
    this.initCanvas(canvasElement);
    this.processVideo(videoElement, canvasElement);
  }
}
