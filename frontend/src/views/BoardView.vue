<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoardPhotoComp from '@/components/board/BoardPhotoComp.vue';
import Page from '@/components/common/PageComp.vue';
import { onMounted, ref } from 'vue';
import { boardListApi, boardSortApi } from '@/api/boardApi';
import { alertResult } from '@/api/baseApi';

const boardList = ref([]);
const sortArrow = ref('');
const prevCriteria = ref('');
const nickname = ref('');

onMounted(() => {
	getBoardList();
});

const getBoardList = async () => {
	const { data } = await boardListApi();
	if (!data.isSuccess) {
		await alertResult(false, '게시판 조회에 실패하였습니다.');
		return;
	}
	boardList.value = data.result;
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

const sortBoards = async (criteria) => {
	const { data } = await boardSortApi(
		nickname.value,
		criteria,
		prevCriteria.value !== criteria || sortArrow.value !== '↓' ? false : true,
	);
	if (!data.isSuccess) {
		await alertResult(false, '게시글 정렬에 실패하였습니다.');
		return;
	}
	boardList.value = data.result;
	toggleSort(criteria);
};

const searchByNickname = async () => {
	sortArrow.value = '';
	prevCriteria.value = '';

	if (!nickname.value) {
		await getBoardList();
		return;
	}

	const { data } = await boardSortApi(nickname.value, 'createdAt', false);
	if (!data.isSuccess) {
		await alertResult(false, '사용자 검색에 실패하였습니다.');
		return;
	}
	boardList.value = data.result;
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
}
</style>
