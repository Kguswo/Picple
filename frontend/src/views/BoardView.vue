<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoardPhotoComp from '@/components/board/BoardPhotoComp.vue';
import Page from '@/components/common/PageComp.vue';
import { onMounted, ref } from 'vue';
import { boardListApi, boardSearchApi, boardSortApi } from '@/api/boardApi';

const boardList = ref([]);
const isLikeClicked = ref(false);
const isTimeClicked = ref(false);

onMounted(async () => {
	try {
		const data = await boardListApi();
		if (!data) {
			return;
		}
		boardList.value = data.result;
	} catch (error) {}
});

const nickname = ref('');

const searchByNickname = async () => {
	if (!nickname.value) {
		return;
	}

	try {
		const data = await boardSearchApi(nickname.value);
		if (!data) {
			return;
		}
		boardList.value = data.result;
		toggleSortButton(false, false);
	} catch (error) {}
};

const sortByCreatedAt = async () => {
	try {
		const data = await boardSortApi('createdAt');
		if (!data) {
			return;
		}
		boardList.value = data.result;
		toggleSortButton(true, false);
	} catch (error) {}
};

const sortByHit = async () => {
	try {
		const data = await boardSortApi('hit');
		if (!data) {
			return;
		}
		boardList.value = data.result;
		toggleSortButton(false, true);
	} catch (error) {}
};

const toggleSortButton = (isTimeClicked, isLikeClicked) => {
	isTimeClicked.value = isTimeClicked;
	isLikeClicked.value = isLikeClicked;
};
</script>

<template>
	<Page>
		<WhiteBoardComp class="whiteboard-area-calendar">
			<div class="name-area">게시판</div>

			<div class="board-area">
				<div class="button-box">
					<form @submit.prevent="searchByNickname">
						<div class="input-container">
							<input
								type="text"
								name="nickname"
								v-model="nickname"
								placeholder=" 닉네임을 입력해주세요!"
								class="form-input"
								maxlength="8"
								autocomplete="off"
							/>
							<button
								type="button"
								class="form-button-small"
								@click="searchByNickname"
							>
								검색
							</button>
						</div>
					</form>

					<div class="button-group">
						<button
							class="button-sort-like"
							:class="{ clicked: isLikeClicked }"
							@click="sortByHit"
						>
							좋아요순
						</button>
						<button
							class="button-sort-time"
							:class="{ clicked: isTimeClicked }"
							@click="sortByCreatedAt"
						>
							최신순
						</button>
					</div>
				</div>

				<div class="board">
					<div
						v-if="boardList.length === 0"
						style="font-size: 50px"
					>
						게시글 없음
					</div>
					<BoardPhotoComp
						v-else
						v-for="board in boardList"
						:key="board.id"
						:board="board"
					/>
				</div>
			</div>
		</WhiteBoardComp>
	</Page>
</template>

<style scoped>
.name-area {
	width: 100%;
	height: 20%;

	display: flex;
	justify-content: center;
	align-items: center;

	border-bottom: 5px solid rgba(0, 0, 0, 0.9);

	color: black;
	font-size: 50px;
	text-shadow: 5px 5px #0000004d;
}

.board-area {
	width: 100%;
	height: 80%;

	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
}

.button-box {
	width: 85%;
	height: 10%;
	padding: 20px 0px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.input-container {
	width: 250px;
	height: 50px;
	position: relative;
	display: flex;
	justify-content: center;
	align-items: center;
	border-radius: 5px;
}

.form-input {
	width: 100%;
	box-sizing: border-box;
	border-radius: 5px;
	padding: 5px 10px;
	line-height: 35px;
	cursor: pointer;
	font-size: 15px;
}

.form-button-small {
	position: absolute;
	right: 5%;
	top: 50%;
	border: none;
	border-radius: 5px;
	transform: translateY(-50%);
	padding: 5px 10px;
	font-size: 15px;
	background-color: #62abd9;
	color: white;
	cursor: pointer;
}

.button-group {
	button {
		border-radius: 8px;
		padding: 5px 10px;
		line-height: 30px;
		margin-left: 8px;
		font-size: 15px;
		background-color: #ffffff;
		color: black;
		transition: background-color 0.3s ease;
		cursor: pointer;
	}

	.button-sort-time,
	.button-sort-like {
		&:hover {
			background-color: rgba(250, 198, 198, 0.3);
		}

		&:active {
			transform: translateY(4px);
		}
	}

	.clicked {
		background-color: rgb(250, 198, 198);
	}
}

.board {
	width: 90%;
	height: 90%;

	display: flex;
	flex-wrap: wrap;
	justify-content: center;
	align-items: center;

	overflow: scroll;
}
</style>
