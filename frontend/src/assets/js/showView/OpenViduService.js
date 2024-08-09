import { OpenVidu } from 'openvidu-browser';
import axios from 'axios';

class OpenViduService {
    constructor() {
        this.OV = null; // OpenVidu 객체
        this.session = null; // OpenVidu 세션
        this.subscribers = []; // 구독자(참가자) 목록
        this.guestVideos = []; // 게스트 비디오 목록
    }

    // OpenVidu 세션 초기화 및 연결
    async initializeSession(sessionId) {
        try {
            // OpenVidu 객체 생성
            this.OV = new OpenVidu();
            // 새 세션 초기화
            this.session = this.OV.initSession();

            // 새 참가자가 들어왔을 때의 이벤트 핸들러
            this.session.on('streamCreated', ({ stream }) => {
                console.log('새로운 스트림 생성됨:', stream.streamId);
                const subscriber = this.session.subscribe(stream);
                this.subscribers.push(subscriber);

                // 게스트 비디오 추가
                this.addGuestVideo(subscriber);
            });

            // 참가자가 나갔을 때의 이벤트 핸들러
            this.session.on('streamDestroyed', ({ stream }) => {
                console.log('스트림 제거됨:', stream.streamId);
                const index = this.subscribers.findIndex((sub) => sub.stream.streamId === stream.streamId);
                if (index >= 0) {
                    this.subscribers.splice(index, 1);

                    // 게스트 비디오 제거
                    this.removeGuestVideo(stream.streamId);
                }
            });

            // 세션 연결을 위한 토큰 얻기
            const token = await this.getToken(sessionId);
            // 세션에 연결
            await this.session.connect(token);

            // 로컬 웹캠 스트림 설정
            const publisher = await this.OV.initPublisherAsync(undefined, {
                audioSource: undefined, // 기본 오디오 소스 사용
                videoSource: undefined, // 기본 비디오 소스 사용
                publishAudio: true, // 오디오 발행
                publishVideo: true, // 비디오 발행
                resolution: '640x480', // 해상도 설정
                frameRate: 30, // 프레임 레이트 설정
                insertMode: 'APPEND', // 비디오 삽입 모드
                mirror: false, // 미러링 비활성화
            });

            // 로컬 스트림을 세션에 게시
            await this.session.publish(publisher);

            console.log('OpenVidu 세션 초기화 완료');
            return publisher;
        } catch (error) {
            console.error('OpenVidu 세션 초기화 중 오류 발생:', error);
            throw error;
        }
    }

    // OpenVidu 토큰 얻기
    async getToken(sessionId) {
        try {
            const response = await axios.post(
                `https://localhost:4443/openvidu/api/sessions/${sessionId}/connection`,
                {},
                {
                    headers: {
                        Authorization: 'Basic ' + btoa('OPENVIDUAPP:MY_SECRET'),
                        'Content-Type': 'application/json',
                    },
                },
            );
            return response.data.token;
        } catch (error) {
            if (error.response && error.response.status === 404) {
                // 세션이 없으면 새로 생성
                await this.createSession(sessionId);
                // 세션 생성 후 다시 토큰 얻기 시도
                return this.getToken(sessionId);
            }
            throw error;
        }
    }

    // OpenVidu 세션 생성
    async createSession(sessionId) {
        await axios.post(
            'https://localhost:4443/openvidu/api/sessions',
            { customSessionId: sessionId },
            {
                headers: {
                    Authorization: 'Basic ' + btoa('OPENVIDUAPP:MY_SECRET'),
                    'Content-Type': 'application/json',
                },
            },
        );
    }

    // OpenVidu 세션 연결 해제
    disconnectSession() {
        if (this.session) {
            this.session.disconnect();
        }
    }

    // 게스트 비디오 추가
    addGuestVideo(subscriber) {
        const guestVideo = {
            id: subscriber.stream.streamId,
            subscriber: subscriber,
            position: this.calculateRandomPosition(),
        };
        this.guestVideos.push(guestVideo);
    }

    // 게스트 비디오 제거
    removeGuestVideo(streamId) {
        const index = this.guestVideos.findIndex((video) => video.id === streamId);
        if (index >= 0) {
            this.guestVideos.splice(index, 1);
        }
    }

    // 랜덤 위치 계산 (예시)
    calculateRandomPosition() {
        return {
            x: Math.random() * 80, // 0-80% 사이의 랜덤 x 위치
            y: Math.random() * 80, // 0-80% 사이의 랜덤 y 위치
        };
    }

    // 게스트 비디오 목록 가져오기
    getGuestVideos() {
        return this.guestVideos;
    }

    // 게스트 비디오 위치 업데이트
    updateGuestVideoPosition(streamId, position) {
        const guestVideo = this.guestVideos.find((video) => video.id === streamId);
        if (guestVideo) {
            guestVideo.position = position;
        }
    }
}

export default new OpenViduService();
