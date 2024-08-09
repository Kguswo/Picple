import WebSocketService from '@/services/WebSocketService';

class WebRTCService {
	constructor() {
		this.peerConnections = {};
		this.localStream = null;
		this.onRemoteStream = null;
	}

	/**
	 * 로컬 미디어 스트림을 초기화합니다.
	 * @returns {Promise<MediaStream>} 초기화된 로컬 미디어 스트림
	 */
	async initializeLocalStream() {
		try {
			this.localStream = await navigator.mediaDevices.getUserMedia({
				video: true,
				audio: true,
			});
			return this.localStream;
		} catch (error) {
			console.error('Error accessing media devices:', error);
			throw error;
		}
	}

	/**
	 * 참가자와의 피어 연결을 생성합니다.
	 * @param {string} participantId 참가자 ID
	 * @returns {Promise<RTCPeerConnection>} 생성된 피어 연결
	 */
	async createPeerConnection(participantId) {
		const peerConnection = new RTCPeerConnection({
			iceServers: [{ urls: 'stun:stun.l.google.com:19302' }],
		});

		this._setupPeerConnectionListeners(peerConnection, participantId);
		this._addLocalStreamTracks(peerConnection);

		this.peerConnections[participantId] = peerConnection;

		peerConnection.onicecandidate = (event) => {
			if (event.candidate) {
				WebSocketService.send({
					type: 'ice-candidate',
					recipient: participantId,
					candidate: event.candidate,
				});
			}
		};

		return peerConnection;
	}

	/**
	 * 오퍼를 생성하고 전송합니다.
	 * @param {string} participantId 참가자 ID
	 */
	async createOffer(participantId) {
		const peerConnection = await this.createPeerConnection(participantId);
		const offer = await peerConnection.createOffer();
		await peerConnection.setLocalDescription(offer);
		this._sendWebSocketMessage('offer', participantId, { offer });
	}

	/**
	 * 수신된 오퍼를 처리하고 응답합니다.
	 * @param {string} participantId 참가자 ID
	 * @param {RTCSessionDescriptionInit} offer 수신된 오퍼
	 */
	async handleOffer(participantId, offer) {
		const peerConnection = await this.createPeerConnection(participantId);
		await peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
		const answer = await peerConnection.createAnswer();
		await peerConnection.setLocalDescription(answer);
		WebSocketService.send({
			type: 'answer',
			recipient: participantId,
			answer: answer,
		});
	}

	/**
	 * 수신된 응답을 처리합니다.
	 * @param {string} participantId 참가자 ID
	 * @param {RTCSessionDescriptionInit} answer 수신된 응답
	 */
	async handleAnswer(participantId, answer) {
		const peerConnection = this.peerConnections[participantId];
		if (peerConnection) {
			await peerConnection.setRemoteDescription(answer);
		}
	}

	/**
	 * ICE 후보를 처리합니다.
	 * @param {string} participantId 참가자 ID
	 * @param {RTCIceCandidateInit} candidate ICE 후보
	 */
	async handleIceCandidate(participantId, candidate) {
		const peerConnection = this.peerConnections[participantId];
		if (peerConnection) {
			await peerConnection.addIceCandidate(candidate);
		}
	}

	/**
	 * 모든 연결을 종료합니다.
	 */
	closeAllConnections() {
		Object.values(this.peerConnections).forEach((connection) => connection.close());
		this.peerConnections = {};
		this._stopLocalStream();
	}

	/**
	 * WebRTC 서비스를 완전히 종료합니다.
	 */
	disconnect() {
		this.closeAllConnections();
		this.localStream = null;
	}

	// Private methods

	/**
	 * 피어 연결에 리스너를 설정합니다.
	 * @param {RTCPeerConnection} peerConnection
	 * @param {string} participantId
	 */
	_setupPeerConnectionListeners(peerConnection, participantId) {
		peerConnection.onicecandidate = (event) => {
			if (event.candidate) {
				this._sendWebSocketMessage('ice_candidate', participantId, {
					candidate: event.candidate,
				});
			}
		};

		peerConnection.ontrack = (event) => {
			if (this.onRemoteStream) {
				this.onRemoteStream(participantId, event.streams[0]);
			}
		};
	}

	/**
	 * 로컬 스트림의 트랙을 피어 연결에 추가합니다.
	 * @param {RTCPeerConnection} peerConnection
	 */
	_addLocalStreamTracks(peerConnection) {
		this.localStream.getTracks().forEach((track) => {
			peerConnection.addTrack(track, this.localStream);
		});
	}

	/**
	 * WebSocket을 통해 메시지를 전송합니다.
	 * @param {string} type 메시지 타입
	 * @param {string} recipient 수신자 ID
	 * @param {Object} data 전송할 데이터
	 */
	_sendWebSocketMessage(type, recipient, data) {
		WebSocketService.socket.send(
			JSON.stringify({
				type,
				recipient,
				...data,
			}),
		);
	}

	/**
	 * 로컬 스트림을 정지합니다.
	 */
	_stopLocalStream() {
		if (this.localStream) {
			this.localStream.getTracks().forEach((track) => track.stop());
		}
	}
}

export default new WebRTCService();
