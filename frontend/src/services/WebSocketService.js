import { Client } from '@stomp/stompjs';

class WebSocketService {
    constructor() {
        this.socket = null;
        this.handlers = {};
        this.boothId = null;
        this._isConnected = false;
        this.connectionPromise = null;
        this.shouldReconnect = true;
        this.participants = [];
        this.boothStore = null;

        // STOMP 클라이언트 추가
        this.stompClient = null;
        this.stompConnected = false;
    }

    connect(url) {
        if (this._isConnected) {
            console.log('WebSocket already connected');
            return Promise.resolve();
        }

        if (this.connectionPromise) {
            return this.connectionPromise;
        }

        this.connectionPromise = new Promise((resolve, reject) => {
            this.socket = new WebSocket(url);

            this.socket.onopen = () => {
                console.log('WebSocket 연결 성공');
                this._isConnected = true;
                resolve();
            };

            this.socket.onmessage = (event) => {
                console.log('메시지 수신:', event.data);
                const message = JSON.parse(event.data);
                if (this.handlers[message.type]) {
                    this.handlers[message.type](message);
                }
            };

            this.socket.onclose = (event) => {
                console.log('WebSocket 연결 종료', event);
                this._isConnected = false;
                this.connectionPromise = null;
                if (this.shouldReconnect) {
                    console.log('WebSocket 재연결 시도');
                    setTimeout(() => this.connect(url), 1000);
                }
            };

            this.socket.onerror = (error) => {
                console.error('WebSocket 연결 에러:', error);
                this._isConnected = false;
                this.connectionPromise = null;
                reject(error);
            };
        });

        return this.connectionPromise;
    }

    connectStomp(url) {
        if (this.stompConnected) {
            console.log('STOMP already connected');
            return Promise.resolve();
        }

        return new Promise((resolve, reject) => {
            this.stompClient = new Client({
                brokerURL: url,
                onConnect: () => {
                    console.log('Connected to STOMP');
                    this.stompConnected = true;
                    this.stompClient.subscribe('/topic/public', (message) => {
                        const data = JSON.parse(message.body);
                        if (this.handlers[data.type]) {
                            this.handlers[data.type](data);
                        }
                    });
                    resolve();
                },
                onStompError: (frame) => {
                    console.error('STOMP error', frame);
                    this.stompConnected = false;
                    reject(new Error('STOMP connection error'));
                },
            });

            this.stompClient.onWebSocketError = (error) => {
                console.error('WebSocket error', error);
                this.stompConnected = false;
                reject(error);
            };

            this.stompClient.activate();
        });
    }

    isConnected() {
        return this._isConnected;
    }

    isStompConnected() {
        return this.stompConnected;
    }

    on(type, handler) {
        this.handlers[type] = handler;
    }

    send(message) {
        return new Promise((resolve, reject) => {
            if (this.isConnected()) {
                console.log('메시지 전송:', JSON.stringify(message));
                this.socket.send(JSON.stringify(message));
                resolve();
            } else {
                console.error('WebSocket is not connected');
                reject(new Error('WebSocket is not connected'));
            }
        });
    }

    sendStomp(message) {
        if (this.stompClient && this.stompConnected) {
            return this.stompClient.publish({
                destination: '/app/chat.sendMessage',
                body: JSON.stringify(message),
            });
        } else {
            console.error('STOMP client is not connected');
            return Promise.reject(new Error('STOMP client is not connected'));
        }
    }

    close() {
        this.shouldReconnect = false;
        if (this.socket) {
            console.log('WebSocket 연결 종료 호출됨');
            this.socket.close();
        }
        this.closeStomp();
    }

    closeStomp() {
        if (this.stompClient) {
            this.stompClient.deactivate();
            this.stompConnected = false;
        }
    }

    setBoothStore(store) {
        this.boothStore = store;
    }

    createBooth() {
        return new Promise((resolve, reject) => {
            const handleBoothCreated = (message) => {
                this.boothId = message.boothId.slice(0, 10);
                this.off('booth_created', handleBoothCreated);
                resolve(message.boothId);
            };

            const handleError = (message) => {
                this.off('error', handleError);
                reject(new Error(message.message));
            };

            this.on('booth_created', handleBoothCreated);
            this.on('error', handleError);

            this.send({ type: 'create_booth' }).catch((error) => {
                this.off('booth_created', handleBoothCreated);
                this.off('error', handleError);
                reject(error);
            });

            setTimeout(() => {
                this.off('booth_created', handleBoothCreated);
                this.off('error', handleError);
                reject(new Error('Booth creation timeout'));
            }, 10000);
        });
    }

    joinBooth(boothId) {
        return new Promise((resolve, reject) => {
            const handleJoinedBooth = (message) => {
                this.off('joined_booth', handleJoinedBooth);
                if (message.currentBackground && this.boothStore) {
                    console.log('Setting background image:', message.currentBackground);
                    this.boothStore.setBgImage(message.currentBackground);
                } else {
                    console.error(
                        'Failed to set background image. boothStore:',
                        this.boothStore,
                        'currentBackground:',
                        message.currentBackground,
                    );
                }
                resolve(message);
            };

            const handleError = (message) => {
                this.off('error', handleError);
                reject(new Error(message.message));
            };

            this.on('joined_booth', handleJoinedBooth);
            this.on('error', handleError);

            this.send({ type: 'join_booth', boothId: boothId }).catch((error) => {
                this.off('joined_booth', handleJoinedBooth);
                this.off('error', handleError);
                reject(error);
            });

            setTimeout(() => {
                this.off('joined_booth', handleJoinedBooth);
                this.off('error', handleError);
                reject(new Error('Booth join timeout'));
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
        background_info: this.handleBackgroundInfo,
    };

    requestCurrentBackground() {
        return this.send({ type: 'request_background' });
    }

    handleBackgroundInfo(message) {
        if (this.handlers['background_info']) {
            this.handlers['background_info'](message);
        }
    }

    handleMessage(message) {
        console.log('Received message:', message);
        switch (message.type) {
            case 'new-peer':
                if (this.handlers['new-peer']) {
                    this.handlers['new-peer'](message.peerId);
                }
                break;
            case 'offer':
                WebRTCService.handleOffer(message.sender, message.offer);
                break;
            case 'answer':
                WebRTCService.handleAnswer(message.sender, message.answer);
                break;
            case 'ice-candidate':
                WebRTCService.handleIceCandidate(message.sender, message.candidate);
                break;
        }
    }

    handleParticipantJoined(message) {
        this.participants.push(message.participant);
    }

    handleParticipantLeft(message) {
        this.participants = this.participants.filter((p) => p.id !== message.participantId);
    }

    handleBackgroundChanged(message) {
        if (this.handlers['background_changed']) {
            this.handlers['background_changed'](message);
        }
    }

    handleBoothCreated(message) {
        this.boothId = message.boothId.slice(0, 10);
        if (this.handlers['booth_created']) {
            this.handlers['booth_created'](message);
        }
    }
}

export default new WebSocketService();
