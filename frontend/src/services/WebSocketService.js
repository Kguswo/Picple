class WebSocketService {
  constructor() {
    this.socket = null;
    this.handlers = {};
    this.boothId = null;
    this._isConnected = false;
    this.connectionPromise = null;
    this.shouldReconnect = true;
    this.participants = [];
  }

  connect(url) {
    if (this._isConnected) {
      console.log("WebSocket already connected");
      return Promise.resolve();
    }

    if (this.connectionPromise) {
      return this.connectionPromise;
    }

    this.connectionPromise = new Promise((resolve, reject) => {
      this.socket = new WebSocket(url);

      this.socket.onopen = () => {
        console.log("WebSocket 연결 성공");
        this._isConnected = true;
        resolve();
      };

      this.socket.onmessage = (event) => {
        console.log("메시지 수신:", event.data);
        const message = JSON.parse(event.data);
        if (this.handlers[message.type]) {
          this.handlers[message.type](message);
        }
      };

      this.socket.onclose = (event) => {
        console.log("WebSocket 연결 종료", event);
        this._isConnected = false;
        this.connectionPromise = null;
        if (this.shouldReconnect) {
          console.log("WebSocket 재연결 시도");
          setTimeout(() => this.connect(url), 1000);
        }
      };

      this.socket.onerror = (error) => {
        console.error("WebSocket 연결 에러:", error);
        this._isConnected = false;
        this.connectionPromise = null;
        reject(error);
      };
    });

    return this.connectionPromise;
  }

  isConnected() {
    return this._isConnected;
  }

  on(type, handler) {
    this.handlers[type] = handler;
  }

  send(message) {
    return new Promise((resolve, reject) => {
      if (this.isConnected()) {
        console.log("메시지 전송:", JSON.stringify(message));
        this.socket.send(JSON.stringify(message));
        resolve();
      } else {
        console.error("WebSocket is not connected");
        reject(new Error("WebSocket is not connected"));
      }
    });
  }

  close() {
    this.shouldReconnect = false;
    if (this.socket) {
      console.log("WebSocket 연결 종료 호출됨");
      this.socket.close();
    }
  }

  createBooth() {
    return new Promise((resolve, reject) => {
      const handleBoothCreated = (message) => {
        this.boothId = message.boothId.slice(0, 10);
        this.off("booth_created", handleBoothCreated);
        resolve(message.boothId);
      };

      const handleError = (message) => {
        this.off("error", handleError);
        reject(new Error(message.message));
      };

      this.on("booth_created", handleBoothCreated);
      this.on("error", handleError);

      this.send({ type: "create_booth" }).catch((error) => {
        this.off("booth_created", handleBoothCreated);
        this.off("error", handleError);
        reject(error);
      });

      setTimeout(() => {
        this.off("booth_created", handleBoothCreated);
        this.off("error", handleError);
        reject(new Error("Booth creation timeout"));
      }, 10000);
    });
  }

  joinBooth(boothId) {
    return new Promise((resolve, reject) => {
      const handleJoinedBooth = (message) => {
        this.off("joined_booth", handleJoinedBooth);
        resolve(message);
      };

      const handleError = (message) => {
        this.off("error", handleError);
        reject(new Error(message.message));
      };

      this.on("joined_booth", handleJoinedBooth);
      this.on("error", handleError);

      this.send({ type: "join_booth", boothId: boothId }).catch((error) => {
        this.off("joined_booth", handleJoinedBooth);
        this.off("error", handleError);
        reject(error);
      });

      setTimeout(() => {
        this.off("joined_booth", handleJoinedBooth);
        this.off("error", handleError);
        reject(new Error("Booth join timeout"));
      }, 10000);
    });
  }

  off(type, handler) {
    if (this.handlers[type] === handler) {
      delete this.handlers[type];
    }
  }

  messageHandlers = {
    booth_created: this.handleBoothCreated,
    participant_joined: this.handleParticipantJoined,
    participant_left: this.handleParticipantLeft,
    background_changed: this.handleBackgroundChanged,
  };

  handleMessage(message) {
    const handler = this.messageHandlers[message.type];
    if (handler) {
      handler(message);
    } else {
      console.warn(`Unhandled message type: ${message.type}`);
    }
  }

  handleParticipantJoined(message) {
    this.participants.push(message.participant);
  }

  handleParticipantLeft(message) {
    this.participants = this.participants.filter((p) => p.id !== message.participantId);
  }

  handleBackgroundChanged(message) {
    if (this.handlers["background_changed"]) {
      this.handlers["background_changed"](message);
    }
  }
}

export default new WebSocketService();
