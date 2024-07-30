<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoardPhotoComp from "@/components/board/BoardPhotoComp.vue";
import Page from "@/components/common/PageComp.vue";
import { onMounted, ref } from "vue";
import { boardSearchApi, boardSortApi } from "@/api/boardApi";
import Swal from "sweetalert2";

const photos = ref([]);
const likeClicked = ref(false);
const timeClicked = ref(false);

onMounted(async () => {
    const data = await boardSortApi("createdAt") // 기본은 최신순 정렬
    photos.value = data.result;
})

const nickname = ref("");

const searchByNickname = async () => {
    if (!nickname.value) {
        return;
    }
    const data = await boardSearchApi(nickname.value);
    if (!data.isSuccess) {
        await Swal.fire({ icon: "error", title: "검색 조회에 실패하였습니다.", width: 600 });
        return;
    }
    photos.value = data.result;
}

const sortByCreatedAt = async () => {
    const data = await boardSortApi("createdAt")
    if (!data.isSuccess) {
        await Swal.fire({ icon: "error", title: "최신순 정렬에 실패하였습니다.", width: 600 });
        return;
    }
    photos.value = data.result;
    timeClicked.value = true;
    likeClicked.value = false;
}

const sortByHit = async () => {
    const data = await boardSortApi("hit")
    if (!data.isSuccess) {
        await Swal.fire({ icon: "error", title: "좋아요순 정렬에 실패하였습니다.", width: 600 });
        return;
    }
    photos.value = data.result;
    likeClicked.value = true;
    timeClicked.value = false;
}

</script>

<template>
    <Page>
        <WhiteBoardComp class="whiteboard-area-calendar">
            <div class="name-area">게시판</div>

            <div class="board-area">
                <div class="button-box">
                    <form @submit.prevent="searchByNickname">
                        <div class="input-container">
                            <input type="text" name="nickname" v-model="nickname" placeholder=" 닉네임을 입력해주세요!"
                                class="form-input" maxlength="8" autocomplete="off" />
                            <button type="button" class="form-button-small" @click="searchByNickname">검색</button>
                        </div>
                    </form>

                    <div class="btn-group">
                        <button class="likeSort" :class="{ clicked: likeClicked }" @click="sortByHit">좋아요순</button>
                        <button class="timeSort" :class="{ clicked: timeClicked }" @click="sortByCreatedAt">최신순</button>
                    </div>
                </div>

                <div class="board">
                    <div v-if="photos.length === 0" style="font-size: 50px">게시물 없음</div>
                    <BoardPhotoComp v-else v-for="photo in photos" :key="photo.id" :photo="photo" />
                </div>
            </div>
        </WhiteBoardComp>
    </Page>
</template>

<style scoped>
@import "@/assets/css/board.css";
</style>
