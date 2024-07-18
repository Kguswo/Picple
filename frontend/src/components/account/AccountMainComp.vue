<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import Page from "@/components/common/Page.vue";

defineProps({
    title: String,
});
</script>

<template>
    <Page>
        <WhiteBoardComp class="whiteboard-area-account">
            <div class="account-content">
                <div id="title">{{ title }}</div>
                <slot></slot>
            </div>
        </WhiteBoardComp>
    </Page>
</template>

<style>
@import "@/assets/css/account.css";
</style>
