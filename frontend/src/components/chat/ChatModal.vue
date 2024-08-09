<template>
	<div>
		<button @click="openChat">채팅 열기</button>
		<div
			v-if="isChatOpen"
			class="chat-modal"
		>
			<div class="chat-content">
				<div
					v-for="(msg, index) in messages"
					:key="index"
				>
					{{ msg.sender }}: {{ msg.content }}
				</div>
			</div>
			<input
				v-model="newMessage"
				@keyup.enter="sendMessage"
				placeholder="메시지 입력..."
			/>
			<button @click="sendMessage">보내기</button>
			<button @click="closeChat">닫기</button>
		</div>
	</div>
</template>

<script>
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

export default {
	data() {
		return {
			isChatOpen: false,
			stompClient: null,
			newMessage: '',
			messages: [],
			username: '',
		};
	},
	methods: {
		openChat() {
			this.isChatOpen = true;
			this.connect();
		},
		closeChat() {
			this.isChatOpen = false;
			if (this.stompClient) {
				this.stompClient.disconnect();
			}
		},
		connect() {
			const socket = new SockJS('http://localhost:8080/ws');
			this.stompClient = Stomp.over(socket);
			this.stompClient.connect({}, this.onConnected, this.onError);
		},
		onConnected() {
			this.stompClient.subscribe('/topic/public', this.onMessageReceived);
			this.username = prompt('사용자 이름을 입력하세요:');
			this.stompClient.send('/app/chat.addUser', JSON.stringify({ sender: this.username, type: 'JOIN' }), {});
		},
		onError(error) {
			console.log('STOMP error', error);
		},
		onMessageReceived(payload) {
			const message = JSON.parse(payload.body);
			this.messages.push(message);
		},
		sendMessage() {
			if (this.newMessage && this.stompClient) {
				const chatMessage = {
					sender: this.username,
					content: this.newMessage,
					type: 'CHAT',
				};
				this.stompClient.send('/app/chat.sendMessage', JSON.stringify(chatMessage), {});
				this.newMessage = '';
			}
		},
	},
};
</script>

<style scoped>
.chat-modal {
	position: fixed;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	background-color: white;
	padding: 20px;
	border: 1px solid #ccc;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.chat-content {
	height: 300px;
	overflow-y: auto;
	margin-bottom: 10px;
}
</style>
