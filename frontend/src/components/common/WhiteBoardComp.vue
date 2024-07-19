<script setup></script>

<template>
    <div>
        <div class="background">
            <slot></slot>
        </div>
    </div>
</template>

<style scoped>
.whiteboard-area-calendar {
    /* size */
    width: 50%;
    height: 80%;
    min-height: 545px;
    min-width: 960px;
}

.whiteboard-area-form {
    /* size */
    width: 50%;
    height: 80%;
    min-height: 545px;
    min-width: 960px;
}

.whiteboard-area-booth {
    /* size */
    width: 70%;
    height: 90%;
    min-height: 545px;
    min-width: 880px;
}

.background {
    /* size */
    width: 100%;
    height: 100%;

    /* display */
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;

    /* color */
    border: 8px solid rgba(0, 0, 0, 0.9);
    border-radius: 20px;
    background-color: rgba(255, 255, 255, 0.85);

    /* 그림자 효과 */
    box-shadow: 10px 15px 10px rgb(0 0 0 / 85%);
}
</style>
