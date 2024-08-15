<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoardPhotoComp from '@/components/board/BoardPhotoComp.vue';
import Page from '@/components/common/PageComp.vue';
import { onMounted, ref } from 'vue';
import { boardListApi } from '@/api/boardApi';
import { alertResult } from '@/api/baseApi';
import { debounce } from '@/assets/js/util';

const boardList = ref([]);
const sortArrow = ref('↓');
const prevCriteria = ref('createdAt');
const prevNickname = ref('');
const nickname = ref('');
const paging = ref({
	page: 0,
	size: 20,
	sort: 'createdAt,desc',
});
const isLoading = ref(true);
const debounceTimer = ref(0);

onMounted(() => {
	debounce(debounceTimer, getBoardList, 1000)();
});

const getNextBoards = async () => {
	++paging.value.page;
	isLoading.value = true;
	debounce(debounceTimer, getBoardList, 2000)();
};

const getBoardList = async () => {
	const { data } = await boardListApi(prevNickname.value, paging.value);
	if (!data.isSuccess) {
		await alertResult(false, '게시판 조회에 실패하였습니다.');
		return;
	}
	boardList.value = boardList.value.concat(data.result);
	isLoading.value = false;
};

const sortBoards = async (criteria) => {
	if (isLoading.value) {
		return;
	}
	paging.value.page = 0;
	boardList.value = [];
	if (prevCriteria.value !== criteria || sortArrow.value === '↑') {
		paging.value.sort = `${criteria},desc`;
	} else {
		paging.value.sort = `${criteria},asc`;
	}
	isLoading.value = true;
	debounce(debounceTimer, getBoardList, 1000)();
	toggleSort(criteria);
};

const searchByNickname = async () => {
	if (isLoading.value || prevNickname.value === nickname.value) {
		return;
	}

	sortArrow.value = '↓';
	prevCriteria.value = 'createdAt';
	paging.value.page = 0;
	paging.value.sort = 'createdAt,desc';
	boardList.value = [];

	isLoading.value = true;
	prevNickname.value = nickname.value;
	debounce(debounceTimer, getBoardList, 1000)();
};

const toggleSort = (criteria) => {
	if (prevCriteria.value === criteria) {
		if (sortArrow.value !== '↓') {
			sortArrow.value = '↓';
			return;
		}
		sortArrow.value = '↑';
		return;
	}

	sortArrow.value = '↓';
	prevCriteria.value = criteria;
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
							@click="sortBoards('hit')"
							:class="{ clicked: prevCriteria === 'hit' }"
						>
							좋아요순 <span>{{ prevCriteria === 'hit' ? sortArrow : '' }}</span>
						</button>
						<button
							@click="sortBoards('createdAt')"
							:class="{ clicked: prevCriteria === 'createdAt' }"
						>
							최신순 <span>{{ prevCriteria === 'createdAt' ? sortArrow : '' }}</span>
						</button>
					</div>
				</div>

				<div class="board">
					<div
						v-if="!isLoading && boardList.length === 0"
						style="font-size: 50px"
					>
						게시글 없음
					</div>
					<BoardPhotoComp
						v-else
						v-for="(board, index) in boardList"
						:key="board.id"
						:count="index + 1"
						:paging="paging"
						:board="board"
						@observe="getNextBoards"
					/>
					<div
						v-if="isLoading"
						class="loading"
					>
						<img
							src="@/assets/icon/모래시계.gif"
							alt="loading..."
						/>
					</div>
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
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
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
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;

	&:active {
		transform: translateY(-40%);
		transition: transform 0.3s ease;
	}
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
		cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;

		&:hover {
			background-color: rgb(98, 171, 217, 0.5);
		}

		&:active {
			transform: translateY(5px);
			transition: transform 0.3s ease;
		}
	}

	.clicked {
		background-color: #62abd9;
		color: white;
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

	&::-webkit-scrollbar {
		width: 8px;
	}

	&::-webkit-scrollbar-thumb {
		background: #888; /* Scrollbar color */
		border-radius: 10px;
	}

	&::-webkit-scrollbar-thumb:hover {
		background: #555;
	}
}
</style>
