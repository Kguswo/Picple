import WebSocketService from "./WebSocketService";

class WebRTCService {
    constructor() {
        this.peerConnections = {};
        this.localStream = null;
        this.onRemoteStream = null;
    }

    async initializeLocalStream() {
        try {
            this.localStream = await navigator.mediaDevices.getUserMedia({
                video: true,
                audio: true,
            });
            return this.localStream;
        } catch (error) {
            console.error("Error accessing media devices:", error);
        }
    }

    async createPeerConnection(participantId) {
        const peerConnection = new RTCPeerConnection({
            iceServers: [{ urls: "stun:stun.l.google.com:19302" }],
        });

        peerConnection.onicecandidate = (event) => {
            if (event.candidate) {
                WebSocketService.socket.send(
                    JSON.stringify({
                        type: "ice_candidate",
                        recipient: participantId,
                        candidate: event.candidate,
                    })
                );
            }
        };

        peerConnection.ontrack = (event) => {
            if (this.onRemoteStream) {
                this.onRemoteStream(participantId, event.streams[0]);
            }
        };

        this.localStream.getTracks().forEach((track) => {
            peerConnection.addTrack(track, this.localStream);
        });

        this.peerConnections[participantId] = peerConnection;
        return peerConnection;
    }

    async createOffer(participantId) {
        const peerConnection = await this.createPeerConnection(participantId);
        const offer = await peerConnection.createOffer();
        await peerConnection.setLocalDescription(offer);
        WebSocketService.socket.send(
            JSON.stringify({
                type: "offer",
                recipient: participantId,
                offer: offer,
            })
        );
    }

    async handleOffer(participantId, offer) {
        const peerConnection = await this.createPeerConnection(participantId);
        await peerConnection.setRemoteDescription(offer);
        const answer = await peerConnection.createAnswer();
        await peerConnection.setLocalDescription(answer);
        WebSocketService.socket.send(
            JSON.stringify({
                type: "answer",
                recipient: participantId,
                answer: answer,
            })
        );
    }

    async handleAnswer(participantId, answer) {
        const peerConnection = this.peerConnections[participantId];
        if (peerConnection) {
            await peerConnection.setRemoteDescription(answer);
        }
    }

    async handleIceCandidate(participantId, candidate) {
        const peerConnection = this.peerConnections[participantId];
        if (peerConnection) {
            await peerConnection.addIceCandidate(candidate);
        }
    }

    closeAllConnections() {
        Object.values(this.peerConnections).forEach((connection) =>
            connection.close()
        );
        this.peerConnections = {};
        if (this.localStream) {
            this.localStream.getTracks().forEach((track) => track.stop());
        }
    }

    disconnect() {
        this.closeAllConnections();
        this.localStream = null;
    }
}

export default new WebRTCService();
