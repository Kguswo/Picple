import { defineStore } from 'pinia';

export const useBoothStore = defineStore('booth', {
	state: () => ({
		bgImage: 'https://via.placeholder.com/400',
		images: [],
		remainPicCnt: 10,
		sessionInfo: null,
		session: null,
		boothCode: null,
	}),
	actions: {
		setBgImage(image) {
			this.bgImage = image;
			console.log('Background image set:', image);
		},
		addImage(image) {
			this.images.push(image);
			this.remainPicCnt--;
			console.log('Image added, remaining count:', this.remainPicCnt);
		},
		setSessionInfo(info) {
			if (!info || typeof info !== 'object') {
				console.error('Invalid session info provided');
				return;
			}
			this.sessionInfo = {
				...info,
				isHost: info.isHost !== undefined ? info.isHost : false
			};			
			console.log('Session info set:', info);
			// 로컬 스토리지에 저장 (선택사항)
			localStorage.setItem('boothSessionInfo', JSON.stringify(this.sessionInfo));
		},
		getSessionInfo() {
			if (!this.sessionInfo) {
				// 로컬 스토리지에서 복원 시도 (선택사항)
				const storedInfo = localStorage.getItem('boothSessionInfo');
				if (storedInfo) {
					this.sessionInfo = JSON.parse(storedInfo);
					console.log('Session info restored from storage:', this.sessionInfo);
				}
			}
			console.log('Retrieving session info:', this.sessionInfo);
			return this.sessionInfo;
		},
		setBoothCode(code) {
			this.boothCode = code;
			console.log('Booth code set:', code);
		},
		getBoothCode() {
			return this.boothCode;
		},
		clearSessionInfo() {
			this.sessionInfo = null;
			this.boothCode = null;
			console.log('Session info cleared');
			// 로컬 스토리지에서도 제거 (선택사항)
			localStorage.removeItem('boothSessionInfo');
		},
	},
	getters: {
		isHost: (state) => state.sessionInfo?.isHost || false
	}
});
