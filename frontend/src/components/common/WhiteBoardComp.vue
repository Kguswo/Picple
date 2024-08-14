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
    width: 50%;
    height: 80%;
    min-height: 545px;
    min-width: 960px;
}

.whiteboard-area-form {
    width: 50%;
    height: 80%;
    min-height: 545px;
    min-width: 960px;
}

.whiteboard-area-booth {
    width: 60%;
    height: 85%;
    min-height: 545px;

    min-width: 880px;
}

.whiteboard-area-shoot-booth {
    width: 70%;
    height: 90%;
    min-height: 545px;

    min-width: 880px;
}

.background {
    width: 100%;
    height: 100%;

    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;

    border: 8px solid rgba(0, 0, 0, 0.9);
    border-radius: 20px;
    background-color: #f4f0d1f7;

    box-shadow: 10px 15px 10px rgb(0 0 0 / 85%);
}
</style>
