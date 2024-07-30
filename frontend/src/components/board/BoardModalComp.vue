<script setup>
import { ref } from "vue";

const props = defineProps({
    photo: Object,
    isOpen: Boolean
})

const emit = defineEmits(["close"]);

const isDropdownOpen = ref(false);

const closeModal = () => {
    emit("close");
}

const toggleDropdown = () => {
    isDropdownOpen.value = !isDropdownOpen.value;
}
</script>

<template>
    <div class="modal" v-if="isOpen">
        <div class="modal-content">
            <div class="close-box">
                <span class="close" @click="closeModal">&times;</span>
                <svg class="dropdown-icon" xmlns="@/assets/icon/three-dots-vertical.svg" width="20" height="20"
                    fill="black" viewBox="0 0 16 16" @click="toggleDropdown">
                    <path
                        d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0" />
                </svg>
                <button v-if="isDropdownOpen" class="dropdown-menu">삭제하기</button>
            </div>
            <div class="modal-img">
                <img src="@/assets/img/tempImg.png" alt="">
                <div class="modal-text">
                    <span class="modal-date">촬영일: {{ photo.createdAt }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-content {
    padding: 20px;
    background-color: #fefefe;
    margin: 10vh auto;
    border: 1px solid #888;
    width: 40%;
    max-width: 60%;
    height: 60%;
    max-height: 80vh;
    overflow-y: auto;
    border-radius: 10px;
}

.dropdown-icon {
    cursor: pointer;
}

.dropdown-menu {
    position: absolute;
    background: white;
    color: red;
    border: 2px solid black;
    padding: 10px;
    margin-top: 30px;
    border-radius: 4px;
}

.close-box {
    padding-top: 0px;
    padding-bottom: 0px;
    height: 8%;
}

.close {
    color: #aaa;
    float: right;
    font-size: 28px;
    line-height: 28px;
    font-weight: bold;
    height: 28px;
}

.close:hover,
.close:focus {
    color: black;
    text-decoration: none;
    cursor: pointer;
}

.modal-img {
    height: 90%;
    display: flex;
    align-items: center;
    text-align: center;
    justify-content: center;
    flex-wrap: wrap;

    img {
        height: 90%;
        width: 95%;
    }

    .modal-text {
        height: 10%;
        width: 90%;
        text-align: center;
    }
}

.button-delete {
    background-color: none;
}
</style>