<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoardPhotoComp from "@/components/board/BoardPhotoComp.vue";
import Page from "@/components/common/PageComp.vue";
import { onMounted, ref } from "vue";
import { boardSortApi } from "@/api/boardApi";

const photos = ref([]);
const likeClicked = ref(false);
const timeClicked = ref(false);

onMounted(async () => {
    const data = await boardSortApi("createdAt") // 기본은 최신순 정렬
    photos.value = data.result;
})

const nickname = ref({ type: "text", label: "", value: "" });

const submitForm = () => {
    if (nickname.value == "") {
        window.alert("값을 입력해주세요!");
    }
    else {
        console.log('전송됨', nickname.value)
    }
}

const sortByCreatedAt = async () => {
    const data = await boardSortApi("createdAt")
    photos.value = data.result;
    timeClicked.value = true;
    likeClicked.value = false;
}

const sortByHit = async () => {
    const data = await boardSortApi("hit")
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
                    <form @submit.prevent="submitForm">
                        <div class="input-container">
                            <input type="text" name="nickname" placeholder=" 닉네임을 입력해주세요!" class="form-input"
                                maxlength="8" autocomplete="off" />
                            <button type="button" class="form-button-small">검색</button>
                        </div>
                    </form>

                    <div class="btn-group">
                        <button class="likeSort" :class="{ clicked: likeClicked }" @click="sortByHit">좋아요 순</button>
                        <button class="timeSort" :class="{ clicked: timeClicked }" @click="sortByCreatedAt">최신순</button>
                    </div>
                </div>

                <div class="board">
                    <BoardPhotoComp v-for="photo in photos" :key="photo.id" :photo="photo" />
                </div>
            </div>
        </WhiteBoardComp>
    </Page>
</template>

<style scoped>
@import "@/assets/css/board.css";
</style>
