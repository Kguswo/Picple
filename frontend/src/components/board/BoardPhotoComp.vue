<script setup>
import { ref } from 'vue';
import BoardModalComp from '@/components/board/BoardModalComp.vue';
import { boardDeleteApi, boardLikeApi } from '@/api/boardApi';
import Swal from 'sweetalert2';
import router from '@/router';

const props = defineProps({
	board: Object,
});

const isModalOpen = ref(false);

const openModal = () => {
	isModalOpen.value = true;
};

const closeModal = () => {
	isModalOpen.value = false;
};

const toggleLike = async () => {
	const data = await boardLikeApi(props.board.id);
	if (!data.isSuccess) {
		await Swal.fire({ icon: 'error', title: '좋아요 누르기에 실패하였습니다.', width: 600 });
		return;
	}
	if (props.board.liked) {
		--props.board.hit;
		props.board.liked = false;
	} else {
		++props.board.hit;
		props.board.liked = true;
	}
};

const deleteBoard = async () => {
	const { value: accept } = await Swal.fire({
		title: '정말 게시글을 삭제하시겠습니까?',
		confirmButtonText: `Continue&nbsp;<i class="fa fa-arrow-right"></i>`,
		showCancelButton: true,
	});
	if (accept) {
		const data = await boardDeleteApi(props.board.id);
		if (data.code === 2000) {
			await Swal.fire({ icon: 'error', title: '게시글 삭제는 작성자만 할 수 있습니다.', width: 600 });
			return;
		}
		if (!data.isSuccess) {
			await Swal.fire({ icon: 'error', title: '게시글 삭제에 실패하였습니다.', width: 600 });
			return;
		}
		await Swal.fire({ icon: 'success', title: '게시글이 삭제되었습니다.', width: 600 });
		router.go(0);
	}
};
</script>

<template>
	<div class="photo-card">
		<div
			class="photo"
			@click="openModal"
		></div>
		<div class="content">
			<div class="like">
				<svg
					v-if="board.liked"
					xmlns="@/assets/icon/hear-fill.svg"
					class="heart"
					width="20"
					height="20"
					fill="red"
					viewBox="0 0 16 16"
					@click="toggleLike"
				>
					<path
						fill-rule="evenodd"
						d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314"
					/>
				</svg>
				<svg
					v-else
					xmlns="@/assets/icon/hear.svg"
					width="20"
					height="20"
					fill="red"
					class="heart"
					viewBox="0 0 16 16"
					@click="toggleLike"
				>
					<path
						d="m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15"
					/>
				</svg>
				<span class="like-cnt">{{ board.hit }}</span>
			</div>
		</div>
	</div>

	<div @keyup.esc="closeModal">
		<BoardModalComp
			:isOpen="isModalOpen"
			:board="board"
			@close="closeModal"
			@delete="deleteBoard"
		/>
	</div>
</template>

<style scoped>
.photo-card {
	margin: 5px;
	height: 80%;
	width: 23%;
	border: 2px solid gray;
	background-color: white;
	box-shadow: 5px 5px 5px black;

	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;

	* {
		font-family: 'PFStardust';
		font-weight: lighter;
	}
}

.photo {
	margin-top: 10px;
	height: 80%;
	width: 90%;
	border: 2px solid gray;
	background-color: rgba(192, 192, 192, 0.722);
}

.content {
	display: flex;
	align-items: center;
	justify-content: end;
	width: 90%;
	height: 20%;

	.like {
		display: flex;
		align-items: center;
		justify-content: center;

		svg {
			margin-right: 7px;
		}

		.like-cnt {
			font-size: 20px;
		}
	}
}
</style>
