<script setup>
import { ref, watch, nextTick } from 'vue';

const props = defineProps({
	username: {
		type: String,
		required: true,
	},
	session: {
		type: Object,
		required: true,
	},
});

const emit = defineEmits(['close']);

const chatMessages = ref([]);
const newMessage = ref('');

// 채팅 메시지 수신 이벤트 핸들러
const setupChatHandler = () => {
	if (props.session) {
		props.session.on('signal:chat', (event) => {
			const messageData = JSON.parse(event.data);
			chatMessages.value.push({
				message: messageData.message,
				username: messageData.username || '익명',
				timestamp: messageData.timestamp,
			});
			nextTick(() => {
				scrollToBottom();
			});
		});
	}
};

// 자동 스크롤
const scrollToBottom = () => {
	const chatContent = document.querySelector('.chat-content');
	if (chatContent) {
		chatContent.scrollTop = chatContent.scrollHeight;
	}
};

// 메시지 전송 메서드
const sendMessage = () => {
	if (newMessage.value.trim() && props.session) {
		const messageData = {
			message: newMessage.value,
			username: props.username,
			timestamp: new Date().getTime(),
		};
		props.session.signal({
			data: JSON.stringify(messageData),
			type: 'chat',
		});
		newMessage.value = '';
	}
};

watch(
	() => props.session,
	(newSession) => {
		if (newSession) {
			setupChatHandler();
		}
	},
	{ immediate: true },
);
</script>

<template>
	<div class="chat-modal">
		<div class="chat-content">
			<div
				v-for="msg in chatMessages"
				:key="msg.timestamp"
				class="chat-message"
			>
				<strong>{{ msg.username }}</strong
				>: {{ msg.message }}
			</div>
		</div>
		<div class="chat-input">
			<input
				v-model="newMessage"
				@keyup.enter="sendMessage"
				placeholder="메시지 입력..."
			/>
			<button @click="sendMessage">전송</button>
		</div>
		<button
			class="close-button"
			@click="$emit('close')"
		>
			닫기
		</button>
	</div>
</template>

<style scoped>
.chat-modal {
	position: fixed;
	bottom: 90px;
	right: 20px;
	width: 300px;
	height: 400px;
	background-color: white;
	border: 1px solid #ccc;
	border-radius: 8px;
	display: flex;
	flex-direction: column;
}

.chat-content {
	flex-grow: 1;
	overflow-y: auto;
	padding: 10px;
}

.chat-message {
	margin-bottom: 5px;
}

.chat-input {
	display: flex;
	padding: 10px;
}

.chat-input input {
	flex-grow: 1;
	margin-right: 6px;
	height: 32px;
	padding-left: 6px;
}

.chat-input button {
	width: 48px;
}

.close-button {
	position: absolute;
	top: 10px;
	right: 10px;
	background: none;
	border: none;
	cursor: pointer;
}
</style>
