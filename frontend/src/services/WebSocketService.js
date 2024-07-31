class WebSocketService {
    constructor() {
        this.socket = null;
        this.handlers = {};
        this.boothId = null;
        this._isConnected = false;
    }

    connect(url) {
        return new Promise((resolve, reject) => {
            this.socket = new WebSocket(url);

            this.socket.onopen = () => {
                console.log("WebSocket 연결 성공");
                this._isConnected = true;
                resolve();
            };

            this.socket.onclose = () => {
                console.log("WebSocket 연결 종료");
                this._isConnected = false;
            };

            this.socket.onerror = (error) => {
                console.error("WebSocket 연결 에러:", error);
                this._isConnected = false;
                reject(error);
            };

            this.socket = new WebSocket(url);

            this.socket.onopen = () => {
                console.log("WebSocket 연결 성공");
            };

            this.socket.onmessage = (event) => {
                const message = JSON.parse(event.data);
                if (this.handlers[message.type]) {
                    this.handlers[message.type](message);
                }
            };

            this.socket.onclose = () => {
                console.log("WebSocket 연결 종료");
            };
        });
    }

    isConnected() {
        return this._isConnected;
    }

    on(type, handler) {
        this.handlers[type] = handler;
    }

    send(message) {
        if (this.isConnected()) {
            this.socket.send(JSON.stringify(message));
        } else {
            console.error("WebSocket is not connected");
        }
    }

    close() {
        if (this.socket) {
            this.socket.close();
        }
    }

    createBooth() {
        return new Promise((resolve, reject) => {
            if (!this.isConnected()) {
                reject(new Error("WebSocket is not connected"));
                return;
            }
            this.send({ type: "create_booth" });
            this.on("booth_created", (message) => {
                this.boothId = message.boothId;
                resolve(message.boothId);
            });
            // 에러 처리를 위한 타임아웃 설정
            setTimeout(() => {
                reject(new Error("Booth creation timeout"));
            }, 5000);
        });
    }

    joinBooth(boothCode) {
        return new Promise((resolve, reject) => {
            if (!this.isConnected()) {
                reject(new Error("WebSocket is not connected"));
                return;
            }
            this.send({ type: "join_booth", boothCode });
            this.on("join_success", () => {
                this.boothId = boothCode;
                resolve();
            });
            this.on("join_error", (message) => {
                reject(new Error(message.error));
            });
            // 에러 처리를 위한 타임아웃 설정
            setTimeout(() => {
                reject(new Error("Join booth timeout"));
            }, 5000);
        });
    }
}

export default new WebSocketService();
